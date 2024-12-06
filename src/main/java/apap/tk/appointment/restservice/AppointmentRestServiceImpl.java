package apap.tk.appointment.restservice;
import apap.tk.appointment.model.Appointment;
import apap.tk.appointment.model.Treatment;
import apap.tk.appointment.repository.AppointmentDb;
import apap.tk.appointment.repository.TreatmentDb;
import apap.tk.appointment.restdto.request.AddAppointmentRequestRestDTO;
import apap.tk.appointment.restdto.request.BillRequestRestDTO;
import apap.tk.appointment.restdto.response.AppointmentResponseRestDTO;
import apap.tk.appointment.restdto.response.BaseResponseDTO;
import apap.tk.appointment.restdto.response.DoctorResponseDTO;
import apap.tk.appointment.restdto.response.PatientResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import apap.tk.appointment.security.jwt.JwtTokenHolder;

@Service
@Transactional
public class AppointmentRestServiceImpl implements AppointmentRestService {

    @Autowired
    private JwtTokenHolder tokenHolder;

    private final AppointmentDb appointmentDb;
    private final TreatmentDb treatmentDb;
    private final WebClient userWebClient;
    private final WebClient billWebClient;

    public AppointmentRestServiceImpl(AppointmentDb appointmentDb, TreatmentDb treatmentDb, WebClient.Builder webClientBuilder) {
        this.appointmentDb = appointmentDb;
        this.treatmentDb = treatmentDb;
        this.userWebClient = webClientBuilder.baseUrl("http://profile:8086/api/profile").build(); // Base URL untuk Profile
        this.billWebClient = webClientBuilder.baseUrl("http://bill:8082/api/bill").build();
    }

    private static final Map<Integer, String> SPECIALIZATION_CODES = new HashMap<>();

    static {
        SPECIALIZATION_CODES.put(0, "UMM");  // Dokter Umum
        SPECIALIZATION_CODES.put(1, "GGI");  // Dokter Gigi
        SPECIALIZATION_CODES.put(2, "ANK");  // Spesialis Anak
        SPECIALIZATION_CODES.put(3, "BDH");  // Spesialis Bedah
        SPECIALIZATION_CODES.put(4, "PRE");  // Bedah Plastik
        SPECIALIZATION_CODES.put(5, "JPD");  // Jantung dan Pembuluh Darah
        SPECIALIZATION_CODES.put(6, "KKL");  // Kulit dan Kelamin
        SPECIALIZATION_CODES.put(7, "MTA");  // Mata
        SPECIALIZATION_CODES.put(8, "PDL");  // Penyakit Dalam
        SPECIALIZATION_CODES.put(9, "PRU");  // Paru
        SPECIALIZATION_CODES.put(10, "THT"); // Telinga, Hidung, Tenggorokan, Bedah Kepala Leher
        SPECIALIZATION_CODES.put(11, "KSJ"); // Kesehatan Jiwa
        SPECIALIZATION_CODES.put(12, "ANS"); // Anestesi
        SPECIALIZATION_CODES.put(13, "NRO"); // Neurologi
        SPECIALIZATION_CODES.put(14, "URO"); // Urologi
        SPECIALIZATION_CODES.put(15, "OBG"); // Obstetri dan Ginekologi
        SPECIALIZATION_CODES.put(16, "RAD"); // Radiologi
    }

    // Method untuk generate appointment ID
    private String generateAppointmentId(Integer specialization, Date appointmentDate) {
        // Karakter [1,3]: Spesialisasi dokter
        String doctorSpecialty = SPECIALIZATION_CODES.get(specialization);

        // Karakter [4,7]: Tanggal appointment dalam format DDMM
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMM");
        String datePart = dateFormat.format(appointmentDate);

        // Karakter [8,10]: Urutan appointment dalam suatu hari
        List<Appointment> appointmentsOnDate = appointmentDb.findByDate(appointmentDate);
        int sequenceNumber = appointmentsOnDate.size() + 1;  // Urutan berdasarkan jumlah appointment yang ada
        String sequencePart = String.format("%03d", sequenceNumber);

        // Gabungkan semua bagian untuk membentuk ID
        return doctorSpecialty + datePart + sequencePart;
    }

    public DoctorResponseDTO getDoctorById(UUID doctorId) throws Exception{
        var response = userWebClient.get()
            .uri("/user?id=" + doctorId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenHolder.getToken())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<DoctorResponseDTO>>(){})
            .block();// Block untuk menunggu responsnya
        if (response == null) {
            throw new Exception("Doctor not found");
        }

        return response.getData();}

    public PatientResponseDTO getPatientById(UUID patientId) throws Exception {
        var response = userWebClient.get()
            .uri("/user?id=" + patientId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<PatientResponseDTO>>(){})
            .block();// Block untuk menunggu responsnya
        if (response == null) {
            throw new Exception("Patient not found");
        }
        return response.getData();
    }

    @Override
    public List<AppointmentResponseRestDTO> getAllAppointment() {
        var listAppointments = appointmentDb.findAllByIsDeletedFalse();
        var listAppointmentsResponseDTO = new ArrayList<AppointmentResponseRestDTO>();
        listAppointments.forEach(appointment -> {
            var appointmentResponseDTO = appointmentToAppointmentResponseDTO(appointment);
            listAppointmentsResponseDTO.add(appointmentResponseDTO);
        });
        return listAppointmentsResponseDTO;
    }    

    @Override
    public AppointmentResponseRestDTO getAppointmentById(String id) {
        var appointment = appointmentDb.findByIdAndIsDeletedFalse(id);
        if (appointment == null) {
            return null;
        }
        return appointmentToAppointmentResponseDTO(appointment);
    }

    @Override
    public List<AppointmentResponseRestDTO> getAllAppointmentByDoctor(UUID doctorId) {
        List<Appointment> listAppointments = appointmentDb.findAllByDoctorAndIsDeletedFalse(doctorId);
        var listAppointmentsResponseDTO = new ArrayList<AppointmentResponseRestDTO>();

        listAppointments.forEach(appointment -> {
            var appointmentResponseDTO = appointmentToAppointmentResponseDTO(appointment);
            listAppointmentsResponseDTO.add(appointmentResponseDTO);
        });

        return listAppointmentsResponseDTO;
    }
    
    @Override
    public List<AppointmentResponseRestDTO> getAllAppointmentsByDate(Date from, Date to) {
        List<Appointment> listAppointments = appointmentDb.findByDateBetween(from, to);
        List<AppointmentResponseRestDTO> listAppointmentsResponseDTO = new ArrayList<>();

        // Konversi setiap Appointment ke AppointmentResponseDTO
        listAppointments.forEach(appointment -> {
            var appointmentResponseDTO = appointmentToAppointmentResponseDTO(appointment);
            listAppointmentsResponseDTO.add(appointmentResponseDTO);
        });

        return listAppointmentsResponseDTO;
    }

    @Override
    public List<AppointmentResponseRestDTO> getAllAppointmentsByPatient(UUID patientId) {
        // Cari semua Appointment terkait Patient
        List<Appointment> listAppointments = appointmentDb.findAllByPatientAndIsDeletedFalse(patientId);
        var listAppointmentsResponseDTO = new ArrayList<AppointmentResponseRestDTO>();

        // Konversi setiap Appointment ke AppointmentResponseDTO
        listAppointments.forEach(appointment -> {
            var appointmentResponseDTO = appointmentToAppointmentResponseDTO(appointment);
            listAppointmentsResponseDTO.add(appointmentResponseDTO);
        });

        return listAppointmentsResponseDTO;
    }    

    @Override
    public AppointmentResponseRestDTO createAppointment(AddAppointmentRequestRestDTO appointment) throws Exception {
        // Check if appointment already exists for the doctor on that date
        boolean appointmentExists = appointmentDb.existsByDoctorAndDateAndIsDeletedFalse(appointment.getDoctor(), appointment.getAppointmentDate()
        );

        if (appointmentExists) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Doctor already has an appointment scheduled for this date"
            );
        }
        var patient = userWebClient.get()
            .uri("/user?id=" + appointment.getPatient())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenHolder.getToken())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<PatientResponseDTO>>(){})
            .block();

        var doctor = userWebClient.get()
            .uri("/user?id=" + appointment.getDoctor())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenHolder.getToken())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<DoctorResponseDTO>>(){})
            .block();
        
        var fee = doctor.getData().getFee();
        var specialization = doctor.getData().getSpecialist();
        var appointmentId = generateAppointmentId(specialization, appointment.getAppointmentDate());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        var newAppointment = new Appointment();
        newAppointment.setId(appointmentId);
        newAppointment.setPatient(appointment.getPatient());
        newAppointment.setDoctor(appointment.getDoctor());
        newAppointment.setDate(appointment.getAppointmentDate());
        newAppointment.setCreatedBy(username);
        newAppointment.setCreatedAt(LocalDateTime.now());
        newAppointment.setStatus(0); // Status Created
        newAppointment.setTotalFee(fee);
        newAppointment.setIsDeleted(false);
        newAppointment.setUpdatedBy(username);
        newAppointment.setUpdatedAt(LocalDateTime.now());

        var svAppointment = appointmentDb.save(newAppointment);

        var billRequest = new BillRequestRestDTO();
        billRequest.setAppointmentId(appointmentId);
        billRequest.setPatientId(newAppointment.getPatient());
        billRequest.setFee(newAppointment.getTotalFee());
        billRequest.setDate(newAppointment.getDate());
        billRequest.setStatus(0); // Status Created
        billRequest.setCreatedBy(username);
        billRequest.setCreatedAt(LocalDateTime.now());
        billRequest.setUpdatedBy(username);
        billRequest.setUpdatedAt(LocalDateTime.now());

        // Call bill service to create bill
        try {
            var billResponse = billWebClient.post()
                .uri("/create")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenHolder.getToken())
                .bodyValue(billRequest)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<Object>>(){})
                .block();
                
            if (billResponse == null || billResponse.getStatus() != HttpStatus.CREATED.value()) {
                // Handle error - you might want to delete the appointment or mark it as failed
                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create bill for appointment"
                );
            }
        } catch (Exception e) {
            // Handle error - you might want to delete the appointment or mark it as failed
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error occurred while creating bill: " + e.getMessage()
            );
        }

        return appointmentToAppointmentResponseDTO(svAppointment);
    }

    @Override
    public AppointmentResponseRestDTO updateAppointmentStatus(String id, int newStatus) {
        // Find appointment by ID
        Optional<Appointment> appointmentOptional = appointmentDb.findById(id);
        if (appointmentOptional.isEmpty()) {
            return null;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Appointment appointment = appointmentOptional.get();
        appointment.setStatus(newStatus);
        appointment.setUpdatedBy(username);
        appointment.setUpdatedAt(LocalDateTime.now());
        
        // If appointment status is set to DONE (1), update the associated bill status to UNPAID (1)
        if (newStatus == 1) {
            try {
                // Create bill update request
                var billRequest = new BillRequestRestDTO();
                billRequest.setAppointmentId(appointment.getId());
                billRequest.setPatientId(appointment.getPatient());
                billRequest.setFee(appointment.getTotalFee());
                billRequest.setDate(appointment.getDate());
                billRequest.setStatus(1); // Status Unpaid
                billRequest.setUpdatedBy(username);
                billRequest.setUpdatedAt(LocalDateTime.now());

                // Call bill service to update bill
                var billResponse = billWebClient.put()
                    .uri("/update/" + appointment.getId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenHolder.getToken())
                    .bodyValue(billRequest)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<Object>>(){})
                    .block();
                    
                if (billResponse == null || billResponse.getStatus() != HttpStatus.OK.value()) {
                    throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to update bill status for appointment"
                    );
                }
            } catch (Exception e) {
                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred while updating bill status: " + e.getMessage()
                );
            }
        }

        appointmentDb.save(appointment);
        return appointmentToAppointmentResponseDTO(appointment);
    }

    @Override
    public AppointmentResponseRestDTO updateAppointmentDiagnosisAndTreatment(String id, String diagnosis, List<String> treatments) {
        // Cari appointment berdasarkan ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Appointment> appointmentOptional = appointmentDb.findById(id);
        if (appointmentOptional.isEmpty()) {
            return null;  // Appointment tidak ditemukan
        }
        Appointment appointment = appointmentOptional.get();
        // Update diagnosis
        appointment.setDiagnosis(diagnosis);
        appointment.setUpdatedBy(username);
        appointment.setUpdatedAt(LocalDateTime.now());
        // Update daftar treatments
        if (treatments != null) {
            List<Treatment> treatmentList = treatments.stream()
                    .map(treatmentDb::findByName)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            appointment.setTreatments(treatmentList);
        }
        var doctor = userWebClient.get()
            .uri("/user?id=" + appointment.getDoctor())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenHolder.getToken())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<DoctorResponseDTO>>(){})
            .block();
        
        var fee = doctor.getData().getFee() + appointment.getTreatments().stream()
            .mapToLong(Treatment::getPrice)
            .sum();
        
        appointment.setTotalFee(fee);

        try {
            // Create bill update request
            var billRequest = new BillRequestRestDTO();
            billRequest.setAppointmentId(appointment.getId());
            billRequest.setPatientId(appointment.getPatient());
            billRequest.setFee(appointment.getTotalFee());
            billRequest.setDate(appointment.getDate());
            billRequest.setUpdatedAt(LocalDateTime.now());
            billRequest.setUpdatedBy(username);
            billRequest.setStatus(appointment.getStatus()); // Status Unpaid
            // Call bill service to update bill
            var billResponse = billWebClient.put()
                .uri("/update/" + appointment.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenHolder.getToken()) // Ensure token is included
                .bodyValue(billRequest)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<Object>>(){})
                .block();
                
            if (billResponse == null || billResponse.getStatus() != HttpStatus.OK.value()) {
                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update bill status for appointment"
                );
            }
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error occurred while updating bill status: " + e.getMessage()
            );
        }
        appointmentDb.save(appointment); 
        return appointmentToAppointmentResponseDTO(appointment);
    }

    @Override
    public boolean deleteAppointmentById(String id) {
        Optional<Appointment> appointmentOptional = appointmentDb.findById(id);
        if (appointmentOptional.isEmpty()) {
            return false;
        }
        Appointment appointment = appointmentOptional.get();
        appointment.setIsDeleted(true);
        appointmentDb.save(appointment); 
        return true;
    }

    @Override
    public List<AppointmentResponseRestDTO> getAllAppointmentsByDoctorUsername(String username) {
        var doctorResponse = userWebClient.get()
            .uri("/user?username=" + username)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenHolder.getToken())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<DoctorResponseDTO>>(){})
            .block();

        if (doctorResponse == null || doctorResponse.getData() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found");
        }

        UUID doctorId = UUID.fromString(doctorResponse.getData().getId());
        List<Appointment> listAppointments = appointmentDb.findAllByDoctorAndIsDeletedFalse(doctorId);
        var listAppointmentsResponseDTO = new ArrayList<AppointmentResponseRestDTO>();

        listAppointments.forEach(appointment -> {
            var appointmentResponseDTO = appointmentToAppointmentResponseDTO(appointment);
            listAppointmentsResponseDTO.add(appointmentResponseDTO);
        });

        return listAppointmentsResponseDTO;
    }

    private AppointmentResponseRestDTO appointmentToAppointmentResponseDTO(Appointment appointment) {
        var appointmentResponseDTO = new AppointmentResponseRestDTO();
        appointmentResponseDTO.setId(appointment.getId());
        appointmentResponseDTO.setDoctor(appointment.getDoctor());
        appointmentResponseDTO.setPatient(appointment.getPatient());
        appointmentResponseDTO.setDiagnosis(appointment.getDiagnosis());
        appointmentResponseDTO.setCreatedAt(java.sql.Timestamp.valueOf(appointment.getCreatedAt()));
        appointmentResponseDTO.setUpdatedAt(java.sql.Timestamp.valueOf(appointment.getUpdatedAt()));
    
        if (appointment.getTreatments() != null && !appointment.getTreatments().isEmpty()) {
            List<String> treatmentNames = appointment.getTreatments().stream()
                    .map(Treatment::getName) // Ambil nama treatment
                    .collect(Collectors.toList()); // Ubah ke list nama treatment
            appointmentResponseDTO.setTreatments(treatmentNames); // Set nama-nama treatment
        }
    
        appointmentResponseDTO.setTotalFee(appointment.getTotalFee());
        appointmentResponseDTO.setStatus(appointment.getStatus());
        appointmentResponseDTO.setDate(appointment.getDate());
    
        return appointmentResponseDTO;
    }
}

package apap.tk.appointment.restservice;
import apap.tk.appointment.model.Appointment;
import apap.tk.appointment.model.Doctor;
import apap.tk.appointment.model.Treatment;
import apap.tk.appointment.repository.AppointmentDb;
import apap.tk.appointment.repository.DoctorDb;
import apap.tk.appointment.repository.TreatmentDb;
import apap.tk.appointment.restdto.response.AppointmentResponseDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppointmentRestServiceImpl implements AppointmentRestService {
    private final AppointmentDb appointmentDb;
    private final DoctorDb doctorDb;
    private final TreatmentDb treatmentDb;
    public AppointmentRestServiceImpl(AppointmentDb appointmentDb, DoctorDb doctorDb, TreatmentDb treatmentDb) {
        this.appointmentDb = appointmentDb;
        this.doctorDb = doctorDb;
        this.treatmentDb = treatmentDb;
    }
    
    @Override
    public List<AppointmentResponseDTO> getAllAppointment() {
        var listAppointments = appointmentDb.findAllByIsDeletedFalse();
        var listAppointmentsResponseDTO = new ArrayList<AppointmentResponseDTO>();
        listAppointments.forEach(appointment -> {
            var appointmentResponseDTO = appointmentToAppointmentResponseDTO(appointment);
            listAppointmentsResponseDTO.add(appointmentResponseDTO);
        });
        return listAppointmentsResponseDTO;
    }

    private AppointmentResponseDTO appointmentToAppointmentResponseDTO(Appointment appointment) {
    var appointmentResponseDTO = new AppointmentResponseDTO();
    appointmentResponseDTO.setId(appointment.getId());
    appointmentResponseDTO.setDoctorName(appointment.getDoctor().getName());
    appointmentResponseDTO.setPatientName(appointment.getPatient().getName());
    appointmentResponseDTO.setDiagnosis(appointment.getDiagnosis());

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

    @Override
    public AppointmentResponseDTO getAppointmentById(String id) {
        var appointment = appointmentDb.findById(id).orElse(null);
        if (appointment == null) {
            return null;
        }
        return appointmentToAppointmentResponseDTO(appointment);
    }

    @Override
    public List<AppointmentResponseDTO> getAllAppointmentByDoctor(String doctorId) {
        // Cari Doctor berdasarkan doctorId
        Optional<Doctor> doctorOptional = doctorDb.findById(doctorId);

        // Jika Doctor tidak ditemukan, kembalikan daftar kosong
        if (doctorOptional.isEmpty()) {
            return new ArrayList<>();
        }

        Doctor doctor = doctorOptional.get();
        
        // Cari semua Appointment terkait Doctor
        List<Appointment> listAppointments = appointmentDb.findAllByDoctorAndIsDeletedFalse(doctor);
        var listAppointmentsResponseDTO = new ArrayList<AppointmentResponseDTO>();

        // Konversi setiap Appointment ke AppointmentResponseDTO
        listAppointments.forEach(appointment -> {
            var appointmentResponseDTO = appointmentToAppointmentResponseDTO(appointment);
            listAppointmentsResponseDTO.add(appointmentResponseDTO);
        });

        return listAppointmentsResponseDTO;
    }
    
    @Override
    public List<AppointmentResponseDTO> getAllAppointmentsByDate(Date from, Date to) {
        // Mencari semua Appointment dalam rentang tanggal dari `from` hingga `to`
        List<Appointment> listAppointments = appointmentDb.findByDateBetween(from, to);
        List<AppointmentResponseDTO> listAppointmentsResponseDTO = new ArrayList<>();

        // Konversi setiap Appointment ke AppointmentResponseDTO
        listAppointments.forEach(appointment -> {
            var appointmentResponseDTO = appointmentToAppointmentResponseDTO(appointment);
            listAppointmentsResponseDTO.add(appointmentResponseDTO);
        });

        return listAppointmentsResponseDTO;
    }

    @Override
    public List<AppointmentResponseDTO> getAllAppointmentsByPatient(UUID patientId) {
        // Cari semua Appointment terkait Patient
        List<Appointment> listAppointments = appointmentDb.findAllByPatientIdAndIsDeletedFalse(patientId);
        var listAppointmentsResponseDTO = new ArrayList<AppointmentResponseDTO>();

        // Konversi setiap Appointment ke AppointmentResponseDTO
        listAppointments.forEach(appointment -> {
            var appointmentResponseDTO = appointmentToAppointmentResponseDTO(appointment);
            listAppointmentsResponseDTO.add(appointmentResponseDTO);
        });

        return listAppointmentsResponseDTO;
    }

    @Override
    public AppointmentResponseDTO createAppointment(Appointment appointment) {
        // Simpan Appointment ke database
        Appointment savedAppointment = appointmentDb.save(appointment);
        return appointmentToAppointmentResponseDTO(savedAppointment);
    }

    @Override
    public AppointmentResponseDTO updateAppointmentStatus(String id, int newStatus) {
        // Cari appointment berdasarkan ID
        Optional<Appointment> appointmentOptional = appointmentDb.findById(id);
        if (appointmentOptional.isEmpty()) {
            return null;  // Appointment tidak ditemukan
        }

        Appointment appointment = appointmentOptional.get();
        appointment.setStatus(newStatus);  // Update status
        appointmentDb.save(appointment);   // Simpan perubahan

        // Kembalikan hasil dalam bentuk AppointmentResponseDTO
        return appointmentToAppointmentResponseDTO(appointment);
    }

    @Override
    public AppointmentResponseDTO updateAppointmentDiagnosisAndTreatment(String id, String diagnosis, List<String> treatments) {
        // Cari appointment berdasarkan ID
        Optional<Appointment> appointmentOptional = appointmentDb.findById(id);
        if (appointmentOptional.isEmpty()) {
            return null;  // Appointment tidak ditemukan
        }

        Appointment appointment = appointmentOptional.get();

        // Update diagnosis
        appointment.setDiagnosis(diagnosis);

        // Update daftar treatments
        if (treatments != null) {
            List<Treatment> treatmentList = treatments.stream()
                    .map(treatmentDb::findByName)
                    .filter(Optional::isPresent) // Hanya ambil yang ditemukan di database
                    .map(Optional::get)
                    .collect(Collectors.toList());
            appointment.setTreatments(treatmentList);
        }

        appointmentDb.save(appointment); // Simpan perubahan

        // Kembalikan hasil dalam bentuk AppointmentResponseDTO
        return appointmentToAppointmentResponseDTO(appointment);
    }

    @Override
    public boolean deleteAppointmentById(String id) {
        // Cari appointment berdasarkan ID
        Optional<Appointment> appointmentOptional = appointmentDb.findById(id);
        if (appointmentOptional.isEmpty()) {
            return false;
        }
        Appointment appointment = appointmentOptional.get();
        appointment.setIsDeleted(true);
        appointmentDb.save(appointment); 
        return true;
    }
}

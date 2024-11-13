package apap.tk.appointment.service;

import apap.tk.appointment.dto.request.AddAppointmentRequestDTO;
import apap.tk.appointment.model.Appointment;
import apap.tk.appointment.model.Doctor;
import apap.tk.appointment.model.Patient;
import apap.tk.appointment.repository.AppointmentDb;
import apap.tk.appointment.repository.DoctorDb;
import apap.tk.appointment.repository.PatientDb;
import apap.tk.appointment.restdto.response.AppointmentResponseDTO;
import apap.tk.appointment.restdto.response.BaseResponseDTO;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.ZoneId;
import java.time.LocalDate;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentDb appointmentDb;
    private final DoctorDb doctorDb;
    private final PatientDb patientDb;
    private final WebClient webClient;

    public AppointmentServiceImpl(AppointmentDb appointmentDb, DoctorDb doctorDb, PatientDb patientDb, WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
        this.appointmentDb = appointmentDb;
        this.doctorDb = doctorDb;
        this.patientDb = patientDb;
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

    @Override
    public List<Appointment> getAllAppointmentsInDateRange(Date startDate, Date endDate) {
        return appointmentDb.findByDateBetween(startDate, endDate);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentDb.findAllByIsDeletedFalse();
    }

    // Membuat appointment untuk pasien yang sudah ada
    @Override
    public Appointment createAppointmentForExistingPatient(String patientNIK, AddAppointmentRequestDTO requestDTO) {
        Patient patient = patientDb.findBynik(patientNIK);
        Doctor doctor = doctorDb.findById(requestDTO.getDoctorIds().get(0)).orElse(null);

        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found");
        }

        // Membuat janji temu baru
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setTreatments(requestDTO.getTreatments());
        appointment.setDiagnosis(requestDTO.getDiagnosis());
        appointment.setStatus(0); // Set status janji temu
        appointment.setDate(requestDTO.getDate());

        // Generate ID secara manual dengan metode yang sudah ada
        String appointmentId = generateAppointmentId(doctor, requestDTO.getDate());
        appointment.setId(appointmentId);

        // Hitung total biaya berdasarkan dokter dan treatment
        appointment.setTotalFee();

        // Simpan janji temu ke database
        return appointmentDb.save(appointment);
    }


    @Override
    public Appointment createAppointmentWithNewPatient(AddAppointmentRequestDTO requestDTO, String patientNIK, String patientName, String email, boolean gender, Date birthDate, String birthPlace) {
        // Membuat pasien baru
        Patient patient = new Patient();
        patient.setNik(patientNIK);
        patient.setName(patientName);
        patient.setEmail(email);
        patient.setGender(gender);
        patient.setBirthDate(birthDate);
        patient.setBirthPlace(birthPlace);

        // Simpan pasien ke database
        patient = patientDb.save(patient);

        // Menggunakan findAllById untuk mendapatkan list dokter berdasarkan list ID
        List<Doctor> selectedDoctors = doctorDb.findAllById(requestDTO.getDoctorIds());

        // Membuat appointment baru
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        Doctor doctor = selectedDoctors.get(0);  // Pilih satu dokter
        appointment.setDoctor(doctor);

        // Buat ID unik
        String appointmentId = generateAppointmentId(doctor, requestDTO.getDate());
        appointment.setId(appointmentId);

        appointment.setDiagnosis(requestDTO.getDiagnosis());
        appointment.setTotalFee(doctor.getFee());
        appointment.setStatus(requestDTO.getStatus());
        appointment.setDate(requestDTO.getDate());
        appointment.setStatus(0);
        appointment.setTreatments(requestDTO.getTreatments());

        // Simpan appointment ke database
        return appointmentDb.save(appointment);
    }

    // Method untuk generate appointment ID
    private String generateAppointmentId(Doctor doctor, Date appointmentDate) {
        // Karakter [1,3]: Spesialisasi dokter
        String doctorSpecialty = SPECIALIZATION_CODES.get(doctor.getSpecialist());

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

    @Override
    public Appointment getAppointmentById(String id) {
        for (Appointment appointment : getAllAppointments()) {
            if (appointment.getId().equals(id)) {
                return appointment;
            }
        }
        return null;
    }

    @Override
    public Appointment updateAppointmentStatus(String id, int status) {
        Appointment appointment = appointmentDb.findById(id).orElse(null);
        if (appointment != null) {
            appointment.setStatus(status);
            return appointmentDb.save(appointment);
        }
        return null;
    }

    @Override
    public Appointment updateAppointment(Appointment appointment) {
        Appointment getAppointment = getAppointmentById(appointment.getId());
        if (getAppointment != null) {
            getAppointment.setId(appointment.getId());
            getAppointment.setPatient(appointment.getPatient());
            getAppointment.setDoctor(appointment.getDoctor());
            getAppointment.setTotalFee(appointment.getTotalFee());
            getAppointment.setStatus(appointment.getStatus());
            getAppointment.setDate(appointment.getDate());
            getAppointment.setDiagnosis(appointment.getDiagnosis());
            getAppointment.setTreatments(appointment.getTreatments());

            appointmentDb.save(getAppointment);

            return getAppointment;
        }
        return null;
    }

    // Method to get the next 4 available practice dates for a doctor based on multiple schedule days
    @Override
    public List<Date> getNextFourPracticeDays(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor is null, cannot fetch available practice days.");
        }

        List<Date> availableDates = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Ambil jadwal praktik dokter (List<Integer> schedules)
        List<Integer> doctorSchedules = doctor.getSchedules();

        // Loop untuk mencari 4 slot waktu yang tersedia
        int count = 0;
        while (count < 4) {
            for (int schedule : doctorSchedules) {
                int mappedDay = (schedule == 6) ? 7 : (schedule + 1); 
                DayOfWeek doctorDay = DayOfWeek.of(mappedDay); // Gunakan DayOfWeek

                LocalDateTime potentialAppointmentDate = now.with(TemporalAdjusters.nextOrSame(doctorDay));
                Date potentialDate = Date.from(potentialAppointmentDate.atZone(ZoneId.systemDefault()).toInstant());

                List<Appointment> existingAppointments = appointmentDb.findByDateAndDoctor(potentialDate, doctor);
                if (existingAppointments.isEmpty()) {
                    availableDates.add(potentialDate);
                    count++;
                }

                if (count >= 4) break;
            }
            now = now.plusWeeks(1);
        }

        return availableDates;
    }

    @Override
    public int countTodayAppointments() {
        // Mendapatkan tanggal hari ini (mulai dari 00:00 hingga 23:59)
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        // Konversi ke tipe Date untuk perbandingan di database
        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

        // Ambil semua janji temu di database dan filter berdasarkan tanggal
        List<Appointment> todayAppointments = appointmentDb.findByDateBetween(startDate, endDate);
        return todayAppointments.size();
    }

    @Override
    public void deleteAppoinment(Appointment appointment) {
        appointment.setIsDeleted(true);
        appointmentDb.save(appointment);  // Menyimpan perubahan soft delete
    }
    
    @Override
    public List<Integer> getAppointmentStatistics(String period, int year) {
        if (period.equalsIgnoreCase("Monthly")) {
            return getMonthlyStatistics(year);
        } else if (period.equalsIgnoreCase("Quarter")) {
            return getQuarterlyStatistics(year);
        } else {
            throw new IllegalArgumentException("Invalid period specified");
        }
    }

    private List<Integer> getMonthlyStatistics(int year) {
        List<Integer> monthlyCounts = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            int count = appointmentDb.countByMonthAndYear(month, year); // Repository method to fetch data
            monthlyCounts.add(count);
        }
        return monthlyCounts;
    }

    private List<Integer> getQuarterlyStatistics(int year) {
        List<Integer> quarterlyCounts = new ArrayList<>();
        for (int quarter = 1; quarter <= 4; quarter++) {
            int count = appointmentDb.countByQuarterAndYear(quarter, year); // Repository method to fetch data
            quarterlyCounts.add(count);
        }
        return quarterlyCounts;
    }

    @Override
    public List<Long> getMonthlyStats(int year) {
        List<Long> monthlyStats = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            monthlyStats.add(appointmentDb.countByMonth(year, i));
        }
        return monthlyStats;
    }

    @Override
    public List<Long> getQuarterlyStats(int year) {
        List<Long> quarterlyStats = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            quarterlyStats.add(appointmentDb.countByQuarter(year, i));
        }
        return quarterlyStats;
    }

    @Override
    public List<AppointmentResponseDTO> getAllAppointmentFromRest() throws Exception {
        var response = webClient
            .get() // RequestHeadersUriSpec<capture of ?>
            .uri("/appointment/all") // capture of ?
            .retrieve() // ResponseSpec
            .bodyToMono(new ParameterizedTypeReference<BaseResponseDTO<List<AppointmentResponseDTO>>>() {})
            .block();

        if (response == null) {
            throw new Exception("Failed consume API getAllAppointment");
        }

        if (response.getStatus() != 200) {
            throw new Exception(response.getMessage());
        }

        return response.getData();
    }
}
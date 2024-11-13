package apap.tk.appointment.controller;

import apap.tk.appointment.dto.request.AddAppointmentRequestDTO;
import apap.tk.appointment.dto.request.UpdateAppointmentRequestDTO;
import apap.tk.appointment.model.Appointment;
import apap.tk.appointment.model.Doctor;
import apap.tk.appointment.model.Patient;
import apap.tk.appointment.model.Treatment;
import apap.tk.appointment.service.AppointmentService;
import apap.tk.appointment.service.DoctorService;
import apap.tk.appointment.service.PatientService;
import apap.tk.appointment.service.TreatmentService;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final TreatmentService treatmentService;

    public AppointmentController(AppointmentService appointmentService, DoctorService doctorService, PatientService patientService, TreatmentService treatmentService) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.treatmentService = treatmentService;
    }

    @GetMapping("/all")
    public String viewAllAppointments(Model model) {
        List<Appointment> listAppointments = appointmentService.getAllAppointments();
        model.addAttribute("listAppointments", listAppointments);
        model.addAttribute("activeAppoinments", "fw-bold");
        return "viewall-appointment";  // Halaman list appointment
    }

    @GetMapping("/{id}")
    public String viewAppointmentDetail(@PathVariable("id") String id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        model.addAttribute("appointment", appointment);
        model.addAttribute("activeAppoinments", "fw-bold");
        return "view-appointment";  // Halaman detail appointment
    }

    // Flow 1 - form untuk membuat appointment untuk pasien yang sudah ada
    @GetMapping("/{nik}/create")
    public String showCreateAppointmentFormForExistingPatient(@PathVariable("nik") String nik, 
                                                            @RequestParam(value = "doctorId", required = false) String doctorId, 
                                                            Model model) {
        // Ambil data pasien yang sudah ada berdasarkan NIK
        Patient patient = patientService.getPatientByNIK(nik);
        if (patient == null) {
            model.addAttribute("responseMessage", "Patient not found");
            return "error-page";  // Kembali ke halaman error jika pasien tidak ditemukan
        }

        // Ambil daftar dokter
        List<Doctor> listDoctors = doctorService.getAllDoctors();

        // Jika doctorId tidak diberikan, ambil salah satu dokter dari daftar
        Doctor selectedDoctor = (doctorId != null && !doctorId.isEmpty()) 
            ? doctorService.getDoctorById(doctorId) 
            : listDoctors.isEmpty() ? null : listDoctors.get(0);

        // Ambil jadwal dari dokter terpilih
        List<Date> availableDates = selectedDoctor != null 
            ? appointmentService.getNextFourPracticeDays(selectedDoctor)
            : new ArrayList<>();  // Tampilkan daftar kosong jika dokter tidak dipilih

        model.addAttribute("nik", nik);
        model.addAttribute("patient", patient);
        model.addAttribute("listDoctors", listDoctors);
        model.addAttribute("availableDates", availableDates);
        model.addAttribute("appointmentRequest", new AddAppointmentRequestDTO());
        model.addAttribute("activeAppointments", "fw-bold");

        return "form-create-appointment";  // Menampilkan halaman form create appointment
    }


    // Flow 1 - submit untuk pasien yang sudah ada
    @PostMapping("/{nik}/create")
    public String createAppointmentForExistingPatient(@PathVariable("nik") String nik, 
                                                    @ModelAttribute AddAppointmentRequestDTO requestDTO, 
                                                    Model model) {
        Patient patient = patientService.getPatientByNIK(nik);
        
        // Memanggil service untuk membuat janji temu
        Appointment appointment = appointmentService.createAppointmentForExistingPatient(nik, requestDTO);
        
        // Tambahkan data pasien ke janji temu
        appointment.setPatient(patient);
        
        model.addAttribute("appointment", appointment);
        model.addAttribute("responseMessage", appointment.getId() + " berhasil terdaftar.");
        model.addAttribute("activeAppoinments", "fw-bold");
        
        return "appointment-success";  // Halaman sukses setelah appointment dibuat
    }


    // Flow 2 - form untuk membuat appointment sekaligus membuat pasien baru
    @GetMapping("/create-with-patient")
    public String showCreateAppointmentWithPatientForm(@RequestParam(value = "doctorId", required = false) String doctorId, 
                                                       Model model) {
        List<Doctor> listDoctors = doctorService.getAllDoctors();
        if (listDoctors.isEmpty()) {
            model.addAttribute("responseMessage", "No doctors available. Please add a doctor first.");
            return "error-page";  // Return an error page or message
        }
        Doctor selectedDoctor = doctorId != null ? doctorService.getDoctorById(doctorId) : listDoctors.get(0);
        List<Date> availableDates = appointmentService.getNextFourPracticeDays(selectedDoctor);
        model.addAttribute("listDoctors", listDoctors);
        model.addAttribute("availableDates", availableDates);
        model.addAttribute("activeAppoinments", "fw-bold");
        return "form-create-appointment-with-patient";  // Halaman form untuk membuat appointment dan pasien baru
    }

    // Flow 2 - submit untuk membuat appointment dan pasien baru
    @PostMapping("/create-with-patient")
    public String createAppointmentWithNewPatient(
            @RequestParam("nik") String nik,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("gender") boolean gender,
            @RequestParam("birthDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthDate,
            @RequestParam("birthPlace") String birthPlace,
            @ModelAttribute AddAppointmentRequestDTO requestDTO,
            Model model) {

        Patient existingPatient = patientService.getPatientByNIK(nik);
        if (existingPatient != null) {
            model.addAttribute("responseMessage", "Patient with NIK " + nik + " already exists.");
            model.addAttribute("activeAppoinments", "fw-bold");
            return "error-page";  // Return an error page or message
        }
        
        Appointment appointment = appointmentService.createAppointmentWithNewPatient(requestDTO, nik, name, email, gender, birthDate, birthPlace);
        model.addAttribute("appointment", appointment);
        model.addAttribute("responseMessage", appointment.getPatient().getName() + " berhasil terdaftar sebagai pasien baru.");
        model.addAttribute("activeAppoinments", "fw-bold");
        return "appointment-success";  // Halaman sukses setelah appointment dan pasien baru dibuat
    }

    // Mark appointment as Done
    @PostMapping("/{id}/done")
    public String markAppointmentAsDone(@PathVariable("id") String id, Model model) {
        Appointment appointment = appointmentService.updateAppointmentStatus(id, 1); // 1 = Done
        model.addAttribute("appointment", appointment);
        model.addAttribute("activeAppoinments", "fw-bold");
        return "redirect:/appointment/" + id;  // Redirect ke halaman detail appointment
    }

    // Cancel appointment
    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable("id") String id, Model model) {
        Appointment appointment = appointmentService.updateAppointmentStatus(id, 2); // 2 = Cancelled
        model.addAttribute("appointment", appointment);
        model.addAttribute("activeAppoinments", "fw-bold");
        return "redirect:/appointment/" + id;  // Redirect ke halaman detail appointment
    }

    @GetMapping("/{id}/update")
    public String updateAppointmentFormPage(@PathVariable("id") String id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);

        // Cek apakah janji temu bisa di-update (tidak dalam 1 hari)
        LocalDateTime appointmentDateTime = appointment.getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime today = LocalDateTime.now();

        if (!appointmentDateTime.isAfter(today.plusDays(1))) {
            model.addAttribute("errorMessage", "Appointment cannot be updated within one day of the scheduled date.");
            model.addAttribute("activeAppoinments", "fw-bold");
            return "appointment-error"; // Return an error page or message
        }

        // Inisialisasi UpdateAppointmentRequestDTO
        UpdateAppointmentRequestDTO appointmentDTO = new UpdateAppointmentRequestDTO();
        appointmentDTO.setId(appointment.getId());
        appointmentDTO.setDate(appointment.getDate());
        appointmentDTO.setDoctorIds(List.of(appointment.getDoctor().getId()));  // Ambil doctorId dari appointment
        appointmentDTO.setStatus(appointment.getStatus());
        appointmentDTO.setTotalFee(appointment.getTotalFee());

        // Ambil daftar dokter
        List<Doctor> listDoctors = doctorService.getAllDoctors();
        
        // Ambil dokter dari appointment yang sedang di-update
        Doctor selectedDoctor = appointment.getDoctor();
        
        // Ambil 4 jadwal ke depan yang belum diambil oleh pasien lain
        List<Date> availableDates = appointmentService.getNextFourPracticeDays(selectedDoctor);

        model.addAttribute("listDoctors", listDoctors);
        model.addAttribute("availableDates", availableDates);
        model.addAttribute("appointmentDTO", appointmentDTO);  // Masukkan appointmentDTO ke model
        
        // Format datetime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String formattedDateTime = appointmentDateTime.format(formatter);
        model.addAttribute("formattedDateTime", formattedDateTime);

        model.addAttribute("activeAppoinments", "fw-bold");
        return "form-update-appointment";
    }

    // Update Appointment - POST submit
    @PostMapping("/update")
    public String updateAppointment(@Valid @ModelAttribute UpdateAppointmentRequestDTO appointmentDTO,
                                    @RequestParam("date") String dateTimeString,
                                    Model model) {
        // Parse dateTimeString ke format Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime newAppointmentDateTime = LocalDateTime.parse(dateTimeString, formatter);
        Date newAppointmentDate = Date.from(newAppointmentDateTime.atZone(ZoneId.systemDefault()).toInstant());

        // Cari janji temu berdasarkan ID
        Appointment existingAppointment = appointmentService.getAppointmentById(appointmentDTO.getId());
        if (existingAppointment == null) {
            model.addAttribute("errorMessage", "Appointment not found.");
            return "appointment-error";  // Return error jika janji temu tidak ditemukan
        }

        // Cek apakah janji temu bisa di-update (tidak dalam 1 hari)
        LocalDateTime existingAppointmentDateTime = existingAppointment.getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime today = LocalDateTime.now();

        if (!existingAppointmentDateTime.isAfter(today.plusDays(1))) {
            model.addAttribute("errorMessage", "Appointment cannot be updated within one day of the scheduled date.");
            model.addAttribute("activeAppoinments", "fw-bold");
            return "appointment-error";  // Return error jika janji temu dalam 1 hari
        }

        // Update hanya waktu dan status janji temu
        existingAppointment.setDate(newAppointmentDate);
        existingAppointment.setStatus(appointmentDTO.getStatus());

        // Simpan janji temu yang diperbarui
        Appointment updatedAppointment = appointmentService.updateAppointment(existingAppointment);

        // Tambahkan data appointment yang diperbarui ke model
        model.addAttribute("appointment", updatedAppointment);
        model.addAttribute("responseMessage", "Appointment updated successfully.");
        model.addAttribute("activeAppoinments", "fw-bold");

        // Redirect ke halaman detail janji temu
        return "appointment-success";
    }

    @GetMapping("/{id}/note")
    public String showUpdateDiagnosisAndTreatmentForm(@PathVariable("id") String id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            model.addAttribute("errorMessage", "Appointment not found.");
            return "appointment-error";
        }

        // Ambil semua treatments dari database
        List<Treatment> treatmentList = treatmentService.getAllTreatments();

        // Kirimkan data treatment ke model
        model.addAttribute("treatmentList", treatmentList);
        model.addAttribute("appointment", appointment);
        return "form-update-diagnosis-treatment";  // Halaman form untuk update
    }

    @PostMapping("/note")
    public String updateDiagnosisAndTreatment(@ModelAttribute Appointment updatedAppointment, 
                                            @RequestParam List<Long> treatmentIds, 
                                            Model model) {
        Appointment appointment = appointmentService.getAppointmentById(updatedAppointment.getId());
        if (appointment == null) {
            model.addAttribute("errorMessage", "Appointment not found.");
            return "appointment-error";
        }

        // Update diagnosis
        appointment.setDiagnosis(updatedAppointment.getDiagnosis());

        // Update treatments
        List<Treatment> selectedTreatments = treatmentService.getTreatmentsByIds(treatmentIds);
        appointment.setTreatments(selectedTreatments);

        // Recalculate total fee based on updated treatments
        appointment.setTotalFee();

        // Simpan perubahan ke database
        appointmentService.updateAppointment(appointment);

        model.addAttribute("responseMessage", "Berhasil mencatat diagnosis & treatment untuk appointment " + appointment.getId() + ".");
        return "appointment-success";  // Halaman feedback setelah berhasil diupdate
    }

    @GetMapping("/stat")
    public String showAppointmentStatisticsPage() {
        return "view-appointment-statistics";  // This assumes your HTML file is named view-appointment-statistics.html
    }

    @GetMapping("/{id}/delete")
    public String showDeleteAppointmentForm(@PathVariable("id") String id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            model.addAttribute("errorMessage", "Appointment not found.");
            return "appointment-error";
        }
        model.addAttribute("appointment", appointment);
        return "form-delete-appointment";  // Menampilkan form konfirmasi penghapusan
    }

    @PostMapping("/delete")
    public String deleteAppointment(@RequestParam("id") String id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            model.addAttribute("errorMessage", "Appointment not found.");
            return "appointment-error";
        }
        appointmentService.deleteAppoinment(appointment);  // Soft delete
        model.addAttribute("responseMessage", "Berhasil menghapus Appointment " + appointment.getId() + ".");
        return "appointment-success";  // Feedback setelah sukses menghapus
    }

    @GetMapping("/rest/all")
    public String ListRestAppointment(Model model) {
        try {
            var listAppointment = appointmentService.getAllAppointmentFromRest();

            model.addAttribute("listAppointment", listAppointment);
            return "viewall-appointment";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "response-error-rest";
        }
    }

    

    
}

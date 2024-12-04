package apap.tk.appointment.restcontroller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import apap.tk.appointment.model.Appointment;
import apap.tk.appointment.restdto.request.AddAppointmentRequestRestDTO;
import apap.tk.appointment.restdto.response.AppointmentResponseRestDTO;
import apap.tk.appointment.restdto.response.BaseResponseDTO;
import apap.tk.appointment.restservice.AppointmentRestService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentRestController {
    private final AppointmentRestService appointmentRestService;

    // Constructor-based dependency injection
    public AppointmentRestController(AppointmentRestService appointmentRestService) {
        this.appointmentRestService = appointmentRestService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> listAppointment() {
        var baseResponseDTO = new BaseResponseDTO<List<AppointmentResponseRestDTO>>();
        List<AppointmentResponseRestDTO> listAppointment = appointmentRestService.getAllAppointment();

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(listAppointment);
        baseResponseDTO.setMessage(String.format("List Appointment berhasil ditemukan"));
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<AppointmentResponseRestDTO>();
        AppointmentResponseRestDTO appointment = appointmentRestService.getAppointmentById(id);

        if (appointment == null) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(String.format("Tidak ada appointment yang ditemukan dengan id %s", id));
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(appointment);
        baseResponseDTO.setMessage(String.format("Appointment dengan id %s berhasil ditemukan", id));
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAppointmentsByDoctorId(@PathVariable UUID doctorId) {
        var baseResponseDTO = new BaseResponseDTO<List<AppointmentResponseRestDTO>>();
        List<AppointmentResponseRestDTO> appointments = appointmentRestService.getAllAppointmentByDoctor(doctorId);

        if (appointments == null || appointments.isEmpty()) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(String.format("Appointments untuk dokter dengan id %s tidak ditemukan", doctorId));
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(appointments);
        baseResponseDTO.setMessage(String.format("Appointments untuk dokter dengan id %s berhasil ditemukan", doctorId));
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    public ResponseEntity<?> getAppointmentsByDateRange(
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {

        var baseResponseDTO = new BaseResponseDTO<List<AppointmentResponseRestDTO>>();
        List<AppointmentResponseRestDTO> appointments = appointmentRestService.getAllAppointmentsByDate(from, to);

        if (appointments.isEmpty()) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Tidak ada appointment yang ditemukan dalam rentang tanggal tersebut");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(appointments);
        baseResponseDTO.setMessage("Daftar appointment berhasil ditemukan dalam rentang tanggal");
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getAppointmentsByPatientId(@PathVariable UUID patientId) {
        var baseResponseDTO = new BaseResponseDTO<List<AppointmentResponseRestDTO>>();
        List<AppointmentResponseRestDTO> appointments = appointmentRestService.getAllAppointmentsByPatient(patientId);
        if (appointments == null || appointments.isEmpty()) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage(String.format("Appointments untuk pasien dengan id %s tidak ditemukan", patientId));
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(appointments);
        baseResponseDTO.setMessage(String.format("Daftar appointment dengan id %s berhasil ditemukan", patientId));
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAppointment(@RequestBody AddAppointmentRequestRestDTO appointment) throws Exception {
        var baseResponseDTO = new BaseResponseDTO<AppointmentResponseRestDTO>();

        // Simpan appointment menggunakan service
        AppointmentResponseRestDTO savedAppointment = appointmentRestService.createAppointment(appointment);

        baseResponseDTO.setStatus(HttpStatus.CREATED.value());
        baseResponseDTO.setData(savedAppointment);
        baseResponseDTO.setMessage("Appointment berhasil disimpan");
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateAppointmentStatus(@PathVariable String id, @RequestBody Map<String, Integer> requestBody) {

        int newStatus = requestBody.get("status");  // Ambil status baru dari request body
        var baseResponseDTO = new BaseResponseDTO<AppointmentResponseRestDTO>();

        // Perbarui status appointment menggunakan service
        AppointmentResponseRestDTO updatedAppointment = appointmentRestService.updateAppointmentStatus(id, newStatus);

        if (updatedAppointment == null) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Appointment dengan ID " + id + " tidak ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(updatedAppointment);
        baseResponseDTO.setMessage("Status dari appointment berhasil diperbarui");
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}/update-diagnosis-treatment")
    public ResponseEntity<?> updateAppointmentDiagnosisAndTreatment(
            @PathVariable String id,
            @RequestBody Map<String, Object> requestBody) {

        String diagnosis = (String) requestBody.get("diagnosis");
        List<String> treatments = (List<String>) requestBody.get("treatments");

        var baseResponseDTO = new BaseResponseDTO<AppointmentResponseRestDTO>();

        // Perbarui diagnosis dan treatments menggunakan service
        AppointmentResponseRestDTO updatedAppointment = appointmentRestService.updateAppointmentDiagnosisAndTreatment(id, diagnosis, treatments);

        if (updatedAppointment == null) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Appointment dengan ID " + id + " tidak ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(updatedAppointment);
        baseResponseDTO.setMessage("Diagnosis dan treatment dari appointment berhasil diperbarui");
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteAppointment(@PathVariable String id) {
        var baseResponseDTO = new BaseResponseDTO<>();
        boolean isDeleted = appointmentRestService.deleteAppointmentById(id);
        if (!isDeleted) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Appointment dengan ID " + id + " tidak ditemukan");
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        }
        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setMessage("Appointment dengan ID " + id + " berhasil dihapus (soft delete)");
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }
}
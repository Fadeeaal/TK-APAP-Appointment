package apap.tk.appointment.restservice;
import apap.tk.appointment.model.Appointment;
import apap.tk.appointment.restdto.response.AppointmentResponseDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface AppointmentRestService {
    List<AppointmentResponseDTO> getAllAppointment();
    AppointmentResponseDTO getAppointmentById(String id);
    List<AppointmentResponseDTO> getAllAppointmentByDoctor(String doctorId);
    List<AppointmentResponseDTO> getAllAppointmentsByPatient(UUID patientId);
    List<AppointmentResponseDTO> getAllAppointmentsByDate(Date from, Date to);
    AppointmentResponseDTO createAppointment(Appointment appointment);
    AppointmentResponseDTO updateAppointmentStatus(String id, int newStatus);
    AppointmentResponseDTO updateAppointmentDiagnosisAndTreatment(String id, String diagnosis, List<String> treatments);
    boolean deleteAppointmentById(String id);
}
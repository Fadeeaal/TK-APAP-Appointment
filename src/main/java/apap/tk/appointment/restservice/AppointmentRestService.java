package apap.tk.appointment.restservice;
import apap.tk.appointment.restdto.request.AddAppointmentRequestRestDTO;
import apap.tk.appointment.restdto.response.AppointmentResponseRestDTO;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface AppointmentRestService {
    List<AppointmentResponseRestDTO> getAllAppointment();
    AppointmentResponseRestDTO getAppointmentById(String id);
    List<AppointmentResponseRestDTO> getAllAppointmentByDoctor(UUID doctorId);
    List<AppointmentResponseRestDTO> getAllAppointmentsByPatient(UUID patientId);
    List<AppointmentResponseRestDTO> getAllAppointmentsByDate(Date from, Date to);
    AppointmentResponseRestDTO createAppointment(AddAppointmentRequestRestDTO appointment) throws Exception;
    AppointmentResponseRestDTO updateAppointmentStatus(String id, int newStatus);
    AppointmentResponseRestDTO updateAppointmentDiagnosisAndTreatment(String id, String diagnosis, List<String> treatments);
    List<AppointmentResponseRestDTO> getAllAppointmentsByDoctorUsername(String username);
    boolean deleteAppointmentById(String id);
}
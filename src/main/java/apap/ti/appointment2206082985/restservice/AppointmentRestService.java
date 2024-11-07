package apap.ti.appointment2206082985.restservice;
import apap.ti.appointment2206082985.restdto.response.AppointmentResponseDTO;

import java.util.List;

public interface AppointmentRestService {
    List<AppointmentResponseDTO> getAllAppointment();
}

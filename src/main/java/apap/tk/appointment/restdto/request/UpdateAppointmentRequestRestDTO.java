package apap.tk.appointment.restdto.request;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateAppointmentRequestRestDTO {
    private UUID doctor;
    private int status;
    private String updatedBy;
    private String diagnosis;
    private List<String> treatments;
}

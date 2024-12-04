package apap.tk.appointment.restdto.request;

import java.util.Date;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddAppointmentRequestRestDTO {
    private UUID patient;
    private UUID doctor;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date appointmentDate;
    private String createdBy;
}

package apap.tk.appointment.dto.request;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import apap.tk.appointment.model.Treatment;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddAppointmentRequestDTO {
    private List<String> doctorIds;  // List of doctor IDs yang dipilih
    private String diagnosis;
    private long totalFee;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date date;
    
    private int status; // Status: 0 - created, 1 - done, 2 - cancelled

    private List<Treatment> treatments;
}

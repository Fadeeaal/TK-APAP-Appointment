package apap.ti.appointment2206082985.restdto.response;

import lombok.*;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {
    private String id;                 // Appointment ID
    private String doctorName;         // Nama Dokter
    private String patientName;        // Nama Pasien
    private String diagnosis;          // Diagnosis
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> treatments;   // Nama-nama Treatment

    private long totalFee;             // Total Biaya
    private int status;                // Status Appointment: 0 (Created), 1 (Done), 2 (Cancelled)
    private Date date;                 // Tanggal Appointment
    private boolean isDeleted;         // Soft delete status
}

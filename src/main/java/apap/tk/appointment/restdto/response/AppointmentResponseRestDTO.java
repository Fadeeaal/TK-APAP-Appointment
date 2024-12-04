package apap.tk.appointment.restdto.response;

import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppointmentResponseRestDTO {
    private String id;                 // Appointment ID
    private UUID doctor;         // Nama Dokter
    private UUID patient;        // Nama Pasien
    private String diagnosis;          // Diagnosis
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> treatments;   // Nama-nama Treatment
    private long totalFee;             // Total Biaya
    private int status;                // Status Appointment: 0 (Created), 1 (Done), 2 (Cancelled)
    private Date date;                 // Tanggal Appointment
    private boolean isDeleted;         // Soft delete status

    private Date createdAt;          // Tanggal pembuatan Appointment
    private Date updatedAt;         // Tanggal modifikasi Appointment
    private Date createdBy;         // Nama pembuat Appointment
    private Date updatedBy;         // Nama pembuat Appointment
}

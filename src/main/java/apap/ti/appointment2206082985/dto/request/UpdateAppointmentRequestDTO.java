package apap.ti.appointment2206082985.dto.request;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateAppointmentRequestDTO extends AddAppointmentRequestDTO{
    @NotNull(message = "ID Dokter tidak boleh kosong")
    private String id;
}

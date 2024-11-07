package apap.ti.appointment2206082985.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateDoctorRequestDTO extends AddDoctorRequestDTO {
    @NotNull(message = "ID Dokter tidak boleh kosong")
    private String id;

    @NotEmpty(message = "Schedules tidak boleh kosong")
    private List<Integer> schedules;
}
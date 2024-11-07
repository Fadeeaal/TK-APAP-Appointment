package apap.ti.appointment2206082985.dto.request;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddPatientRequestDTO {
    private UUID id;
    private String name;
    private String nik;
    private boolean gender;
    private String email;
    private Date birthDate;
    private String birthPlace;
}

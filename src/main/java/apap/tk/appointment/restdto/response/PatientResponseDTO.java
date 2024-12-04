package apap.tk.appointment.restdto.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PatientResponseDTO {
    private String id;
    private String name;
    private String username;
    private String password;
    private String email;
    private Boolean gender;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private String role;
    private String nik;
    private String birthPlace;
    private Date birthDate;
    private Integer pClass;
}

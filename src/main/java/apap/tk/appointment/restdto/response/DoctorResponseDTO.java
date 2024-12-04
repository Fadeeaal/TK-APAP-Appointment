package apap.tk.appointment.restdto.response;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DoctorResponseDTO {
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
    private Integer specialist;
    private Integer yearsOfExperience;
    private Long fee;
    private List<Integer> schedules;
}

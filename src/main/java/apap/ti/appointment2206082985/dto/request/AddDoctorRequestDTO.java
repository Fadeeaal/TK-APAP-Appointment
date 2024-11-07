package apap.ti.appointment2206082985.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddDoctorRequestDTO {
    private String id;
    private String name;
    private String email;
    private boolean gender;
    private int specialist;
    private int yearsOfExperience;
    private long fee;
    private List<Integer> schedules;
}

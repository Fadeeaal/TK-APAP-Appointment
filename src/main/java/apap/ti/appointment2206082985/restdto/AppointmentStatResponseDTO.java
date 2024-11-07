package apap.ti.appointment2206082985.restdto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AppointmentStatResponseDTO {
    private List<Long> stats;
    private String period;
    private int year;

    public AppointmentStatResponseDTO(List<Long> stats, String period, int year) {
        this.stats = stats;
        this.period = period;
        this.year = year;
    }
}

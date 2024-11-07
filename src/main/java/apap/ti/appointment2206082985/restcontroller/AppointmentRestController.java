package apap.ti.appointment2206082985.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import apap.ti.appointment2206082985.restdto.AppointmentStatResponseDTO;
import apap.ti.appointment2206082985.restdto.response.AppointmentResponseDTO;
import apap.ti.appointment2206082985.restdto.response.BaseResponseDTO;
import apap.ti.appointment2206082985.restservice.AppointmentRestService;
import apap.ti.appointment2206082985.service.AppointmentServiceImpl;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentRestController {

    private final AppointmentServiceImpl appointmentService;
    private final AppointmentRestService appointmentRestService;

    // Constructor-based dependency injection
    public AppointmentRestController(AppointmentServiceImpl appointmentService, AppointmentRestService appointmentRestService) {
        this.appointmentService = appointmentService;
        this.appointmentRestService = appointmentRestService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> listAppointment() {
        var baseResponseDTO = new BaseResponseDTO<List<AppointmentResponseDTO>>();
        List<AppointmentResponseDTO> listAppointment = appointmentRestService.getAllAppointment();

        baseResponseDTO.setStatus(HttpStatus.OK.value());
        baseResponseDTO.setData(listAppointment);
        baseResponseDTO.setMessage(String.format("List Appointment berhasil ditemukan"));
        baseResponseDTO.setTimestamp(new Date());
        return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/stat")
    public ResponseEntity<AppointmentStatResponseDTO> getAppointmentStats(@RequestParam String period, @RequestParam int year) {
        List<Long> stats;
        if (period.equalsIgnoreCase("monthly")) {
            stats = appointmentService.getMonthlyStats(year);
        } else if (period.equalsIgnoreCase("quarter")) {
            stats = appointmentService.getQuarterlyStats(year);
        } else {
            return ResponseEntity.badRequest().build(); // Handle invalid period
        }
        
        AppointmentStatResponseDTO responseDTO = new AppointmentStatResponseDTO(stats, period, year);
        return ResponseEntity.ok(responseDTO);
    }

}


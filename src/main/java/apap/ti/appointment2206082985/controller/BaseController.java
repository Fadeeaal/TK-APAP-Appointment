package apap.ti.appointment2206082985.controller;

import apap.ti.appointment2206082985.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BaseController {

    private final AppointmentService appointmentService;

    // Constructor-based dependency injection
    public BaseController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping(value = "/")
    public String home(Model model) {
        // Mendapatkan jumlah janji temu hari ini
        int todayAppointments = appointmentService.countTodayAppointments();
        model.addAttribute("todayAppointments", todayAppointments);

        return "home";
    }
}


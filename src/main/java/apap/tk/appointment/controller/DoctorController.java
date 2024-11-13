package apap.tk.appointment.controller;

import apap.tk.appointment.dto.request.AddDoctorRequestDTO;
import apap.tk.appointment.dto.request.UpdateDoctorRequestDTO;
import apap.tk.appointment.model.Doctor;
import apap.tk.appointment.service.DoctorService;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "schedules", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && !text.isEmpty()) {
                    // Mengubah string "1,2,3" menjadi List<Integer> {1, 2, 3}
                    List<Integer> schedules = Arrays.stream(text.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                    setValue(schedules);
                } else {
                    setValue(null);
                }
            }
        });
    }

    @GetMapping("/doctor/all")
    public String listDoctors(Model model) {
        var listDoctor = doctorService.getAllDoctors();
        // Map yang menghubungkan angka spesialisasi ke nama spesialisasi
        Map<Integer, String> specialistMap = Map.ofEntries(
            Map.entry(0, "Dokter Umum"),
            Map.entry(1, "Dokter Gigi"),
            Map.entry(2, "Spesialis Anak"),
            Map.entry(3, "Bedah"),
            Map.entry(4, "Bedah Plastik"),
            Map.entry(5, "Jantung dan Pembuluh Darah"),
            Map.entry(6, "Kulit dan Kelamin"),
            Map.entry(7, "Mata"),
            Map.entry(8, "Penyakit Dalam"),
            Map.entry(9, "Paru"),
            Map.entry(10, "THT"),
            Map.entry(11, "Kesehatan Jiwa"),
            Map.entry(12, "Anestesi"),
            Map.entry(13, "Neurologi"),
            Map.entry(14, "Urologi"),
            Map.entry(15, "Obstetri dan Ginekologi"),
            Map.entry(16, "Radiologi")
        );
        model.addAttribute("listDoctor", listDoctor);
        model.addAttribute("specialistMap", specialistMap);
        model.addAttribute("activeDoctors", "fw-bold");
        return "viewall-doctor";
    }

    @GetMapping("/doctor/add")
    public String addDoctorFormPage(Model model) {
        model.addAttribute("doctorDTO", new AddDoctorRequestDTO());
        model.addAttribute("activeDoctors", "fw-bold");
        return "form-add-doctor";
    }

    @PostMapping("/doctor/add")
    public String addDoctor(@ModelAttribute("doctorDTO") AddDoctorRequestDTO doctorDTO, Model model) {
        var doctor = new Doctor();
        doctor.setId(doctorDTO.getId());
        doctor.setName(doctorDTO.getName());
        doctor.setEmail(doctorDTO.getEmail());
        doctor.setGender(doctorDTO.isGender());
        doctor.setSpecialist(doctorDTO.getSpecialist());
        doctor.setYearsOfExperience(doctorDTO.getYearsOfExperience());
        doctor.setSchedules(doctorDTO.getSchedules());
        doctor.setFee(doctorDTO.getFee());

        doctorService.addDoctor(doctor);
        model.addAttribute("responseMessage", 
        String.format("Dokter %s dengan ID %s berhasil ditambahkan.", doctor.getName(), doctor.getId()));
        model.addAttribute("activeDoctors", "fw-bold");
        return "response-doctor";
    }

    @GetMapping("/doctor/{id}")
    public String detailDoctor(@PathVariable("id") String id, Model model) {
        var doctor = doctorService.getDoctorById(id);

        // Map untuk spesialisasi
        Map<Integer, String> specialistMap = Map.ofEntries(
            Map.entry(0, "Dokter Umum"),
            Map.entry(1, "Dokter Gigi"),
            Map.entry(2, "Spesialis Anak"),
            Map.entry(3, "Bedah"),
            Map.entry(4, "Bedah Plastik"),
            Map.entry(5, "Jantung dan Pembuluh Darah"),
            Map.entry(6, "Kulit dan Kelamin"),
            Map.entry(7, "Mata"),
            Map.entry(8, "Penyakit Dalam"),
            Map.entry(9, "Paru"),
            Map.entry(10, "THT"),
            Map.entry(11, "Kesehatan Jiwa"),
            Map.entry(12, "Anestesi"),
            Map.entry(13, "Neurologi"),
            Map.entry(14, "Urologi"),
            Map.entry(15, "Obstetri dan Ginekologi"),
            Map.entry(16, "Radiologi")
        );

        model.addAttribute("doctor", doctor);
        model.addAttribute("specialistMap", specialistMap);
        model.addAttribute("activeDoctors", "fw-bold");
        return "view-doctor";
    }

    @GetMapping("/doctor/{id}/update")
    public String updateDoctorFormPage(@PathVariable("id") String id, Model model) {
        var doctor = doctorService.getDoctorById(id);

        var doctorDTO = new AddDoctorRequestDTO();
        doctorDTO.setId(doctor.getId());
        doctorDTO.setName(doctor.getName());
        doctorDTO.setEmail(doctor.getEmail());
        doctorDTO.setGender(doctor.isGender());
        doctorDTO.setSpecialist(doctor.getSpecialist());
        doctorDTO.setYearsOfExperience(doctor.getYearsOfExperience());
        doctorDTO.setSchedules(doctor.getSchedules());
        doctorDTO.setFee(doctor.getFee());

        model.addAttribute("doctorDTO", doctorDTO);
        model.addAttribute("activeDoctors", "fw-bold");
        return "form-update-doctor";
    }

    @PostMapping("/doctor/update")
    public String updateDoctor(@Valid @ModelAttribute UpdateDoctorRequestDTO doctorDTO, Model model) {
        var doctor = new Doctor();
        doctor.setId(doctorDTO.getId());
        doctor.setName(doctorDTO.getName());
        doctor.setEmail(doctorDTO.getEmail());
        doctor.setGender(doctorDTO.isGender());
        doctor.setSpecialist(doctorDTO.getSpecialist());
        doctor.setYearsOfExperience(doctorDTO.getYearsOfExperience());
        doctor.setSchedules(doctorDTO.getSchedules());
        doctor.setFee(doctorDTO.getFee());

        doctorService.updateDoctor(doctor);
        model.addAttribute("responseMessage", 
        String.format("Dokter %s dengan ID %s berhasil diupdate.", doctor.getName(), doctor.getId()));
        model.addAttribute("activeDoctors", "fw-bold");
        return "response-update-doctor";
    }

    @GetMapping("/doctor/{id}/delete")
    public String deleteDoctor(@PathVariable("id") String id, Model model) {
        var doctor = doctorService.getDoctorById(id);
        if (doctor != null) {
            doctorService.deleteDoctor(doctor);
            model.addAttribute("responseMessage",
                    String.format("Doctor %s dengan ID %s berhasil dihapus.", doctor.getName(), doctor.getId()));
        } else {
            model.addAttribute("responseMessage", "Doctor tidak ditemukan atau sudah dihapus.");
        }
        model.addAttribute("activeDoctors", "fw-bold");
        return "response-doctor";
    }

    @PostMapping("/doctor/delete")
    public String deleteDoctor(@RequestBody Doctor doctor, Model model) {
        doctorService.deleteDoctor(doctor);
        model.addAttribute("responseMessage",
                String.format("Doctor %s dengan ID %s berhasil dihapus.", doctor.getName(), doctor.getId()));
        model.addAttribute("activeDoctors", "fw-bold");
        return "response-doctor";
    }
    
}
package apap.ti.appointment2206082985.controller;

import java.text.SimpleDateFormat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import apap.ti.appointment2206082985.service.PatientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // Menampilkan form pencarian pasien (GET)
    @GetMapping("/patient/search")
    public String searchPatientForm() {
        return "search-patient";  // Menampilkan halaman form pencarian
    }

    // Memproses pencarian pasien berdasarkan NIK (POST)
    @PostMapping("/patient/search/{nik}")
    public String searchPatientByNik(@PathVariable("nik") String nik, Model model) {
        // Cari pasien berdasarkan NIK
        var patient = patientService.getPatientByNIK(nik);
        model.addAttribute("nik", nik);

        if (patient != null) {
            // Jika pasien ditemukan, tambahkan detail pasien ke model
            model.addAttribute("patient", patient);

            // Format tanggal lahir sebelum menambahkannya ke model
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = dateFormat.format(patient.getBirthDate());  // Format birthDate (Date type)
            model.addAttribute("formattedDateOfBirth", formattedDate);

            return "view-patient-details";  // Tampilkan detail pasien
        } else {
            model.addAttribute("message", "Pasien dengan NIK " + nik + " tidak ditemukan.");
            return "patient-not-found";  // Tampilkan pesan jika pasien tidak ditemukan
        }
    }
}

package apap.tk.appointment;

import apap.tk.appointment.model.Treatment;
import apap.tk.appointment.repository.TreatmentDb;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
public class AppointmentApplication implements CommandLineRunner {

    @Autowired
    private TreatmentDb treatmentDb;

    public static void main(String[] args) {
        SpringApplication.run(AppointmentApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker(Locale.of("in", "ID"));
        LocalDateTime now = LocalDateTime.now();

        List<Treatment> treatments = Arrays.asList(
            createTreatment(1L, "X-ray", 150000L),
            createTreatment(2L, "CT Scan", 1000000L),
            createTreatment(3L, "MRI", 2500000L),
            createTreatment(4L, "Ultrasound", 300000L),
            createTreatment(5L, "Blood Clotting Test", 50000L),
            createTreatment(6L, "Blood Glucose Test", 30000L),
            createTreatment(7L, "Liver Function Test", 75000L),
            createTreatment(8L, "Complete Blood Count", 50000L),
            createTreatment(9L, "Urinalysis", 40000L),
            createTreatment(10L, "COVID-19 testing", 150000L),
            createTreatment(11L, "Cholesterol Test", 60000L),
            createTreatment(12L, "Inpatient care", 1000000L),
            createTreatment(13L, "Surgery", 7000000L),
            createTreatment(14L, "ICU", 2000000L),
            createTreatment(15L, "ER", 500000L),
            createTreatment(16L, "Flu shot", 100000L),
            createTreatment(17L, "Hepatitis vaccine", 200000L),
            createTreatment(18L, "COVID-19 Vaccine", 200000L),
            createTreatment(19L, "MMR Vaccine", 350000L),
            createTreatment(20L, "HPV Vaccine", 800000L),
            createTreatment(21L, "Pneumococcal Vaccine", 900000L),
            createTreatment(22L, "Herpes Zoster Vaccine", 1500000L),
            createTreatment(23L, "Physical exam", 250000L),
            createTreatment(24L, "Mammogram", 500000L),
            createTreatment(25L, "Colonoscopy", 3000000L),
            createTreatment(26L, "Dental X-ray", 200000L),
            createTreatment(27L, "Fillings", 400000L),
            createTreatment(28L, "Dental scaling", 500000L),
            createTreatment(29L, "Physical therapy", 250000L),
            createTreatment(30L, "Occupational therapy", 300000L),
            createTreatment(31L, "Speech therapy", 300000L),
            createTreatment(32L, "Psychiatric evaluation", 600000L),
            createTreatment(33L, "Natural delivery", 3500000L),
            createTreatment(34L, "C-section", 12000000L)
        );

        treatmentDb.saveAll(treatments);
    }

    private Treatment createTreatment(Long id, String name, Long price) {
        Treatment treatment = new Treatment();
        treatment.setId(id);
        treatment.setName(name);
        treatment.setPrice(price);
        treatment.setAppointments(new ArrayList<>());
        treatment.setCreatedAt(LocalDateTime.now());
        treatment.setUpdatedAt(LocalDateTime.now());
        return treatment;
    }
}
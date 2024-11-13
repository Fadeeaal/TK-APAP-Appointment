package apap.tk.appointment.repository;

import apap.tk.appointment.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientDb extends JpaRepository<Patient, UUID> {
    Patient findBynik(String nik);
}

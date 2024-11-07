package apap.ti.appointment2206082985.repository;

import apap.ti.appointment2206082985.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientDb extends JpaRepository<Patient, UUID> {
    Patient findBynik(String nik);
}

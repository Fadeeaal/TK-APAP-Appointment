package apap.tk.appointment.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import apap.tk.appointment.model.Treatment;

import java.util.Optional;

@Repository
public interface TreatmentDb extends JpaRepository<Treatment, Long> {
    Optional<Treatment> findByName(String name);
}

package apap.ti.appointment2206082985.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import apap.ti.appointment2206082985.model.Treatment;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentDb extends JpaRepository<Treatment, Long> {

}

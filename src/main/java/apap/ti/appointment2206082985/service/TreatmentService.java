package apap.ti.appointment2206082985.service;

import java.util.List;
import apap.ti.appointment2206082985.model.Treatment;
import java.util.Optional;

public interface TreatmentService {
    List<Treatment> getAllTreatments();
    List<Treatment> getTreatmentsByIds(List<Long> ids);
    Optional<Treatment> getTreatmentById(Long id);
}
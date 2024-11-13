package apap.tk.appointment.service;

import java.util.List;
import apap.tk.appointment.model.Treatment;
import java.util.Optional;

public interface TreatmentService {
    List<Treatment> getAllTreatments();
    List<Treatment> getTreatmentsByIds(List<Long> ids);
    Optional<Treatment> getTreatmentById(Long id);
}
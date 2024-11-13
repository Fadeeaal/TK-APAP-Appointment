package apap.tk.appointment.service;

import org.springframework.stereotype.Service;

import apap.tk.appointment.model.Treatment;
import apap.tk.appointment.repository.TreatmentDb;

import java.util.List;
import java.util.Optional;

@Service
public class TreatmentServiceImpl implements TreatmentService {
    private final TreatmentDb treatmentDb;

    public TreatmentServiceImpl(TreatmentDb treatmentDb) {
        this.treatmentDb = treatmentDb;
    }

    @Override
    public List<Treatment> getAllTreatments() {
        return treatmentDb.findAll();
    }

    @Override
    public List<Treatment> getTreatmentsByIds(List<Long> treatmentIds) {
        return treatmentDb.findAllById(treatmentIds);
    }

    @Override
    public Optional<Treatment> getTreatmentById(Long id) {
        return treatmentDb.findById(id);  // Mengembalikan null jika treatment tidak ditemukan
    }
}

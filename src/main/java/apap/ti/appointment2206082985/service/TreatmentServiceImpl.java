package apap.ti.appointment2206082985.service;

import org.springframework.stereotype.Service;

import apap.ti.appointment2206082985.model.Treatment;
import apap.ti.appointment2206082985.repository.TreatmentDb;

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

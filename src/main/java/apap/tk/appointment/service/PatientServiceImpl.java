package apap.tk.appointment.service;

import org.springframework.stereotype.Service;

import apap.tk.appointment.model.Patient;
import apap.tk.appointment.repository.PatientDb;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientDb patientDb;

    public PatientServiceImpl(PatientDb patientDb) {
        this.patientDb = patientDb;
    }

    @Override
    public Patient addPatient(Patient patient) {
        return patientDb.save(patient);
    }

    @Override
    public Patient getPatientByNIK(String nik) {
        return patientDb.findBynik(nik);
    }
}

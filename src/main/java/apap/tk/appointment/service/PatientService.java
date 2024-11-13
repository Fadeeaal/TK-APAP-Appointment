package apap.tk.appointment.service;

import apap.tk.appointment.model.Patient;

public interface PatientService {
    Patient addPatient(Patient patient);
    Patient getPatientByNIK(String nik);
}

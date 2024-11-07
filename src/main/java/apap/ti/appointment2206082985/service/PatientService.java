package apap.ti.appointment2206082985.service;

import apap.ti.appointment2206082985.model.Patient;

public interface PatientService {
    Patient addPatient(Patient patient);
    Patient getPatientByNIK(String nik);
}

package apap.ti.appointment2206082985.service;
import apap.ti.appointment2206082985.model.Doctor;

import java.util.List;

public interface DoctorService {
    Doctor addDoctor(Doctor doctor);
    List<Doctor> getAllDoctors();
    Doctor getDoctorById(String doctorId);
    Doctor updateDoctor(Doctor doctor);
    void deleteDoctor(Doctor doctor);
}

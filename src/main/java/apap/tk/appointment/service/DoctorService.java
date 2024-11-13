package apap.tk.appointment.service;
import apap.tk.appointment.model.Doctor;

import java.util.List;

public interface DoctorService {
    Doctor addDoctor(Doctor doctor);
    List<Doctor> getAllDoctors();
    Doctor getDoctorById(String doctorId);
    Doctor updateDoctor(Doctor doctor);
    void deleteDoctor(Doctor doctor);
}

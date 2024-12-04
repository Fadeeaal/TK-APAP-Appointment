package apap.tk.appointment.repository;

import apap.tk.appointment.model.Appointment;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentDb extends JpaRepository<Appointment, String>{
    List<Appointment> findAll();  // Mendapatkan dokter yang tidak dihapus
    List<Appointment> findByDate(Date appointmentDate);
    List<Appointment> findByDateBetween(Date startDate, Date endDate);
    List<Appointment> findByDateAndDoctor(Date potentialDate, UUID doctor);
    List<Appointment> findAllByIsDeletedFalse();
    Appointment findByIdAndIsDeletedFalse(String id);
    List<Appointment> findAllByPatientAndIsDeletedFalse(UUID patientId);
    List<Appointment> findAllByDoctorAndIsDeletedFalse(UUID doctor);
    int countByDateBetween(LocalDate start, LocalDate end);
    boolean existsByDoctorAndDateAndIsDeletedFalse(UUID doctor, Date appointmentDate);

}

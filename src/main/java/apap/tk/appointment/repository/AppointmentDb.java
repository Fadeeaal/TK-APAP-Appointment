package apap.tk.appointment.repository;

import apap.tk.appointment.model.Appointment;
import apap.tk.appointment.model.Doctor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentDb extends JpaRepository<Appointment, String>{
    List<Appointment> findAll();  // Mendapatkan dokter yang tidak dihapus
    List<Appointment> findByDate(Date appointmentDate);
    List<Appointment> findByDateBetween(Date startDate, Date endDate);
    List<Appointment> findByDateAndDoctor(Date potentialDate, Doctor doctor);
    List<Appointment> findAllByIsDeletedFalse();
    List<Appointment> findAllByPatientIdAndIsDeletedFalse(UUID patientId);
    List<Appointment> findAllByDoctorAndIsDeletedFalse(Doctor doctor);
    int countByDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE YEAR(a.date) = :year AND MONTH(a.date) = :month AND a.isDeleted = false")
    Long countByMonth(@Param("year") int year, @Param("month") int month);

    // Query untuk menghitung jumlah appointment per kuartal
    @Query("SELECT COUNT(a) FROM Appointment a WHERE YEAR(a.date) = :year AND QUARTER(a.date) = :quarter AND a.isDeleted = false")
    Long countByQuarter(@Param("year") int year, @Param("quarter") int quarter);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE MONTH(a.date) = :month AND YEAR(a.date) = :year")
    int countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE QUARTER(a.date) = :quarter AND YEAR(a.date) = :year")
    int countByQuarterAndYear(@Param("quarter") int quarter, @Param("year") int year);
}

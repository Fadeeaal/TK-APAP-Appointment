package apap.tk.appointment.repository;

import apap.tk.appointment.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorDb extends JpaRepository<Doctor, String> {
    List<Doctor> findAll();  // Mendapatkan semua dokter
    int countBySpecialist(int specialist);  // Menghitung jumlah dokter berdasarkan spesialis
    List<Doctor> findAllByIsDeletedFalse();  // Mendapatkan dokter yang tidak dihapus

    // Method untuk single Doctor berdasarkan ID
    Doctor getById(String id);

    // Method untuk mendapatkan list dokter berdasarkan list of IDs
    @Override
    List<Doctor> findAllById(Iterable<String> ids);
}

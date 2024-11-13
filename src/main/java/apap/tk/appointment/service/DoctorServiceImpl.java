package apap.tk.appointment.service;

import apap.tk.appointment.model.Doctor;
import apap.tk.appointment.repository.DoctorDb;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;


import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {
    private final DoctorDb doctorDb;

    public DoctorServiceImpl(DoctorDb doctorDb) {
        this.doctorDb = doctorDb;
    }

    private static final Map<Integer, String> SPECIALIZATION_CODES = new HashMap<>();

    static {
        SPECIALIZATION_CODES.put(0, "UMM");  // Dokter Umum
        SPECIALIZATION_CODES.put(1, "GGI");  // Dokter Gigi
        SPECIALIZATION_CODES.put(2, "ANK");  // Spesialis Anak
        SPECIALIZATION_CODES.put(3, "BDH");  // Spesialis Bedah
        SPECIALIZATION_CODES.put(4, "PRE");  // Bedah Plastik
        SPECIALIZATION_CODES.put(5, "JPD");  // Jantung dan Pembuluh Darah
        SPECIALIZATION_CODES.put(6, "KKL");  // Kulit dan Kelamin
        SPECIALIZATION_CODES.put(7, "MTA");  // Mata
        SPECIALIZATION_CODES.put(8, "PDL");  // Penyakit Dalam
        SPECIALIZATION_CODES.put(9, "PRU");  // Paru
        SPECIALIZATION_CODES.put(10, "THT"); // Telinga, Hidung, Tenggorokan, Bedah Kepala Leher
        SPECIALIZATION_CODES.put(11, "KSJ"); // Kesehatan Jiwa
        SPECIALIZATION_CODES.put(12, "ANS"); // Anestesi
        SPECIALIZATION_CODES.put(13, "NRO"); // Neurologi
        SPECIALIZATION_CODES.put(14, "URO"); // Urologi
        SPECIALIZATION_CODES.put(15, "OBG"); // Obstetri dan Ginekologi
        SPECIALIZATION_CODES.put(16, "RAD"); // Radiologi
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorDb.findAllByIsDeletedFalse();
    }

    @Override
    public Doctor addDoctor(Doctor doctor) {
        // Generate ID based on specialization
        String specializationCode = SPECIALIZATION_CODES.get(doctor.getSpecialist());
        if (specializationCode == null) {
            throw new IllegalArgumentException("Invalid specialization code.");
        }
        int doctorCount = doctorDb.countBySpecialist(doctor.getSpecialist());

        // Generate unique 3-digit number berdasarkan urutan dokter (mulai dari 001)
        String uniqueNumber = String.format("%03d", doctorCount + 1);

        // Combine to form the ID
        String doctorId = specializationCode + uniqueNumber;
        doctor.setId(doctorId);

        // Persist the doctor to the database
        return doctorDb.save(doctor);
    }

    @Override
    public Doctor getDoctorById(String doctorId) {
        return doctorDb.findById(doctorId).orElse(null);  // Pastikan ini mengembalikan null jika tidak ada
    }    

    @Override
    public Doctor updateDoctor(Doctor doctor) {
        Doctor getDoctor = getDoctorById(doctor.getId());
        if (getDoctor != null) {
            getDoctor.setName(doctor.getName());
            getDoctor.setEmail(doctor.getEmail());
            getDoctor.setGender(doctor.isGender());
            getDoctor.setSpecialist(doctor.getSpecialist());
            getDoctor.setYearsOfExperience(doctor.getYearsOfExperience());
            getDoctor.setSchedules(doctor.getSchedules());
            getDoctor.setFee(doctor.getFee());
            doctorDb.save(getDoctor);

            return getDoctor;
        }
        return null;
    }

    @Override
    public void deleteDoctor(Doctor doctor) {
        Doctor getDoctor = getDoctorById(doctor.getId());
        if (getDoctor != null) {
            getDoctor.setIsDeleted(true);
            doctorDb.save(getDoctor);
        }
    }
}


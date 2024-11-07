package apap.ti.appointment2206082985.restservice;
import apap.ti.appointment2206082985.model.Appointment;
import apap.ti.appointment2206082985.model.Treatment;
import apap.ti.appointment2206082985.repository.AppointmentDb;
import apap.ti.appointment2206082985.restdto.response.AppointmentResponseDTO;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppointmentRestServiceImpl implements AppointmentRestService {
    private final AppointmentDb appointmentDb;
    public AppointmentRestServiceImpl(AppointmentDb appointmentDb) {
        this.appointmentDb = appointmentDb;
    }
    
    @Override
    public List<AppointmentResponseDTO> getAllAppointment() {
        var listAppointments = appointmentDb.findAll();
        var listAppointmentsResponseDTO = new ArrayList<AppointmentResponseDTO>();
        listAppointments.forEach(appointment -> {
            var appointmentResponseDTO = appointmentToAppointmentResponseDTO(appointment);
            listAppointmentsResponseDTO.add(appointmentResponseDTO);
        });

        return listAppointmentsResponseDTO;
    }

    private AppointmentResponseDTO appointmentToAppointmentResponseDTO(Appointment appointment) {
    // Buat objek AppointmentResponseDTO baru
    var appointmentResponseDTO = new AppointmentResponseDTO();
    
    // Set ID Appointment
    appointmentResponseDTO.setId(appointment.getId());

    // Set Nama Dokter
    appointmentResponseDTO.setDoctorName(appointment.getDoctor().getName());

    // Set Nama Pasien
    appointmentResponseDTO.setPatientName(appointment.getPatient().getName());

    // Set Diagnosis (bisa null)
    appointmentResponseDTO.setDiagnosis(appointment.getDiagnosis());

    // Set Treatments (jika ada)
    if (appointment.getTreatments() != null && !appointment.getTreatments().isEmpty()) {
        List<String> treatmentNames = appointment.getTreatments().stream()
                .map(Treatment::getName) // Ambil nama treatment
                .collect(Collectors.toList()); // Ubah ke list nama treatment
        appointmentResponseDTO.setTreatments(treatmentNames); // Set nama-nama treatment
    }

    // Set Total Biaya
    appointmentResponseDTO.setTotalFee(appointment.getTotalFee());

    // Set Status Appointment
    appointmentResponseDTO.setStatus(appointment.getStatus());

    // Set Tanggal Appointment
    appointmentResponseDTO.setDate(appointment.getDate());



    return appointmentResponseDTO; // Kembalikan objek DTO
}

    
}

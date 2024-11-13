package apap.tk.appointment.service;

import java.util.Date;
import java.util.List;

import apap.tk.appointment.dto.request.AddAppointmentRequestDTO;
import apap.tk.appointment.model.Appointment;
import apap.tk.appointment.model.Doctor;
import apap.tk.appointment.restdto.response.AppointmentResponseDTO;

public interface AppointmentService {
    List<Appointment> getAllAppointments();
    Appointment createAppointmentForExistingPatient(String patientNIK, AddAppointmentRequestDTO requestDTO);
    Appointment createAppointmentWithNewPatient(AddAppointmentRequestDTO requestDTO, String patientNIK, String patientName, String email, boolean gender, Date birthDate, String birthPlace);
    Appointment getAppointmentById(String id);
    Appointment updateAppointmentStatus(String id, int status);
    Appointment updateAppointment (Appointment appointment);
    List<Date> getNextFourPracticeDays(Doctor selectedDoctor);
    int countTodayAppointments();
    void deleteAppoinment(Appointment appointment);
    List<Long> getMonthlyStats(int year);
    List<Long> getQuarterlyStats(int year);
    List<Appointment> getAllAppointmentsInDateRange(Date startDate, Date endDate);
    List<Integer> getAppointmentStatistics(String period, int year);
    List<AppointmentResponseDTO> getAllAppointmentFromRest() throws Exception;
}

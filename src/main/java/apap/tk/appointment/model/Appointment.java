package apap.tk.appointment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @NotNull
    @Column(name = "id")
    private String id;

    @NotNull
    @JoinColumn(name = "doctor_id", nullable = false)
    private UUID doctor;

    @NotNull
    @JoinColumn(name = "patient_id", nullable = false)  // Ensure there's a correct join on patient_id
    private UUID patient;


    @Column(name = "diagnosis")
    private String diagnosis;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "appointment_treatment", // Name of the join table
        joinColumns = @JoinColumn(name = "appointment_id"), // Foreign key to Appointment
        inverseJoinColumns = @JoinColumn(name = "treatment_id") // Foreign key to Treatment
    )
    private List<Treatment> treatments;

    @NotNull
    @Column(name = "total_fee", nullable = false)
    private long totalFee;

    @NotNull
    @Column(name = "status", nullable = false)
    private int status; //0 : created, 1 : Done, 2 : Cancelled

    @NotNull
    @Column(name = "date", nullable = false)
    private Date date;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @NotNull
    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
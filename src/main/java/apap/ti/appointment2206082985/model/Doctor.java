package apap.ti.appointment2206082985.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private String id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "specialist", nullable = false)
    private int specialist;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Column(name = "gender", nullable = false)
    private boolean gender;

    @NotNull
    @Column(name = "years_of_experience", nullable = false)
    private int yearsOfExperience;

    @ElementCollection
    @CollectionTable(name = "doctor_schedules", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "schedules")
    private List<Integer> schedules;
    // 0 : Senin, 1 : Selasa, 2 : Rabu, 3 : Kamis, 4 : Jumat, 5 : Sabtu, 6 : Minggu

    @NotNull
    @Column(name = "fee", nullable = false)
    private long fee;

    @OneToMany(mappedBy = "doctor")
    @Column(name = "appointments")
    private List<Appointment> appointments;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}

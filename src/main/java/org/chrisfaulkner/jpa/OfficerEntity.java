package org.chrisfaulkner.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@Table(name = "officer")
public class OfficerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "officer_role")
    private String officerRole;

    @Column(name = "appointed_on")
    private LocalDate appointedOn;

    @Column(name = "occupation")
    private String occupation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        // Choosing name and appointed date (just for the purposes of the exercise)
        // In the real world, I'd find a way to derive a proper external identity
        OfficerEntity that = (OfficerEntity) o;
        return getName().equals(that.getName()) && getAppointedOn().equals(that.getAppointedOn());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getAppointedOn().hashCode();
        return result;
    }
}

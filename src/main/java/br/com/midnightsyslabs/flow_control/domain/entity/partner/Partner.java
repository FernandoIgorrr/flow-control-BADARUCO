package br.com.midnightsyslabs.flow_control.domain.entity.partner;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = jakarta.persistence.InheritanceType.JOINED)
public abstract class Partner {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @Column(nullable = false)
    protected String name;

    @Column(nullable = true)
    protected String email;

    @Column(nullable = true)
    protected String phone;

    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    protected City city;

    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    protected PartnerRole role;

    @NotNull
    @Column(nullable = false)
    protected OffsetDateTime createdAt;

    protected OffsetDateTime deletedAt;

    protected boolean confirmed;

    public abstract String getDocument();

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}

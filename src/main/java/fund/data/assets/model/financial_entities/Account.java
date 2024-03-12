package fund.data.assets.model.financial_entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * Банковский счёт.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Entity
@Table(name = "In-fund and out-fund accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /**
     * Первое значение, характеризующее банковский счёт.
     * Обычно, речь о банке, где открыт счёт. Или о брокерском подразделении банка.
     */
    @NotBlank
    private String organisationWhereAccountOpened;

    /**
     * Второе значение, характеризующее счёт.
     * В одном и том же банке не может быть счётов с одинаковыми номерами.
     */
    @NotBlank
    @Column(unique = true)
    private String accountNumber;

    @NotNull
    private LocalDate accountOpeningDate;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}

package fund.data.assets.model.financial_entities;

import fund.data.assets.utils.enums.CommissionSystem;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
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

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * Размер комиссии с оборота для типа актива на счёте.
 * Наиболее частая практика взимания брокерской комиссии в РФ - это % с оборота
 * по торгам определёнными типами активов на конкретном счёте.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Entity
@Table(name = "Turnover commission percent values on all accounts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TurnoverCommissionValue {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CommissionSystem commissionSystem;

    /**
     * Комиссия обычно устанавливается для определённого счёта.
     */
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /**
     * Комиссия обычно устанавливается для определённого тип актива.
     */
    @NotBlank
    @Column(unique = true)
    private String assetTypeName;

    /**
     * Размер комиссии - это % с оборота. Потому нужен Float, Double будет занимать лишнее место.
     */
    @NotNull
    private Float commissionPercentValue;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}

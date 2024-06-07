package fund.data.assets.model.financial_entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
@Table(name = "turnover_commission_percent_values",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"account_id", "asset_type_name"})})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TurnoverCommissionValue {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /**
     * Комиссия обычно устанавливается для определённого счёта.
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /**
     * Комиссия обычно устанавливается для определённого тип актива.
     */
    @NotBlank
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

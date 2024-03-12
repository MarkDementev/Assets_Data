package fund.data.assets.model.asset;

import fund.data.assets.model.asset.relationship.AssetRelationship;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.TaxSystem;

import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Getter
@Setter
public abstract class Asset {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AssetCurrency assetCurrency;

    @NotBlank
    private String assetTypeName;

    @NotBlank
    private String assetTitle;

    @NotNull
    @PositiveOrZero
    private Integer assetCount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TaxSystem assetTaxSystem;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "assetRelationship_id")
    private AssetRelationship assetRelationship;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public Asset(AssetCurrency assetCurrency, String assetTypeName, String assetTitle,
                 Integer assetCount, TaxSystem assetTaxSystem) {
        this.assetCurrency = assetCurrency;
        this.assetTypeName = assetTypeName;
        this.assetTitle = assetTitle;
        this.assetCount = assetCount;
        this.assetTaxSystem = assetTaxSystem;
    }
}

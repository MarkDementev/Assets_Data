package fund.data.assets.model;

import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.TaxSystem;

//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.OneToOne;
//import jakarta.persistence.CascadeType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;

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
    /*
    check - does fund.data.assets.utils.Currency validation works correctly without any validation annotation?
    */
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

//    /*
//     check - does cascade and fetch works correctly?
//     */
//    @OneToOne(cascade = CascadeType.MERGE)
//    @JoinColumn(name = "assetRelationship_id", nullable = false)
//    private AssetRelationship assetRelationship;

//    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
//    private Set<Asset> assets;
}

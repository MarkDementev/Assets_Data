package fund.data.assets.model;

import fund.data.assets.utils.AssetsCurrency;
import fund.data.assets.utils.TaxSystem;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.CascadeType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/*
check - do I need AllArgsConstructor?
*/
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class Asset {
    /*
    check - does fund.data.assets.utils.Currency validation works correctly without any validation annotation?
    */
    @NotNull
    @Enumerated(EnumType.STRING)
    private AssetsCurrency assetCurrency;

    @NotBlank
    private String assetTypeName;

    @NotBlank
    private String assetTitle;

    @NotNull
    @Size(min = 1)
    private Integer assetCount;

//    @NotNull
//    private Boolean isTaxableAsset;
//
//    @Enumerated(EnumType.STRING)
//    private TaxSystem assetTaxSystem;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "assetRelationship_id", nullable = false)
    private AssetRelationship assetRelationship;
    /*
    check - does cascade and fetch works correctly?
    */
//    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
//    private Set<Asset> assets;
}

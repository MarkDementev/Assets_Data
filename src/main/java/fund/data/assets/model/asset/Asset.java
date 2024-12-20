package fund.data.assets.model.asset;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fund.data.assets.model.asset.exchange.FixedRateBondPackage;
import fund.data.assets.model.asset.relationship.AssetRelationship;
import fund.data.assets.model.asset.relationship.FinancialAssetRelationship;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.FinancialAssetTypeName;
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
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

import java.util.Map;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * Актив с учётом своего количества.
 * Абстрактный класс - отец всех типов и вариантов активов.
 * @version 0.1-b
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Entity
@Table(name = "abstract_assets")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({@JsonSubTypes.Type(value = FixedRateBondPackage.class)})
@NoArgsConstructor
@Getter
@Setter
public abstract class Asset {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /**
     * Валюта актива определяется валютой его покупки.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private AssetCurrency assetCurrency;

    /**
     * Тип актива. Например, облигация с фиксированным купоном - FixedRateBond.
     */
    @NotBlank
    private String assetTypeName;

    /**
     * Название конкретного актива. Например, облигация может называться так - МТС-Банк, 001Р-02.
     * Название биржевого актива нужно брать из брокерского приложения, где актив был куплен.
     * Вклады имеют собственное название, данное банком. С иными типами активов - именовать, исходя из их сути.
     */
    @NotBlank
    private String assetTitle;

    /**
     * Количество единиц актива в пакете на учёте инвестиционного фонда.
     */
    @NotNull
    @PositiveOrZero
    private Integer assetCount;

    /**
     * Тип налоговой системы для налогообложения операций и доходов по активу.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private TaxSystem assetTaxSystem;

    /**
     * Это поле - связующее между активом, собственниками актива, и иными сущностями, характерными для
     * конкретного типа актива. Как и Asset, AssetRelationship - это абстрактный класс.
     * Поле инициализируется после заполнения полей актива, но до инициализации полей его наследников.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_relationship_id")
    private AssetRelationship assetRelationship;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public Asset(AssetCurrency assetCurrency, String assetTypeName, String assetTitle, Integer assetCount,
                 TaxSystem assetTaxSystem, Map<String, Float> assetOwnersWithAssetCounts, Account account) {
        this.assetCurrency = assetCurrency;
        this.assetTypeName = assetTypeName;
        this.assetTitle = assetTitle;
        this.assetCount = assetCount;
        this.assetTaxSystem = assetTaxSystem;

        if (FinancialAssetTypeName.isInFinancialAssetTypeNameEnum(assetTypeName)) {
            this.assetRelationship = new FinancialAssetRelationship(assetOwnersWithAssetCounts, account);
        } else {
            throw new IllegalArgumentException("Sorry, this asset type is not yet supported by the fund!");
        }
    }
}

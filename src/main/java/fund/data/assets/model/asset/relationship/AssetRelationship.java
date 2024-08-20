package fund.data.assets.model.asset.relationship;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fund.data.assets.model.asset.Asset;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Column;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

import java.util.Map;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * Сущность - связующее между активом и собственником актива.
 * Абстрактный класс - отец взаимосвязей для всех типов активов.
 * В классе все ссылки на другие сущности со значением fetch = FetchType.LAZY, т.к. в сущности много ссылочных полей
 * на другие сущности, и дефолтная fetch = FetchType.EAGER замедлит работу программы.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Entity
@Table(name = "abstract_asset_ownerships_with_account_placement")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({@JsonSubTypes.Type(value = FinancialAssetRelationship.class)})
@NoArgsConstructor
@Getter
@Setter
public abstract class AssetRelationship {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    @JsonBackReference
    private Asset asset;

    /**
     * Поле ниже представляет собой мапу с указанием того, каким владельцам принадлежит какое количество ценных бумаг
     * в рамках пакета, который обслуживает данная сущность.
     */
    @ElementCollection
    @CollectionTable(name = "asset_ownership_counts", joinColumns = @JoinColumn(name = "asset_relationship_id"))
    @MapKeyColumn(name = "assets_owner_id")
    @Column(name = "asset_count")
    private Map<String, Float> assetOwnersWithAssetCounts;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public AssetRelationship(Asset asset, Map<String, Float> assetOwnersWithAssetCounts) {
        this.asset = asset;
        this.assetOwnersWithAssetCounts = assetOwnersWithAssetCounts;
    }
}

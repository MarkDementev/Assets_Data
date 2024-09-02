package fund.data.assets.model.asset.relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fund.data.assets.service.impl.FixedRateBondServiceImpl;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Column;

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

    /**
     * Поле инициализируется в сервисе {@link FixedRateBondServiceImpl}, а не с помощью конструкторов сущности.
     * Поле нужно для того, чтобы ссылаться при нужде на связанную сущность Asset, но не иметь ссылку, чтобы можно
     * было сначала удалять Asset без проблем, а потом уже "схлопывать" и AssetRelationship.
     */
    private Long assetId;

    /**
     * Поле ниже представляет собой мапу с указанием того, каким владельцам принадлежит какое количество ценных бумаг
     * в рамках пакета, который обслуживает данная сущность.
     */
    @ElementCollection
    @CollectionTable(name = "asset_ownership_counts",
            joinColumns = @JoinColumn(name = "abstract_asset_ownerships_with_account_placement_id"))
    @MapKeyColumn(name = "assets_owner_id")
    @Column(name = "asset_count")
    private Map<String, Float> assetOwnersWithAssetCounts;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public AssetRelationship(Map<String, Float> assetOwnersWithAssetCounts) {
        this.assetOwnersWithAssetCounts = assetOwnersWithAssetCounts;
    }
}

package fund.data.assets.model.asset.relationship;

import fund.data.assets.model.financial_entities.Account;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Сущность - связующее для финансовых активов.
 * Класс - наследник абстрактного AssetRelationship.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Entity
@Table(name = "financial_asset_relationships")
@NoArgsConstructor
@Getter
@Setter
public class FinancialAssetRelationship extends AssetRelationship {
    /**
     * Все финансовые активы находятся на конкретном счету. Поэтому дополняем AssetRelationship ссылкой на счёт.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public FinancialAssetRelationship(Map<String, Float> assetOwnersWithAssetCounts, Account account) {
        super(assetOwnersWithAssetCounts);

        this.account = account;
    }
}

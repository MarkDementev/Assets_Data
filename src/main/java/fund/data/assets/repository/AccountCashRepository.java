package fund.data.assets.repository;

import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.model.financial_entities.AccountCash;
import fund.data.assets.model.owner.AssetsOwner;
import fund.data.assets.utils.enums.AssetCurrency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO для обслуживания денежных средств собственников активов на счетах.
 * Обслуживаемая сущность - {@link AccountCash}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Repository
public interface AccountCashRepository extends JpaRepository<AccountCash, Long> {
    AccountCash findByAccountAndAssetCurrencyAndAssetsOwner(Account account, AssetCurrency assetCurrency,
                                                            AssetsOwner assetsOwner);
}

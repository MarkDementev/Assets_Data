package fund.data.assets.repository;

import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.model.financial_entities.TurnoverCommissionValue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO для обслуживания размера комиссии с оборота для типа актива на счёте.
 * Обслуживаемая сущность - {@link fund.data.assets.model.financial_entities.TurnoverCommissionValue}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Repository
public interface TurnoverCommissionValueRepository extends JpaRepository<TurnoverCommissionValue, Long> {
    TurnoverCommissionValue findByAccountAndAssetTypeName(Account account, String assetTypeName);
}

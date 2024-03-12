package fund.data.assets.repository;

import fund.data.assets.model.financial_entities.TurnoverCommissionValue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurnoverCommissionValueRepository extends JpaRepository<TurnoverCommissionValue, Long> {
//    TurnoverCommissionValue findByAssetTypeName(String assetTypeName);
}

package fund.data.assets.repository;

import fund.data.assets.model.asset.exchange.FixedRateBondPackage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO для обслуживания пакетов облигаций с фиксированным купоном.
 * Обслуживаемая сущность - {@link FixedRateBondPackage}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Repository
public interface FixedRateBondRepository extends JpaRepository<FixedRateBondPackage, Long> {
}

package fund.data.assets.repository;

import fund.data.assets.model.asset.exchange.FixedRateBond;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixedRateBondRepository extends JpaRepository<FixedRateBond, Long> {
}

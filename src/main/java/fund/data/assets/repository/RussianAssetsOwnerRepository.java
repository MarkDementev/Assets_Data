package fund.data.assets.repository;

import fund.data.assets.model.asset.user.RussianAssetsOwner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO для обслуживания владельца активов с гражданством РФ.
 * Обслуживаемая сущность - {@link fund.data.assets.model.asset.user.RussianAssetsOwner}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Repository
public interface RussianAssetsOwnerRepository extends JpaRepository<RussianAssetsOwner, Long> {
}

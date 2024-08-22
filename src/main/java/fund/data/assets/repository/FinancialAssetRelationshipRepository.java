package fund.data.assets.repository;

import fund.data.assets.model.asset.relationship.FinancialAssetRelationship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO для обслуживания сущностей - связующих для финансовых активов.
 * Обслуживаемая сущность - {@link FinancialAssetRelationship}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Repository
public interface FinancialAssetRelationshipRepository extends JpaRepository<FinancialAssetRelationship, Long> {
}

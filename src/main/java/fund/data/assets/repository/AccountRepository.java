package fund.data.assets.repository;

import fund.data.assets.model.financial_entities.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByOrganisationWhereAccountOpened(String organisationWhereAccountOpened);
}

package fund.data.assets.repository;

import fund.data.assets.model.financial_entities.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO для обслуживания банковских счетов.
 * Обслуживаемая сущность - {@link fund.data.assets.model.financial_entities.Account}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    /**
     * Хотя счёт определяет сочетание организации, где он открыт, и его номера,
     * был добавлен этот метод для упрощения написания интеграционных тестов контроллера счёта.
     * @param organisationWhereAccountOpened наименование организации, где открыт счёт.
     * @return сущность - банковский счёт.
     * @since 0.0.1-alpha
     */
    Account findByOrganisationWhereAccountOpened(String organisationWhereAccountOpened);
}

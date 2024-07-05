package fund.data.assets.service;

import fund.data.assets.dto.financial_entities.AccountDTO;
import fund.data.assets.model.financial_entities.Account;

import java.util.List;

/**
 * Сервис для обслуживания банковских счетов.
 * Обслуживаемая сущность - {@link fund.data.assets.model.financial_entities.Account}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public interface AccountService {
    Account getAccount(Long id);
    List<Account> getAccounts();
    Account createAccount(AccountDTO accountDTO);
    Account updateAccount(Long id, AccountDTO accountDTO);
    void deleteAccount(Long id);
}

package fund.data.assets.service;

import fund.data.assets.dto.AccountDTO;
import fund.data.assets.model.Account;

import java.util.List;

public interface AccountService {
    Account getAccount(Long id);
    List<Account> getAccounts();
    Account createAccount(AccountDTO accountDTO);
    Account updateAccount(Long id, AccountDTO accountDTO);
    void deleteAccount(Long id);
}

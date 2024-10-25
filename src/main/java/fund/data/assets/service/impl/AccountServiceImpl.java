package fund.data.assets.service.impl;

import fund.data.assets.dto.financial_entities.AccountDTO;
import fund.data.assets.exception.EntityWithIDNotFoundException;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.service.AccountService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Реализация сервиса для обслуживания банковских счетов.
 * Обслуживаемая сущность - {@link Account}.
 * @version 0.0.2-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException("Account", id));
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = {Exception.class})
    public Account createAccount(AccountDTO accountDTO) {
        AtomicReference<Account> atomicNewAccount = new AtomicReference<>(new Account());

        getFromDTOThenSetAll(atomicNewAccount, accountDTO);

        return accountRepository.save(atomicNewAccount.get());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public Account updateAccount(Long id, AccountDTO accountDTO) {
        AtomicReference<Account> atomicAccountToUpdate = new AtomicReference<>(
                accountRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException("Account", id))
        );

        getFromDTOThenSetAll(atomicAccountToUpdate, accountDTO);

        return accountRepository.save(atomicAccountToUpdate.get());
    }

    @Override
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    private void getFromDTOThenSetAll(AtomicReference<Account> accountToWorkWith, AccountDTO accountDTO) {
        accountToWorkWith.get().setOrganisationWhereAccountOpened(accountDTO.getOrganisationWhereAccountOpened());
        accountToWorkWith.get().setAccountNumber(accountDTO.getAccountNumber());
        accountToWorkWith.get().setAccountOpeningDate(accountDTO.getAccountOpeningDate());
    }
}

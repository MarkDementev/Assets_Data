package fund.data.assets.service.impl;

import fund.data.assets.dto.AccountDTO;
import fund.data.assets.model.Account;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.service.AccountService;

import lombok.RequiredArgsConstructor;

//import org.hibernate.Session;
//import org.hibernate.Transaction;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    public Account getAccount(Long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account createAccount(AccountDTO accountDTO) {
        Account newAccount = new Account();

        getFromDTOThenSetAll(newAccount, accountDTO);

        return accountRepository.save(newAccount);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class})
    public Account updateAccount(Long id, AccountDTO accountDTO) {
        final Account accountToUpdate = accountRepository.findById(id).orElseThrow();

        getFromDTOThenSetAll(accountToUpdate, accountDTO);

        return accountRepository.save(accountToUpdate);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class})
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    private void getFromDTOThenSetAll(Account accountToWorkWith, AccountDTO accountDTO) {
        accountToWorkWith.setOrganisationWhereAccountOpened(accountDTO.getOrganisationWhereAccountOpened());
        accountToWorkWith.setAccountNumber(accountDTO.getAccountNumber());
        accountToWorkWith.setAccountOpeningDate(accountDTO.getAccountOpeningDate());
    }

//    private final SessionFactory sessionFactory;
//
//    @Autowired
//    public AccountService(EntityManagerFactory factory) {
//        if (factory.unwrap(SessionFactory.class) == null){
//            throw new NullPointerException("factory is not a hibernate factory");
//        }
//        this.sessionFactory = factory.unwrap(SessionFactory.class);
//    }
//
//    public Account getAccount(Long id) {
//        try (Session session = sessionFactory.openSession()) {
//            return Optional.ofNullable(session.get(Account.class, id)).orElseThrow();
//        }
//    }
//
//    public List<Account> getAccounts() {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery("from Account", Account.class).list();
//        }
//    }
//
//    public void createAccount(AccountDTO accountDTO) {
//        Account newAccount = new Account();
//
//        getFromDTOThenSetAll(newAccount, accountDTO);
//
//        try (Session session = sessionFactory.openSession()) {
//            session.persist(newAccount);
//        }
//    }
//
//    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class})
//    public void updateAccount(Long id, AccountDTO accountDTO) {
//        try (Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.getTransaction();
//
//            transaction.begin();
//
//            Account accountToUpdate = getAccount(id);
//            getFromDTOThenSetAll(accountToUpdate, accountDTO);
//            session.merge(accountToUpdate);
//
//            transaction.commit();
//        }
//    }
//
//    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class})
//    public void deleteAccount(Long id) {
//        try (Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.getTransaction();
//
//            transaction.begin();
//
//            Account accountToRemove = getAccount(id);
//            session.remove(accountToRemove);
//
//            transaction.commit();
//        }
//    }
//
//    private void getFromDTOThenSetAll(Account accountToWorkWith, AccountDTO accountDTO) {
//        accountToWorkWith.setOrganisationWhereAccountOpened(accountDTO.getOrganisationWhereAccountOpened());
//        accountToWorkWith.setAccountNumber(accountDTO.getAccountNumber());
//        accountToWorkWith.setAccountOpeningDate(accountDTO.getAccountOpeningDate());
//    }
}

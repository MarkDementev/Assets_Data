package fund.data.assets.service;

import fund.data.assets.dto.AccountDTO;
import fund.data.assets.model.Account;

import lombok.NoArgsConstructor;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
//@Transactional
@NoArgsConstructor
public class AccountService {
    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public Account getAccount(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Account.class, id)).orElseThrow();
        }
    }

    public List<Account> getAccounts() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Account", Account.class).list();
        }
    }

    public void createAccount(AccountDTO accountDTO) {
        Account newAccount = new Account();

        getFromDTOThenSetAll(newAccount, accountDTO);

        try (Session session = sessionFactory.openSession()) {
            session.persist(newAccount);
        }
    }

    public void updateAccount(Long id, AccountDTO accountDTO) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.getTransaction();

            try {
                transaction.begin();

                Account accountToUpdate = getAccount(id);
                getFromDTOThenSetAll(accountToUpdate, accountDTO);
                session.merge(accountToUpdate);

                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }
        }
    }

    public void deleteAccount(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.getTransaction();

            try {
                transaction.begin();
                Account accountToRemove = getAccount(id);
                session.remove(accountToRemove);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }
        }
    }

    private void getFromDTOThenSetAll(Account accountToWorkWith, AccountDTO accountDTO) {
        accountToWorkWith.setOrganisationWhereAccountOpened(accountDTO.getOrganisationWhereAccountOpened());
        accountToWorkWith.setAccountNumber(accountDTO.getAccountNumber());
        accountToWorkWith.setAccountOpeningDate(accountDTO.getAccountOpeningDate());
    }
}

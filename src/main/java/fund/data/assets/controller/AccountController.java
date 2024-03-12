package fund.data.assets.controller;

import fund.data.assets.dto.AccountDTO;
import fund.data.assets.model.Account;

import jakarta.validation.Valid;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static fund.data.assets.controller.AccountController.ACCOUNT_CONTROLLER_PATH;

@RestController
@RequestMapping("{base-url}" + ACCOUNT_CONTROLLER_PATH)
public class AccountController {
    public static final String ACCOUNT_CONTROLLER_PATH = "/accounts";
    public static final String ID_PATH = "/{id}";
    SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    @GetMapping(ID_PATH)
    public Account getAccount(@PathVariable Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Account.class, id);
        }
    }

    @GetMapping
    public List<Account> getAccounts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Account> query = session.createQuery("from Account", Account.class);

            return query.list();
        }
    }

    @PostMapping
    public void createAccount(@RequestBody @Valid AccountDTO accountDTO) {
        try (Session session = sessionFactory.openSession()) {
            session.persist(accountDTO);
        }
    }
//
//    @GetMapping
//    public List<Account> getAccounts() {
//        try (Session session = sessionFactory.openSession()) {
//            Query<Account> query = session.createQuery("from Account", Account.class);
//
//            return query.list();
//        }
//    }

    @DeleteMapping(ID_PATH)
    public void deleteAccount(@PathVariable Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Account accountToRemove = session.get(Account.class, id);
            session.remove(accountToRemove);

            transaction.commit();
        }
    }
}

package fund.data.assets.controller;

import fund.data.assets.dto.AccountDTO;
import fund.data.assets.model.Account;
import fund.data.assets.service.impl.AccountServiceImpl;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static fund.data.assets.controller.AccountController.ACCOUNT_CONTROLLER_PATH;

@RestController
@RequestMapping("{base-url}" + ACCOUNT_CONTROLLER_PATH)
@AllArgsConstructor
public class AccountController {
    public static final String ACCOUNT_CONTROLLER_PATH = "/accounts";
    public static final String ID_PATH = "/{id}";
    private final AccountServiceImpl accountService;

    @GetMapping(ID_PATH)
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }

    @GetMapping
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@RequestBody @Valid AccountDTO accountDTO) {
        return accountService.createAccount(accountDTO);
    }

    @PutMapping(ID_PATH)
    public Account updateAccount(@PathVariable Long id, @RequestBody @Valid AccountDTO accountDTO) {
        return accountService.updateAccount(id, accountDTO);
    }

    @DeleteMapping(ID_PATH)
    public void deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
    }
}

package fund.data.assets.controller;

import fund.data.assets.dto.financial_entities.AccountDTO;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static fund.data.assets.controller.AccountController.ACCOUNT_CONTROLLER_PATH;

/**
 * Контроллер для работы с банковскими счетами.
 * Обслуживаемая сущность - {@link fund.data.assets.model.financial_entities.Account}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@RestController
@RequestMapping("{base-url}" + ACCOUNT_CONTROLLER_PATH)
@AllArgsConstructor
public class AccountController {
    public static final String ACCOUNT_CONTROLLER_PATH = "/accounts";
    public static final String ID_PATH = "/{id}";
    private final AccountService accountService;

    @Operation(summary = "Get account by id")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = Account.class))
    )
    @GetMapping(ID_PATH)
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok().body(accountService.getAccount(id));
    }

    @Operation(summary = "Get all accounts")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = Account.class)))
    )
    @GetMapping
    public ResponseEntity<List<Account>> getAccounts() {
        return ResponseEntity.ok().body(accountService.getAccounts());
    }

    @Operation(summary = "Create new account")
    @ApiResponse(responseCode = "201", description = "Account created")
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody @Valid AccountDTO accountDTO) {
        return ResponseEntity.created(null).body(accountService.createAccount(accountDTO));
    }

    @Operation(summary = "Update account")
    @ApiResponse(responseCode = "200", description = "Account updated")
    @PutMapping(ID_PATH)
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody @Valid AccountDTO accountDTO) {
        return ResponseEntity.ok().body(accountService.updateAccount(id, accountDTO));
    }

    @Operation(summary = "Delete account")
    @ApiResponse(responseCode = "200", description = "Account deleted")
    @DeleteMapping(ID_PATH)
    public void deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
    }
}

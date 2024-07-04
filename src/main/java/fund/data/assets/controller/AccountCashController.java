package fund.data.assets.controller;

import fund.data.assets.dto.AccountCashDTO;
import fund.data.assets.model.financial_entities.AccountCash;
import fund.data.assets.service.AccountCashService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static fund.data.assets.controller.AccountCashController.ACCOUNT_CASH_CONTROLLER_PATH;

/**
 * Контроллер для работы с денежными средствами собственников активов на счетах.
 * Обслуживаемая сущность - {@link AccountCash}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@RestController
@RequestMapping("{base-url}" + ACCOUNT_CASH_CONTROLLER_PATH)
@AllArgsConstructor
public class AccountCashController {
    public static final String ACCOUNT_CASH_CONTROLLER_PATH = "/account_cash";
    public static final String ID_PATH = "/{id}";
    private final AccountCashService accountCashService;

    @Operation(summary = "Get owner account cash info by id")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = AccountCash.class))
    )
    @GetMapping(ID_PATH)
    public ResponseEntity<AccountCash> getCash(@PathVariable Long id) {
        return ResponseEntity.ok().body(accountCashService.getCash(id));
    }

    @Operation(summary = "Get all owners account cash info")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = AccountCash.class)))
    )
    @GetMapping
    public ResponseEntity<List<AccountCash>> getAllCash() {
        return ResponseEntity.ok().body(accountCashService.getAllCash());
    }

    @Operation(summary = "Change owner account cash amount")
    @ApiResponse(responseCode = "200", description = "Cash amount changed")
    @PostMapping
    public ResponseEntity<AccountCash> createAccountCashOrChangeAmount(@RequestBody @Valid
                                                                           AccountCashDTO accountCashDTO) {
        return ResponseEntity.ok().body(accountCashService.createAccountCashOrChangeAmount(accountCashDTO));
    }
}

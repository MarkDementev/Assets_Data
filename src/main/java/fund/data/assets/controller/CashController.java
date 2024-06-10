package fund.data.assets.controller;

import fund.data.assets.dto.CashDTO;
import fund.data.assets.model.financial_entities.Cash;
import fund.data.assets.service.CashService;

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

import static fund.data.assets.controller.CashController.CASH_CONTROLLER_PATH;

/**
 * Контроллер для работы с денежными средствами собственников активов на счетах.
 * Обслуживаемая сущность - {@link fund.data.assets.model.financial_entities.Cash}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@RestController
@RequestMapping("{base-url}" + CASH_CONTROLLER_PATH)
@AllArgsConstructor
public class CashController {
    public static final String CASH_CONTROLLER_PATH = "/cash";
    public static final String ID_PATH = "/{id}";
    private final CashService cashService;

    @Operation(summary = "Get cash info by id")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = Cash.class))
    )
    @GetMapping(ID_PATH)
    public ResponseEntity<Cash> getCash(@PathVariable Long id) {
        return ResponseEntity.ok().body(cashService.getCash(id));
    }

    @Operation(summary = "Get all cash info")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = Cash.class)))
    )
    @GetMapping
    public ResponseEntity<List<Cash>> getAllCash() {
        return ResponseEntity.ok().body(cashService.getAllCash());
    }

    @Operation(summary = "Change cash amount")
    @ApiResponse(responseCode = "200", description = "Cash amount changed")
    @PostMapping
    public ResponseEntity<Cash> depositOrWithdrawCashAmount(@RequestBody @Valid CashDTO cashDTO) {
        return ResponseEntity.ok().body(cashService.depositOrWithdrawCashAmount(cashDTO));
    }
}

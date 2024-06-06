package fund.data.assets.controller;

//import fund.data.assets.dto.FixedRateBondDTO;
import fund.data.assets.model.asset.exchange.FixedRateBond;
import fund.data.assets.service.FixedRateBondService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fund.data.assets.controller.FixedRateBondController.FIXED_RATE_BOND_CONTROLLER_PATH;

/**
 * Контроллер для работы с облигациями с фиксированным купоном.
 * Обслуживаемая сущность - {@link fund.data.assets.model.asset.exchange.FixedRateBond}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@RestController
@RequestMapping("{base-url}" + FIXED_RATE_BOND_CONTROLLER_PATH)
@AllArgsConstructor
public class FixedRateBondController {
    public static final String FIXED_RATE_BOND_CONTROLLER_PATH = "/bonds/simple";
    public static final String ID_PATH = "/{id}";
    private final FixedRateBondService fixedRateBondService;

    @Operation(summary = "Get fixed rate bond by id")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = FixedRateBond.class))
    )
    @GetMapping(ID_PATH)
    public ResponseEntity<FixedRateBond> getFixedRateBond(@PathVariable Long id) {
        return ResponseEntity.ok().body(fixedRateBondService.getFixedRateBond(id));
    }

    @Operation(summary = "Get all fixed rate bonds")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = FixedRateBond.class)))
    )
    @GetMapping
    public ResponseEntity<List<FixedRateBond>> getFixedRateBonds() {
        return ResponseEntity.ok().body(fixedRateBondService.getFixedRateBonds());
    }

//    @Operation(summary = "Buy fixed rate bond first time on this account")
//    @ApiResponse(responseCode = "201", description = "Fixed rate bond bought")
//    @PostMapping
//    public ResponseEntity<FixedRateBond> firstBuyFixedRateBond(@RequestBody @Valid FixedRateBondDTO fixedRateBondDTO) {
//        return ResponseEntity.created(null).body(fixedRateBondService.firstBuyFixedRateBond(fixedRateBondDTO));
//    }

    //TODO - Реализуй остальные контроллеры
}

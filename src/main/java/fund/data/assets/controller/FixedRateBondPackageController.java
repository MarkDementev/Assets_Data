package fund.data.assets.controller;

import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.dto.asset.exchange.FixedRateBondFullSellDTO;
import fund.data.assets.model.asset.exchange.FixedRateBondPackage;
import fund.data.assets.service.FixedRateBondService;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

import static fund.data.assets.controller.FixedRateBondPackageController.FIXED_RATE_BOND_CONTROLLER_PATH;

/**
 * Контроллер для работы с пакетами облигаций с фиксированным купоном.
 * Обслуживаемая сущность - {@link FixedRateBondPackage}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@RestController
@RequestMapping("{base-url}" + FIXED_RATE_BOND_CONTROLLER_PATH)
@AllArgsConstructor
public class FixedRateBondPackageController {
    public static final String FIXED_RATE_BOND_CONTROLLER_PATH = "/bonds/simple";
    public static final String ID_PATH = "/{id}";
    private final FixedRateBondService fixedRateBondService;

    @Operation(summary = "Get fixed rate bond by id")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = FixedRateBondPackage.class))
    )
    @GetMapping(ID_PATH)
    public ResponseEntity<FixedRateBondPackage> getFixedRateBond(@PathVariable Long id) {
        return ResponseEntity.ok().body(fixedRateBondService.getFixedRateBond(id));
    }

    @Operation(summary = "Get all fixed rate bonds")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = FixedRateBondPackage.class)))
    )
    @GetMapping
    public ResponseEntity<List<FixedRateBondPackage>> getFixedRateBonds() {
        return ResponseEntity.ok().body(fixedRateBondService.getFixedRateBonds());
    }

    @Operation(summary = "Buy fixed rate bond package first time on this account")
    @ApiResponse(responseCode = "201", description = "Fixed rate bond package bought")
    @PostMapping
    public ResponseEntity<FixedRateBondPackage> firstBuyFixedRateBond(@RequestBody @Valid
                                                                   FirstBuyFixedRateBondDTO firstBuyFixedRateBondDTO) {
        return ResponseEntity.created(null).body(
                fixedRateBondService.firstBuyFixedRateBond(firstBuyFixedRateBondDTO));
    }

    @Operation(summary = "Sell all fixed rate bond package")
    @ApiResponse(responseCode = "200", description = "All package is sold")
    @DeleteMapping(ID_PATH)
    public void sellAllPackageFixedRateBond(@PathVariable Long id,
                                            @RequestBody @Valid FixedRateBondFullSellDTO fixedRateBondFullSellDTO) {
        fixedRateBondService.sellAllPackage(id, fixedRateBondFullSellDTO);
    }
    //TODO - Реализуй остальные контроллеры - 1) докупку бумаг в пакет и 2) частичную продажу бумаг из пакета
}

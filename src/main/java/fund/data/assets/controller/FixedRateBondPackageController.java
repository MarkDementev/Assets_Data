package fund.data.assets.controller;

import fund.data.assets.dto.asset.exchange.AssetsOwnersCountryDTO;
import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.dto.asset.exchange.FixedRateBondFullSellDTO;
import fund.data.assets.dto.asset.exchange.FixedRateBondPartialSellDTO;
import fund.data.assets.dto.asset.exchange.FixedRateBondBuyDTO;
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
import org.springframework.web.bind.annotation.PutMapping;
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
    public static final String BUY_PATH = "/buy";
    public static final String REDEEM_PATH = "/redeem";
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

    @Operation(summary = "Update fixed rate bond package - buy package part")
    @ApiResponse(responseCode = "200", description = "Fixed rate bond package part bought")
    @PutMapping(ID_PATH + BUY_PATH)
    public ResponseEntity<FixedRateBondPackage> buyFixedRateBondPackagePartial(@PathVariable Long id,
        @RequestBody @Valid FixedRateBondBuyDTO fixedRateBondBuyDTO) {
        return ResponseEntity.ok().body(fixedRateBondService.partialBuyFixedRateBondPackage(id,
                fixedRateBondBuyDTO));
    }

    @Operation(summary = "Update fixed rate bond package - sell package part")
    @ApiResponse(responseCode = "200", description = "Fixed rate bond package part sold")
    @PutMapping(ID_PATH)
    public ResponseEntity<FixedRateBondPackage> sellFixedRateBondPackagePartial(@PathVariable Long id,
        @RequestBody @Valid FixedRateBondPartialSellDTO fixedRateBondPartialSellDTO) {
        return ResponseEntity.ok().body(fixedRateBondService.partialSellFixedRateBondPackage(id,
                fixedRateBondPartialSellDTO));
    }

    @Operation(summary = "Sell all fixed rate bond package")
    @ApiResponse(responseCode = "200", description = "All package is sold")
    @DeleteMapping(ID_PATH)
    public void sellAllPackageFixedRateBond(@PathVariable Long id,
                                            @RequestBody @Valid FixedRateBondFullSellDTO fixedRateBondFullSellDTO) {
        fixedRateBondService.sellAllPackage(id, fixedRateBondFullSellDTO);
    }

    @Operation(summary = "Redeem fixed rate bond package")
    @ApiResponse(responseCode = "200", description = "Package is redeemed")
    @DeleteMapping(ID_PATH + REDEEM_PATH)
    public void redeemBonds(@PathVariable Long id,
                            @RequestBody @Valid AssetsOwnersCountryDTO assetsOwnersCountryDTO) {
        fixedRateBondService.redeemBonds(id, assetsOwnersCountryDTO);
    }
}

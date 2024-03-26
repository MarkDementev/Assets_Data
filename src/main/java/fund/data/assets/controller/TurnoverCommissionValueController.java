package fund.data.assets.controller;

import fund.data.assets.dto.common.PercentFloatValueDTO;
import fund.data.assets.dto.TurnoverCommissionValueDTO;
import fund.data.assets.model.financial_entities.TurnoverCommissionValue;
import fund.data.assets.service.TurnoverCommissionValueService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

import static fund.data.assets.controller.TurnoverCommissionValueController.TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH;

/**
 * Контроллер с базовыми возможностями для работы с размерами комиссии с оборота для типа актива на счёте.
 * Обслуживаемая сущность - {@link fund.data.assets.model.financial_entities.TurnoverCommissionValue}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@RestController
@RequestMapping("{base-url}" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH)
@AllArgsConstructor
public class TurnoverCommissionValueController {
    public static final String TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH = "/turnover-commission-values";
    public static final String ID_PATH = "/{id}";
    private final TurnoverCommissionValueService turnoverCommissionValueService;

    @Operation(summary = "Get turnover commission value by id")
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = TurnoverCommissionValue.class))
    )
    @GetMapping(ID_PATH)
    public TurnoverCommissionValue getTurnoverCommissionValue(@PathVariable Long id) {
        return turnoverCommissionValueService.getTurnoverCommissionValue(id);
    }

    @Operation(summary = "Get all turnover commission values")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = TurnoverCommissionValue.class)))
    )
    @GetMapping
    public List<TurnoverCommissionValue> getTurnoverCommissionValues() {
        return turnoverCommissionValueService.getTurnoverCommissionValues();
    }

    @Operation(summary = "Create new turnover commission value")
    @ApiResponse(responseCode = "201", description = "Turnover commission value created")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TurnoverCommissionValue createTurnoverCommissionValue(
            @RequestBody @Valid TurnoverCommissionValueDTO turnoverCommissionValueDTO) {
        return turnoverCommissionValueService.createTurnoverCommissionValue(turnoverCommissionValueDTO);
    }

    @Operation(summary = "Update turnover commission value commission percent value")
    @ApiResponse(responseCode = "200", description = "Turnover commission value updated")
    @PutMapping(ID_PATH)
    public TurnoverCommissionValue updateTurnoverCommissionValue(@PathVariable Long id,
                                                                 @RequestBody @Valid PercentFloatValueDTO percentFloatValueDTO)
    {
        return turnoverCommissionValueService.updateTurnoverCommissionValue(id, percentFloatValueDTO);
    }

    @Operation(summary = "Delete turnover commission value")
    @ApiResponse(responseCode = "200", description = "Turnover commission value deleted")
    @DeleteMapping(ID_PATH)
    public void deleteTurnoverCommissionValue(@PathVariable Long id) {
        turnoverCommissionValueService.deleteTurnoverCommissionValue(id);
    }
}

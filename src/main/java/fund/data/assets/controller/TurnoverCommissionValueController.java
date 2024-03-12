package fund.data.assets.controller;

import fund.data.assets.dto.TurnoverCommissionValueDTO;
import fund.data.assets.model.financial_entities.TurnoverCommissionValue;
import fund.data.assets.service.TurnoverCommissionValueService;

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

@RestController
@RequestMapping("{base-url}" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH)
@AllArgsConstructor
public class TurnoverCommissionValueController {
    public static final String TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH = "/turnover-commission-values";
    public static final String ID_PATH = "/{id}";
    private final TurnoverCommissionValueService turnoverCommissionValueService;

    @GetMapping(ID_PATH)
    public TurnoverCommissionValue getTurnoverCommissionValue(@PathVariable Long id) {
        return turnoverCommissionValueService.getTurnoverCommissionValue(id);
    }

    @GetMapping
    public List<TurnoverCommissionValue> getTurnoverCommissionValues() {
        return turnoverCommissionValueService.getTurnoverCommissionValues();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TurnoverCommissionValue createTurnoverCommissionValue(
            @RequestBody @Valid TurnoverCommissionValueDTO turnoverCommissionValueDTO) {
        return turnoverCommissionValueService.createTurnoverCommissionValue(turnoverCommissionValueDTO);
    }

    @PutMapping(ID_PATH)
    public TurnoverCommissionValue updateTurnoverCommissionValue(@PathVariable Long id, @RequestBody @Valid
                                                                 TurnoverCommissionValueDTO turnoverCommissionValueDTO)
    {
        return turnoverCommissionValueService.updateTurnoverCommissionValue(id, turnoverCommissionValueDTO);
    }

    @DeleteMapping(ID_PATH)
    public void deleteTurnoverCommissionValue(@PathVariable Long id) {
        turnoverCommissionValueService.deleteTurnoverCommissionValue(id);
    }
}

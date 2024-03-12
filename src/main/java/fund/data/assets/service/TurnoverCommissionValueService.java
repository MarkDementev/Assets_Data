package fund.data.assets.service;

import fund.data.assets.dto.TurnoverCommissionValueDTO;
import fund.data.assets.model.financial_entities.TurnoverCommissionValue;

import java.util.List;

public interface TurnoverCommissionValueService {
    TurnoverCommissionValue getTurnoverCommissionValue(Long id);
    List<TurnoverCommissionValue> getTurnoverCommissionValues();
    TurnoverCommissionValue createTurnoverCommissionValue(TurnoverCommissionValueDTO TurnoverCommissionValueDTO);
    TurnoverCommissionValue updateTurnoverCommissionValue(Long id,
                                                          TurnoverCommissionValueDTO TurnoverCommissionValueDTO);
    void deleteTurnoverCommissionValue(Long id);
}

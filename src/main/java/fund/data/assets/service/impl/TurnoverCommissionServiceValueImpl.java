package fund.data.assets.service.impl;

import fund.data.assets.dto.TurnoverCommissionValueDTO;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.model.financial_entities.TurnoverCommissionValue;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.service.TurnoverCommissionValueService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurnoverCommissionServiceValueImpl implements TurnoverCommissionValueService {
    private final AccountRepository accountRepository;
    private final TurnoverCommissionValueRepository turnoverCommissionValueRepository;

    @Override
    public TurnoverCommissionValue getTurnoverCommissionValue(Long id) {
        return turnoverCommissionValueRepository.findById(id).orElseThrow();
    }

    @Override
    public List<TurnoverCommissionValue> getTurnoverCommissionValues() {
        return turnoverCommissionValueRepository.findAll();
    }

    @Override
    public TurnoverCommissionValue createTurnoverCommissionValue(
            TurnoverCommissionValueDTO TurnoverCommissionValueDTO) {
        TurnoverCommissionValue newTurnoverCommissionValue = new TurnoverCommissionValue();
        Account account = accountRepository.findById(TurnoverCommissionValueDTO.getAccountID()).orElseThrow();

        newTurnoverCommissionValue.setAccount(account);
        getFromDTOThenSetAll(newTurnoverCommissionValue, TurnoverCommissionValueDTO);

        return turnoverCommissionValueRepository.save(newTurnoverCommissionValue);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class})
    public TurnoverCommissionValue updateTurnoverCommissionValue(Long id,
                                                          TurnoverCommissionValueDTO turnoverCommissionValueDTO) {
        TurnoverCommissionValue turnoverCommissionValueToUpdate = turnoverCommissionValueRepository.findById(id)
                .orElseThrow();

        getFromDTOThenSetAll(turnoverCommissionValueToUpdate, turnoverCommissionValueDTO);

        return turnoverCommissionValueRepository.save(turnoverCommissionValueToUpdate);
    }

    @Override
    public void deleteTurnoverCommissionValue(Long id) {
        turnoverCommissionValueRepository.deleteById(id);
    }

    private void getFromDTOThenSetAll(TurnoverCommissionValue turnoverCommissionValueToWorkWith,
                                      TurnoverCommissionValueDTO turnoverCommissionValueDTO) {
        turnoverCommissionValueToWorkWith.setCommissionSystem(turnoverCommissionValueDTO.getCommissionSystem());
        turnoverCommissionValueToWorkWith.setAssetTypeName(turnoverCommissionValueDTO.getAssetTypeName());
        turnoverCommissionValueToWorkWith.setCommissionPercentValue(
                turnoverCommissionValueDTO.getCommissionPercentValue());
    }
}

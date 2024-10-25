package fund.data.assets.service.impl;

import fund.data.assets.dto.common.PercentFloatValueDTO;
import fund.data.assets.dto.financial_entities.TurnoverCommissionValueDTO;
import fund.data.assets.exception.EntityWithIDNotFoundException;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.model.financial_entities.TurnoverCommissionValue;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.service.TurnoverCommissionValueService;
import fund.data.assets.utils.InputPercentValueStringsFormatter;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Реализация сервиса для обслуживания размера комиссии с оборота для типа актива на счёте.
 * Обслуживаемая сущность - {@link TurnoverCommissionValue}.
 * @version 0.2-a
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Service
@RequiredArgsConstructor
public class TurnoverCommissionValueServiceImpl implements TurnoverCommissionValueService {
    private final AccountRepository accountRepository;
    private final TurnoverCommissionValueRepository turnoverCommissionValueRepository;

    @Override
    public TurnoverCommissionValue getTurnoverCommissionValue(Long id) {
        return turnoverCommissionValueRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException(
                "TurnoverCommissionValue", id));
    }

    @Override
    public List<TurnoverCommissionValue> getTurnoverCommissionValues() {
        return turnoverCommissionValueRepository.findAll();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class})
    public TurnoverCommissionValue createTurnoverCommissionValue(
            TurnoverCommissionValueDTO turnoverCommissionValueDTO) {
        AtomicReference<TurnoverCommissionValue> atomicTurnoverCommissionValue = new AtomicReference<>(
                new TurnoverCommissionValue());
        Long accountID = turnoverCommissionValueDTO.getAccountID();
        Account account = accountRepository.findById(accountID).orElseThrow(() -> new EntityWithIDNotFoundException(
                "Account", accountID));

        atomicTurnoverCommissionValue.get().setAccount(account);
        atomicTurnoverCommissionValue.get().setAssetTypeName(turnoverCommissionValueDTO.getAssetTypeName());
        atomicTurnoverCommissionValue.get().setCommissionPercentValue(InputPercentValueStringsFormatter
                .getCheckedAndFormatted(turnoverCommissionValueDTO.getCommissionPercentValue()));

        return turnoverCommissionValueRepository.save(atomicTurnoverCommissionValue.get());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public TurnoverCommissionValue updateTurnoverCommissionValue(Long id, PercentFloatValueDTO percentFloatValueDTO) {
        AtomicReference<TurnoverCommissionValue> atomicTurnoverCommissionValueToUpdate = new AtomicReference<>(
                turnoverCommissionValueRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException(
                        "TurnoverCommissionValue", id)));

        atomicTurnoverCommissionValueToUpdate.get().setCommissionPercentValue(InputPercentValueStringsFormatter
                .getCheckedAndFormatted(percentFloatValueDTO.getPercentValue()));

        return turnoverCommissionValueRepository.save(atomicTurnoverCommissionValueToUpdate.get());
    }

    @Override
    public void deleteTurnoverCommissionValue(Long id) {
        turnoverCommissionValueRepository.deleteById(id);
    }
}

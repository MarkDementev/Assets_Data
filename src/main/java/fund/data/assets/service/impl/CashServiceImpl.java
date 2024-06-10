package fund.data.assets.service.impl;

import fund.data.assets.dto.CashDTO;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.model.financial_entities.Cash;
import fund.data.assets.model.owner.AssetsOwner;
import fund.data.assets.repository.CashRepository;
import fund.data.assets.service.CashService;
import fund.data.assets.utils.enums.AssetCurrency;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Реализация сервиса для обслуживания денежных средств собственников активов на счетах.
 * Обслуживаемая сущность - {@link fund.data.assets.model.financial_entities.Cash}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Service
@RequiredArgsConstructor
public class CashServiceImpl implements CashService {
    CashRepository cashRepository;

    @Override
    public Cash getCash(Long id) {
        return cashRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Cash> getAllCash() {
        return cashRepository.findAll();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class})
    public Cash depositOrWithdrawCashAmount(CashDTO cashDTO) {
        Account accountFromDTO = cashDTO.getAccount();
        AssetCurrency assetCurrencyFromDTO = cashDTO.getAssetCurrency();
        AssetsOwner assetsOwnerFromDTO = cashDTO.getAssetsOwner();
        Float amountFromDTO = cashDTO.getAmount();
        Cash cashToWorkWith = cashRepository.findByAccountAndAssetCurrencyAndAssetsOwner(accountFromDTO,
                assetCurrencyFromDTO, assetsOwnerFromDTO);

        if (cashToWorkWith == null) {
            AtomicReference<Cash> newCashAtomicReference = new AtomicReference<>(new Cash(accountFromDTO,
                    assetCurrencyFromDTO, assetsOwnerFromDTO, amountFromDTO));

            return cashRepository.save(newCashAtomicReference.get());
        } else {
            AtomicReference<Cash> alreadyExistsCashAtomicReference = new AtomicReference<>(cashToWorkWith);
            alreadyExistsCashAtomicReference.get().setAmount(amountFromDTO);

            return cashRepository.save(alreadyExistsCashAtomicReference.get());
        }
    }
}

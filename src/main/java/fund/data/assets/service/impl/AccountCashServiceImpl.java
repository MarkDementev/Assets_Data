package fund.data.assets.service.impl;

import fund.data.assets.dto.financial_entities.AccountCashDTO;
import fund.data.assets.exception.AmountFromDTOMoreThanAccountCashAmountException;
import fund.data.assets.exception.NegativeValueNotExistAccountCashException;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.model.financial_entities.AccountCash;
import fund.data.assets.model.owner.RussianAssetsOwner;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.AccountCashRepository;
import fund.data.assets.repository.RussianAssetsOwnerRepository;
import fund.data.assets.service.AccountCashService;
import fund.data.assets.utils.enums.AssetCurrency;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Реализация сервиса для обслуживания денежных средств собственников активов на счетах.
 * Обслуживаемая сущность - {@link AccountCash}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Service
@RequiredArgsConstructor
public class AccountCashServiceImpl implements AccountCashService {
    final AccountCashRepository accountCashRepository;
    final AccountRepository accountRepository;
    final RussianAssetsOwnerRepository russianAssetsOwnerRepository;

    @Override
    public AccountCash getCash(Long id) {
        return accountCashRepository.findById(id).orElseThrow();
    }

    @Override
    public List<AccountCash> getAllCash() {
        return accountCashRepository.findAll();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class})
    public AccountCash createAccountCashOrChangeAmount(AccountCashDTO accountCashDTO) {
        Account accountFromDTO = accountRepository.findById(accountCashDTO.getAccountID()).orElseThrow();
        AssetCurrency assetCurrencyFromDTO = accountCashDTO.getAssetCurrency();
        RussianAssetsOwner assetsOwnerFromDTO = russianAssetsOwnerRepository.findById(accountCashDTO.getAssetsOwnerID())
                .orElseThrow();
        float amountFromDTO = accountCashDTO.getAmountChangeValue();
        Optional<AccountCash> accountCashToWorkWith = Optional.ofNullable(
                accountCashRepository.findByAccountAndAssetCurrencyAndAssetsOwner(accountFromDTO, assetCurrencyFromDTO,
                        assetsOwnerFromDTO));

        if (accountCashToWorkWith.isEmpty() && amountFromDTO >= 0) {
            return accountCashRepository.save(new AtomicReference<>(new AccountCash(accountFromDTO,
                    assetCurrencyFromDTO, assetsOwnerFromDTO, amountFromDTO)).get());
        } else if (accountCashToWorkWith.isEmpty() && amountFromDTO < 0) {
            throw new NegativeValueNotExistAccountCashException();
        }
        AtomicReference<AccountCash> accountCashAtomicReference = new AtomicReference<>(accountCashToWorkWith
                .orElseThrow());
        float accountCashAmount = accountCashToWorkWith.orElseThrow().getAmount();

        if (amountFromDTO < 0 && Math.abs(amountFromDTO) >= accountCashAmount) {
            throw new AmountFromDTOMoreThanAccountCashAmountException(amountFromDTO, accountCashAmount);
        } else {
            accountCashAtomicReference.get().setAmount(accountCashAmount + amountFromDTO);
        }
        return accountCashRepository.save(accountCashAtomicReference.get());
    }
}

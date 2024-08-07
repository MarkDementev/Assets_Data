package fund.data.assets.service.impl;

import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.model.asset.exchange.FixedRateBondPackage;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.model.financial_entities.AccountCash;
import fund.data.assets.model.owner.AssetsOwner;
import fund.data.assets.repository.AccountCashRepository;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.FixedRateBondRepository;
import fund.data.assets.repository.RussianAssetsOwnerRepository;
import fund.data.assets.service.FixedRateBondService;
import fund.data.assets.utils.enums.AssetCurrency;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Реализация сервиса для обслуживания облигаций с фиксированным купоном.
 * Обслуживаемая сущность - {@link FixedRateBondPackage}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Service
@RequiredArgsConstructor
public class FixedRateBondServiceImpl implements FixedRateBondService {
    private final FixedRateBondRepository fixedRateBondRepository;
    private final AccountRepository accountRepository;
    //TODO Надо-бы не только чтобы с русскими ПО работало!
    private final RussianAssetsOwnerRepository russianAssetsOwnerRepository;
    private final AccountCashRepository accountCashRepository;

    @Override
    public FixedRateBondPackage getFixedRateBond(Long id) {
        return fixedRateBondRepository.findById(id).orElseThrow();
    }

    @Override
    public List<FixedRateBondPackage> getFixedRateBonds() {
        return fixedRateBondRepository.findAll();
    }

    //TODO продумай изоляцию и подобные прибамбасы после реализации всего внутри!
    @Override
    public FixedRateBondPackage firstBuyFixedRateBond(FirstBuyFixedRateBondDTO firstBuyFixedRateBondDTO) {
        //проверяется, валидны ли поля DTO - сколько всего хотят бумаг купить, и равно ли это сумме хотелок всех оунеров
        isAssetOwnersWithAssetCountsValid(firstBuyFixedRateBondDTO.getAssetCount(),
                firstBuyFixedRateBondDTO.getAssetOwnersWithAssetCounts());

        AssetCurrency assetCurrency = firstBuyFixedRateBondDTO.getAssetCurrency();
        String assetTitle = firstBuyFixedRateBondDTO.getAssetTitle();
        Integer assetCount = firstBuyFixedRateBondDTO.getAssetCount();
        Map<String, Float> assetOwnersWithAssetCounts = firstBuyFixedRateBondDTO.getAssetOwnersWithAssetCounts();
        Account accountFromDTO = accountRepository.findById(firstBuyFixedRateBondDTO.getAccountID()).orElseThrow();
        String iSIN = firstBuyFixedRateBondDTO.getISIN();
        String assetIssuerTitle = firstBuyFixedRateBondDTO.getAssetIssuerTitle();
        LocalDate lastAssetBuyDate = firstBuyFixedRateBondDTO.getLastAssetBuyDate();
        Integer bondParValue = firstBuyFixedRateBondDTO.getBondParValue();
        Float purchaseBondParValuePercent = firstBuyFixedRateBondDTO.getPurchaseBondParValuePercent();
        Float bondAccruedInterest = firstBuyFixedRateBondDTO.getBondAccruedInterest();
        Float bondCouponValue = firstBuyFixedRateBondDTO.getBondCouponValue();
        Integer expectedBondCouponPaymentsCount = firstBuyFixedRateBondDTO.getExpectedBondCouponPaymentsCount();
        LocalDate bondMaturityDate = firstBuyFixedRateBondDTO.getBondMaturityDate();

        //используя инфу из DTO, создается сущность, но не сохраняется
        FixedRateBondPackage fixedRateBondPackageToCreate = new FixedRateBondPackage(assetCurrency, assetTitle,
                assetCount, assetOwnersWithAssetCounts, accountFromDTO, iSIN, assetIssuerTitle, lastAssetBuyDate,
                bondParValue, purchaseBondParValuePercent, bondAccruedInterest, bondCouponValue,
                expectedBondCouponPaymentsCount, bondMaturityDate);

        //Надо проверить, могут ли купить оунеры активы - пробежаться по их долям и т.п!
        //Если у кого-то не хватит, сразу выброс исключения.
        isOwnersHaveEnoughMoney(fixedRateBondPackageToCreate);

        //если всё ок, создаётся атомарная ссылка, чтобы потом сохранять в базу сущность
        AtomicReference<FixedRateBondPackage> atomicFixedRateBondPackage = new AtomicReference<>(
                fixedRateBondPackageToCreate);

        //Если сущность пакета бумаг может быть создана (т.е. ничего не выбросилось и не упало выше), то измени
        // положение на счетах в сущностях AccountCash оунеров
        changeAccountCashAmountsOfOwners(accountFromDTO, assetCurrency, assetOwnersWithAssetCounts);

        //СОХРАНЯЕМ, УРРА!
        return fixedRateBondRepository.save(atomicFixedRateBondPackage.get());
    }

    /**
     * Проводится валидация - сумма выльюс в assetOwnersWithAssetCounts, приведённая к Integer, должна быть равна
     * значению assetCount. Аргументы метода берутся из DTO {@link FirstBuyFixedRateBondDTO}.
     @param assetCount количество бумаг в пакете ценных бумаг.
     @param assetOwnersWithAssetCounts мапа, где кей - это владелец актива, вэлью - количество ценных бумаг оунера
     в рамках данного пакета ценных бумаг.
     @since 0.0.1-alpha
     */
    private void isAssetOwnersWithAssetCountsValid(Integer assetCount,
                                                   Map<String, Float> assetOwnersWithAssetCounts) {
        Float mapValuesSum = 0.0F;

        for (Map.Entry<String, Float> mapElement : assetOwnersWithAssetCounts.entrySet()) {
            mapValuesSum += mapElement.getValue();
        }
        Integer mapValuesSumToInteger = mapValuesSum.intValue();

        if ((Math.ceil(mapValuesSum) != Math.floor(mapValuesSum))
                || (!mapValuesSumToInteger.equals(assetCount))) {
            throw new IllegalArgumentException("Field assetOwnersWithAssetCounts in DTO is not correct - sum of values"
                    + " are not equals field assetCount!");
        }
    }

    /**
     * Проводится проверка, могут ли собственники активов купить данный пакет облигаций, исходя из наличия денежных
     * средств у себя на счетах (эта информация находится в сущности {@link AccountCash}.
     * @param fixedRateBondPackageToCreate пакет облигаций с фиксированным купоном с общим ISIN, который проверяется.
     * @since 0.0.1-alpha
     */
    private void isOwnersHaveEnoughMoney(FixedRateBondPackage fixedRateBondPackageToCreate) {
        Float totalAssetPurchasePriceWithCommission = fixedRateBondPackageToCreate
                .getTotalAssetPurchasePriceWithCommission();
        Map<String, Float> assetOwnersWithAssetCounts = fixedRateBondPackageToCreate.getAssetRelationship()
                .getAssetOwnersWithAssetCounts();
        Account account = fixedRateBondPackageToCreate.getAssetRelationship()
        AssetCurrency assetCurrency = fixedRateBondPackageToCreate.getAssetCurrency();


        for (Map.Entry<String, Float> mapElement : assetOwnersWithAssetCounts.entrySet()) {
            //находится оунер по кею в мапе
            AssetsOwner assetsOwner = russianAssetsOwnerRepository.findById(Long.valueOf(mapElement.getKey()))
                    .orElseThrow();
            //находится счёт с деньгами данного оунера
            AccountCash assetsOwnerAccountCash = accountCashRepository.findByAccountAndAssetCurrencyAndAssetsOwner(
                    accountFromDTO, assetCurrency, assetsOwner);
            //считается, сколько оунер хочет потратить (т.е. сколько стоит ЕГО ДОЛЯ в пакете бумаг, которую хочет купить)
            Float ownerWantToBuyAssetsInCurrency =

            //проверка, если величина в переменной выше больше, чем денег у оунера, то сущность не создаётся, нужен
            //новый DTO!
            if (ownerWantToBuyAssetsInCurrency > assetsOwnerAccountCash.getAmount()) {
                throw new IllegalArgumentException("At least one of the owners does not have enough money to buy!");
            }
        }
    }
}

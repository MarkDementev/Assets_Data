package fund.data.assets.service.impl;

import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.dto.asset.exchange.FixedRateBondFullSellDTO;
import fund.data.assets.model.asset.exchange.FixedRateBondPackage;
import fund.data.assets.model.asset.relationship.FinancialAssetRelationship;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.model.financial_entities.AccountCash;
import fund.data.assets.model.owner.AssetsOwner;
import fund.data.assets.repository.AccountCashRepository;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.FixedRateBondRepository;
import fund.data.assets.repository.RussianAssetsOwnerRepository;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.service.FixedRateBondService;
import fund.data.assets.utils.FinancialAndAnotherConstants;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.AssetsOwnersCountry;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.HashMap;
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
    /*
    По мере расширения странового охвата, нужно будет здесь расширить перечень разных типов репозиториев
    для оунеров активов, чтобы обращаться к ним в методах класса.
     */
    private final RussianAssetsOwnerRepository russianAssetsOwnerRepository;
    private final AccountCashRepository accountCashRepository;
    private final TurnoverCommissionValueRepository turnoverCommissionValueRepository;

    @Override
    public FixedRateBondPackage getFixedRateBond(Long id) {
        return fixedRateBondRepository.findById(id).orElseThrow();
    }

    @Override
    public List<FixedRateBondPackage> getFixedRateBonds() {
        return fixedRateBondRepository.findAll();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class})
    public FixedRateBondPackage firstBuyFixedRateBond(FirstBuyFixedRateBondDTO firstBuyFixedRateBondDTO) {
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

        FixedRateBondPackage fixedRateBondPackageToCreate = new FixedRateBondPackage(assetCurrency, assetTitle,
                assetCount, assetOwnersWithAssetCounts, accountFromDTO, iSIN, assetIssuerTitle, lastAssetBuyDate,
                bondParValue, purchaseBondParValuePercent, bondAccruedInterest, bondCouponValue,
                expectedBondCouponPaymentsCount, bondMaturityDate);
        AtomicReference<FixedRateBondPackage> atomicFixedRateBondPackage = new AtomicReference<>(
                fixedRateBondPackageToCreate);
        Map<AccountCash, Float> accountCashAmountChanges = formAccountCashAmountChanges(
                atomicFixedRateBondPackage.get(), firstBuyFixedRateBondDTO, accountFromDTO);

        changeAccountCashAmountsOfOwners(accountCashAmountChanges);
        /*
         * Сущность сохраняется, а потом снова сохраняется. Это нужно, чтобы инициализировать поле assetId
         * в сущности AssetRelationship, которое заполняется значением из поля id из сохранённой в БД ранее сущности.
         */
        fixedRateBondRepository.save(atomicFixedRateBondPackage.get());
        atomicFixedRateBondPackage.get().getAssetRelationship().setAssetId(fixedRateBondPackageToCreate.getId());
        return fixedRateBondRepository.save(atomicFixedRateBondPackage.get());
    }

    //TODO раз идёт работа с несколькими сущностями, надо подумать о добавлении уровня изоляции транзакции
    @Override
    public void sellAllPackage(Long id, FixedRateBondFullSellDTO fixedRateBondFullSellDTO) {
        FixedRateBondPackage fixedRateBondPackageToWorkWith = fixedRateBondRepository.findById(id).orElseThrow();
        float correctedPackageSellValue = correctSellValueByCommission(fixedRateBondFullSellDTO,
                fixedRateBondPackageToWorkWith);
        correctedPackageSellValue = correctSellValueByTaxes(correctedPackageSellValue, fixedRateBondFullSellDTO,
                fixedRateBondPackageToWorkWith);
        Map<String, Float> assetOwnersWithAccountCashAmountDiffs = formAssetOwnersWithAccountCashAmountDiffsMap(
                fixedRateBondPackageToWorkWith, correctedPackageSellValue);
        addMoneyToPreviousOwners(assetOwnersWithAccountCashAmountDiffs);

        //ещё должен удалиться релатионшип (наверное, он должен схлопнуться?
        fixedRateBondRepository.deleteById(id);
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
     * Формируется мапа для изменения состояния группы счетов с денежными средствами в процессе создания
     * сущности {@link FixedRateBondPackage}.
     * Параллельно проводится валидация, могут ли собственники активов купить данный пакет облигаций, исходя из наличия
     * денежных средств у себя на счетах (эта информация находится в сущности {@link AccountCash}).
     * @param fixedRateBondPackageToCreate пакет облигаций с фиксированным купоном с общим ISIN, который создаётся.
     * @param firstBuyFixedRateBondDTO DTO пакета облигаций с фиксированным купоном с общим ISIN, который создаётся.
     * @param accountToWorkOn счёт, на котором хранятся нужные денежные средства.
     * @return мапа, где кэй - счет с денежными средствами {@link AccountCash}, вэлью - сумма, на которую надо будет
     * уменьшить соответствующий счёт.
     * @since 0.0.1-alpha
     */
    private Map<AccountCash, Float> formAccountCashAmountChanges(FixedRateBondPackage fixedRateBondPackageToCreate,
                                                                 FirstBuyFixedRateBondDTO firstBuyFixedRateBondDTO,
                                                                 Account accountToWorkOn) {
        Map<AccountCash, Float> accountCashes = new HashMap<>();
        Float totalAssetPurchasePriceWithCommission = fixedRateBondPackageToCreate
                .getTotalAssetPurchasePriceWithCommission();
        Map<String, Float> assetOwnersWithAssetCounts = fixedRateBondPackageToCreate.getAssetRelationship()
                .getAssetOwnersWithAssetCounts();
        AssetCurrency assetCurrency = fixedRateBondPackageToCreate.getAssetCurrency();

        for (Map.Entry<String, Float> mapElement : assetOwnersWithAssetCounts.entrySet()) {
            AssetsOwner assetsOwner;

            /*
            TODO В данный момент (30.08.24) проверка ниже всегда будет верной, т.к. имплементируется поддержка работы
              только с инвесторами с паспортом РФ. В дальнейшем, можно расширить кол-во условий в этой конструкции
               if-else для расширения охвата стран и инвесторов.
             */
            if (firstBuyFixedRateBondDTO.getAssetsOwnersCountry().equals(AssetsOwnersCountry.RUS)) {
                assetsOwner = russianAssetsOwnerRepository.findById(Long.valueOf(mapElement.getKey())).orElseThrow();
            } else {
                throw new IllegalArgumentException("Work with investors from these countries is not yet supported" +
                        " by the fund!");
            }
            AccountCash assetsOwnerAccountCash = accountCashRepository.findByAccountAndAssetCurrencyAndAssetsOwner(
                    accountToWorkOn, assetCurrency, assetsOwner);
            Float ownerWantToBuyAssetsInCurrency = totalAssetPurchasePriceWithCommission
                    * (mapElement.getValue() / Float.valueOf(fixedRateBondPackageToCreate.getAssetCount()));

            if (ownerWantToBuyAssetsInCurrency > assetsOwnerAccountCash.getAmount()) {
                throw new IllegalArgumentException("At least one of the owners does not have enough money to buy!");
            } else {
                accountCashes.put(assetsOwnerAccountCash, ownerWantToBuyAssetsInCurrency);
            }
        }
        return accountCashes;
    }

    /**
     * Суммы на счетах с денежными средствами уменьшаются при создании (покупке) пакета облигаций с фиксированным
     * купоном.
     * @param accountCashAmountChanges мапа, где кэй - счет с денежными средствами {@link AccountCash}, вэлью - сумма,
     * на которую надо будет уменьшить соответствующий счёт.
     * @since 0.0.1-alpha
     */
    private void changeAccountCashAmountsOfOwners(Map<AccountCash, Float> accountCashAmountChanges) {
        for (Map.Entry<AccountCash, Float> mapElement : accountCashAmountChanges.entrySet()) {
            Float mapElementAccountCashAmount = mapElement.getKey().getAmount();

            mapElement.getKey().setAmount(mapElementAccountCashAmount - mapElement.getValue());
        }
    }

    /**
     * При полной продаже пакета определяем, платится ли комиссия. Если да - уменьшаем продажную сумму и возвращаем её,
     * если нет - возвращаем без изменений.
     * @param fixedRateBondFullSellDTO DTO с информаций о продажной стоимости при полной продаже пакета облигаций
     * с фиксированным купоном.
     * @param fixedRateBondPackageToWorkWith пакет облигаций, для которого проводятся расчёты.
     * @return скорректированная или оставленная без изменений сумма.
     * @since 0.0.1-alpha
     */
    private Float correctSellValueByCommission(FixedRateBondFullSellDTO fixedRateBondFullSellDTO,
                                               FixedRateBondPackage fixedRateBondPackageToWorkWith) {
        float packageSellValue = fixedRateBondFullSellDTO.getPackageSellValue();

        if (fixedRateBondPackageToWorkWith.getAssetCommissionSystem().equals(CommissionSystem.TURNOVER)) {
            FinancialAssetRelationship financialAssetRelationship
                    = (FinancialAssetRelationship) fixedRateBondPackageToWorkWith.getAssetRelationship();
            Account account = financialAssetRelationship.getAccount();
            String assetTypeName = fixedRateBondPackageToWorkWith.getAssetTypeName();
            float commissionPercentValue = turnoverCommissionValueRepository.findByAccountAndAssetTypeName(account,
                    assetTypeName).getCommissionPercentValue();
            packageSellValue = packageSellValue - packageSellValue * commissionPercentValue;
        }
        return packageSellValue;
    }

    /**
     * При полной продаже пакета определяем, платится ли НДФЛ. Если да - уменьшаем продажную сумму и возвращаем её,
     * если нет - возвращаем без изменений.
     * @param correctSellValueByCommission продажная цена пакета бумаг, ранее уже возможно уменьшенная на комиссию
     * при продаже.
     * @param fixedRateBondFullSellDTO DTO с информаций о налоговом резидентстве при полной продаже пакета облигаций.
     * @param fixedRateBondPackageToWorkWith пакет облигаций, для которого проводятся расчёты.
     * @return скорректированная или оставленная без изменений сумма.
     * @since 0.0.1-alpha
     */
    private Float correctSellValueByTaxes(float correctSellValueByCommission,
                                          FixedRateBondFullSellDTO fixedRateBondFullSellDTO,
                                          FixedRateBondPackage fixedRateBondPackageToWorkWith) {
        if (fixedRateBondPackageToWorkWith.getAssetTaxSystem().equals(TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE)) {
            /* TODO В данный момент (04.09.24) проверка ниже всегда будет верной, т.к. имплементируется поддержка
                 работы только налоговых резидентов РФ. В дальнейшем, можно расширить кол-во условий в этой конструкции
                  if-else для расширения охвата стран и инвесторов.
             */
            if (fixedRateBondFullSellDTO.getAssetsOwnersTaxResidency().equals(AssetsOwnersCountry.RUS)) {
                float diffBetweenSellBuyCommissions = correctSellValueByCommission
                        - fixedRateBondPackageToWorkWith.getTotalAssetPurchasePriceWithCommission();

                correctSellValueByCommission = diffBetweenSellBuyCommissions > 0 ? correctSellValueByCommission
                        - (1.00F - FinancialAndAnotherConstants.RUSSIAN_TAX_SYSTEM_CORRECTION_VALUE)
                        * diffBetweenSellBuyCommissions : correctSellValueByCommission;
            }
        }
        return correctSellValueByCommission;
    }

    /**
     * Формируется мапа при продаже пакета облигаций для распределения денежных средств между бывшими собственниками.
     * @param fixedRateBondPackageToWorkWith пакет облигаций, для которого проводятся расчёты.
     * @param sellValue продажная цена пакета бумаг, возможно, скорректированная ранее на налог и комиссии.
     * @return мапа, где кей - id оунеров продаваемых облигаций, вэлью - размер их доли в валюте от
     * суммы продажи пакета.
     * @since 0.0.1-alpha
     */
    private Map<String, Float> formAssetOwnersWithAccountCashAmountDiffsMap(
            FixedRateBondPackage fixedRateBondPackageToWorkWith, float sellValue) {
        Integer assetCount = fixedRateBondPackageToWorkWith.getAssetCount();
        Map<String, Float> assetOwnersWithAssetCounts = fixedRateBondPackageToWorkWith.getAssetRelationship()
                .getAssetOwnersWithAssetCounts();

        for (Map.Entry<String, Float> mapElement : assetOwnersWithAssetCounts.entrySet()) {
                Float ownerAssetCount = mapElement.getValue();
                Float ownerAccountCashDiff = (ownerAssetCount / assetCount) * sellValue;

                mapElement.setValue(ownerAccountCashDiff);
        }
        return assetOwnersWithAssetCounts;
    }

    //TODO докуха
    private void addMoneyToPreviousOwners(Map<String, Float> assetOwnersWithAccountCashAmountDiffs) {
        for (Map.Entry<String, Float> mapElement : assetOwnersWithAccountCashAmountDiffs.entrySet()) {
            Long assetsOwnerId = Long.parseLong(mapElement.getKey());


        }
    }
}

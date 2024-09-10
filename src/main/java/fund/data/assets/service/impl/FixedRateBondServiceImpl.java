package fund.data.assets.service.impl;

import fund.data.assets.dto.asset.exchange.AssetsOwnersCountryDTO;
import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.dto.asset.exchange.FixedRateBondFullSellDTO;
import fund.data.assets.model.asset.exchange.FixedRateBondPackage;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.model.financial_entities.AccountCash;
import fund.data.assets.model.owner.AssetsOwner;
import fund.data.assets.model.owner.RussianAssetsOwner;
import fund.data.assets.repository.AccountCashRepository;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.FixedRateBondRepository;
import fund.data.assets.repository.RussianAssetsOwnerRepository;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.repository.FinancialAssetRelationshipRepository;
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
import java.util.TreeMap;
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
    private static final String ERROR_OWNER_COUNTRY = "Work with investors from these countries is not yet supported"
            + "by the fund!";
    private static final String ERROR_DATE_REDEEM = "The bonds haven't matured yet, it's too early to write them down!";
    private final FixedRateBondRepository fixedRateBondRepository;
    private final AccountRepository accountRepository;
    /* TODO - По мере расширения странового охвата, нужно будет здесь расширить перечень разных типов репозиториев
         для оунеров активов, чтобы обращаться к ним в методах класса. Также в методах расширится перечень условиях
         в некоторых конструкциях if-else.
     */
    private final RussianAssetsOwnerRepository russianAssetsOwnerRepository;
    private final AccountCashRepository accountCashRepository;
    private final TurnoverCommissionValueRepository turnoverCommissionValueRepository;
    private final FinancialAssetRelationshipRepository financialAssetRelationshipRepository;

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
        Map<AccountCash, Float> accountCashAmountChanges = formAccountCashAmountChanges(firstBuyFixedRateBondDTO,
                atomicFixedRateBondPackage.get(), accountFromDTO);

        changeAccountCashAmountsOfOwners(accountCashAmountChanges);
        /*
         * Сущность сохраняется, а потом снова сохраняется. Это нужно, чтобы инициализировать поле assetId
         * в сущности AssetRelationship, которое заполняется значением из поля id из сохранённой в БД ранее сущности.
         */
        fixedRateBondRepository.save(atomicFixedRateBondPackage.get());
        atomicFixedRateBondPackage.get().getAssetRelationship().setAssetId(fixedRateBondPackageToCreate.getId());
        return fixedRateBondRepository.save(atomicFixedRateBondPackage.get());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public void sellAllPackage(Long id, FixedRateBondFullSellDTO fixedRateBondFullSellDTO) {
        AtomicReference<FixedRateBondPackage> atomicFixedRateBondPackage = new AtomicReference<>(
                fixedRateBondRepository.findById(id).orElseThrow());
        float correctedPackageSellValue = correctSellValueByCommission(fixedRateBondFullSellDTO,
                atomicFixedRateBondPackage.get());
        correctedPackageSellValue = correctValueByTaxes(fixedRateBondFullSellDTO, atomicFixedRateBondPackage.get(),
                correctedPackageSellValue);
        Map<String, Float> assetOwnersWithAccountCashAmountDiffs = formAssetOwnersWithAccountCashAmountDiffsMap(
                atomicFixedRateBondPackage.get(), correctedPackageSellValue);

        addMoneyToPreviousOwners(fixedRateBondFullSellDTO, atomicFixedRateBondPackage.get(),
                assetOwnersWithAccountCashAmountDiffs);
        fixedRateBondRepository.deleteById(id);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public void redeemBonds(Long id, AssetsOwnersCountryDTO assetsOwnersCountryDTO) {
        AtomicReference<FixedRateBondPackage> atomicFixedRateBondPackage = new AtomicReference<>(
                fixedRateBondRepository.findById(id).orElseThrow());

        if (LocalDate.now().toEpochDay() < atomicFixedRateBondPackage.get().getBondMaturityDate().toEpochDay()) {
            throw new IllegalArgumentException(ERROR_DATE_REDEEM);
        }

        float redeemedBondsParValuesSum = atomicFixedRateBondPackage.get().getBondParValue()
                * atomicFixedRateBondPackage.get().getAssetCount();
        float correctedByTaxesPackageRedeemValue = correctValueByTaxes(assetsOwnersCountryDTO,
                atomicFixedRateBondPackage.get(), redeemedBondsParValuesSum);
        Map<String, Float> assetOwnersWithAccountCashAmountDiffs = formAssetOwnersWithAccountCashAmountDiffsMap(
                atomicFixedRateBondPackage.get(), correctedByTaxesPackageRedeemValue);

        addMoneyToPreviousOwners(assetsOwnersCountryDTO, atomicFixedRateBondPackage.get(),
                assetOwnersWithAccountCashAmountDiffs);
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
     * @param firstBuyFixedRateBondDTO DTO пакета облигаций с фиксированным купоном с общим ISIN, который создаётся.
     * @param fixedRateBondPackageToCreate пакет облигаций с фиксированным купоном с общим ISIN, который создаётся.
     * @param accountToWorkOn счёт, на котором хранятся нужные денежные средства.
     * @return мапа, где кэй - счет с денежными средствами {@link AccountCash}, вэлью - сумма, на которую надо будет
     * уменьшить соответствующий счёт.
     * @since 0.0.1-alpha
     */
    private Map<AccountCash, Float> formAccountCashAmountChanges(FirstBuyFixedRateBondDTO firstBuyFixedRateBondDTO,
                                                                 FixedRateBondPackage fixedRateBondPackageToCreate,
                                                                 Account accountToWorkOn) {
        Map<AccountCash, Float> accountCashes = new HashMap<>();
        Float totalAssetPurchasePriceWithCommission = fixedRateBondPackageToCreate
                .getTotalAssetPurchasePriceWithCommission();
        Map<String, Float> assetOwnersWithAssetCounts = fixedRateBondPackageToCreate.getAssetRelationship()
                .getAssetOwnersWithAssetCounts();
        AssetCurrency assetCurrency = fixedRateBondPackageToCreate.getAssetCurrency();

        for (Map.Entry<String, Float> mapElement : assetOwnersWithAssetCounts.entrySet()) {
            AssetsOwner assetsOwner;

            if (firstBuyFixedRateBondDTO.getAssetsOwnersCountry().equals(AssetsOwnersCountry.RUS)) {
                assetsOwner = russianAssetsOwnerRepository.findById(Long.valueOf(mapElement.getKey())).orElseThrow();
            } else {
                throw new IllegalArgumentException(ERROR_OWNER_COUNTRY);
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
     * @param dTO DTO с информаций о гражданстве собственников активов и о продажной стоимости при полной продаже
     * пакета облигаций с фиксированным купоном.
     * @param fixedRateBondPackageToWorkWith пакет облигаций, для которого проводятся расчёты.
     * @return скорректированная или оставленная без изменений сумма.
     * @since 0.0.1-alpha
     */
    private Float correctSellValueByCommission(FixedRateBondFullSellDTO dTO ,
                                               FixedRateBondPackage fixedRateBondPackageToWorkWith) {
        float packageSellValue = dTO.getPackageSellValue();

        if (fixedRateBondPackageToWorkWith.getAssetCommissionSystem().equals(CommissionSystem.TURNOVER)) {
            Account account = financialAssetRelationshipRepository.findById(
                    fixedRateBondPackageToWorkWith.getAssetRelationship().getId()).orElseThrow().getAccount();
            String assetTypeName = fixedRateBondPackageToWorkWith.getAssetTypeName();
            float commissionPercentValue = turnoverCommissionValueRepository.findByAccountAndAssetTypeName(account,
                    assetTypeName).getCommissionPercentValue();
            packageSellValue = packageSellValue - packageSellValue * commissionPercentValue;
        }
        return packageSellValue;
    }

    /**
     * При полной продаже пакета или при его погашении определяем, платится ли НДФЛ. Если да - уменьшаем получаемую
     * сумму и возвращаем её, если нет - возвращаем без изменений.
     * @param dTO DTO с информаций о гражданстве собственников активов, также может содержать информацию о продажной
     * стоимости при полной продаже пакета облигаций с фиксированным купоном.
     * @param fixedRateBondPackageToWorkWith пакет облигаций, для которого проводятся расчёты.
     * @param valueToCorrect возвращаемая цена пакета бумаг, ранее уже, возможно, уменьшенная на комиссию при продаже.
     * @return скорректированная или оставленная без изменений сумма.
     * @since 0.0.1-alpha
     */
    private Float correctValueByTaxes(AssetsOwnersCountryDTO dTO,
                                      FixedRateBondPackage fixedRateBondPackageToWorkWith, float valueToCorrect) {
        if (fixedRateBondPackageToWorkWith.getAssetTaxSystem().equals(TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE)) {
            if (dTO.getAssetsOwnersTaxResidency().equals(AssetsOwnersCountry.RUS)) {
                float diffBetweenSellBuyCommissions = valueToCorrect
                        - fixedRateBondPackageToWorkWith.getTotalAssetPurchasePriceWithCommission();

                valueToCorrect = diffBetweenSellBuyCommissions > 0 ?
                        valueToCorrect - (1.00F - FinancialAndAnotherConstants.RUSSIAN_TAX_SYSTEM_CORRECTION_VALUE)
                        * diffBetweenSellBuyCommissions
                        : valueToCorrect;
            } else {
                throw new IllegalArgumentException(ERROR_OWNER_COUNTRY);
            }
        }
        return valueToCorrect;
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
        Map<String, Float> assetOwnersWithAccountCashAmountDiffsMap = new TreeMap<>();

        for (Map.Entry<String, Float> mapElement : assetOwnersWithAssetCounts.entrySet()) {
                Float ownerAssetCount = mapElement.getValue();
                Float ownerAccountCashDiff = (ownerAssetCount / assetCount) * sellValue;

                assetOwnersWithAccountCashAmountDiffsMap.put(mapElement.getKey(), ownerAccountCashDiff);
        }
        return assetOwnersWithAccountCashAmountDiffsMap;
    }

    /**
     * Перед удалением пакета облигаций бывшие собственники получают на счета денежные средства от продажи.
     * @param dTO DTO с информаций о налоговом резидентстве при полной продаже пакета облигаций.
     * @param fixedRateBondPackageToWorkWith пакет облигаций, для которого проводятся расчёты.
     * @param assetOwnersWithAccountCashAmountDiffs мапа, где кей - id оунеров продаваемых облигаций, вэлью - размер их
     * доли в валюте от суммы продажи пакета.
     * @since 0.0.1-alpha
     */
    private void addMoneyToPreviousOwners(AssetsOwnersCountryDTO dTO,
                                          FixedRateBondPackage fixedRateBondPackageToWorkWith,
                                          Map<String, Float> assetOwnersWithAccountCashAmountDiffs) {
        for (Map.Entry<String, Float> mapElement : assetOwnersWithAccountCashAmountDiffs.entrySet()) {
            Account account = financialAssetRelationshipRepository.findById(
                    fixedRateBondPackageToWorkWith.getAssetRelationship().getId()).orElseThrow().getAccount();
            AssetCurrency assetCurrency = fixedRateBondPackageToWorkWith.getAssetCurrency();
            RussianAssetsOwner russianAssetsOwner;

            if (dTO.getAssetsOwnersTaxResidency().equals(AssetsOwnersCountry.RUS)) {
                russianAssetsOwner = russianAssetsOwnerRepository.findById(Long.parseLong(mapElement.getKey()))
                        .orElseThrow();
            } else {
                throw new IllegalArgumentException(ERROR_OWNER_COUNTRY);
            }
            AccountCash accountCash = accountCashRepository.findByAccountAndAssetCurrencyAndAssetsOwner(account,
                    assetCurrency, russianAssetsOwner);
            accountCash.setAmount(accountCash.getAmount() + mapElement.getValue());
        }
    }
}

package fund.data.assets.service.impl;

import fund.data.assets.dto.asset.exchange.AssetsOwnersCountryDTO;
import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.dto.asset.exchange.FixedRateBondFullSellDTO;
import fund.data.assets.dto.asset.exchange.FixedRateBondPartialSellDTO;
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

import java.util.LinkedHashMap;
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
    private static final String ERROR_DATE_REDEEM = "The bonds haven't matured yet, it's too early to write them down!";
    private static final String SUM_ASSET_COUNT_NOT_EQUALS = "Field assetOwnersWithAssetCounts in DTO is not"
            + " correct - sum of values are not equals field assetCount!";
    private static final String ERROR_OWNER_COUNTRY = "Work with investors from these countries is not yet supported"
            + " by the fund!";
    private static final String ONE_OWNER_NOT_ENOUGH_MONEY = "At least one of the owners does not have enough money"
            + " to buy!";
    private static final String ONE_OWNER_CANNOT_SELL_SO_MUCH = "At least one assets owner cannot sell so many bonds!";
    private static final String ERROR_WRONG_DTO = "DTO has an invalid class!";
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
    //TODO После имплементации и окончания тестирования partialSellFixedRateBondPackage проведи инвентаризацию методов!
    //TODO После имплементации и окончания тестирования partialSellFixedRateBondPackage переименуй DTO!

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
        isOwnershipMapValuesSumEqualsAssetCount(firstBuyFixedRateBondDTO.getAssetOwnersWithAssetCounts(),
                firstBuyFixedRateBondDTO.getAssetCount());

        FixedRateBondPackage fixedRateBondPackageToCreate = getNewFixedRateBondByDTO(firstBuyFixedRateBondDTO);
        AtomicReference<FixedRateBondPackage> atomicFixedRateBondPackage = new AtomicReference<>(
                fixedRateBondPackageToCreate);
        Map<AccountCash, Float> accountCashAmountChanges = formAccountCashAmountChangesMap(firstBuyFixedRateBondDTO,
                atomicFixedRateBondPackage.get());

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
    public FixedRateBondPackage partialSellFixedRateBondPackage(Long id,
        FixedRateBondPartialSellDTO fixedRateBondPartialSellDTO) {
        AtomicReference<FixedRateBondPackage> atomicFixedRateBondPackage = new AtomicReference<>(
                fixedRateBondRepository.findById(id).orElseThrow());

        isAssetsOwnersHaveThisAssetsAmounts(atomicFixedRateBondPackage.get(), fixedRateBondPartialSellDTO);

        float packagePartSellValue = fixedRateBondPartialSellDTO.getPackageSellValue();
        packagePartSellValue = correctSellValueByCommission(packagePartSellValue, atomicFixedRateBondPackage.get());
        packagePartSellValue = correctValueByTaxes(fixedRateBondPartialSellDTO, atomicFixedRateBondPackage.get(),
                packagePartSellValue);
        Map<String, Float> ownersMoneyDistribution = formOwnersMoneyDistributionMap(atomicFixedRateBondPackage.get(),
                packagePartSellValue, fixedRateBondPartialSellDTO);

        addMoneyToPreviousOwners(fixedRateBondPartialSellDTO, atomicFixedRateBondPackage.get(),
                ownersMoneyDistribution);
        updateAssetOwnersWithAssetCounts(atomicFixedRateBondPackage.get(), fixedRateBondPartialSellDTO, true);
        //TODO надо поменять многие значения в самом бонде!
        /*
            lastAssetBuyDate - ExchangeAsset - м.б. удалить и заменить везде на креэйтэд эт/апдейтед эт?
            bondAccruedInterest - FixedRateBondPackage
            totalCommissionForPurchase - FixedRateBondPackage
            totalAssetPurchasePriceWithCommission - FixedRateBondPackage
            expectedBondCouponPaymentsCount - FixedRateBondPackage
            simpleYieldToMaturity - FixedRateBondPackage
            markDementevYieldIndicator - FixedRateBondPackage
         */
//        updateFixedRateBondFields();
        return fixedRateBondRepository.save(atomicFixedRateBondPackage.get());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public void sellAllPackage(Long id, FixedRateBondFullSellDTO fixedRateBondFullSellDTO) {
        AtomicReference<FixedRateBondPackage> atomicFixedRateBondPackage = new AtomicReference<>(
                fixedRateBondRepository.findById(id).orElseThrow());
        float packageSellValue = fixedRateBondFullSellDTO.getPackageSellValue();
        packageSellValue = correctSellValueByCommission(packageSellValue, atomicFixedRateBondPackage.get());
        packageSellValue = correctValueByTaxes(fixedRateBondFullSellDTO, atomicFixedRateBondPackage.get(),
                packageSellValue);
        Map<String, Float> ownersMoneyDistribution = formOwnersMoneyDistributionMap(atomicFixedRateBondPackage.get(),
                packageSellValue, fixedRateBondFullSellDTO);

        addMoneyToPreviousOwners(fixedRateBondFullSellDTO, atomicFixedRateBondPackage.get(),
                ownersMoneyDistribution);
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
        Map<String, Float> ownersMoneyDistribution = formOwnersMoneyDistributionMap(atomicFixedRateBondPackage.get(),
                correctedByTaxesPackageRedeemValue, assetsOwnersCountryDTO);

        addMoneyToPreviousOwners(assetsOwnersCountryDTO, atomicFixedRateBondPackage.get(),
                ownersMoneyDistribution);
        fixedRateBondRepository.deleteById(id);
    }

    /**
     * Возвращается новый объект FixedRateBondPackage с использованием данных из DTO класса FirstBuyFixedRateBondDTO.
     * @param firstBuyFixedRateBondDTO DTO с данными для создания нового объекта.
     * @return новый объект FixedRateBondPackage.
     */
    private FixedRateBondPackage getNewFixedRateBondByDTO(FirstBuyFixedRateBondDTO firstBuyFixedRateBondDTO) {
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

        return new FixedRateBondPackage(assetCurrency, assetTitle, assetCount, assetOwnersWithAssetCounts,
                accountFromDTO, iSIN, assetIssuerTitle, lastAssetBuyDate, bondParValue, purchaseBondParValuePercent,
                bondAccruedInterest, bondCouponValue, expectedBondCouponPaymentsCount, bondMaturityDate);
    }

    /**
     * Проводится валидация - сумма выльюс в assetOwnersWithAssetCounts, приведённая к Integer, должна быть равна
     * значению assetCount. Аргументы метода берутся из DTO {@link FirstBuyFixedRateBondDTO}.
     @param assetOwnersWithAssetCounts мапа, где кей - это владелец актива, вэлью - количество ценных бумаг оунера
     в рамках данного пакета ценных бумаг.
     @param assetCount количество бумаг в пакете ценных бумаг.
     @since 0.0.1-alpha
     */
    private void isOwnershipMapValuesSumEqualsAssetCount(Map<String, Float> assetOwnersWithAssetCounts,
                                                         Integer assetCount) {
        Float mapValuesSum = 0.0F;

        for (Map.Entry<String, Float> mapElement : assetOwnersWithAssetCounts.entrySet()) {
            mapValuesSum += mapElement.getValue();
        }
        Integer mapValuesSumToInteger = mapValuesSum.intValue();

        if ((Math.ceil(mapValuesSum) != Math.floor(mapValuesSum)) || (!mapValuesSumToInteger.equals(assetCount))) {
            throw new IllegalArgumentException(SUM_ASSET_COUNT_NOT_EQUALS);
        }
    }

    /**
     * Формируется мапа для изменения состояния группы счетов с денежными средствами в процессе создания
     * сущности {@link FixedRateBondPackage}.
     * Параллельно проводится валидация, могут ли собственники активов купить данный пакет облигаций, исходя из наличия
     * денежных средств у себя на счетах (эта информация находится в сущности {@link AccountCash}).
     * @param dTO DTO пакета облигаций с фиксированным купоном с общим ISIN, который создаётся.
     * @param fixedRateBondPackage пакет облигаций с фиксированным купоном с общим ISIN, который создаётся.
     * @return мапа, где кэй - счет с денежными средствами {@link AccountCash}, вэлью - сумма, на которую надо будет
     * уменьшить соответствующий счёт.
     * @since 0.0.1-alpha
     */
    //TODO переименуй и отрефактори метод
    private Map<AccountCash, Float> formAccountCashAmountChangesMap(FirstBuyFixedRateBondDTO dTO,
                                                                    FixedRateBondPackage fixedRateBondPackage) {
        Map<String, Float> assetOwnersWithAssetCounts = fixedRateBondPackage.getAssetRelationship()
                .getAssetOwnersWithAssetCounts();
        Account account = ((FinancialAssetRelationship) fixedRateBondPackage.getAssetRelationship()).getAccount();
        AssetCurrency assetCurrency = fixedRateBondPackage.getAssetCurrency();
        Float totalAssetPurchasePriceWithCommission = fixedRateBondPackage.getTotalAssetPurchasePriceWithCommission();
        Map<AccountCash, Float> accountCashes = new LinkedHashMap<>();

        for (Map.Entry<String, Float> mapElement : assetOwnersWithAssetCounts.entrySet()) {
            AssetsOwner assetsOwner;

            if (dTO.getAssetsOwnersCountry().equals(AssetsOwnersCountry.RUS)) {
                assetsOwner = russianAssetsOwnerRepository.findById(Long.valueOf(mapElement.getKey())).orElseThrow();
            } else {
                throw new IllegalArgumentException(ERROR_OWNER_COUNTRY);
            }
            AccountCash assetsOwnerAccountCash = accountCashRepository.findByAccountAndAssetCurrencyAndAssetsOwner(
                    account, assetCurrency, assetsOwner);
            Float ownerWantToBuyAssetsInCurrency = totalAssetPurchasePriceWithCommission
                    * (mapElement.getValue() / Float.valueOf(fixedRateBondPackage.getAssetCount()));

            if (ownerWantToBuyAssetsInCurrency > assetsOwnerAccountCash.getAmount()) {
                throw new IllegalArgumentException(ONE_OWNER_NOT_ENOUGH_MONEY);
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
    //TODO возможно, надо переписать полиморфизма для! Добавить какой-то модификатор, чтобы и уменьшал, и увеличивал
    //данный метод
    private void changeAccountCashAmountsOfOwners(Map<AccountCash, Float> accountCashAmountChanges) {
        for (Map.Entry<AccountCash, Float> mapElement : accountCashAmountChanges.entrySet()) {
            Float mapElementAccountCashAmount = mapElement.getKey().getAmount();

            mapElement.getKey().setAmount(mapElementAccountCashAmount - mapElement.getValue());
        }
    }

//    /**
//     * Значения на счетах с денежными средствами изменяются на определённые значения либо в сторону увеличения, либо
//     * в сторону уменьшения. Сторона определяется вторым аргументом метода. Если True - то вэльюс из мапы - первого
//     * аргумента - увеличивают значения, если False - то уменьшают.
//     * @param accountCashAmountChanges мапа, где кэй - счет с денежными средствами {@link AccountCash}, вэлью - сумма,
//     * на которую надо будет изменить соответствующий счёт.
//     * @param isSum аргумент, определяющий, будут ли значения увеличиваться или уменьшаться.
//     * @since 0.0.1-alpha
//     */
//    private void changeAccountCashAmountsOfOwners(Map<AccountCash, Float> accountCashAmountChanges, boolean isSum) {
//        for (Map.Entry<AccountCash, Float> mapElement : accountCashAmountChanges.entrySet()) {
//            Float mapElementAccountCashAmount = mapElement.getKey().getAmount();
//
//            if (isSum) {
//                mapElement.getKey().setAmount(mapElementAccountCashAmount + mapElement.getValue());
//            } else {
//                mapElement.getKey().setAmount(mapElementAccountCashAmount - mapElement.getValue());
//            }
//        }
//    }

    /**
     * Проводится валидация, все ли владельцы активов могут продать данное количество облигаций.
     * @param fixedRateBondPackage пакет облигаций, для которого проводится валидация.
     * @param dTO DTO для обслуживания внесения в систему данных о продаже части облигаций из пакета бумаг выпуска
     облигаций.
     * @since 0.0.1-alpha
     */
    private void isAssetsOwnersHaveThisAssetsAmounts(FixedRateBondPackage fixedRateBondPackage,
                                                     FixedRateBondPartialSellDTO dTO) {
        Map<String, Integer> assetOwnersWithAssetCountsToSell = dTO.getAssetOwnersWithAssetCountsToSell();

        for (Map.Entry<String, Integer> mapElement : assetOwnersWithAssetCountsToSell.entrySet()) {
            Float assetsOwnerAssetCount = financialAssetRelationshipRepository.findById(
                    fixedRateBondPackage.getAssetRelationship().getId()).orElseThrow()
                    .getAssetOwnersWithAssetCounts().get(mapElement.getKey());
            Integer assetToSellCount = mapElement.getValue();

            if (assetsOwnerAssetCount < assetToSellCount) {
                throw new IllegalArgumentException(ONE_OWNER_CANNOT_SELL_SO_MUCH);
            }
        }
    }

    /**
     * При полной или частичной продаже пакета определяем, платится ли комиссия. Если да - уменьшаем продажную сумму
     * и возвращаем её, если нет - возвращаем без изменений.
     * @param sellValue продажная цена облигаций в валюте эмиссии.
     * @param fixedRateBondPackage пакет облигаций, для работы с которым проводятся расчёты.
     * @return скорректированная или оставленная без изменений сумма.
     * @since 0.0.1-alpha
     */
    private Float correctSellValueByCommission(float sellValue, FixedRateBondPackage fixedRateBondPackage) {
        if (fixedRateBondPackage.getAssetCommissionSystem().equals(CommissionSystem.TURNOVER)) {
            Account account = financialAssetRelationshipRepository.findById(
                    fixedRateBondPackage.getAssetRelationship().getId())
                    .orElseThrow().getAccount();
            String assetTypeName = fixedRateBondPackage.getAssetTypeName();
            float commissionPercentValue = turnoverCommissionValueRepository.findByAccountAndAssetTypeName(account,
                    assetTypeName).getCommissionPercentValue();
            sellValue = sellValue - sellValue * commissionPercentValue;
        }
        return sellValue;
    }

    /**
     * При полной, частичной продаже пакета или при его погашении определяем, платится ли НДФЛ. Если
     * да - уменьшаем получаемую сумму и возвращаем её, если нет - возвращаем без изменений.
     * @param dTO DTO с информаций о гражданстве собственников активов.
     * @param fixedRateBondPackage пакет облигаций, для работы с которым проводятся расчёты.
     * @param valueToCorrect возвращаемая цена пакета бумаг с возможной коррекцией.
     * @return скорректированная на НДФЛ или оставленная без изменений сумма.
     * @since 0.0.1-alpha
     */
    private Float correctValueByTaxes(AssetsOwnersCountryDTO dTO, FixedRateBondPackage fixedRateBondPackage,
                                      float valueToCorrect) {
        if (fixedRateBondPackage.getAssetTaxSystem().equals(TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE)) {
            if (dTO.getAssetsOwnersTaxResidency().equals(AssetsOwnersCountry.RUS)) {
                float valueToCompared = getValueToCompared(dTO, fixedRateBondPackage);
                float diffBetweenSellAndBuyCommissions = valueToCorrect - valueToCompared;
                valueToCorrect = diffBetweenSellAndBuyCommissions > 0 ?
                        valueToCorrect - (1.00F - FinancialAndAnotherConstants.RUSSIAN_TAX_SYSTEM_CORRECTION_VALUE)
                        * diffBetweenSellAndBuyCommissions
                        : valueToCorrect;
            } else {
                throw new IllegalArgumentException(ERROR_OWNER_COUNTRY);
            }
        }
        return valueToCorrect;
    }

    /**
     * В зависимости от того, идёт продажа всего пакета облигаций или только его части, возвращается определённая
     * пропорция от общей стоимости пакета с НКД и комиссиями.
     * @param dTO DTO для выбора одного из алгоритмов расчёта размера возвращаемой пропорции.
     * @param fixedRateBondPackage пакет облигаций, для работы с которым проводятся расчёты.
     * @return определённая пропорция от общей стоимости пакета с НКД и комиссиями.
     * @since 0.0.1-alpha
     */
    private float getValueToCompared(AssetsOwnersCountryDTO dTO, FixedRateBondPackage fixedRateBondPackage) {
        if (dTO.getClass().equals(AssetsOwnersCountryDTO.class)
                || dTO.getClass().equals(FixedRateBondFullSellDTO.class)) {
            return fixedRateBondPackage.getTotalAssetPurchasePriceWithCommission();
        } else if (dTO.getClass().equals(FixedRateBondPartialSellDTO.class)) {
            int bondsToSellCount = 0;

            for (Map.Entry<String, Integer> mapElement :
                    ((FixedRateBondPartialSellDTO) dTO).getAssetOwnersWithAssetCountsToSell().entrySet()) {
                bondsToSellCount += mapElement.getValue();
            }
            return fixedRateBondPackage.getTotalAssetPurchasePriceWithCommission() * ((float) bondsToSellCount
                    / fixedRateBondPackage.getAssetCount());
        }
        throw new IllegalArgumentException(ERROR_WRONG_DTO);
    }

    /**
     * Формируется мапа при полной или частичной продаже пакета облигаций для распределения денежных средств между
     * бывшими собственниками.
     * @param fixedRateBondPackage пакет облигаций, для которого проводятся расчёты.
     * @param sellValue продажная цена пакета бумаг, возможно, скорректированная ранее на налог и комиссии.
     * @param dTO DTO с информацией о том, какие собственники и по сколько облигаций продают.
     * @return мапа, где кей - id оунеров продаваемых облигаций, вэлью - размер их доли в валюте эмиссии от
     * суммы продажи пакета.
     * @since 0.0.1-alpha
     */
    private Map<String, Float> formOwnersMoneyDistributionMap(FixedRateBondPackage fixedRateBondPackage,
                                                              float sellValue, AssetsOwnersCountryDTO dTO) {
        int assetCountToSell;
        Map<String, ? extends Number> assetOwnersWithAssetCounts;
        Map<String, Float> ownersMoneyDistributionMap = new TreeMap<>();

        if (dTO.getClass().equals(FixedRateBondPartialSellDTO.class)) {
            assetCountToSell = getAssetCountToSellSumFromDTO((FixedRateBondPartialSellDTO) dTO);
            assetOwnersWithAssetCounts = ((FixedRateBondPartialSellDTO) dTO).getAssetOwnersWithAssetCountsToSell();
        } else {
            assetCountToSell = fixedRateBondPackage.getAssetCount();
            assetOwnersWithAssetCounts = fixedRateBondPackage.getAssetRelationship().getAssetOwnersWithAssetCounts();
        }

        for (Map.Entry<String, ? extends Number> mapElement : assetOwnersWithAssetCounts.entrySet()) {
            float ownerAssetCountToSell = mapElement.getValue().floatValue();
            Float ownerAccountCashDiff = (ownerAssetCountToSell / assetCountToSell) * sellValue;

            ownersMoneyDistributionMap.put(mapElement.getKey(), ownerAccountCashDiff);
        }
        return ownersMoneyDistributionMap;
    }

    /**
     * При частичной продаже пакета облигаций необходимо получить общее количество продаваемых облигаций из DTO. Этот
     * метод позволяет калькулировать и получить данное значение.
     * @param dTO DTO с информацией о количестве продаваемых облигаций и о том, кто их продаёт.
     * @return общее количество продаваемых облигаций.
     * @since 0.0.1-alpha
     */
    private int getAssetCountToSellSumFromDTO(FixedRateBondPartialSellDTO dTO) {
        int assetCountToSell = 0;

        for (Map.Entry<String, Integer> mapElement : dTO.getAssetOwnersWithAssetCountsToSell().entrySet()) {
            assetCountToSell += mapElement.getValue();
        }
        return assetCountToSell;
    }

    /**
     * При полной или частичной продаже пакета облигаций бывшие собственники получают на счета денежные средства.
     * @param dTO DTO с информаций о налоговом резидентстве бывших владельцев бумаг.
     * @param fixedRateBondPackage пакет облигаций, для которого проводятся расчёты.
     * @param assetOwnersWithAccountCashAmountDiffs мапа, где кей - id оунеров продаваемых облигаций, вэлью - размер их
     * доли в валюте от суммы продажи пакета.
     * @since 0.0.1-alpha
     */
    private void addMoneyToPreviousOwners(AssetsOwnersCountryDTO dTO, FixedRateBondPackage fixedRateBondPackage,
                                          Map<String, Float> assetOwnersWithAccountCashAmountDiffs) {
        for (Map.Entry<String, Float> mapElement : assetOwnersWithAccountCashAmountDiffs.entrySet()) {
            Account account = financialAssetRelationshipRepository.findById(
                    fixedRateBondPackage.getAssetRelationship().getId()).orElseThrow().getAccount();
            AssetCurrency assetCurrency = fixedRateBondPackage.getAssetCurrency();
            AssetsOwner assetsOwner;

            if (dTO.getAssetsOwnersTaxResidency().equals(AssetsOwnersCountry.RUS)) {
                assetsOwner = russianAssetsOwnerRepository.findById(Long.parseLong(mapElement.getKey())).orElseThrow();
            } else {
                throw new IllegalArgumentException(ERROR_OWNER_COUNTRY);
            }
            AccountCash accountCash = accountCashRepository.findByAccountAndAssetCurrencyAndAssetsOwner(account,
                    assetCurrency, assetsOwner);
            accountCash.setAmount(accountCash.getAmount() + mapElement.getValue());
        }
    }

    /**
     * При частичных продаже и покупке облигаций надо уменьшать количество облигаций во владении, для чего используется
     * этот метод.
     * @param fixedRateBondPackage пакет облигаций, который затрагивают изменения.
     * @param dTO DTO с информацией о том, сколько и чьи облигации продаются/покупаются.
     * @param isSell если true, то идёт частичная продажа облигаций, если false, то идёт частичная покупка.
     * @since 0.0.1-alpha
     */
    private void updateAssetOwnersWithAssetCounts(FixedRateBondPackage fixedRateBondPackage,
                                                  FixedRateBondPartialSellDTO dTO, boolean isSell) {
        Map<String, Float> assetOwnersWithAssetCounts = fixedRateBondPackage.getAssetRelationship()
                .getAssetOwnersWithAssetCounts();

        for (Map.Entry<String, Integer> mapElement : dTO.getAssetOwnersWithAssetCountsToSell().entrySet()) {
            float newAssetCount;
            String assetsOwnerID = mapElement.getKey();
            Integer assetCountDiff = mapElement.getValue();

            if (isSell) {
                newAssetCount = assetOwnersWithAssetCounts.get(assetsOwnerID) - assetCountDiff;
                fixedRateBondPackage.setAssetCount(fixedRateBondPackage.getAssetCount() - assetCountDiff);
            } else {
                newAssetCount = assetOwnersWithAssetCounts.get(assetsOwnerID) + assetCountDiff;
                fixedRateBondPackage.setAssetCount(fixedRateBondPackage.getAssetCount() + assetCountDiff);
            }
            assetOwnersWithAssetCounts.put(assetsOwnerID, newAssetCount);
        }
    }
}

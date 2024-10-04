package fund.data.assets.service.impl;

import fund.data.assets.dto.asset.exchange.AssetsOwnersCountryDTO;
import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.dto.asset.exchange.SellFixedRateBondDTO;
import fund.data.assets.dto.asset.exchange.PartialSellFixedRateBondDTO;
import fund.data.assets.dto.asset.exchange.BuyFixedRateBondDTO;
import fund.data.assets.model.asset.exchange.FixedRateBondPackage;
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
    //TODO После достижения функциональности всех методов проведи их инвентаризацию - особенно ДОКУМЕНТАЦИЮ И ИМЕНОВАНИЯ

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

        fixedRateBondRepository.save(atomicFixedRateBondPackage.get());
        atomicFixedRateBondPackage.get().getAssetRelationship().setAssetId(fixedRateBondPackageToCreate.getId());

        float packageBuyValueCorrectedByCommission = fixedRateBondPackageToCreate
                .getTotalAssetPurchasePriceWithCommission();
        Map<String, Float> ownersMoneyNegativeDistribution = formOwnersMoneyDistributionMap(
                atomicFixedRateBondPackage.get(), packageBuyValueCorrectedByCommission, firstBuyFixedRateBondDTO);

        isAssetsOwnersHaveThisAccountCashAmounts(atomicFixedRateBondPackage.get(), firstBuyFixedRateBondDTO,
                ownersMoneyNegativeDistribution);
        distributeMoneyOrExpensesAmongOwners(firstBuyFixedRateBondDTO, atomicFixedRateBondPackage.get(),
                ownersMoneyNegativeDistribution, false);
        return fixedRateBondRepository.save(atomicFixedRateBondPackage.get());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public FixedRateBondPackage partialBuyFixedRateBondPackage(Long id, BuyFixedRateBondDTO buyFixedRateBondDTO) {
        //1 - валидируем кол-во бумаг к покупке с вэльюс в мапе их распределения по оунерам
        isOwnershipMapValuesSumEqualsAssetCount(buyFixedRateBondDTO.getAssetOwnersWithAssetCounts(),
                buyFixedRateBondDTO.getAssetCount());

        //2 - находим бонд для изменения
        AtomicReference<FixedRateBondPackage> atomicFixedRateBondPackage = new AtomicReference<>(
                fixedRateBondRepository.findById(id).orElseThrow());
        //3 - определяем совокупную стоимость покупаемого пакета с учётом комиссий
        float packageBuyValue = calculatePartialPackageBuyValue(id, buyFixedRateBondDTO);
        packageBuyValue = correctOperationValueByCommission(packageBuyValue, atomicFixedRateBondPackage.get());
        //4 - формируем мапу изменения денег
        Map<String, Float> ownersMoneyNegativeDistribution = formOwnersMoneyDistributionMap(
                atomicFixedRateBondPackage.get(), packageBuyValue, buyFixedRateBondDTO);

        //5 - проверяем, могут ли все оунеры позволить себе такие траты
        isAssetsOwnersHaveThisAccountCashAmounts(atomicFixedRateBondPackage.get(), buyFixedRateBondDTO,
                ownersMoneyNegativeDistribution);
        //6 - уменьшаем деньги у оунеров
        distributeMoneyOrExpensesAmongOwners(buyFixedRateBondDTO, atomicFixedRateBondPackage.get(),
                ownersMoneyNegativeDistribution, false);
        //7 - распределяем новые бонды по оунерам
        updateAssetOwnersWithAssetCounts(atomicFixedRateBondPackage.get(),
                buyFixedRateBondDTO.getAssetOwnersWithAssetCounts(), false);
        //TODO 8 - меняем остальные поля у бонда и релатионшип, если надо - НОВОЕ (УЧТИ, ЧТО МОГУТ БЫТЬ НОВЫЕ ОУНЕРЫ!)
        //9 - сохраняем и возвращаем новый пакет!
        return fixedRateBondRepository.save(atomicFixedRateBondPackage.get());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public FixedRateBondPackage partialSellFixedRateBondPackage(Long id,
        PartialSellFixedRateBondDTO partialSellFixedRateBondDTO) {
        AtomicReference<FixedRateBondPackage> atomicFixedRateBondPackage = new AtomicReference<>(
                fixedRateBondRepository.findById(id).orElseThrow());
        int[] oldNewAssetCount = new int[2];
        oldNewAssetCount[0] = atomicFixedRateBondPackage.get().getAssetCount();

        isAssetsOwnersHaveThisAssetsAmounts(atomicFixedRateBondPackage.get(), partialSellFixedRateBondDTO);
        updateFixedRateBondFieldsByDTO(atomicFixedRateBondPackage.get(), partialSellFixedRateBondDTO);

        float packagePartSellValue = partialSellFixedRateBondDTO.getPackageSellValue();
        packagePartSellValue = correctOperationValueByCommission(packagePartSellValue,
                atomicFixedRateBondPackage.get());
        packagePartSellValue = correctValueByTaxes(partialSellFixedRateBondDTO, atomicFixedRateBondPackage.get(),
                packagePartSellValue);
        Map<String, Float> ownersMoneyDistribution = formOwnersMoneyDistributionMap(atomicFixedRateBondPackage.get(),
                packagePartSellValue, partialSellFixedRateBondDTO);

        distributeMoneyOrExpensesAmongOwners(partialSellFixedRateBondDTO, atomicFixedRateBondPackage.get(),
                ownersMoneyDistribution, true);
        updateAssetOwnersWithAssetCounts(atomicFixedRateBondPackage.get(),
                partialSellFixedRateBondDTO.getAssetOwnersWithAssetCountsToSell(), true);

        oldNewAssetCount[1] = atomicFixedRateBondPackage.get().getAssetCount();

        recalculateSomeFixedRateBondFieldsAtSell(atomicFixedRateBondPackage.get(), oldNewAssetCount);
        return fixedRateBondRepository.save(atomicFixedRateBondPackage.get());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public void sellAllPackage(Long id, SellFixedRateBondDTO sellFixedRateBondDTO) {
        AtomicReference<FixedRateBondPackage> atomicFixedRateBondPackage = new AtomicReference<>(
                fixedRateBondRepository.findById(id).orElseThrow());
        float packageSellValue = sellFixedRateBondDTO.getPackageSellValue();
        packageSellValue = correctOperationValueByCommission(packageSellValue, atomicFixedRateBondPackage.get());
        packageSellValue = correctValueByTaxes(sellFixedRateBondDTO, atomicFixedRateBondPackage.get(),
                packageSellValue);
        Map<String, Float> ownersMoneyDistribution = formOwnersMoneyDistributionMap(atomicFixedRateBondPackage.get(),
                packageSellValue, sellFixedRateBondDTO);

        distributeMoneyOrExpensesAmongOwners(sellFixedRateBondDTO, atomicFixedRateBondPackage.get(),
                ownersMoneyDistribution, true);
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

        distributeMoneyOrExpensesAmongOwners(assetsOwnersCountryDTO, atomicFixedRateBondPackage.get(),
                ownersMoneyDistribution, true);
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
        LocalDate lastAssetBuyOrSellDate = firstBuyFixedRateBondDTO.getLastAssetBuyDate();
        Integer bondParValue = firstBuyFixedRateBondDTO.getBondParValue();
        Float purchaseBondParValuePercent = firstBuyFixedRateBondDTO.getPurchaseBondParValuePercent();
        Float bondAccruedInterest = firstBuyFixedRateBondDTO.getBondsAccruedInterest();
        Float bondCouponValue = firstBuyFixedRateBondDTO.getBondCouponValue();
        Integer expectedBondCouponPaymentsCount = firstBuyFixedRateBondDTO.getExpectedBondCouponPaymentsCount();
        LocalDate bondMaturityDate = firstBuyFixedRateBondDTO.getBondMaturityDate();

        return new FixedRateBondPackage(assetCurrency, assetTitle, assetCount, assetOwnersWithAssetCounts,
                accountFromDTO, iSIN, assetIssuerTitle, lastAssetBuyOrSellDate, bondParValue,
                purchaseBondParValuePercent, bondAccruedInterest, bondCouponValue, expectedBondCouponPaymentsCount,
                bondMaturityDate);
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
     * При докупке облигаций в уже существующий в системе пакет, данный метод рассчитывает стоимость докупаемых
     * облигаций в валюте.
     * @param id айди пакета облигаций, в который идёт докупка.
     * @param dTO DTO с информацией о покупке облигаций с фиксированным купоном.
     * @return стоимость докупаемых облигаций в валюте.
     * @since 0.0.1-alpha
     */
    private float calculatePartialPackageBuyValue(Long id, BuyFixedRateBondDTO dTO) {
        return dTO.getAssetCount() * (dTO.getPurchaseBondParValuePercent() / 100.00F)
                * fixedRateBondRepository.findById(id).orElseThrow().getBondParValue() + dTO.getBondsAccruedInterest();
    }

    /**
     * При частичных продаже или покупке пакета облигаций ряд полей сущности нужно просто перезаполнить
     * без проведения дополнительных расчётов.
     * @param fixedRateBondPackage пакет облигаций, поля которого перезаполняются.
     * @param dTO DTO с информацией о новых значениях полей.
     * @since 0.0.1-alpha
     */
    private void updateFixedRateBondFieldsByDTO(FixedRateBondPackage fixedRateBondPackage,
                                                PartialSellFixedRateBondDTO dTO) {
        fixedRateBondPackage.setLastAssetBuyOrSellDate(dTO.getLastAssetSellDate());
        fixedRateBondPackage.setExpectedBondCouponPaymentsCount(dTO.getExpectedBondCouponPaymentsCount());
    }

    /**
     * Проводится валидация, все ли владельцы активов могут продать данное количество облигаций.
     * @param fixedRateBondPackage пакет облигаций, для которого проводится валидация.
     * @param dTO DTO для обслуживания внесения в систему данных о продаже части облигаций из пакета бумаг выпуска
     облигаций.
     * @since 0.0.1-alpha
     */
    private void isAssetsOwnersHaveThisAssetsAmounts(FixedRateBondPackage fixedRateBondPackage,
                                                     PartialSellFixedRateBondDTO dTO) {
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
     * При полной или частичной продаже пакета, при докупке в пакет, определяем, платится ли комиссия.
     * Если да - уменьшаем сумму операции на размер комиссии и возвращаем её, если нет - возвращаем без изменений.
     * @param operationValue цена продаваемого или покупаемого пакета облигаций в валюте эмиссии.
     * @param fixedRateBondPackage пакет облигаций, для работы с которым проводятся расчёты.
     * @return скорректированная или оставленная без изменений сумма.
     * @since 0.0.1-alpha
     */
    private Float correctOperationValueByCommission(float operationValue, FixedRateBondPackage fixedRateBondPackage) {
        if (fixedRateBondPackage.getAssetCommissionSystem().equals(CommissionSystem.TURNOVER)) {
            Account account = financialAssetRelationshipRepository.findById(
                    fixedRateBondPackage.getAssetRelationship().getId())
                    .orElseThrow().getAccount();
            String assetTypeName = fixedRateBondPackage.getAssetTypeName();
            float commissionPercentValue = turnoverCommissionValueRepository.findByAccountAndAssetTypeName(account,
                    assetTypeName).getCommissionPercentValue();
            operationValue = operationValue - operationValue * commissionPercentValue;
        }
        return operationValue;
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
                || dTO.getClass().equals(SellFixedRateBondDTO.class)) {
            return fixedRateBondPackage.getTotalAssetPurchasePriceWithCommission();
        } else if (dTO.getClass().equals(PartialSellFixedRateBondDTO.class)) {
            int bondsToSellCount = 0;

            for (Map.Entry<String, Integer> mapElement :
                    ((PartialSellFixedRateBondDTO) dTO).getAssetOwnersWithAssetCountsToSell().entrySet()) {
                bondsToSellCount += mapElement.getValue();
            }
            return fixedRateBondPackage.getTotalAssetPurchasePriceWithCommission() * ((float) bondsToSellCount
                    / fixedRateBondPackage.getAssetCount());
        }
        throw new IllegalArgumentException(ERROR_WRONG_DTO);
    }

    /**
     * Формируется мапа при продаже или покупке пакета облигаций для увеличения или уменьшения денежных средств
     * участников операции.
     * @param fixedRateBondPackage пакет облигаций, для которого проводятся расчёты.
     * @param operationValue цена пакета бумаг, возможно, скорректированная ранее на налог и комиссии.
     * @param dTO DTO - либо AssetsOwnersCountryDTO, либо прямые или более дальние наследники этого класса DTO.
     * @return мапа, где кей - id оунеров покупаемых/продаваемых облигаций, вэлью - размер их доли в валюте эмиссии от
     * суммы операции.
     * @since 0.0.1-alpha
     */
    private Map<String, Float> formOwnersMoneyDistributionMap(FixedRateBondPackage fixedRateBondPackage,
                                                              float operationValue, AssetsOwnersCountryDTO dTO) {
        float assetCountToOperate;
        Map<String, ? extends Number> assetOwnersWithAssetCounts;
        Map<String, Float> ownersMoneyDistributionMap = new TreeMap<>();

        if (dTO.getClass().equals(PartialSellFixedRateBondDTO.class)) {
            assetCountToOperate = getAssetCountSumFromDTO((PartialSellFixedRateBondDTO) dTO);
            assetOwnersWithAssetCounts = ((PartialSellFixedRateBondDTO) dTO).getAssetOwnersWithAssetCountsToSell();
        } else if (dTO.getClass().equals(BuyFixedRateBondDTO.class)) {
            assetCountToOperate = getAssetCountSumFromDTO((BuyFixedRateBondDTO) dTO);
            assetOwnersWithAssetCounts = ((BuyFixedRateBondDTO) dTO).getAssetOwnersWithAssetCounts();
        } else {
            assetCountToOperate = fixedRateBondPackage.getAssetCount();
            assetOwnersWithAssetCounts = fixedRateBondPackage.getAssetRelationship().getAssetOwnersWithAssetCounts();
        }

        for (Map.Entry<String, ? extends Number> mapElement : assetOwnersWithAssetCounts.entrySet()) {
            float ownerAssetCountToOperate = mapElement.getValue().floatValue();
            Float ownerAccountCashDiff = (ownerAssetCountToOperate / assetCountToOperate) * operationValue;

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
    private float getAssetCountSumFromDTO(PartialSellFixedRateBondDTO dTO) {
        int assetCountToSell = 0;

        for (Map.Entry<String, Integer> mapElement : dTO.getAssetOwnersWithAssetCountsToSell().entrySet()) {
            assetCountToSell += mapElement.getValue();
        }
        return Float.parseFloat(String.valueOf(assetCountToSell));
    }

    /**
     * Является перегруженным методом getAssetCountSumFromDTO(PartialSellFixedRateBondDTO dTO) для работы с иным типом
     * DTO - BuyFixedRateBondDTO - в качестве аргумента.
     * @param dTO DTO с информацией о количестве продаваемых облигаций и о том, кто их продаёт.
     * @return общее количество облигаций, участвующих в операции.
     * @since 0.0.1-alpha
     */
    private float getAssetCountSumFromDTO(BuyFixedRateBondDTO dTO) {
        float assetCountToSell = 0;

        for (Map.Entry<String, Float> mapElement : dTO.getAssetOwnersWithAssetCounts().entrySet()) {
            assetCountToSell += mapElement.getValue();
        }
        return assetCountToSell;
    }

    /**
     * Проводится проверка, все ли инвесторы могут потратить указанное количество денежных средств при операции.
     * @param fixedRateBondPackage пакет облигаций, для которого проводится проверка.
     * @param dTO DTO с информацией о коллективном гражданстве участников операции.
     * @param ownersMoneyDistributionMap мапа, где кей - id будущих оунеров покупаемых облигаций, вэлью - размер их
     * доли в валюте эмиссии от суммы операции.
     * @since 0.0.1-alpha
     */
    private void isAssetsOwnersHaveThisAccountCashAmounts(FixedRateBondPackage fixedRateBondPackage,
                                                          AssetsOwnersCountryDTO dTO,
                                                          Map<String, Float> ownersMoneyDistributionMap) {
        Account account = financialAssetRelationshipRepository.findById(fixedRateBondPackage.getAssetRelationship()
                        .getId()).orElseThrow().getAccount();
        AssetCurrency assetCurrency = fixedRateBondPackage.getAssetCurrency();

        for (Map.Entry<String, Float> mapElement : ownersMoneyDistributionMap.entrySet()) {
            AssetsOwner assetsOwner;

            if (dTO.getAssetsOwnersTaxResidency().equals(AssetsOwnersCountry.RUS)) {
                assetsOwner = russianAssetsOwnerRepository.findById(Long.valueOf(mapElement.getKey())).orElseThrow();
            } else {
                throw new IllegalArgumentException(ERROR_OWNER_COUNTRY);
            }
            Float ownerAccountCashAmount = accountCashRepository.findByAccountAndAssetCurrencyAndAssetsOwner(account,
                            assetCurrency, assetsOwner).getAmount();

            if (mapElement.getValue() > ownerAccountCashAmount) {
                throw new IllegalArgumentException(ONE_OWNER_NOT_ENOUGH_MONEY);
            }
        }
    }

    /**
     * Метод добавляет или уменьшает объём денежных средств на счетах инвесторов при купле-продаже ценных бумаг.
     * @param dTO DTO с информаций о налоговом резидентстве владельцев бумаг.
     * @param fixedRateBondPackage пакет облигаций, для которого проводятся операции.
     * @param assetOwnersWithAccountCashAmountDiffs мапа, где кей - id оунеров облигаций, вэлью - размер их
     * доли в валюте от суммы операции.
     * @param isSell если true, то идёт продажа облигаций, если false, то идёт покупка.
     * @since 0.0.1-alpha
     */
    private void distributeMoneyOrExpensesAmongOwners(AssetsOwnersCountryDTO dTO,
                                                      FixedRateBondPackage fixedRateBondPackage,
                                                      Map<String, Float> assetOwnersWithAccountCashAmountDiffs,
                                                      boolean isSell) {
        for (Map.Entry<String, Float> mapElement : assetOwnersWithAccountCashAmountDiffs.entrySet()) {
            Account account = financialAssetRelationshipRepository.findById(fixedRateBondPackage
                    .getAssetRelationship().getId()).orElseThrow().getAccount();
            AssetCurrency assetCurrency = fixedRateBondPackage.getAssetCurrency();
            AssetsOwner assetsOwner;

            if (dTO.getAssetsOwnersTaxResidency().equals(AssetsOwnersCountry.RUS)) {
                assetsOwner = russianAssetsOwnerRepository.findById(Long.parseLong(mapElement.getKey())).orElseThrow();
            } else {
                throw new IllegalArgumentException(ERROR_OWNER_COUNTRY);
            }
            AccountCash accountCash = accountCashRepository.findByAccountAndAssetCurrencyAndAssetsOwner(account,
                    assetCurrency, assetsOwner);

            if (isSell) {
                accountCash.setAmount(accountCash.getAmount() + mapElement.getValue());
            } else {
                accountCash.setAmount(accountCash.getAmount() - mapElement.getValue());
            }
        }
    }

    /**
     * При частичных продаже и покупке облигаций надо изменять количество облигаций во владении, для чего используется
     * этот метод.
     * @param fixedRateBondPackage пакет облигаций, который затрагивают изменения.
     * @param assetOwnersWithAssetCountsDiffMap мапа с информацией, у кого на сколько изменится облигаций во владении.
     * @param isSell если true, то идёт частичная продажа облигаций, если false, то идёт частичная покупка.
     * @since 0.0.1-alpha
     */
    private void updateAssetOwnersWithAssetCounts(FixedRateBondPackage fixedRateBondPackage,
                                                  Map<String, ? extends Number> assetOwnersWithAssetCountsDiffMap,
                                                  boolean isSell) {
        Map<String, Float> assetOwnersWithAssetCounts = fixedRateBondPackage.getAssetRelationship()
                .getAssetOwnersWithAssetCounts();

        for (Map.Entry<String, ? extends Number> mapElement : assetOwnersWithAssetCountsDiffMap.entrySet()) {
            float newAssetCount;
            String assetsOwnerID = mapElement.getKey();
            int assetCountDiff = mapElement.getValue().intValue();

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

    /**
     * При частичной продаже пакета облигаций, часть значений полей необходимо пересчитать. При этом, показатели
     * доходности меняться не будут, т.к. при продаже не меняются параметры, влияющие на них!
     * @param fixedRateBondPackage пакет облигаций, для которого проводятся расчёты.
     * @param oldNewAssetCount массив со старым [0] и новым [1] количествами облигаций в пакете.
     * @since 0.0.1-alpha
     */
    private void recalculateSomeFixedRateBondFieldsAtSell(FixedRateBondPackage fixedRateBondPackage,
                                                          int[] oldNewAssetCount) {
        float assetCountDiffProportion = (float) oldNewAssetCount[1] / (float) oldNewAssetCount[0];

        fixedRateBondPackage.setBondsAccruedInterest(fixedRateBondPackage.getBondsAccruedInterest()
                * assetCountDiffProportion);
        fixedRateBondPackage.setTotalCommissionForPurchase(fixedRateBondPackage.getTotalCommissionForPurchase()
                * assetCountDiffProportion);
        fixedRateBondPackage.setTotalAssetPurchasePriceWithCommission(fixedRateBondPackage
                .getTotalAssetPurchasePriceWithCommission() * assetCountDiffProportion);
    }
}

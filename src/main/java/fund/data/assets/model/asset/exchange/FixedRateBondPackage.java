package fund.data.assets.model.asset.exchange;

import fund.data.assets.config.SpringConfiguration;
import fund.data.assets.exception.UnrealAddingAssetsParameterException;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.utils.AutoSelector;
import fund.data.assets.utils.CommissionCalculator;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.FinancialAndAnotherConstants;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.Map;

/**
 * Облигация с фиксированным купоном.
 * Класс - наследник абстрактного ExchangeAsset.
 * Один из вариантов финализации сути Asset.
 * Представляет собой информацию о пакете облигаций с общим ISIN на балансе фонда. В пакете может быть как одна
 * облигация, так и бесконечно большое количество.
 * @version 0.1-b
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Entity
@Table(name = "fixed_rate_bonds")
@NoArgsConstructor
@Getter
@Setter
public class FixedRateBondPackage extends ExchangeAsset {
    @Transient
    public static final String WRONG_DATE_BOND_ADDING_WARNING = "This is error - don't add into system already" +
            " redeemed bond";

    /**
     * Номинальная стоимость одной облигации, определённая эмитентом. Обычно в РФ равна 1000 рублей.
     */
    @NotNull
    @Positive
    private Integer bondParValue;

    /**
     * "Чистая" цена облигации при покупке. Представляет собой формируемую на рынке цену, являющуюся % от номинала.
     * Например, если бумага куплена по цене 1500, а номинал равен 1000, то значение поля равно 150.00F.
     */
    @NotNull
    @Positive
    private Float purchaseBondParValuePercent;

    /**
     * Совокупный НКД пакета облигаций. НКД - это накопленный купонный доход.
     */
    @NotNull
    @PositiveOrZero
    private Float bondsAccruedInterest;

    /**
     * Совокупная комиссия при покупке облигаций.
     */
    @PositiveOrZero
    private Float totalCommissionForPurchase;

    /**
     * Величина в валюте. Сколько надо конкретно заплатить за облигации в реальности - т.е. с учётом
     * необходимости уплатить НКД и комиссию. Самое "народно-популярное" и наглядное значение!
     */
    @NotNull
    @PositiveOrZero
    private Float totalAssetPurchasePriceWithCommission;

    /**
     * Размер купонной выплаты по одной облигации в валюте.
     */
    @NotNull
    @PositiveOrZero
    private Float bondCouponValue;

    /**
     * Ожидаемое количество купонных выплат на момент последней покупки/продажи до даты погашения облигации.
     * Данный тип облигаций всегда имеет минимум одну ожидаемую купонную выплату, иначе это был бы иной тип облигаций.
     */
    @NotNull
    @Positive
    private Integer expectedBondCouponPaymentsCount;

    /**
     * Дата погашения облигации.
     */
    @NotNull
    private LocalDate bondMaturityDate;

    /**
     * Простая (без реинвестирования купонных выплат) доходность к погашению облигации. К примеру, если показатель
     * равен 8.45% - то поле будет равно 8.45F.
     */
    @NotNull
    private Float simpleYieldToMaturity;

    /**
     * Реальная доходность % в год. Является ожидаемой доходностью при инвестировании в облигацию после уплаты всех
     * налогов и комиссий, приведённая к % годовых, чтобы можно было сравнивать облигации с разным периодом обращения.
     * Для определения размера дохода учитывает не только купоны, но и возможный доход при погашении по более
     * высокой цене, чем облигация была приобретена.
     * Не учитывает возможный дополнительный доход от реинвестирования купонов.
     * К примеру, если показатель равен 8.45% - то поле будет равно 8.45F.
     */
    private Float markDementevYieldIndicator;

    public FixedRateBondPackage(AssetCurrency assetCurrency, String assetTitle, Integer assetCount,
                                Map<String, Float> assetOwnersWithAssetCounts, Account account, String iSIN,
                                String assetIssuerTitle, LocalDate lastAssetBuyOrSellDate, Integer bondParValue,
                                Float purchaseBondParValuePercent, Float bondsAccruedInterest, Float bondCouponValue,
                                Integer expectedBondCouponPaymentsCount, LocalDate bondMaturityDate) {
        super(assetCurrency, FixedRateBondPackage.class.getSimpleName(), assetTitle, assetCount,
                (TaxSystem) AutoSelector.selectAssetOperationsCostSystem(assetCurrency,
                        FixedRateBondPackage.class.getSimpleName(), AutoSelector.TAX_SYSTEM_CHOOSE),
                assetOwnersWithAssetCounts, account, iSIN, assetIssuerTitle, lastAssetBuyOrSellDate);

        this.bondParValue = bondParValue;
        this.purchaseBondParValuePercent = purchaseBondParValuePercent;
        this.bondsAccruedInterest = bondsAccruedInterest;

        if (getAssetCommissionSystem() != null) {
            CommissionCalculator commissionCalculator = new CommissionCalculator(SpringConfiguration.contextProvider()
                    .getApplicationContext().getBean(TurnoverCommissionValueRepository.class));
            this.totalCommissionForPurchase = commissionCalculator.calculateTotalCommissionForPurchase(
                    getAssetCommissionSystem(),
                    account,
                    FixedRateBondPackage.class.getSimpleName(),
                    getAssetCount(),
                    (purchaseBondParValuePercent / 100.00F) * bondParValue + bondsAccruedInterest);
        } else {
            this.totalCommissionForPurchase = 0.00F;
        }
        this.totalAssetPurchasePriceWithCommission = calculateTotalAssetPurchasePriceWithCommission();
        this.bondCouponValue = bondCouponValue;
        this.expectedBondCouponPaymentsCount = expectedBondCouponPaymentsCount;
        this.bondMaturityDate = bondMaturityDate;
        this.simpleYieldToMaturity = calculateSimpleYieldToMaturity(purchaseBondParValuePercent, bondParValue,
                bondCouponValue, expectedBondCouponPaymentsCount, bondsAccruedInterest);

        if (getAssetCurrency().equals(AssetCurrency.RUSRUB)
                && getAssetTaxSystem().equals(TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE)
                && getAssetCommissionSystem().equals(CommissionSystem.TURNOVER)) {
            this.markDementevYieldIndicator = calculateMarkDementevYieldIndicator(bondCouponValue,
                    expectedBondCouponPaymentsCount, totalAssetPurchasePriceWithCommission, assetCount, bondParValue);
        }
    }

    /**
     * Возвращает простую доходность к погашению.
     * Источник формулы - https://bcs-express.ru/novosti-i-analitika/dokhodnost-obligatsii-na-vse-sluchai-zhizni
     * @return Простая доходность к погашению в % годовых, выраженная в десятичной форме. К примеру, 8% годовых = 8.00F.
     * @since 0.0.1-alpha
     */
    public Float calculateSimpleYieldToMaturity(Float purchaseBondParValuePercent, Integer bondParValue,
                                                Float bondCouponValue, Integer expectedBondCouponPaymentsCount,
                                                Float bondsAccruedInterest) {
        float marketClearPriceInCurrency = purchaseBondParValuePercent * bondParValue / 100.00F;
        float allExpectedCouponPaymentsSum = bondCouponValue * expectedBondCouponPaymentsCount;
        float daysInYear = getDaysInYear();
        float daysBeforeMaturity = calculateDaysBeforeMaturity();

        return  (((bondParValue - marketClearPriceInCurrency + (allExpectedCouponPaymentsSum - bondsAccruedInterest))
                / marketClearPriceInCurrency) * (daysInYear / daysBeforeMaturity))
                * 100.00F;
    }

    /**
     * Возвращает "неакадемический параметр" реальной доходности % в год - основной параметр Фонда
     * для отбора облигаций с фиксированным купоном.
     * @return Показатель реальной доходности по облигации в % годовых, выраженный в десятичной форме. К примеру,
     * 8% годовых = 8.00F.
     * @since 0.0.1-alpha
     */
    public Float calculateMarkDementevYieldIndicator(Float bondCouponValue, Integer expectedBondCouponPaymentsCount,
                                                     float totalAssetPurchasePriceWithCommission,
                                                     Integer assetCount,
                                                     Integer bondParValue) {
        //TODO - возможно, стоит потом рефакторить метод, чтобы учесть работу с налоговыми резидентами НЕ РФ!
        float expectedBondCouponPaymentsSum = bondCouponValue * Float.valueOf(expectedBondCouponPaymentsCount);
        float incomeTaxCorrection = FinancialAndAnotherConstants.RUSSIAN_TAX_SYSTEM_CORRECTION_VALUE;
        float oneBondValueSummedWithHisCommission = (totalAssetPurchasePriceWithCommission
                / Float.valueOf(assetCount));
        float taxValueOfMaturityIncome = 0.00F;
        float daysInYear = getDaysInYear();
        float daysBeforeMaturity = calculateDaysBeforeMaturity();

        if (bondParValue > oneBondValueSummedWithHisCommission) {
            taxValueOfMaturityIncome = (1 - incomeTaxCorrection) * (bondParValue - oneBondValueSummedWithHisCommission);
        }
        return ((expectedBondCouponPaymentsSum * incomeTaxCorrection + bondParValue - taxValueOfMaturityIncome)
                / oneBondValueSummedWithHisCommission - 1) / (daysBeforeMaturity
                / daysInYear) * 100.00F;
    }

    /**
     * Умножает количество облигаций на их цену в валюте с учётом НКД, после чего суммирует с комиссией за покупку
     * данных облигаций при данных параметрах.
     * @return Сколько надо конкретно заплатить за облигации в реальности в валюте.
     * @since 0.0.1-alpha
     */
    private Float calculateTotalAssetPurchasePriceWithCommission() {
        return getAssetCount() * ((purchaseBondParValuePercent / 100.00F) * bondParValue + bondsAccruedInterest)
                + totalCommissionForPurchase;
    }

    /**
     * Если год високосный, это влияет на формулы выше. Поэтому, необходимо делать на это поправку, и возвращать либо
     * 365 дней в году, либо 366.
     * @return количество дней в году.
     * @since 0.0.1-alpha
     */
    private float getDaysInYear() {
        boolean firstLeapYearCheck = getLastAssetBuyOrSellDate().isLeapYear();
        boolean secondLeapYearCheck = (bondMaturityDate.getYear() - getLastAssetBuyOrSellDate().getYear()) >= 4
                && ChronoUnit.DAYS.between(getLastAssetBuyOrSellDate(), bondMaturityDate)
                >= (FinancialAndAnotherConstants.LEAP_YEAR_DAYS_COUNT
                + 3 * FinancialAndAnotherConstants.YEAR_DAYS_COUNT);

        if (firstLeapYearCheck || secondLeapYearCheck) {
            return FinancialAndAnotherConstants.LEAP_YEAR_DAYS_COUNT;
        }
        return FinancialAndAnotherConstants.YEAR_DAYS_COUNT;
    }

    /**
     * Позволяет подсчитать на момент покупки, сколько облигация будет существовать, если держать её до погашения.
     * @return Количество дней со дня покупки облигации до дня погашения.
     * @throws UnrealAddingAssetsParameterException Если в систему вводится уже погашенный бонд.
     * @since 0.0.1-alpha
     */
    private float calculateDaysBeforeMaturity() {
        if (ChronoUnit.DAYS.between(getLastAssetBuyOrSellDate(), bondMaturityDate) < 0) {
            throw new UnrealAddingAssetsParameterException(WRONG_DATE_BOND_ADDING_WARNING);
        }
        return (float) ChronoUnit.DAYS.between(getLastAssetBuyOrSellDate(), bondMaturityDate);
    }
}

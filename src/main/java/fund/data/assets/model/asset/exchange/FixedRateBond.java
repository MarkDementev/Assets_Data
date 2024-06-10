package fund.data.assets.model.asset.exchange;

import fund.data.assets.exception.UnrealAddingAssetsParameterException;
import fund.data.assets.model.owner.AssetsOwner;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.utils.AutoSelector;
import fund.data.assets.utils.CommissionCalculator;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.FinancialAndAnotherConstants;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;

import jakarta.persistence.Entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Облигация с фиксированным купоном.
 * Класс - наследник абстрактного ExchangeAsset.
 * Один из вариантов финализации сути Asset.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
public class FixedRateBond extends ExchangeAsset {
    public static final String WRONG_DATE_BOND_ADDING_WARNING = "This is error - don't add into system already" +
            " redeemed bond";

    /**
     * Номинальная стоимость облигации, определённая эмитентом. Обычно в РФ равна 1000 рублей.
     */
    @NotNull
    @Positive
    private Integer bondParValue;

    /**
     * "Чистая" цена облигации при покупке. Представляет собой формируемую на рынке цену, являющуюся % от номинала.
     */
    @NotNull
    @Positive
    private Float purchaseBondParValuePercent;

    /**
     * НКД облигации. НКД - накопленный купонный доход.
     */
    @NotNull
    @PositiveOrZero
    private Float bondAccruedInterest;

    /**
     * Совокупная комиссия при покупке облигации.
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
     * Размер купонной выплаты в валюте.
     */
    @NotNull
    @PositiveOrZero
    private Float bondCouponValue;

    /**
     * Ожидаемое количество купонных выплат на момент покупки до даты погашения облигации.
     */
    @NotNull
    @PositiveOrZero
    private Integer expectedBondCouponPaymentsCount;

    /**
     * Дата погашения облигации.
     */
    @NotNull
    private LocalDate bondMaturityDate;

    /**
     * Простая (без реинвестирования купонных выплат) доходность к погашению облигации.
     */
    @NotNull
    private Float simpleYieldToMaturity;

    /**
     * Реальная доходность % в год. Является ожидаемой доходностью при инвестировании в облигацию после уплаты всех
     * налогов и комиссий, приведённая к % годовых, чтобы можно было сравнивать облигации с разным периодом обращения.
     * Для определения размера дохода учитывает не только купоны, но и возможный доход при погашении по более
     * высокой цене, чем облигация была приобретена.
     * Не учитывает возможный дополнительный доход от реинвестирования купонов.
     */
    private Float markDementevYieldIndicator;

    public FixedRateBond(AssetCurrency assetCurrency, String assetTitle, Integer assetCount,
                         String iSIN, String assetIssuerTitle, LocalDate lastAssetBuyDate,
                         Integer bondParValue,
                         Float purchaseBondParValuePercent,
                         Float bondAccruedInterest,
                         Account account,
                         AssetsOwner assetsOwner,
                         Float bondCouponValue,
                         Integer expectedBondCouponPaymentsCount,
                         LocalDate bondMaturityDate) {
        super(assetCurrency, FixedRateBond.class.getTypeName(), assetTitle, assetCount,
                (TaxSystem) AutoSelector.selectAssetOperationsCostSystem(assetCurrency,
                        FixedRateBond.class.getTypeName(), AutoSelector.TAX_SYSTEM_CHOOSE), account, assetsOwner,
                iSIN, assetIssuerTitle, lastAssetBuyDate);
        this.bondParValue = bondParValue;
        this.purchaseBondParValuePercent = purchaseBondParValuePercent;
        this.bondAccruedInterest = bondAccruedInterest;

        if (getAssetCommissionSystem() != null) {
            CommissionCalculator commissionCalculator = new CommissionCalculator();

            this.totalCommissionForPurchase = commissionCalculator.calculateTotalCommissionForPurchase(
                    getAssetCommissionSystem(),
                    account,
                    FixedRateBond.class.getTypeName(),
                    getAssetCount(),
                    purchaseBondParValuePercent * bondParValue + bondAccruedInterest);
        } else {
            this.totalCommissionForPurchase = 0.00F;
        }
        this.totalAssetPurchasePriceWithCommission = calculateTotalAssetPurchasePriceWithCommission();
        this.bondCouponValue = bondCouponValue;
        this.expectedBondCouponPaymentsCount = expectedBondCouponPaymentsCount;
        this.bondMaturityDate = bondMaturityDate;
        this.simpleYieldToMaturity = calculateSimpleYieldToMaturity();

        if (getAssetCurrency().equals(AssetCurrency.RUSRUB)
                && getAssetTaxSystem().equals(TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE)
                && getAssetCommissionSystem().equals(CommissionSystem.TURNOVER)) {
            this.markDementevYieldIndicator = calculateMarkDementevYieldIndicator();
        }
    }

    /**
     * Умножает количество облигаций на их цену в валюте с учётом НКД, после чего суммирует с комиссией за покупку
     * данных облигаций при данных параметрах.
     * @return Сколько надо конкретно заплатить за облигации в реальности в валюте.
     * @since 0.0.1-alpha
     */
    private Float calculateTotalAssetPurchasePriceWithCommission() {
        return getAssetCount() * (purchaseBondParValuePercent * bondParValue + bondAccruedInterest)
                + totalCommissionForPurchase;
    }

    /**
     * Возвращает простую доходность к погашению.
     * Источник формулы - https://bcs-express.ru/novosti-i-analitika/dokhodnost-obligatsii-na-vse-sluchai-zhizni
     * @return Простая доходность к погашению в % годовых, выраженная в десятичной форме. К примеру, 8% годовых = 0.08.
     * @since 0.0.1-alpha
     */
    private Float calculateSimpleYieldToMaturity() {
        float marketClearPriceInCurrency = purchaseBondParValuePercent * bondParValue;
        float allExpectedCouponPaymentsSum = bondCouponValue * expectedBondCouponPaymentsCount;
        int daysBeforeMaturity = calculateDaysBeforeMaturity();

        return  ((bondParValue - marketClearPriceInCurrency + (allExpectedCouponPaymentsSum - bondAccruedInterest))
                / marketClearPriceInCurrency) * (FinancialAndAnotherConstants.YEAR_DAYS_COUNT / daysBeforeMaturity);
    }

    /**
     * Возвращает "неакадемический параметр" реальной доходности % в год - основной параметр Фонда
     * для отбора облигаций с фиксированным купоном.
     * @return Показатель реальной доходности по облигации в % годовых.
     * @since 0.0.1-alpha
     */
    private Float calculateMarkDementevYieldIndicator() {
        float expectedBondCouponPaymentsSum = bondCouponValue * expectedBondCouponPaymentsCount;
        float incomeTaxCorrection = FinancialAndAnotherConstants.RUSSIAN_TAX_SYSTEM_CORRECTION_VALUE;
        float oneBondValueSummedWithHisCommission = (totalAssetPurchasePriceWithCommission / getAssetCount());
        float taxValueOfMaturityIncome = 0.00F;
        int daysBeforeMaturity = calculateDaysBeforeMaturity();

        if (bondParValue > oneBondValueSummedWithHisCommission) {
            taxValueOfMaturityIncome = incomeTaxCorrection * (bondParValue - oneBondValueSummedWithHisCommission);
        }
        return ((expectedBondCouponPaymentsSum * incomeTaxCorrection + bondParValue + taxValueOfMaturityIncome)
                / oneBondValueSummedWithHisCommission - 1)
                / (daysBeforeMaturity / FinancialAndAnotherConstants.YEAR_DAYS_COUNT);
    }

    /**
     * Позволяет подсчитать на момент покупки, сколько облигация будет существовать, если держать её до погашения.
     * @return Количество дней со дня покупки облигации до дня погашения.
     * @throws UnrealAddingAssetsParameterException Если в систему вводится уже погашенный бонд.
     * @since 0.0.1-alpha
     */
    private int calculateDaysBeforeMaturity() {
        if (ChronoUnit.DAYS.between(getLastAssetBuyDate(), bondMaturityDate) < 0) {
            throw new UnrealAddingAssetsParameterException(WRONG_DATE_BOND_ADDING_WARNING);
        }
        return (int) ChronoUnit.DAYS.between(getLastAssetBuyDate(), bondMaturityDate);
    }
}

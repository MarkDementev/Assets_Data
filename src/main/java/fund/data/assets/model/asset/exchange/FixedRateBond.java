package fund.data.assets.model.asset.exchange;

import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.utils.AutoSelector;
import fund.data.assets.utils.CommissionCalculator;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.FinancialAndAnotherConstants;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;

import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Getter
@Setter
public class FixedRateBond extends ExchangeAsset {
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
    private Double totalAssetPurchasePriceWithCommission;

    @NotNull
    @PositiveOrZero
    private Float bondCouponValue;

    @NotNull
    @PositiveOrZero
    private Integer expectedBondCouponPaymentsCount;

    @NotNull
    private LocalDate bondMaturityDate;

    @NotNull
    private Float simpleYieldToMaturity;

    private Float markDementevYieldIndicator;

    public FixedRateBond(AssetCurrency assetCurrency, String assetTitle, Integer assetCount,
                         String iSIN, String assetIssuerTitle, LocalDate lastAssetBuyDate,
                         Integer bondParValue,
                         Float purchaseBondParValuePercent,
                         Float bondAccruedInterest,
                         Account account,

                         Float bondCouponValue,
                         Integer expectedBondCouponPaymentsCount,
                         LocalDate bondMaturityDate) {
        super(assetCurrency, FixedRateBond.class.getTypeName(), assetTitle, assetCount,
                (TaxSystem) AutoSelector.selectAssetOperationsCostSystem(assetCurrency,
                        FixedRateBond.class.getTypeName(), AutoSelector.TAX_SYSTEM_CHOOSE), account, iSIN,
                assetIssuerTitle, lastAssetBuyDate);
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
        //TODO Проверяй и рефактори код далее.
        this.markDementevYieldIndicator = calculateMarkDementevYieldIndicator();
    }

    /**
     * Умножает количество облигаций на их цену в валюте с учётом НКД, после чего суммирует с комиссией за покупку
     * данных облигаций при данных параметрах.
     * @return Сколько надо конкретно заплатить за облигации в реальности в валюте.
     * @since 0.0.1-alpha
     */
    private Double calculateTotalAssetPurchasePriceWithCommission() {
        return (double) (getAssetCount() * (purchaseBondParValuePercent * bondParValue + bondAccruedInterest)
                        + totalCommissionForPurchase);
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

        return  (
                (bondParValue - marketClearPriceInCurrency + (allExpectedCouponPaymentsSum - bondAccruedInterest))
                / marketClearPriceInCurrency
        ) * (FinancialAndAnotherConstants.YEAR_DAYS_COUNT / daysBeforeMaturity);
    }

    private Float calculateMarkDementevYieldIndicator() {
        Float incomeTaxCorrection;

        if (super.getAssetCurrency().getTitle().equals(AssetCurrency.RUSRUB.getTitle())
                && super.getAssetTaxSystem().getTitle().equals(TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE.getTitle())
                && super.getAssetCommissionSystem().getTitle().equals(CommissionSystem.TURNOVER.getTitle())) {
            incomeTaxCorrection = FinancialAndAnotherConstants.RUSSIAN_FIXED_RATE_BONDS_TAX_SYSTEM_CORRECTION;
        } else {
            return null;
        }
        float bondValueSummedWithCommission = (purchaseBondParValuePercent * bondParValue)
                + (getTotalCommissionForPurchase() / getAssetCount());
        float allExpectedCouponPaymentsValue = bondCouponValue * expectedBondCouponPaymentsCount;
        int daysBeforeMaturity = calculateDaysBeforeMaturity();
        float yieldIndicator;

        if (bondParValue > bondValueSummedWithCommission) {
            yieldIndicator = ((allExpectedCouponPaymentsValue * incomeTaxCorrection)
                    + (bondParValue - bondValueSummedWithCommission)
                    * incomeTaxCorrection)
                    / bondValueSummedWithCommission
                    / daysBeforeMaturity
                    * FinancialAndAnotherConstants.YEAR_DAYS_COUNT;
        } else {
            yieldIndicator = ((allExpectedCouponPaymentsValue * incomeTaxCorrection)
                    / bondValueSummedWithCommission)
                    / daysBeforeMaturity
                    * FinancialAndAnotherConstants.YEAR_DAYS_COUNT;
        }
        return yieldIndicator;
    }

    //TODO ПРОВЕРЬ
    private int calculateDaysBeforeMaturity() {
        return (int) ChronoUnit.DAYS.between(getLastAssetBuyDate(), bondMaturityDate);
//        long hoursBeforeMaturity = ChronoUnit.HOURS.between(getLastAssetBuyDate(), bondMaturityDate);
//        int daysBeforeMaturity = Integer.parseInt(String.valueOf(hoursBeforeMaturity))
//                / FinancialAndAnotherConstants.DAY_HOURS_COUNT;
//
//        if (hoursBeforeMaturity % FinancialAndAnotherConstants.DAY_HOURS_COUNT != 0) {
//            daysBeforeMaturity++;
//        }
//        return daysBeforeMaturity;
    }
}

package fund.data.assets.model.assets.exchange;

import fund.data.assets.model.Account;
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

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Getter
@Setter
public class FixedRateBond extends ExchangeAsset {
    @NotNull
    @Positive
    private Integer bondParValue;

    @NotNull
    @Positive
    private Float bondPurchaseMarketPrice;

    @NotNull
    @PositiveOrZero
    private Float bondAccruedInterest;

    @NotNull
    @PositiveOrZero
    private Float bondCouponValue;

    @NotNull
    @PositiveOrZero
    private Integer expectedBondCouponPaymentsCount;

    @NotNull
    private LocalDate bondMaturityDate;

    @NotNull
    private Float yieldToMaturity;

    private Float markDementevYieldIndicator;

    public FixedRateBond(Account bondAccount,
                         String iSIN,
                         String assetIssuerTitle,
                         LocalDate lastAssetBuyDate,
                         AssetCurrency assetCurrency,
                         String assetTitle,
                         Integer assetCount,
                         Integer bondParValue,
                         Float bondPurchaseMarketPrice,
                         Float bondAccruedInterest,
                         Float bondCouponValue,
                         Integer expectedBondCouponPaymentsCount,
                         LocalDate bondMaturityDate) {
        super(iSIN, assetIssuerTitle, lastAssetBuyDate);
        super.setAssetCurrency(assetCurrency);
        super.setAssetTypeName(FixedRateBond.class.getTypeName());
        super.setAssetTitle(assetTitle);
        super.setAssetCount(assetCount);
        super.setAssetTaxSystem(AutoSelector.selectTaxSystem(getAssetCurrency(), getAssetTypeName()));
//        super.setAssetCommissionSystem(AutoSelector.selectCommissionSystem(getAssetCurrency(), getAssetTypeName(),
//                bondAccount.getOrganisationWhereAccountOpened()));
        this.bondParValue = bondParValue;
        this.bondPurchaseMarketPrice = bondPurchaseMarketPrice;

//        if (getAssetCommissionSystem() != null) {
//            super.setTotalCommissionForPurchase(CommissionCalculator.calculateCommission(
//                    getAssetCommissionSystem(),
//                    getAssetCount(),
//                    bondPurchaseMarketPrice,
//                    bondParValue));
//        } else {
//            super.setTotalCommissionForPurchase(0.00F);
//        }
//        super.setTotalAssetPurchasePriceWithCommission(calculateTotalAssetPurchasePriceWithCommission());
        this.bondAccruedInterest = bondAccruedInterest;
        this.bondCouponValue = bondCouponValue;
        this.expectedBondCouponPaymentsCount = expectedBondCouponPaymentsCount;
        this.bondMaturityDate = bondMaturityDate;
//        this.yieldToMaturity = calculateYieldToMaturity();
//        this.markDementevYieldIndicator = calculateMarkDementevYieldIndicator();
    }

    private Double calculateTotalAssetPurchasePriceWithCommission() {
        return (double) (getAssetCount() * bondParValue * bondPurchaseMarketPrice + getTotalCommissionForPurchase());
    }

    private Float calculateYieldToMaturity() {
        float allExpectedCouponPaymentsValue = bondCouponValue * expectedBondCouponPaymentsCount;
        int daysBeforeMaturity = calculateDaysBeforeMaturity();

        return ((bondParValue - (bondParValue * bondPurchaseMarketPrice)
                + (allExpectedCouponPaymentsValue - bondAccruedInterest))
                / (bondParValue * bondPurchaseMarketPrice))
                * FinancialAndAnotherConstants.YEAR_DAYS_COUNT
                / daysBeforeMaturity;
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
        float bondValueSummedWithCommission = (bondPurchaseMarketPrice * bondParValue)
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

    private int calculateDaysBeforeMaturity() {
        long hoursBeforeMaturity = ChronoUnit.HOURS.between(getLastAssetBuyDate(), bondMaturityDate);
        int daysBeforeMaturity = Integer.parseInt(String.valueOf(hoursBeforeMaturity))
                / FinancialAndAnotherConstants.DAY_HOURS_COUNT;

        if (hoursBeforeMaturity % FinancialAndAnotherConstants.DAY_HOURS_COUNT != 0) {
            daysBeforeMaturity++;
        }
        return daysBeforeMaturity;
    }
}

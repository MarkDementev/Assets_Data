package fund.data.assets.model.assets.exchange;

import com.fasterxml.jackson.annotation.JsonFormat;

import fund.data.assets.utils.AutoSelector;
import fund.data.assets.utils.CommissionCalculator;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.FinancialCalculationConstants;

import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "fixed rate bonds")
@Getter
@Setter
public class FixedRateBond extends ExchangeAsset {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

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

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Size(min = 10, max = 10)
    private Instant bondMaturityDate;

    @NotNull
    private Float yieldToMaturity;

    private Float markDementevYieldIndicator;

    public FixedRateBond(String iSIN,
                         String assetIssuerTitle,
                         Instant lastAssetBuyDate,
                         AssetCurrency assetCurrency,
                         String assetTitle,
                         Integer assetCount,
                         Integer bondParValue,
                         Float bondPurchaseMarketPrice,
                         Float bondAccruedInterest,
                         Float bondCouponValue,
                         Integer expectedBondCouponPaymentsCount,
                         Instant bondMaturityDate) {
        super(iSIN, assetIssuerTitle, lastAssetBuyDate);
        super.setAssetCurrency(assetCurrency);
        super.setAssetTypeName(FixedRateBond.class.getTypeName());
        super.setAssetTitle(assetTitle);
        super.setAssetCount(assetCount);
        super.setAssetTaxSystem(AutoSelector.selectTaxSystem(getAssetCurrency(), getAssetTypeName()));
        super.setAssetCommissionSystem(AutoSelector.selectCommissionSystem(getAssetCurrency(), getAssetTypeName(),
                getAssetRelationship().getAccount().getOrganisationWhereAccountOpened()));
        this.bondParValue = bondParValue;
        this.bondPurchaseMarketPrice = bondPurchaseMarketPrice;

        if (getAssetCommissionSystem() != null) {
            super.setTotalCommissionForPurchase(CommissionCalculator.calculateCommission(
                    getAssetCommissionSystem(),
                    getAssetCount(),
                    bondPurchaseMarketPrice,
                    bondParValue));
        } else {
            super.setTotalCommissionForPurchase(0.00F);
        }
        super.setTotalAssetPurchasePriceWithCommission(calculateTotalAssetPurchasePriceWithCommission());
        this.bondAccruedInterest = bondAccruedInterest;
        this.bondCouponValue = bondCouponValue;
        this.expectedBondCouponPaymentsCount = expectedBondCouponPaymentsCount;
        this.bondMaturityDate = bondMaturityDate;
        this.yieldToMaturity = calculateYieldToMaturity();
        this.markDementevYieldIndicator = calculateMarkDementevYieldIndicator();
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
                * FinancialCalculationConstants.YEAR_DAYS_COUNT
                / daysBeforeMaturity;
    }

    private Float calculateMarkDementevYieldIndicator() {
        Float incomeTaxCorrection;

        if (super.getAssetCurrency().getTitle().equals(AssetCurrency.RUSRUB.getTitle())
                && super.getAssetTaxSystem().getTitle().equals(TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE.getTitle())
                && super.getAssetCommissionSystem().getTitle().equals(CommissionSystem.TURNOVER.getTitle())) {
//            incomeTaxCorrection = 0.87F;
            /*
            Корректировка incomeTaxCorrection с помощью запроса через контроллер для получения записи в БД
            о размере НДФЛ. Конкретно здесь incomeTaxCorrection должно стать 0.87F, из-за размера РФ НДФЛ в 13%.
             */
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
                    * FinancialCalculationConstants.YEAR_DAYS_COUNT;
        } else {
            yieldIndicator = ((allExpectedCouponPaymentsValue * incomeTaxCorrection)
                    / bondValueSummedWithCommission)
                    / daysBeforeMaturity
                    * FinancialCalculationConstants.YEAR_DAYS_COUNT;
        }
        return yieldIndicator;
    }

    private int calculateDaysBeforeMaturity() {
        long hoursBeforeMaturity = ChronoUnit.HOURS.between(getLastAssetBuyDate(), bondMaturityDate);
        int daysBeforeMaturity = Integer.parseInt(String.valueOf(hoursBeforeMaturity))
                / FinancialCalculationConstants.DAY_HOURS_COUNT;

        if (hoursBeforeMaturity % FinancialCalculationConstants.DAY_HOURS_COUNT != 0) {
            daysBeforeMaturity++;
        }
        return daysBeforeMaturity;
    }
}

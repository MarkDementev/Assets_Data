package fund.data.assets.model.assets.exchange;

import com.fasterxml.jackson.annotation.JsonFormat;

import fund.data.assets.utils.AssetsCurrency;
import fund.data.assets.utils.FinancialCalculationConstants;

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

    @NotNull
    private Float markDementevYieldIndicator;

    public FixedRateBond(String iSIN,
                         String assetIssuerTitle,
                         Instant lastAssetBuyDate,
                         AssetsCurrency assetCurrency,
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



        this.bondParValue = bondParValue;
        this.bondPurchaseMarketPrice = bondPurchaseMarketPrice;
        this.bondAccruedInterest = bondAccruedInterest;
        this.bondCouponValue = bondCouponValue;
        this.expectedBondCouponPaymentsCount = expectedBondCouponPaymentsCount;
        this.bondMaturityDate = bondMaturityDate;
        this.yieldToMaturity = calculateYieldToMaturity();
        this.markDementevYieldIndicator = calculateMarkDementevYieldIndicator();
    }

    private Float calculateYieldToMaturity() {
        float allExpectedCouponPaymentsValue = bondCouponValue * expectedBondCouponPaymentsCount;
        int daysBeforeMaturity = calculateDaysBeforeMaturity();

        return ((bondParValue - (bondParValue * bondPurchaseMarketPrice)
                + (allExpectedCouponPaymentsValue - bondAccruedInterest))
                / (bondParValue * bondPurchaseMarketPrice))
                * FinancialCalculationConstants.YEAR_DAYS_COUNT / daysBeforeMaturity;
    }

    private Float calculateMarkDementevYieldIndicator() {
        float bondValueSummedWithCommission = (bondPurchaseMarketPrice * bondParValue)
                + (bondPurchaseMarketPrice * bondParValue) * getOneAssetCommissionForPurchase();
        float allExpectedCouponPaymentsValue = bondCouponValue * expectedBondCouponPaymentsCount;
        int daysBeforeMaturity = calculateDaysBeforeMaturity();
        float yieldIndicator;

        if (bondParValue > bondValueSummedWithCommission) {
            yieldIndicator = ((allExpectedCouponPaymentsValue * FinancialCalculationConstants.PROFIT_SHARE_AFTER_NDFL)
                    + (bondParValue - bondValueSummedWithCommission)
                    * FinancialCalculationConstants.PROFIT_SHARE_AFTER_NDFL)
                    / bondValueSummedWithCommission
                    / daysBeforeMaturity * FinancialCalculationConstants.YEAR_DAYS_COUNT;
        } else {
            yieldIndicator = ((allExpectedCouponPaymentsValue * FinancialCalculationConstants.PROFIT_SHARE_AFTER_NDFL)
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

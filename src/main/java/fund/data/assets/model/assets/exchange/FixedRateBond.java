package fund.data.assets.model.assets.exchange;

import com.fasterxml.jackson.annotation.JsonFormat;

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
import java.util.Currency;

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
                         Currency assetCurrency,
                         String assetTitle,
                         Integer assetCount,
//                         Instant lastAssetBuyDate,
                         Integer bondParValue,
                         Float bondPurchaseMarketPrice,
                         Float bondAccruedInterest,
                         Float bondCouponValue,
                         Integer expectedBondCouponPaymentsCount,
                         Instant bondMaturityDate) {
        super(iSIN, assetIssuerTitle);
        super.setAssetCurrency(assetCurrency);
        super.setAssetTypeName(FixedRateBond.class.getTypeName());
        super.setAssetTitle(assetTitle);
        super.setAssetCount(assetCount);
//        super.setLastAssetBuyDate(lastAssetBuyDate);
//        super.setTotalAssetMarketPurchasePriceAsCurrency(getAssetCount() * getBondPurchaseMarketPrice() * bondParValue);
//        super.setTotalCommissionForPurchase(calculateCommission(getTotalAssetMarketPurchasePriceAsCurrency(),
//                getAssetTypeName()));
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
        float expectedCouponPaymentsValuesCount = expectedCouponPaymentsCount * bondCouponValueInCurrency;
        long hoursBeforeMaturity = ChronoUnit.HOURS.between(getLastAssetBuyDate(), bondMaturityDate);
        int daysBeforeMaturity = Integer.parseInt(String.valueOf(hoursBeforeMaturity)) / 24;

        if (hoursBeforeMaturity % 24 != 0) {
            daysBeforeMaturity++;
        }

        return ((bondParValue
                - (bondParValue * bondPurchaseMarketPrice)
                + (expectedCouponPaymentsValuesCount - accumulatedBondCouponIncome))
                / (bondParValue * bondPurchaseMarketPrice)) * 365 / daysBeforeMaturity;
    }

    private Float calculateMarkDementevYieldIndicator() {
        Float bondValueWithCommissionInCurrency = (bondPurchaseMarketPrice * bondParValue)
                + (bondPurchaseMarketPrice * bondParValue)
        Float yieldIndicator;

        //ситуация, когда будет налог
        if (bondParValue > bondValueWithCommissionInCurrency) {
            yieldIndicator =;
        } else {
            yieldIndicator =;
        }
        return yieldIndicator;
    }
}

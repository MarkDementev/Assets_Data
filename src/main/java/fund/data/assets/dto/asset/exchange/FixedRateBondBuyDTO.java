package fund.data.assets.dto.asset.exchange;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import java.util.Map;

/**
 * DTO для внесения информации в систему о покупке облигаций с фиксированным купоном.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@NoArgsConstructor
public class FixedRateBondBuyDTO {
    @NotNull
    @Positive
    private Integer assetCount;

    @NotNull
    private Map<String, Float> assetOwnersWithAssetCounts;

    @NotNull
    private LocalDate lastAssetBuyOrSellDate;

    @NotNull
    @Positive
    private Float purchaseBondParValuePercent;

    @NotNull
    @PositiveOrZero
    private Float bondAccruedInterest;

    @NotNull
    @Positive
    private Integer expectedBondCouponPaymentsCount;

    public FixedRateBondBuyDTO(Integer assetCount, Map<String, Float> assetOwnersWithAssetCounts,
                               LocalDate lastAssetBuyOrSellDate, Float purchaseBondParValuePercent,
                               Float bondAccruedInterest, Integer expectedBondCouponPaymentsCount) {
        this.assetCount = assetCount;
        this.assetOwnersWithAssetCounts = assetOwnersWithAssetCounts;
        this.lastAssetBuyOrSellDate = lastAssetBuyOrSellDate;
        this.purchaseBondParValuePercent = purchaseBondParValuePercent;
        this.bondAccruedInterest = bondAccruedInterest;
        this.expectedBondCouponPaymentsCount = expectedBondCouponPaymentsCount;
    }
}

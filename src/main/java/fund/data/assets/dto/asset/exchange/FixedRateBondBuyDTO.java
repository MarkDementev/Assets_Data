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

    //TODO поправь название
    @NotNull
    private LocalDate lastAssetBuyDate;

    @NotNull
    @Positive
    private Float purchaseBondParValuePercent;

    @NotNull
    @PositiveOrZero
    private Float bondsAccruedInterest;

    @NotNull
    @Positive
    private Integer expectedBondCouponPaymentsCount;

    public FixedRateBondBuyDTO(Integer assetCount, Map<String, Float> assetOwnersWithAssetCounts,
                               LocalDate lastAssetBuyDate, Float purchaseBondParValuePercent,
                               Float bondsAccruedInterest, Integer expectedBondCouponPaymentsCount) {
        this.assetCount = assetCount;
        this.assetOwnersWithAssetCounts = assetOwnersWithAssetCounts;
        this.lastAssetBuyDate = lastAssetBuyDate;
        this.purchaseBondParValuePercent = purchaseBondParValuePercent;
        this.bondsAccruedInterest = bondsAccruedInterest;
        this.expectedBondCouponPaymentsCount = expectedBondCouponPaymentsCount;
    }
}

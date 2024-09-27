package fund.data.assets.dto.asset.exchange;

import fund.data.assets.utils.enums.AssetsOwnersCountry;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import java.util.Map;

/**
 * DTO для внесения информации в систему о покупке облигаций с фиксированным купоном.
 * Используется как при первичной покупке облигаций в пакет, так и при повторной.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@NoArgsConstructor
@Getter
@Setter
public class BuyFixedRateBondDTO extends AssetsOwnersCountryDTO {
    @NotNull
    @Positive
    private Integer assetCount;

    @NotNull
    private Map<String, Float> assetOwnersWithAssetCounts;

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

    public BuyFixedRateBondDTO(AssetsOwnersCountry assetsOwnersTaxResidency, Integer assetCount,
                               Map<String, Float> assetOwnersWithAssetCounts, LocalDate lastAssetBuyDate,
                               Float purchaseBondParValuePercent, Float bondsAccruedInterest,
                               Integer expectedBondCouponPaymentsCount) {
        super(assetsOwnersTaxResidency);

        this.assetCount = assetCount;
        this.assetOwnersWithAssetCounts = assetOwnersWithAssetCounts;
        this.lastAssetBuyDate = lastAssetBuyDate;
        this.purchaseBondParValuePercent = purchaseBondParValuePercent;
        this.bondsAccruedInterest = bondsAccruedInterest;
        this.expectedBondCouponPaymentsCount = expectedBondCouponPaymentsCount;
    }
}

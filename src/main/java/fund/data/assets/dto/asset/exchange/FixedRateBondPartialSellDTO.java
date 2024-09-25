package fund.data.assets.dto.asset.exchange;

import fund.data.assets.utils.enums.AssetsOwnersCountry;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import java.util.Map;

/**
 * DTO для обслуживания внесения в систему данных о продаже части облигаций из пакета бумаг выпуска облигаций.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@NoArgsConstructor
@Getter
@Setter
public class FixedRateBondPartialSellDTO extends FixedRateBondFullSellDTO {
    @NotNull
    private Map<String, Integer> assetOwnersWithAssetCountsToSell;

    @NotNull
    private LocalDate lastAssetSellDate;

    /**
     * Данное поле может быть только больше нуля, хотя существуют облигации, где нет купонных выплат. Эта сущность
     * обеспечивает работу с облигациями, где всегда есть минимум один купон ещё не выплаченный - такой приходится на
     * последний день обращения облигации. Значит, даже если мы бесконечно будем продавать или покупать облигации,
     * всегда останется минимум одна купонная выплата, которая ещё не произведена.
     */
    @NotNull
    @Positive
    private Integer expectedBondCouponPaymentsCount;

    public FixedRateBondPartialSellDTO(AssetsOwnersCountry assetsOwnersTaxResidency, Float packageSellValue,
                                       Map<String, Integer> assetOwnersWithAssetCountsToSell,
                                       LocalDate lastAssetSellDate, Integer expectedBondCouponPaymentsCount) {
        super(assetsOwnersTaxResidency, packageSellValue);

        this.assetOwnersWithAssetCountsToSell = assetOwnersWithAssetCountsToSell;
        this.lastAssetSellDate = lastAssetSellDate;
        this.expectedBondCouponPaymentsCount = expectedBondCouponPaymentsCount;
    }
}

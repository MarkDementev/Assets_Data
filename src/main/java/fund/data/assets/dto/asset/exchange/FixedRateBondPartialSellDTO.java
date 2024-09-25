package fund.data.assets.dto.asset.exchange;

import fund.data.assets.utils.enums.AssetsOwnersCountry;

import jakarta.validation.constraints.NotNull;

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

    public FixedRateBondPartialSellDTO(AssetsOwnersCountry assetsOwnersTaxResidency, Float packageSellValue,
                                       Map<String, Integer> assetOwnersWithAssetCountsToSell,
                                       LocalDate lastAssetSellDate) {
        super(assetsOwnersTaxResidency, packageSellValue);

        this.assetOwnersWithAssetCountsToSell = assetOwnersWithAssetCountsToSell;
        this.lastAssetSellDate = lastAssetSellDate;
    }
}

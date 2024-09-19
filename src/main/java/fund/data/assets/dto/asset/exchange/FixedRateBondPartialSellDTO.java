package fund.data.assets.dto.asset.exchange;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO для обслуживания внесения в систему данных о продаже части облигаций из пакета бумаг выпуска облигаций.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedRateBondPartialSellDTO {
    @NotNull
    private Map<String, Integer> assetOwnersWithAssetCountsToSell;
}

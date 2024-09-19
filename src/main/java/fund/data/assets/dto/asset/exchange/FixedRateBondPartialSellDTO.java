package fund.data.assets.dto.asset.exchange;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

//TODO добавь докуху
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedRateBondPartialSellDTO {
    @NotNull
    private Map<String, Float> assetOwnersWithAssetCountsToSell;
}

package fund.data.assets.dto.asset.exchange;

import fund.data.assets.utils.enums.AssetsOwnersCountry;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO с информаций о продажной стоимости при полной продаже пакета облигаций с фиксированным купоном.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@NoArgsConstructor
@Getter
@Setter
public class FixedRateBondFullSellDTO extends AssetsOwnersCountryDTO {
    @NotNull
    @Positive
    private Float packageSellValue;

    public FixedRateBondFullSellDTO(AssetsOwnersCountry assetsOwnersTaxResidency, Float packageSellValue) {
        super(assetsOwnersTaxResidency);

        this.packageSellValue = packageSellValue;
    }
}

package fund.data.assets.dto.asset.exchange;

import fund.data.assets.utils.enums.AssetsOwnersCountry;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO с информаций о продажной стоимости при полной продаже пакета облигаций с фиксированным купоном.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedRateBondFullSellDTO {
    @NotNull
    @Enumerated(EnumType.STRING)
    private AssetsOwnersCountry assetsOwnersTaxResidency;

    @NotNull
    @Positive
    private Float packageSellValue;
}
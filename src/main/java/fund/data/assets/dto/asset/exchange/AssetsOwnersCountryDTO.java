package fund.data.assets.dto.asset.exchange;

import fund.data.assets.utils.enums.AssetsOwnersCountry;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO с информаций о коллективном гражданстве собственников активов.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@NoArgsConstructor
public class AssetsOwnersCountryDTO {
    @NotNull
    @Enumerated(EnumType.STRING)
    private AssetsOwnersCountry assetsOwnersTaxResidency;

    public AssetsOwnersCountryDTO(AssetsOwnersCountry assetsOwnersTaxResidency) {
        this.assetsOwnersTaxResidency = assetsOwnersTaxResidency;
    }
}

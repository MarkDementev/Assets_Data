package fund.data.assets.dto.asset.exchange;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import fund.data.assets.utils.enums.AssetsOwnersCountry;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO с информаций о коллективном гражданстве собственников активов.
 * Необходимо, т.к. в силу отличий налогового и правового регулирования деятельности инвесторов разных стран на бирже
 * в РФ, пакеты ценных бумаг принадлежат группам инвесторов только одной страны.
 * @version 0.1-b
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@JsonSubTypes(
        {
            @JsonSubTypes.Type(AssetsOwnersCountryDTO.class),
            @JsonSubTypes.Type(SellFixedRateBondDTO.class),
            @JsonSubTypes.Type(PartialSellFixedRateBondDTO.class),
            @JsonSubTypes.Type(BuyFixedRateBondDTO.class),
            @JsonSubTypes.Type(FirstBuyFixedRateBondDTO.class)
        }
)
@NoArgsConstructor
public class AssetsOwnersCountryDTO {
    @NotNull
    @Enumerated(EnumType.STRING)
    private AssetsOwnersCountry assetsOwnersTaxResidency;

    public AssetsOwnersCountryDTO(AssetsOwnersCountry assetsOwnersTaxResidency) {
        this.assetsOwnersTaxResidency = assetsOwnersTaxResidency;
    }
}

package fund.data.assets.dto.asset.exchange;

import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.AssetsOwnersCountry;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import java.util.Map;

/**
 * DTO для обслуживания внесения в систему данных о первой покупке пакета бумаг данного выпуска облигаций.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@NoArgsConstructor
@Getter
@Setter
public class FirstBuyFixedRateBondDTO extends FixedRateBondBuyDTO {
    @NotNull
    @Enumerated(EnumType.STRING)
    private AssetCurrency assetCurrency;

    @NotBlank
    private String assetTitle;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AssetsOwnersCountry assetsOwnersCountry;

    @NotNull
    private Long accountID;

    @NotNull
    @Size(min = 12, max = 12)
    @Pattern(regexp = "^[A-Z]{2}[A-Z0-9]{9}[0-9]$")
    private String iSIN;

    @NotBlank
    private String assetIssuerTitle;

    @NotNull
    @Positive
    private Integer bondParValue;

    @NotNull
    @PositiveOrZero
    private Float bondCouponValue;

    @NotNull
    private LocalDate bondMaturityDate;

    public FirstBuyFixedRateBondDTO(Integer assetCount, Map<String, Float> assetOwnersWithAssetCounts,
                                    LocalDate lastAssetBuyDate, Float purchaseBondParValuePercent,
                                    Float bondsAccruedInterest, Integer expectedBondCouponPaymentsCount,
                                    AssetCurrency assetCurrency, String assetTitle,
                                    AssetsOwnersCountry assetsOwnersCountry, Long accountID, String iSIN,
                                    String assetIssuerTitle, Integer bondParValue, Float bondCouponValue,
                                    LocalDate bondMaturityDate) {
        super(assetCount, assetOwnersWithAssetCounts, lastAssetBuyDate, purchaseBondParValuePercent,
                bondsAccruedInterest, expectedBondCouponPaymentsCount);

        this.assetCurrency = assetCurrency;
        this.assetTitle = assetTitle;
        this.assetsOwnersCountry = assetsOwnersCountry;
        this.accountID = accountID;
        this.iSIN = iSIN;
        this.assetIssuerTitle = assetIssuerTitle;
        this.bondParValue = bondParValue;
        this.bondCouponValue = bondCouponValue;
        this.bondMaturityDate = bondMaturityDate;
    }
}

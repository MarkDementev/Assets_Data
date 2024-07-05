package fund.data.assets.dto.asset.exchange;

import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.CommissionSystem;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO для обслуживания внесения в систему данных о первой покупке пакета бумаг данного выпуска облигаций.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirstBuyFixedRateBondDTO {
    @NotNull
    @Enumerated(EnumType.STRING)
    private AssetCurrency assetCurrency;

    @NotBlank
    private String assetTitle;

    @NotNull
    @Positive
    private Integer assetCount;

    @NotNull
    @Size(min = 12, max = 12)
    @Pattern(regexp = "^[A-Z]{2}[A-Z0-9]{9}[0-9]$")
    private String iSIN;

    @NotBlank
    private String assetIssuerTitle;

    @NotNull
    private LocalDate lastAssetBuyDate;

    @Enumerated(EnumType.STRING)
    private CommissionSystem assetCommissionSystem;

    @NotNull
    @Positive
    private Integer bondParValue;

    @NotNull
    @Positive
    private Float purchaseBondParValuePercent;

    @NotNull
    @PositiveOrZero
    private Float bondAccruedInterest;

    @NotNull
    @PositiveOrZero
    private Float bondCouponValue;

    @NotNull
    @PositiveOrZero
    private Integer expectedBondCouponPaymentsCount;

    @NotNull
    private LocalDate bondMaturityDate;
}

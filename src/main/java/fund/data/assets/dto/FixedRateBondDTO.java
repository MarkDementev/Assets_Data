package fund.data.assets.dto;

import fund.data.assets.model.Account;
import fund.data.assets.utils.enums.AssetCurrency;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedRateBondDTO {
    @NotNull
    private Account account;

    @NotNull
    @Size(min = 12, max = 12)
    @Pattern(regexp = "[a-z]{2}[0-9]{10}")
    private String iSIN;

    @NotBlank
    private String assetIssuerTitle;

    @NotNull
    private LocalDate lastAssetBuyDate;

    @NotNull
    private AssetCurrency assetCurrency;

    @NotBlank
    private String assetTitle;

    @NotNull
    @Size(min = 1)
    private Integer assetCount;

    @NotNull
    @Positive
    private Integer bondParValue;

    @NotNull
    @Positive
    private Float bondPurchaseMarketPrice;

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

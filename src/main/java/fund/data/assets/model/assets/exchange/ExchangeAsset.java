package fund.data.assets.model.assets.exchange;

import fund.data.assets.model.Asset;

import jakarta.persistence.MappedSuperclass;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
check - does I need AllArgsConstructor?
*/
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class ExchangeAsset extends Asset {
    @NotNull
    @Size(min = 12, max = 12)
    @Pattern(regexp = "[a-z]{2}[0-9]{10}")
    private String iSIN;

    @NotBlank
    private String assetIssuerTitle;

//    @JsonFormat(pattern = "dd-MM-yyyy")
//    @Size(min = 10, max = 10)
//    private Instant lastAssetBuyDate;
//
//    @PositiveOrZero
//    private Double totalAssetMarketPurchasePriceAsCurrency;
//
//    @PositiveOrZero
//    private Float totalCommissionForPurchase;
}

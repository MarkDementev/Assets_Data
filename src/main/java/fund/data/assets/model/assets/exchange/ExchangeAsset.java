package fund.data.assets.model.assets.exchange;

import fund.data.assets.model.Asset;
import fund.data.assets.utils.CommissionSystem;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/*
check - do I need AllArgsConstructor?
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

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Size(min = 10, max = 10)
    private Instant lastAssetBuyDate;

//    @NotNull
//    private Boolean isCommissionableAsset;
//
//    @Enumerated(EnumType.STRING)
//    private CommissionSystem assetCommissionSystem;
//
//    @PositiveOrZero
//    private Float oneAssetCommissionForPurchase;
//
//    @PositiveOrZero
//    private Float totalCommissionForPurchase;
//
//    @NotNull
//    @PositiveOrZero
//    private Double totalAssetPurchasePriceWithCommission;

    public ExchangeAsset(String iSIN, String assetIssuerTitle, Instant lastAssetBuyDate) {
        this.iSIN = iSIN;
        this.assetIssuerTitle = assetIssuerTitle;
        this.lastAssetBuyDate = lastAssetBuyDate;
//        this.oneAssetCommissionForPurchase = 0.00F;
//        this.totalCommissionForPurchase = 0.00F;
//        this.totalAssetPurchasePriceWithCommission = 0.00;
    }
}

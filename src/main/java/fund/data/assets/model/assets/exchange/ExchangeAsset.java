package fund.data.assets.model.assets.exchange;

import fund.data.assets.model.Asset;
import fund.data.assets.utils.enums.CommissionSystem;

import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
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
    private LocalDate lastAssetBuyDate;

    @Enumerated(EnumType.STRING)
    private CommissionSystem assetCommissionSystem;

    @PositiveOrZero
    private Float totalCommissionForPurchase;

    @NotNull
    @PositiveOrZero
    private Double totalAssetPurchasePriceWithCommission;

    public ExchangeAsset(String iSIN, String assetIssuerTitle, LocalDate lastAssetBuyDate) {
        this.iSIN = iSIN;
        this.assetIssuerTitle = assetIssuerTitle;
        this.lastAssetBuyDate = lastAssetBuyDate;
    }
}

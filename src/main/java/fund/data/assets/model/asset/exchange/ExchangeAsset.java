package fund.data.assets.model.asset.exchange;

import fund.data.assets.model.asset.Asset;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.utils.AutoSelector;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;

import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Getter
@Setter
public abstract class ExchangeAsset extends Asset {
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

    public ExchangeAsset(AssetCurrency assetCurrency, String assetTypeName, String assetTitle, Integer assetCount,
                         TaxSystem assetTaxSystem, Account account, String iSIN, String assetIssuerTitle,
                         LocalDate lastAssetBuyDate) {
        super(assetCurrency, assetTypeName, assetTitle, assetCount, assetTaxSystem, account);

        this.iSIN = iSIN;
        this.assetIssuerTitle = assetIssuerTitle;
        this.lastAssetBuyDate = lastAssetBuyDate;
        this.assetCommissionSystem = (CommissionSystem) AutoSelector.selectAssetOperationsCostSystem(assetCurrency,
                assetTypeName, AutoSelector.COMMISSION_SYSTEM_CHOOSE);
    }
}

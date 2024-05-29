package fund.data.assets.model.asset.exchange;

import fund.data.assets.model.asset.Asset;
import fund.data.assets.model.asset.owner.AssetsOwner;
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

/**
 * Биржевой актив - сущность для начала конкретизации сути актива.
 * Абстрактный класс -  наследник абстрактного Asset.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Getter
@Setter
public abstract class ExchangeAsset extends Asset {
    /**
     * Условия валидации поля соответствуют международному стандарту для ISIN.
     * Источник о стандарте - https://ru.wikipedia.org/wiki/Международный_идентификационный_код_ценной_бумаги
     */
    @NotNull
    @Size(min = 12, max = 12)
    @Pattern(regexp = "^[A-Z]{2}[A-Z0-9]{9}[0-9]$")
    private String iSIN;

    /**
     * Любой биржевой актив имеет эмитента - организацию, его выпустившую. Данное поле содержит его наименование
     * в свободной форме.
     */
    @NotBlank
    private String assetIssuerTitle;

    /**
     * В определении состояния актива на учёте фонда важна дата его последнего приобретения.
     * Её достаточно знать в формате ГГГГ-ММ-ДД.
     */
    @NotNull
    private LocalDate lastAssetBuyDate;

    /**
     * Тип системы сбора комиссии за брокерское и иное обслуживание по активу. Не содержит в себе числовые значения,
     * они находятся в отдельных сущностях. Пока что тип выбирается и инициализируется в конструкторе при создании.
     */
    @Enumerated(EnumType.STRING)
    private CommissionSystem assetCommissionSystem;

    public ExchangeAsset(AssetCurrency assetCurrency, String assetTypeName, String assetTitle, Integer assetCount,
                         TaxSystem assetTaxSystem, Account account, AssetsOwner assetsOwner, String iSIN,
                         String assetIssuerTitle, LocalDate lastAssetBuyDate) {
        super(assetCurrency, assetTypeName, assetTitle, assetCount, assetTaxSystem, account, assetsOwner);

        this.iSIN = iSIN;
        this.assetIssuerTitle = assetIssuerTitle;
        this.lastAssetBuyDate = lastAssetBuyDate;
        this.assetCommissionSystem = (CommissionSystem) AutoSelector.selectAssetOperationsCostSystem(assetCurrency,
                assetTypeName, AutoSelector.COMMISSION_SYSTEM_CHOOSE);
    }
}

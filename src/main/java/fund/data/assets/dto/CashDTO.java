package fund.data.assets.dto;

import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.model.owner.AssetsOwner;
import fund.data.assets.utils.enums.AssetCurrency;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для обслуживания денежных средств собственников активов на счетах.
 * Обслуживаемая сущность - {@link fund.data.assets.model.financial_entities.Cash}.
 * Сервис сущности - {@link fund.data.assets.service.impl.CashServiceImpl}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashDTO {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AssetCurrency assetCurrency;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assets_owner_id", nullable = false)
    private AssetsOwner assetsOwner;

    @NotNull
    @PositiveOrZero
    private Float amount;
}

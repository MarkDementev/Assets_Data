package fund.data.assets.dto.financial_entities;

import fund.data.assets.model.financial_entities.AccountCash;
import fund.data.assets.service.impl.AccountCashServiceImpl;
import fund.data.assets.utils.enums.AssetCurrency;

import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для обслуживания денежных средств собственников активов на счетах.
 * Обслуживаемая сущность - {@link AccountCash}.
 * Сервис сущности - {@link AccountCashServiceImpl}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCashDTO {
    @NotNull
    private Long accountID;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AssetCurrency assetCurrency;

    @NotNull
    private Long assetsOwnerID;

    @NotNull
    private Float amountChangeValue;
}

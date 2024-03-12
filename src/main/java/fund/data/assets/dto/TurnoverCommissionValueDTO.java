package fund.data.assets.dto;

import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.utils.enums.CommissionSystem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoverCommissionValueDTO {
    @NotNull
    private CommissionSystem commissionSystem;

    private Account account;

    @NotBlank
    private String assetTypeName;

    @NotNull
    private Float commissionPercentValue;
}

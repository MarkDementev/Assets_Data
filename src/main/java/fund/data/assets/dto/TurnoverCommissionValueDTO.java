package fund.data.assets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для обслуживания размера комиссии с оборота для типа актива на счёте.
 * Обслуживаемая сущность - {@link fund.data.assets.model.financial_entities.TurnoverCommissionValue}.
 * Сервис сущности - {@link fund.data.assets.service.impl.TurnoverCommissionValueServiceImpl}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoverCommissionValueDTO {
    @NotNull
    private Long accountID;

    @NotBlank
    private String assetTypeName;

    @NotNull
    private String commissionPercentValue;
}

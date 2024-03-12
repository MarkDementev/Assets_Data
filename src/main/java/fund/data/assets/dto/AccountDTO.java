package fund.data.assets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO для обслуживания банковских счетов.
 * Обслуживаемая сущность - {@link fund.data.assets.model.financial_entities.Account}.
 * Сервис сущности - {@link fund.data.assets.service.impl.AccountServiceImpl}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    @NotBlank
    private String organisationWhereAccountOpened;

    @NotBlank
    private String accountNumber;

    @NotNull
    private LocalDate accountOpeningDate;
}

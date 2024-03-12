package fund.data.assets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

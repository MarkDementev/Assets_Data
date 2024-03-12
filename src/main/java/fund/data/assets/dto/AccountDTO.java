package fund.data.assets.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    @NotBlank
    private String organisationWhereAccountOpened;

    @NotBlank
    private String accountNumber;

    @NotNull
//    @JsonFormat(pattern = "dd-MM-yyyy")
//    @Size(min = 10, max = 10)
    private Instant accountOpeningDate;
}

package fund.data.assets.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

import java.time.Instant;

@Data
public class AccountDTO {
    @NotBlank
    private String organisationWhereAccountOpened;

    @NotBlank
    private String accountNumber;

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Size(min = 10, max = 10)
    private Instant accountOpeningDate;

    /*
    Не знаю, надо ли прописывать, если есть в Account Entity - cascade = CascadeType.ALL
     */
//    private Set<Long> assetRelationshipIds;
}

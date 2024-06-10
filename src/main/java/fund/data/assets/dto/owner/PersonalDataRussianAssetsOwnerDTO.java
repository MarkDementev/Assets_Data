package fund.data.assets.dto.owner;

import fund.data.assets.model.owner.RussianAssetsOwner;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.openapitools.jackson.nullable.JsonNullable;

/**
 * DTO для обслуживания обновления личных данных о владельце активов с гражданством РФ.
 * Поддерживает частичное обновление данных сущности.
 * Обслуживаемая сущность - {@link RussianAssetsOwner}.
 * Сервис сущности - {@link fund.data.assets.service.impl.RussianAssetsOwnerServiceImpl}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDataRussianAssetsOwnerDTO {
    @NotBlank
    private JsonNullable<String> name;

    @NotBlank
    private JsonNullable<String> surname;

    @NotBlank
    private JsonNullable<String> patronymic;

    @NotNull
    @Pattern(regexp = "[0-9]{2}\\s?[0-9]{2}")
    private JsonNullable<String> passportSeries;

    @NotNull
    @Pattern(regexp = "[0-9]{6}")
    private JsonNullable<String> passportNumber;

    @NotBlank
    private JsonNullable<String> placeOfPassportGiven;

    @NotBlank
    @Pattern(regexp = "(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.((19|20)\\d\\d)")
    private JsonNullable<String> issueDate;

    @NotNull
    @Pattern(regexp = "[0-9]{3}-[0-9]{3}")
    private JsonNullable<String> issuerOrganisationCode;
}

package fund.data.assets.dto;

import fund.data.assets.utils.enums.RussianSexEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO для обслуживания создания владельца активов с гражданством РФ.
 * Обслуживаемая сущность - {@link fund.data.assets.model.asset.user.RussianAssetsOwner}.
 * Сервис сущности - {@link fund.data.assets.service.impl.RussianAssetsOwnerServiceImpl}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewRussianAssetsOwnerDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    //TODO В LocalDate порядок данных не как в паспорте РФ. Потому надо будет преобразовывать, исходя из типа оунера.
    @NotBlank
    private LocalDate birthDate;

    //TODO Проверь шифрацию.
    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @NotBlank
    private String patronymic;

    @NotNull
    private RussianSexEnum sex;

    //TODO Пропиши валидацию в сервисе с использованием библиотеки libphonenumber. Не получится - пропиши через аннотац.
    @NotNull
    private String phoneNumber;

    @NotNull
    @Pattern(regexp = "[0-9]{2}\\s?[0-9]{2}")
    private String passportSeries;

    @NotNull
    @Pattern(regexp = "[0-9]{6}")
    private String passportNumber;

    @NotBlank
    private String placeOfBirth;

    @NotBlank
    private String placeOfPassportGiven;

    //TODO В LocalDate порядок данных не как в паспорте РФ. Потому надо будет преобразовывать, исходя из типа оунера.
    @NotBlank
    private LocalDate issueDate;

    @NotNull
    @Pattern(regexp = "[0-9]{3}-[0-9]{3}")
    private String issuerOrganisationCode;
}

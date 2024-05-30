package fund.data.assets.model.asset.owner;

import fund.data.assets.utils.converter.StringCryptoConverter;
import fund.data.assets.utils.enums.RussianSexEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Convert;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.context.annotation.Primary;

import java.time.LocalDate;

/**
 * Сущность - собственник активов - гражданин РФ. Для идентификации используются данные из паспорта гражданина РФ.
 * Класс - наследник абстрактного AssetsOwner. Используется по дефолту (@Primary).
 * @since 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Primary
@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"passport_series", "passport_number", "place_of_birth",
        "place_of_passport_given", "issue_date", "issuer_organisation_code"})})
public class RussianAssetsOwner extends AssetsOwner {
    @NotBlank
    private String patronymic;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RussianSexEnum sex;

    /**
     * Номер мобильного телефона в формате ХХХ-ХХХ-ХХ-ХХ без +7. +7 добавляет сервис при операциях с полем.
     */
    //TODO Учти одновременность юниг и шифровки
    @NotNull
//    @Convert(converter = StringCryptoConverter.class)
    @Column(unique = true)
    private String mobilePhoneNumber;

    /**
     * Можно вводить как с пробелом между 2-й и 3-й цифрами (как напечатано в паспорте), так и подряд все 4 цифры.
     */
    //TODO Учти одновременность юниг и шифровки
    @NotNull
//    @Convert(converter = StringCryptoConverter.class)
    private String passportSeries;

    //TODO Учти одновременность юниг и шифровки
    @NotNull
//    @Convert(converter = StringCryptoConverter.class)
    private String passportNumber;

    @NotBlank
    private String placeOfBirth;

    @NotBlank
    private String placeOfPassportGiven;

    @NotNull
    private LocalDate issueDate;

    @NotNull
    private String issuerOrganisationCode;

    public RussianAssetsOwner(String name, String surname, LocalDate birthDate, String email, String patronymic,
                              RussianSexEnum sex, String mobilePhoneNumber, String passportSeries, String passportNumber,
                              String placeOfBirth, String placeOfPassportGiven, LocalDate issueDate,
                              String issuerOrganisationCode) {
        super(name, surname, birthDate, email);

        this.patronymic = patronymic;
        this.sex = sex;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.passportSeries = passportSeries;
        this.passportNumber = passportNumber;
        this.placeOfBirth = placeOfBirth;
        this.placeOfPassportGiven = placeOfPassportGiven;
        this.issueDate = issueDate;
        this.issuerOrganisationCode = issuerOrganisationCode;
    }
}

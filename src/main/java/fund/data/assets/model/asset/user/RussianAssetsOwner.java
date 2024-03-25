package fund.data.assets.model.asset.user;

import fund.data.assets.utils.converter.StringCryptoConverter;
import fund.data.assets.utils.enums.RussianSexEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Convert;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

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
@Table(name = "Asset owners with Russia Federation citizenship")
@NoArgsConstructor
@Getter
@Setter
public class RussianAssetsOwner extends AssetsOwner {
    @NotBlank
    private String patronymic;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RussianSexEnum sex;

    /**
     * Номер мобильного телефона в формате ХХХ-ХХХ-ХХ-ХХ без +7. +7 добавляет сервис при операциях с полем.
     */
    //TODO Проверь - возможно, регексп надо оставить только в ДТО, т.к. здесь идёт шифрование
    @NotNull
    @Pattern(regexp = "9[0-9]{9}")
    @Convert(converter = StringCryptoConverter.class)
    private String mobilePhoneNumber;

    /**
     * Можно вводить как с пробелом между 2-й и 3-й цифрами (как напечатано в паспорте), так и подряд все 4 цифры.
     */
    //TODO Проверь - возможно, регексп надо оставить только в ДТО, т.к. здесь идёт шифрование
    @NotNull
    @Pattern(regexp = "[0-9]{2}\\s?[0-9]{2}")
    @Convert(converter = StringCryptoConverter.class)
    private String passportSeries;

    //TODO Проверь - возможно, регексп надо оставить только в ДТО, т.к. здесь идёт шифрование
    @NotNull
    @Pattern(regexp = "[0-9]{6}")
    @Convert(converter = StringCryptoConverter.class)
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

    public RussianAssetsOwner(String name, String surname, LocalDate birthDate, String email, String patronymic,
                              RussianSexEnum sex, String mobilePhoneNumber, String passportSeries, String passportNumber,
                              String placeOfBirth, String placeOfPassportGiven) {
        super(name, surname, birthDate, email);

        this.patronymic = patronymic;
        this.sex = sex;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.passportSeries = passportSeries;
        this.passportNumber = passportNumber;
        this.placeOfBirth = placeOfBirth;
        this.placeOfPassportGiven = placeOfPassportGiven;
    }

    public RussianAssetsOwner(String name, String surname, LocalDate birthDate, String patronymic,
                              RussianSexEnum sex, String mobilePhoneNumber, String passportSeries, String passportNumber,
                              String placeOfBirth, String placeOfPassportGiven) {
        super(name, surname, birthDate);

        this.patronymic = patronymic;
        this.sex = sex;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.passportSeries = passportSeries;
        this.passportNumber = passportNumber;
        this.placeOfBirth = placeOfBirth;
        this.placeOfPassportGiven = placeOfPassportGiven;
    }
}

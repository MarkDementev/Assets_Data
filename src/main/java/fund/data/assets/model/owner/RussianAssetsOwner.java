package fund.data.assets.model.owner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fund.data.assets.utils.converter.StringCryptoConverter;
import fund.data.assets.utils.enums.RussianSexEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Convert;
import jakarta.persistence.Table;

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
@Table(name = "russian_assets_owners")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @NotNull
    @Convert(converter = StringCryptoConverter.class)
    private String mobilePhoneNumber;

    /**
     * Можно вводить как с пробелом между 2-й и 3-й цифрами (как напечатано в паспорте), так и подряд все 4 цифры.
     */
    @NotNull
    @Convert(converter = StringCryptoConverter.class)
    private String passportSeries;

    @NotNull
    @Convert(converter = StringCryptoConverter.class)
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

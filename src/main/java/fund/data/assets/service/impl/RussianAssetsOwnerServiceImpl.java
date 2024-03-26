package fund.data.assets.service.impl;

import fund.data.assets.dto.NewRussianAssetsOwnerDTO;
import fund.data.assets.model.asset.user.RussianAssetsOwner;
import fund.data.assets.repository.RussianAssetsOwnerRepository;
import fund.data.assets.service.RussianAssetsOwnerService;
import fund.data.assets.utils.enums.RussianSexEnum;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Реализация сервиса для обслуживания владельца активов с гражданством РФ.
 * Обслуживаемая сущность - {@link fund.data.assets.model.asset.user.RussianAssetsOwner}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Service
@RequiredArgsConstructor
public class RussianAssetsOwnerServiceImpl implements RussianAssetsOwnerService {
    public static final String RUSSIAN_MOBILE_PHONE_PREFIX = "+7";
    public static final String WRONG_DATES_WARNING = "This is error - issueDate doesn't before birthDate!";
    private final RussianAssetsOwnerRepository russianAssetsOwnerRepository;

    @Override
    public RussianAssetsOwner getRussianAssetsOwner(Long id) {
        return russianAssetsOwnerRepository.findById(id).orElseThrow();
    }

    @Override
    public List<RussianAssetsOwner> getRussianAssetsOwners() {
        return russianAssetsOwnerRepository.findAll();
    }

    @Override
    public RussianAssetsOwner createRussianAssetsOwner(NewRussianAssetsOwnerDTO newRussianAssetsOwnerDTO) {
        String name = newRussianAssetsOwnerDTO.getName();
        String surname = newRussianAssetsOwnerDTO.getSurname();
        LocalDate birthDate = parseDatePassportFormatIntoLocalDate(newRussianAssetsOwnerDTO.getBirthDate());
        String email = newRussianAssetsOwnerDTO.getEmail();
        String patronymic = newRussianAssetsOwnerDTO.getPatronymic();
        RussianSexEnum sex = newRussianAssetsOwnerDTO.getSex();
        String mobilePhoneNumber = addRussianNumberPrefixPhoneNumber(newRussianAssetsOwnerDTO.getMobilePhoneNumber());
        String passportSeries = newRussianAssetsOwnerDTO.getPassportSeries();
        String passportNumber = newRussianAssetsOwnerDTO.getPassportNumber();
        String placeOfBirth = newRussianAssetsOwnerDTO.getPlaceOfBirth();
        String placeOfPassportGiven = newRussianAssetsOwnerDTO.getPlaceOfPassportGiven();
        LocalDate issueDate = parseDatePassportFormatIntoLocalDate(newRussianAssetsOwnerDTO.getIssueDate());
        String issuerOrganisationCode = newRussianAssetsOwnerDTO.getIssuerOrganisationCode();

        if (ChronoUnit.DAYS.between(birthDate, issueDate) < 0) {
            throw new IllegalArgumentException(WRONG_DATES_WARNING);
        }

        RussianAssetsOwner russianAssetsOwner = new RussianAssetsOwner(name, surname, birthDate, email, patronymic,
                sex, mobilePhoneNumber, passportSeries, passportNumber, placeOfBirth, placeOfPassportGiven, issueDate,
                issuerOrganisationCode);

        return russianAssetsOwnerRepository.save(russianAssetsOwner);
    }

//    @Override
//    public RussianAssetsOwner updatePersonalDataOfRussianAssetsOwner(
//            Long id, PersonalDataRussianAssetsOwnerDTO personalDataRussianAssetsOwnerDTO) {
//        return null;
//    }

//    @Override
//    public RussianAssetsOwner addNewAssetToRussianAssetsOwner(
//            Long id, NewAssetDataRussianAssetsOwnerDTO russianAssetsOwnerDTO) {
//        //Приходит DTO только с новым Asset
//        return null;
//    }

    //Возможно, нужен метод для удаления AssetRelationship!?

    @Override
    public void deleteRussianAssetsOwner(Long id) {
        russianAssetsOwnerRepository.deleteById(id);
    }

    /**
     * В паспорте дата рождения оунера записывается в ином, чем в LocalDate, формате. Поэтому эта дата должна
     * быть преобразована в нужный формат для последующего хранения.
     * @param stringDate дата рождения оунера, записанная, как в паспорте РФ.
     * @return дату в формате LocalDate.
     * @since 0.0.1-alpha
     */
    private LocalDate parseDatePassportFormatIntoLocalDate(String stringDate) {
        DateTimeFormatter fromRFPassportFormatToLocalDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        return LocalDate.parse(stringDate, fromRFPassportFormatToLocalDateFormatter);
    }

    /**
     * Чтобы не вводить префикс и не проводить сложную валидацию на фронте, номер вводится в формате ХХХ-ХХХ-ХХ-ХХ
     *  без +7 и приходит через DTO как String. Данный метод собирает его, добавляя префикс +7.
     * @param mobilePhoneNumber
     * @return номер мобильного телефона РФ с добавлением префикса +7.
     * @since 0.0.1-alpha
     */
    private String addRussianNumberPrefixPhoneNumber(String mobilePhoneNumber) {
        return RUSSIAN_MOBILE_PHONE_PREFIX + mobilePhoneNumber;
    }
}

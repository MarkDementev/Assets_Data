package fund.data.assets.service.impl;

import fund.data.assets.dto.NewRussianAssetsOwnerDTO;
import fund.data.assets.model.asset.user.RussianAssetsOwner;
import fund.data.assets.repository.RussianAssetsOwnerRepository;
import fund.data.assets.service.RussianAssetsOwnerService;
import fund.data.assets.utils.enums.RussianSexEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public final String RUSSIAN_MOBILE_PHONE_PREFIX = "+7";
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
        LocalDate birthDate = parseBirthDate(newRussianAssetsOwnerDTO.getBirthDate());
        String email = newRussianAssetsOwnerDTO.getEmail();
        String patronymic = newRussianAssetsOwnerDTO.getPatronymic();
        RussianSexEnum sex = newRussianAssetsOwnerDTO.getSex();
        String mobilePhoneNumber = addRussianNumberPrefixPhoneNumber(newRussianAssetsOwnerDTO.getMobilePhoneNumber());
        String passportSeries = newRussianAssetsOwnerDTO.getPassportSeries();
        String passportNumber = newRussianAssetsOwnerDTO.getPassportNumber();
        String placeOfBirth = newRussianAssetsOwnerDTO.getPlaceOfBirth();
        String placeOfPassportGiven = newRussianAssetsOwnerDTO.getPlaceOfPassportGiven();
//        private LocalDate issueDate;
//        private String issuerOrganisationCode;
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
//
//    @Override
//    public RussianAssetsOwner addNewAssetToRussianAssetsOwner(
//            Long id, NewAssetDataRussianAssetsOwnerDTO russianAssetsOwnerDTO) {
//        return null;
//    }

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
    private LocalDate parseBirthDate(String stringDate) {
        DateTimeFormatter fromRFPassportFormatToLocalDate = DateTimeFormatter.ofPattern("dd.MM.-yyyy");

        return LocalDate.parse(stringDate, fromRFPassportFormatToLocalDate);
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

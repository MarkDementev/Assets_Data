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
        this.phoneNumber = phoneNumber;
        this.passportSeries = passportSeries;
        this.passportNumber = passportNumber;
        this.placeOfBirth = placeOfBirth;
        this.placeOfPassportGiven = placeOfPassportGiven;

        RussianAssetsOwner russianAssetsOwner = new RussianAssetsOwner();

        return russianAssetsOwnerRepository.save(russianAssetsOwner);

//       public RussianAssetsOwner(String name, String surname, LocalDate birthDate, String email, String patronymic,
//            RussianSexEnum sex, String phoneNumber, String passportSeries, String passportNumber,
//            String placeOfBirth, String placeOfPassportGiven) {
//            super(name, surname, birthDate, email);
//
//            this.patronymic = patronymic;
//            this.sex = sex;
//            this.phoneNumber = phoneNumber;
//            this.passportSeries = passportSeries;
//            this.passportNumber = passportNumber;
//            this.placeOfBirth = placeOfBirth;
//            this.placeOfPassportGiven = placeOfPassportGiven;
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
}

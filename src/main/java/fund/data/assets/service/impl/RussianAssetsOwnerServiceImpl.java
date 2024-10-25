package fund.data.assets.service.impl;

import fund.data.assets.dto.owner.ContactDataRussianAssetsOwnerDTO;
import fund.data.assets.dto.owner.PersonalDataRussianAssetsOwnerDTO;
import fund.data.assets.dto.owner.NewRussianAssetsOwnerDTO;
import fund.data.assets.exception.EntityWithIDNotFoundException;
import fund.data.assets.model.owner.RussianAssetsOwner;
import fund.data.assets.repository.RussianAssetsOwnerRepository;
import fund.data.assets.service.RussianAssetsOwnerService;
import fund.data.assets.utils.enums.RussianSexEnum;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Реализация сервиса для обслуживания владельца активов с гражданством РФ.
 * Обслуживаемая сущность - {@link RussianAssetsOwner}.
 * @version 0.0.2-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Service
@RequiredArgsConstructor
public class RussianAssetsOwnerServiceImpl implements RussianAssetsOwnerService {
    public static final String RUSSIAN_MOBILE_PHONE_PREFIX = "+7";
    public static final String WRONG_DATES_WARNING = "This is error - issueDate doesn't before birthDate!";
    public static final String NOT_UNIQUE_RF_PASSPORT_WARNING = "This is error - client with this RF passport already" +
            " added into system!";
    private final RussianAssetsOwnerRepository russianAssetsOwnerRepository;

    @Override
    public RussianAssetsOwner getRussianAssetsOwner(Long id) {
        return russianAssetsOwnerRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException(
                "RussianAssetsOwner", id));
    }

    @Override
    public List<RussianAssetsOwner> getRussianAssetsOwners() {
        return russianAssetsOwnerRepository.findAll();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
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

        checkUniquenessRFPassportFields(passportSeries, passportNumber, placeOfBirth, placeOfPassportGiven, issueDate,
                issuerOrganisationCode);
        if (ChronoUnit.DAYS.between(birthDate, issueDate) < 0) {
            throw new IllegalArgumentException(WRONG_DATES_WARNING);
        }

        AtomicReference<RussianAssetsOwner> atomicRussianAssetsOwner = new AtomicReference<>(new RussianAssetsOwner(
                name, surname, birthDate, email, patronymic, sex, mobilePhoneNumber, passportSeries, passportNumber,
                placeOfBirth, placeOfPassportGiven, issueDate, issuerOrganisationCode));

        return russianAssetsOwnerRepository.save(atomicRussianAssetsOwner.get());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public RussianAssetsOwner updateRussianAssetsOwnerPersonalData(Long id, PersonalDataRussianAssetsOwnerDTO
            personalDataRussianAssetsOwnerDTO) {
        AtomicReference<RussianAssetsOwner> atomicRussianAssetsOwnerToUpdate = new AtomicReference<>(
                russianAssetsOwnerRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException(
                        "RussianAssetsOwner", id))
        );

        atomicRussianAssetsOwnerToUpdate.get().setName(personalDataRussianAssetsOwnerDTO.getName().get());
        atomicRussianAssetsOwnerToUpdate.get().setSurname(personalDataRussianAssetsOwnerDTO.getSurname().get());
        atomicRussianAssetsOwnerToUpdate.get().setPatronymic(personalDataRussianAssetsOwnerDTO.getPatronymic().get());
        atomicRussianAssetsOwnerToUpdate.get().setPassportSeries(personalDataRussianAssetsOwnerDTO
                .getPassportSeries().get());
        atomicRussianAssetsOwnerToUpdate.get().setPassportNumber(personalDataRussianAssetsOwnerDTO
                .getPassportNumber().get());
        atomicRussianAssetsOwnerToUpdate.get().setPlaceOfPassportGiven(personalDataRussianAssetsOwnerDTO
                .getPlaceOfPassportGiven().get());
        atomicRussianAssetsOwnerToUpdate.get().setIssueDate(parseDatePassportFormatIntoLocalDate(
                personalDataRussianAssetsOwnerDTO.getIssueDate().get()));
        atomicRussianAssetsOwnerToUpdate.get().setIssuerOrganisationCode(personalDataRussianAssetsOwnerDTO
                .getIssuerOrganisationCode().get());

        return russianAssetsOwnerRepository.save(atomicRussianAssetsOwnerToUpdate.get());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = {Exception.class})
    public RussianAssetsOwner updateRussianAssetsOwnerContactData(Long id, ContactDataRussianAssetsOwnerDTO
            contactDataRussianAssetsOwnerDTO) {
        AtomicReference<RussianAssetsOwner> atomicRussianAssetsOwnerToUpdate = new AtomicReference<>(
                russianAssetsOwnerRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException(
                        "RussianAssetsOwner", id))
        );

        atomicRussianAssetsOwnerToUpdate.get().setEmail(contactDataRussianAssetsOwnerDTO.getEmail().get());
        atomicRussianAssetsOwnerToUpdate.get().setMobilePhoneNumber(addRussianNumberPrefixPhoneNumber(
                contactDataRussianAssetsOwnerDTO.getMobilePhoneNumber().get()));

        return russianAssetsOwnerRepository.save(atomicRussianAssetsOwnerToUpdate.get());
    }

    @Override
    public void deleteRussianAssetsOwner(Long id) {
        russianAssetsOwnerRepository.deleteById(id);
    }

    /**
     * В паспорте даты записываются в ином, чем в LocalDate, формате. Поэтому эта дата должна
     * быть преобразована в нужный формат для последующего хранения.
     * @param stringDate дата, записанная, как в паспорте РФ.
     * @return дату в формате LocalDate.
     * @since 0.0.1-alpha
     */
    @Override
    public LocalDate parseDatePassportFormatIntoLocalDate(String stringDate) {
        DateTimeFormatter fromRFPassportFormatToLocalDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        return LocalDate.parse(stringDate, fromRFPassportFormatToLocalDateFormatter);
    }

    /**
     * Чтобы не вводить префикс и не проводить сложную валидацию на фронте, номер вводится в формате ХХХ-ХХХ-ХХ-ХХ
     *  без +7 и приходит через DTO как String. Данный метод собирает его, добавляя префикс +7.
     * @param mobilePhoneNumber цифры номера мобильного телефона без добавления префикса
     * @return номер мобильного телефона РФ с добавлением префикса +7.
     * @since 0.0.1-alpha
     */
    @Override
    public String addRussianNumberPrefixPhoneNumber(String mobilePhoneNumber) {
        return RUSSIAN_MOBILE_PHONE_PREFIX + mobilePhoneNumber;
    }

    /**
     * Валидация одновременной уникальности новых полей таблицы, определяющих паспорт РФ, реализована не на уровнях
     * DTO или Entity, а в этом методе, т.к. необходимо работать с частично зашифрованными Jasypt данными, что сложно
     * легко написать на других уровнях реализации.
     * @param passportSeries серия паспорта РФ
     * @param passportNumber номер паспорта РФ
     * @param placeOfBirth место рождения
     * @param placeOfPassportGiven место выдачи паспорта
     * @param issueDate дата выдачи паспорта
     * @param issuerOrganisationCode код организации, выдавшей паспорт
     * @since 0.0.1-alpha
     */
    //TODO мб можно переписать с использованием Predicate и коллекции какой-нибудь?
    @Override
    public void checkUniquenessRFPassportFields(String passportSeries, String passportNumber, String placeOfBirth,
                               String placeOfPassportGiven, LocalDate issueDate, String issuerOrganisationCode) {
        List<RussianAssetsOwner> russianAssetsOwnerList = russianAssetsOwnerRepository.findAll();

        for (RussianAssetsOwner ownerFromDB : russianAssetsOwnerList) {
            if ((ownerFromDB.getPassportSeries().equals(passportSeries))
                    && (ownerFromDB.getPassportNumber().equals(passportNumber))
                    && (ownerFromDB.getPlaceOfBirth().equals(placeOfBirth))
                    && (ownerFromDB.getPlaceOfPassportGiven().equals(placeOfPassportGiven))
                    && (ownerFromDB.getIssueDate().equals(issueDate))
                    && (ownerFromDB.getIssuerOrganisationCode().equals(issuerOrganisationCode))) {
                throw new IllegalArgumentException(NOT_UNIQUE_RF_PASSPORT_WARNING);
            }
        }
    }
}

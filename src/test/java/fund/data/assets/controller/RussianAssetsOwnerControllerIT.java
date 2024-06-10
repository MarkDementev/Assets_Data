package fund.data.assets.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.dto.owner.NewRussianAssetsOwnerDTO;
import fund.data.assets.model.owner.RussianAssetsOwner;
import fund.data.assets.repository.RussianAssetsOwnerRepository;
import fund.data.assets.service.RussianAssetsOwnerService;

import jakarta.servlet.ServletException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static fund.data.assets.TestUtils.fromJson;
import static fund.data.assets.TestUtils.asJson;
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;
import static fund.data.assets.controller.RussianAssetsOwnerController.ID_PATH;
import static fund.data.assets.controller.RussianAssetsOwnerController.CONTACT_DATA_PATH;
import static fund.data.assets.controller.RussianAssetsOwnerController.PERSONAL_DATA_PATH;
import static fund.data.assets.controller.RussianAssetsOwnerController.RUSSIAN_OWNERS_CONTROLLER_PATH;
import static fund.data.assets.utils.enums.RussianSexEnum.WOMAN;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringConfigForTests.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class RussianAssetsOwnerControllerIT {
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private RussianAssetsOwnerRepository russianAssetsOwnerRepository;
    @Autowired
    private RussianAssetsOwnerService russianAssetsOwnerService;

    @AfterEach
    public void clearRepositories() {
        testUtils.tearDown();
    }

    @Test
    public void getRussianAssetsOwnerIT() throws Exception {
        testUtils.createDefaultRussianAssetsOwner();

        RussianAssetsOwner expectedRussianAssetsOwner = russianAssetsOwnerRepository.findAll().get(0);
        var response = testUtils.perform(
                get("/data" + RUSSIAN_OWNERS_CONTROLLER_PATH + ID_PATH, expectedRussianAssetsOwner.getId())
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        RussianAssetsOwner russianAssetsOwnerFromResponse = fromJson(response.getContentAsString(),
                new TypeReference<>() {});

        assertEquals(expectedRussianAssetsOwner.getId(), russianAssetsOwnerFromResponse.getId());
        assertEquals(expectedRussianAssetsOwner.getName(), russianAssetsOwnerFromResponse.getName());
        assertEquals(expectedRussianAssetsOwner.getSurname(), russianAssetsOwnerFromResponse.getSurname());
        assertEquals(expectedRussianAssetsOwner.getBirthDate(), russianAssetsOwnerFromResponse.getBirthDate());
        assertEquals(expectedRussianAssetsOwner.getEmail(), russianAssetsOwnerFromResponse.getEmail());
        assertNotNull(russianAssetsOwnerFromResponse.getAssetRelationships());
        assertNotNull(russianAssetsOwnerFromResponse.getCreatedAt());
        assertNotNull(russianAssetsOwnerFromResponse.getUpdatedAt());
        assertEquals(expectedRussianAssetsOwner.getPatronymic(), russianAssetsOwnerFromResponse.getPatronymic());
        assertEquals(expectedRussianAssetsOwner.getSex(), russianAssetsOwnerFromResponse.getSex());
        assertEquals(expectedRussianAssetsOwner.getMobilePhoneNumber(),
                russianAssetsOwnerFromResponse.getMobilePhoneNumber());
        assertEquals(expectedRussianAssetsOwner.getPassportSeries(),
                russianAssetsOwnerFromResponse.getPassportSeries());
        assertEquals(expectedRussianAssetsOwner.getPassportNumber(),
                russianAssetsOwnerFromResponse.getPassportNumber());
        assertEquals(expectedRussianAssetsOwner.getPlaceOfBirth(), russianAssetsOwnerFromResponse.getPlaceOfBirth());
        assertEquals(expectedRussianAssetsOwner.getPlaceOfPassportGiven(),
                russianAssetsOwnerFromResponse.getPlaceOfPassportGiven());
        assertEquals(expectedRussianAssetsOwner.getIssueDate(), russianAssetsOwnerFromResponse.getIssueDate());
        assertEquals(expectedRussianAssetsOwner.getIssuerOrganisationCode(),
                russianAssetsOwnerFromResponse.getIssuerOrganisationCode());
    }

    @Test
    public void getRussianAssetsOwnersIT() throws Exception {
        testUtils.createDefaultRussianAssetsOwner();

        var response = testUtils.perform(
                        get("/data" + RUSSIAN_OWNERS_CONTROLLER_PATH)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        List <RussianAssetsOwner> allRussianAssetsOwnersFromResponse = fromJson(response.getContentAsString(),
                new TypeReference<>() {});

        assertThat(allRussianAssetsOwnersFromResponse).hasSize(1);
    }

    @Test
    public void createRussianAssetsOwnerIT() throws Exception {
        var response = testUtils.perform(
                        post("/data" + RUSSIAN_OWNERS_CONTROLLER_PATH)
                                .content(asJson(testUtils.getNewRussianAssetsOwnerDTO()))
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        RussianAssetsOwner russianAssetsOwnerFromResponse = fromJson(response.getContentAsString(),
                new TypeReference<>() {});

        assertNotNull(russianAssetsOwnerFromResponse.getId());
        assertEquals(russianAssetsOwnerFromResponse.getName(), testUtils.getNewRussianAssetsOwnerDTO().getName());
        assertEquals(russianAssetsOwnerFromResponse.getSurname(), testUtils.getNewRussianAssetsOwnerDTO().getSurname());
        assertEquals(russianAssetsOwnerFromResponse.getBirthDate(),
                russianAssetsOwnerService.parseDatePassportFormatIntoLocalDate(
                        testUtils.getNewRussianAssetsOwnerDTO().getBirthDate()));
        assertEquals(russianAssetsOwnerFromResponse.getEmail(), testUtils.getNewRussianAssetsOwnerDTO().getEmail());
        assertNotNull(russianAssetsOwnerFromResponse.getAssetRelationships());
        assertNotNull(russianAssetsOwnerFromResponse.getCreatedAt());
        assertNotNull(russianAssetsOwnerFromResponse.getUpdatedAt());
        assertEquals(russianAssetsOwnerFromResponse.getPatronymic(),
                testUtils.getNewRussianAssetsOwnerDTO().getPatronymic());
        assertEquals(russianAssetsOwnerFromResponse.getSex(), testUtils.getNewRussianAssetsOwnerDTO().getSex());
        assertEquals(russianAssetsOwnerFromResponse.getMobilePhoneNumber(),
                russianAssetsOwnerService.addRussianNumberPrefixPhoneNumber(
                        testUtils.getNewRussianAssetsOwnerDTO().getMobilePhoneNumber()));
        assertEquals(russianAssetsOwnerFromResponse.getPassportSeries(),
                testUtils.getNewRussianAssetsOwnerDTO().getPassportSeries());
        assertEquals(russianAssetsOwnerFromResponse.getPassportNumber(),
                testUtils.getNewRussianAssetsOwnerDTO().getPassportNumber());
        assertEquals(russianAssetsOwnerFromResponse.getPlaceOfBirth(),
                testUtils.getNewRussianAssetsOwnerDTO().getPlaceOfBirth());
        assertEquals(russianAssetsOwnerFromResponse.getPlaceOfPassportGiven(),
                testUtils.getNewRussianAssetsOwnerDTO().getPlaceOfPassportGiven());
        assertEquals(russianAssetsOwnerFromResponse.getIssueDate(),
                russianAssetsOwnerService.parseDatePassportFormatIntoLocalDate(
                        testUtils.getNewRussianAssetsOwnerDTO().getIssueDate()));
        assertEquals(russianAssetsOwnerFromResponse.getIssuerOrganisationCode(),
                testUtils.getNewRussianAssetsOwnerDTO().getIssuerOrganisationCode());
    }

    @Test
    public void createNotValidRussianAssetsOwnerIT() throws Exception {
        testUtils.perform(post("/data" + RUSSIAN_OWNERS_CONTROLLER_PATH)
                        .content(asJson(testUtils.getNotValidNewRussianAssetsOwnerDTO()))
                        .contentType(APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
        assertThat(russianAssetsOwnerRepository.findAll()).hasSize(0);

        testUtils.createDefaultRussianAssetsOwner();
        assertThat(russianAssetsOwnerRepository.findAll()).hasSize(1);

        NewRussianAssetsOwnerDTO newRussianAssetsOwnerDTOWithAlreadyExistsPassportData
                = new NewRussianAssetsOwnerDTO(
                "another",
                "another",
                "24.05.1995",
                "Mark_email@mail.ru",
                "another",
                WOMAN,
                "9988888888",
                testUtils.getNewRussianAssetsOwnerDTO().getPassportSeries(),
                testUtils.getNewRussianAssetsOwnerDTO().getPassportNumber(),
                testUtils.getNewRussianAssetsOwnerDTO().getPlaceOfBirth(),
                testUtils.getNewRussianAssetsOwnerDTO().getPlaceOfPassportGiven(),
                testUtils.getNewRussianAssetsOwnerDTO().getIssueDate(),
                testUtils.getNewRussianAssetsOwnerDTO().getIssuerOrganisationCode()
        );
        Assertions.assertThrows(ServletException.class,
                () -> testUtils.perform(post("/data" + RUSSIAN_OWNERS_CONTROLLER_PATH)
                        .content(asJson(newRussianAssetsOwnerDTOWithAlreadyExistsPassportData))
                        .contentType(APPLICATION_JSON)));
        assertThat(russianAssetsOwnerRepository.findAll()).hasSize(1);

        NewRussianAssetsOwnerDTO newRussianAssetsOwnerDTOWithAlreadyExistsPhoneAndEmail
                = new NewRussianAssetsOwnerDTO(
                "another",
                "another",
                "24.05.1995",
                testUtils.getNewRussianAssetsOwnerDTO().getEmail(),
                "another",
                WOMAN,
                testUtils.getNewRussianAssetsOwnerDTO().getMobilePhoneNumber(),
                "2425",
                "111112",
                "another",
                "another",
                "25.08.2021",
                "377-778"
        );
        testUtils.perform(post("/data" + RUSSIAN_OWNERS_CONTROLLER_PATH)
                        .content(asJson(newRussianAssetsOwnerDTOWithAlreadyExistsPhoneAndEmail))
                        .contentType(APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
        assertThat(russianAssetsOwnerRepository.findAll()).hasSize(1);
    }

    @Test
    public void updateRussianAssetsOwnerIT() throws Exception {
        testUtils.createDefaultRussianAssetsOwner();

        Long createdRussianAssetsOwnerId = russianAssetsOwnerRepository.findAll().get(0).getId();
        var responseUpdateContactData = testUtils.perform(put("/data" + RUSSIAN_OWNERS_CONTROLLER_PATH
                        + CONTACT_DATA_PATH + ID_PATH, createdRussianAssetsOwnerId)
                        .content(asJson(testUtils.getValidRussianAssetsOwnerContactDataDTO()))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        RussianAssetsOwner russianAssetsOwnerWithUpdatedContactDataFromResponse = fromJson(responseUpdateContactData
                        .getContentAsString(), new TypeReference<>() {});

        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getEmail(),
                testUtils.getValidRussianAssetsOwnerContactDataDTO().getEmail().get());
        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getMobilePhoneNumber(),
                russianAssetsOwnerService.addRussianNumberPrefixPhoneNumber(testUtils
                        .getValidRussianAssetsOwnerContactDataDTO().getMobilePhoneNumber().get()));

        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getId(), createdRussianAssetsOwnerId);
        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getName(),
                testUtils.getNewRussianAssetsOwnerDTO().getName());
        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getSurname(),
                testUtils.getNewRussianAssetsOwnerDTO().getSurname());
        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getBirthDate(),
                russianAssetsOwnerService.parseDatePassportFormatIntoLocalDate(testUtils.getNewRussianAssetsOwnerDTO()
                        .getBirthDate()));
        assertNotNull(russianAssetsOwnerWithUpdatedContactDataFromResponse.getAssetRelationships());
        assertNotNull(russianAssetsOwnerWithUpdatedContactDataFromResponse.getCreatedAt());
        assertNotNull(russianAssetsOwnerWithUpdatedContactDataFromResponse.getUpdatedAt());
        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getPatronymic(),
                testUtils.getNewRussianAssetsOwnerDTO().getPatronymic());
        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getSex(),
                testUtils.getNewRussianAssetsOwnerDTO().getSex());
        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getPassportSeries(),
                testUtils.getNewRussianAssetsOwnerDTO().getPassportSeries());
        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getPassportNumber(),
                testUtils.getNewRussianAssetsOwnerDTO().getPassportNumber());
        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getPlaceOfBirth(),
                testUtils.getNewRussianAssetsOwnerDTO().getPlaceOfBirth());
        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getPlaceOfPassportGiven(),
                testUtils.getNewRussianAssetsOwnerDTO().getPlaceOfPassportGiven());
        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getIssueDate(),
                russianAssetsOwnerService.parseDatePassportFormatIntoLocalDate(testUtils.getNewRussianAssetsOwnerDTO()
                        .getIssueDate()));
        assertEquals(russianAssetsOwnerWithUpdatedContactDataFromResponse.getIssuerOrganisationCode(),
                testUtils.getNewRussianAssetsOwnerDTO().getIssuerOrganisationCode());

        var responseUpdatePersonalData = testUtils.perform(put("/data" + RUSSIAN_OWNERS_CONTROLLER_PATH
                                + PERSONAL_DATA_PATH + ID_PATH, createdRussianAssetsOwnerId)
                        .content(asJson(testUtils.getValidRussianAssetsOwnerPersonalDataDTO()))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        RussianAssetsOwner russianAssetsOwnerWithUpdatedPersonalDataFromResponse = fromJson(responseUpdatePersonalData
                        .getContentAsString(), new TypeReference<>() {});

        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getName(),
                testUtils.getValidRussianAssetsOwnerPersonalDataDTO().getName().get());
        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getSurname(),
                testUtils.getValidRussianAssetsOwnerPersonalDataDTO().getSurname().get());
        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getPatronymic(),
                testUtils.getValidRussianAssetsOwnerPersonalDataDTO().getPatronymic().get());
        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getPassportSeries(),
                testUtils.getValidRussianAssetsOwnerPersonalDataDTO().getPassportSeries().get());
        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getPassportNumber(),
                testUtils.getValidRussianAssetsOwnerPersonalDataDTO().getPassportNumber().get());
        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getPlaceOfPassportGiven(),
                testUtils.getValidRussianAssetsOwnerPersonalDataDTO().getPlaceOfPassportGiven().get());
        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getIssueDate(),
                russianAssetsOwnerService.parseDatePassportFormatIntoLocalDate(testUtils
                        .getValidRussianAssetsOwnerPersonalDataDTO().getIssueDate().get()));
        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getIssuerOrganisationCode(),
                testUtils.getValidRussianAssetsOwnerPersonalDataDTO().getIssuerOrganisationCode().get());

        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getId(), createdRussianAssetsOwnerId);
        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getBirthDate(),
                russianAssetsOwnerService.parseDatePassportFormatIntoLocalDate(testUtils.getNewRussianAssetsOwnerDTO()
                        .getBirthDate()));
        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getEmail(),
                testUtils.getValidRussianAssetsOwnerContactDataDTO().getEmail().get());
        assertNotNull(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getAssetRelationships());
        assertNotNull(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getCreatedAt());
        assertNotNull(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getUpdatedAt());
        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getSex(),
                testUtils.getNewRussianAssetsOwnerDTO().getSex());
        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getMobilePhoneNumber(),
                russianAssetsOwnerService.addRussianNumberPrefixPhoneNumber(testUtils
                        .getValidRussianAssetsOwnerContactDataDTO().getMobilePhoneNumber().get()));
        assertEquals(russianAssetsOwnerWithUpdatedPersonalDataFromResponse.getPlaceOfBirth(),
                testUtils.getNewRussianAssetsOwnerDTO().getPlaceOfBirth());
    }

    @Test
    public void notValidUpdateRussianAssetsOwnerIT() throws Exception {
        testUtils.createDefaultRussianAssetsOwner();

        Long createdRussianAssetsOwnerId = russianAssetsOwnerRepository.findAll().get(0).getId();

        testUtils.perform(put("/data" + RUSSIAN_OWNERS_CONTROLLER_PATH
                + CONTACT_DATA_PATH + ID_PATH, createdRussianAssetsOwnerId)
                .content(asJson(testUtils.getNotValidRussianAssetsOwnerContactDataDTO()))
                .contentType(APPLICATION_JSON));
        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getEmail(),
                testUtils.getNewRussianAssetsOwnerDTO().getEmail());
        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getMobilePhoneNumber(),
                russianAssetsOwnerService.addRussianNumberPrefixPhoneNumber(testUtils
                        .getNewRussianAssetsOwnerDTO().getMobilePhoneNumber()));

        testUtils.perform(put("/data" + RUSSIAN_OWNERS_CONTROLLER_PATH
                + PERSONAL_DATA_PATH + ID_PATH, createdRussianAssetsOwnerId)
                .content(asJson(testUtils.getNotValidRussianAssetsOwnerPersonalDataDTO()))
                .contentType(APPLICATION_JSON));
        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getName(),
                testUtils.getNewRussianAssetsOwnerDTO().getName());
        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getSurname(),
                testUtils.getNewRussianAssetsOwnerDTO().getSurname());
        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getPatronymic(),
                testUtils.getNewRussianAssetsOwnerDTO().getPatronymic());
        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getPassportSeries(),
                testUtils.getNewRussianAssetsOwnerDTO().getPassportSeries());
        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getPassportNumber(),
                testUtils.getNewRussianAssetsOwnerDTO().getPassportNumber());
        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getPlaceOfPassportGiven(),
                testUtils.getNewRussianAssetsOwnerDTO().getPlaceOfPassportGiven());
        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getIssueDate(),
                russianAssetsOwnerService.parseDatePassportFormatIntoLocalDate(testUtils
                        .getNewRussianAssetsOwnerDTO().getIssueDate()));
        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getIssuerOrganisationCode(),
                testUtils.getNewRussianAssetsOwnerDTO().getIssuerOrganisationCode());

        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getId(), createdRussianAssetsOwnerId);
        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getBirthDate(),
                russianAssetsOwnerService.parseDatePassportFormatIntoLocalDate(testUtils.getNewRussianAssetsOwnerDTO()
                        .getBirthDate()));
        assertNotNull(russianAssetsOwnerRepository.findAll().get(0).getAssetRelationships());
        assertNotNull(russianAssetsOwnerRepository.findAll().get(0).getCreatedAt());
        assertNotNull(russianAssetsOwnerRepository.findAll().get(0).getUpdatedAt());
        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getSex(),
                testUtils.getNewRussianAssetsOwnerDTO().getSex());
        assertEquals(russianAssetsOwnerRepository.findAll().get(0).getPlaceOfBirth(),
                testUtils.getNewRussianAssetsOwnerDTO().getPlaceOfBirth());
    }

    @Test
    public void deleteRussianAssetsOwnerIT() throws Exception {
        testUtils.createDefaultRussianAssetsOwner();

        Long createdRussianAssetsOwnerId = russianAssetsOwnerRepository.findAll().get(0).getId();

        testUtils.perform(delete("/data" + RUSSIAN_OWNERS_CONTROLLER_PATH + ID_PATH,
                        createdRussianAssetsOwnerId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(russianAssetsOwnerRepository.findAll()).hasSize(0);
    }
}

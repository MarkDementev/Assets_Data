package fund.data.assets.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.dto.owner.NewRussianAssetsOwnerDTO;
import fund.data.assets.model.asset.owner.RussianAssetsOwner;
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
import static fund.data.assets.controller.AccountController.ID_PATH;
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
    public void updateRussianAssetsOwnerIT() {
    }

    @Test
    public void notValidUpdateRussianAssetsOwnerIT() {
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

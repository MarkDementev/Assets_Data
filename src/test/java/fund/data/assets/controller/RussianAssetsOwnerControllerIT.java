package fund.data.assets.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.model.asset.owner.RussianAssetsOwner;
import fund.data.assets.repository.RussianAssetsOwnerRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static fund.data.assets.TestUtils.fromJson;
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;
import static fund.data.assets.controller.AccountController.ID_PATH;
import static fund.data.assets.controller.RussianAssetsOwnerController.RUSSIAN_OWNERS_CONTROLLER_PATH;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringConfigForTests.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class RussianAssetsOwnerControllerIT {
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private RussianAssetsOwnerRepository russianAssetsOwnerRepository;

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
        assertEquals("expectedRussianAssetsOwner.getEmail()", russianAssetsOwnerFromResponse.getEmail());
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
}

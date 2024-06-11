package fund.data.assets.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.model.financial_entities.Cash;
import fund.data.assets.repository.AccountRepository;

import fund.data.assets.repository.CashRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static fund.data.assets.TestUtils.asJson;
import static fund.data.assets.TestUtils.fromJson;
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;

import static fund.data.assets.controller.AccountController.ACCOUNT_CONTROLLER_PATH;
import static fund.data.assets.controller.AccountController.ID_PATH;
import static fund.data.assets.controller.CashController.CASH_CONTROLLER_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringConfigForTests.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class CashControllerIT {
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private CashRepository cashRepository;

    @AfterEach
    public void clearRepositories() {
        testUtils.tearDown();
    }

    @Test
    public void getCashIT() throws Exception {
        testUtils.createDefaultCash();

        Cash expectedCash = cashRepository.findAll().get(0);
        var response = testUtils.perform(get("/data" + CASH_CONTROLLER_PATH + ID_PATH, expectedCash.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Cash cashFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {});

        assertEquals(expectedCash.getId(), cashFromResponse.getId());
        assertEquals(expectedCash.getAccount().getId(), cashFromResponse.getAccount().getId());
        assertEquals(expectedCash.getAssetCurrency(), cashFromResponse.getAssetCurrency());
        assertEquals(expectedCash.getAssetsOwner().getId(), cashFromResponse.getAssetsOwner().getId());
        assertEquals(expectedCash.getAmount(), cashFromResponse.getAmount());
        assertNotNull(cashFromResponse.getCreatedAt());
        assertNotNull(cashFromResponse.getUpdatedAt());
    }

    @Test
    public void getAllCashIT() throws Exception {
        testUtils.createDefaultCash();

        var response = testUtils.perform(get("/data" + CASH_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        List<Cash> allCash = fromJson(response.getContentAsString(), new TypeReference<>() {});

        assertThat(allCash).hasSize(1);
    }

//    @Test
//    public void createAccountIT() throws Exception {
//        var response = testUtils.perform(
//                        post("/data" + ACCOUNT_CONTROLLER_PATH)
//                                .content(asJson(testUtils.getAccountDTO()))
//                                .contentType(APPLICATION_JSON)
//                )
//                .andExpect(status().isCreated())
//                .andReturn()
//                .getResponse();
//        Account accountFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
//        });
//
//        assertNotNull(accountFromResponse.getId());
//        assertEquals(accountFromResponse.getOrganisationWhereAccountOpened(),
//                testUtils.getAccountDTO().getOrganisationWhereAccountOpened());
//        assertEquals(accountFromResponse.getAccountNumber(),
//                testUtils.getAccountDTO().getAccountNumber());
//        assertEquals(accountFromResponse.getAccountOpeningDate(),
//                testUtils.getAccountDTO().getAccountOpeningDate());
//        assertNotNull(accountFromResponse.getCreatedAt());
//        assertNotNull(accountFromResponse.getUpdatedAt());
//    }
//
//    @Test
//    public void createNotValidAccountIT() throws Exception {
//        testUtils.perform(post("/data" + ACCOUNT_CONTROLLER_PATH)
//                .content(asJson(testUtils.getNotValidAccountDTO()))
//                .contentType(APPLICATION_JSON));
//
//        assertThat(accountRepository.findAll()).hasSize(0);
//
//        testUtils.createDefaultAccount();
//
//        Assertions.assertThrows(ServletException.class,
//                () -> testUtils.perform(post("/data" + ACCOUNT_CONTROLLER_PATH)
//                        .content(asJson(testUtils.getAnotherBankButSameAccountNumberAccountDTO()))
//                        .contentType(APPLICATION_JSON)));
//        assertThat(accountRepository.findAll()).hasSize(1);
//    }
//
//    @Test
//    public void updateAccountIT() throws Exception {
//        testUtils.createDefaultAccount();
//
//        Long createdAccountId = accountRepository.findByOrganisationWhereAccountOpened(
//                testUtils.getAccountDTO().getOrganisationWhereAccountOpened()).getId();
//        var response = testUtils.perform(put("/data" + ACCOUNT_CONTROLLER_PATH + ID_PATH,
//                        createdAccountId)
//                        .content(asJson(testUtils.getSecondAccountDTO())).contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse();
//        Account accountFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
//        });
//
//        assertEquals(accountFromResponse.getId(), createdAccountId);
//        assertEquals(accountFromResponse.getOrganisationWhereAccountOpened(),
//                testUtils.getSecondAccountDTO().getOrganisationWhereAccountOpened());
//        assertEquals(accountFromResponse.getAccountNumber(),
//                testUtils.getSecondAccountDTO().getAccountNumber());
//        assertEquals(accountFromResponse.getAccountOpeningDate(),
//                testUtils.getSecondAccountDTO().getAccountOpeningDate());
//        assertNotNull(accountFromResponse.getCreatedAt());
//        assertNotNull(accountFromResponse.getUpdatedAt());
//        assertNotEquals(accountFromResponse.getCreatedAt(), accountFromResponse.getUpdatedAt());
//    }
//
//    @Test
//    public void notValidUpdateAccountIT() throws Exception {
//        testUtils.createDefaultAccount();
//
//        Long createdAccountId = accountRepository.findByOrganisationWhereAccountOpened(
//                testUtils.getAccountDTO().getOrganisationWhereAccountOpened()).getId();
//        testUtils.perform(put("/data" + ACCOUNT_CONTROLLER_PATH + ID_PATH,
//                createdAccountId)
//                .content(asJson(testUtils.getNotValidAccountDTO()))
//                .contentType(APPLICATION_JSON));
//
//        assertEquals(accountRepository.findAll().get(0).getOrganisationWhereAccountOpened(),
//                testUtils.getAccountDTO().getOrganisationWhereAccountOpened());
//        assertEquals(accountRepository.findAll().get(0).getAccountNumber(),
//                testUtils.getAccountDTO().getAccountNumber());
//        assertEquals(accountRepository.findAll().get(0).getAccountOpeningDate(),
//                testUtils.getAccountDTO().getAccountOpeningDate());
//
//        testUtils.createDefaultSecondAccount();
//
//        Long createdSecondAccountId = accountRepository.findByOrganisationWhereAccountOpened(
//                testUtils.getSecondAccountDTO().getOrganisationWhereAccountOpened()).getId();
//        Assertions.assertThrows(ServletException.class,
//                () -> testUtils.perform(put("/data" + ACCOUNT_CONTROLLER_PATH + ID_PATH,
//                        createdSecondAccountId)
//                        .content(asJson(testUtils.getAccountDTO()))
//                        .contentType(APPLICATION_JSON)));
//    }
//
//    @Test
//    public void deleteAccountIT() throws Exception {
//        testUtils.createDefaultAccount();
//
//        Long createdAccountId = accountRepository.findByOrganisationWhereAccountOpened(
//                testUtils.getAccountDTO().getOrganisationWhereAccountOpened()).getId();
//
//        testUtils.perform(delete("/data" + ACCOUNT_CONTROLLER_PATH + ID_PATH, createdAccountId))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse();
//
//        assertNull(accountRepository.findByOrganisationWhereAccountOpened(
//                testUtils.getAccountDTO().getOrganisationWhereAccountOpened()));
//    }
}

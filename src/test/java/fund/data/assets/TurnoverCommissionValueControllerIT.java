package fund.data.assets;

import com.fasterxml.jackson.core.type.TypeReference;
import fund.data.assets.config.SpringConfigForTests;

import fund.data.assets.dto.TurnoverCommissionValueDTO;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.model.financial_entities.TurnoverCommissionValue;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.utils.enums.CommissionSystem;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static fund.data.assets.TestUtils.asJson;
import static fund.data.assets.TestUtils.fromJson;
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;
import static fund.data.assets.controller.AccountController.ACCOUNT_CONTROLLER_PATH;
import static fund.data.assets.controller.AccountController.ID_PATH;
import static fund.data.assets.controller.TurnoverCommissionValueController.TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForTests.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class TurnoverCommissionValueControllerIT {
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TurnoverCommissionValueRepository turnoverCommissionValueRepository;

    @AfterEach
    public void clear() {
        testUtils.tearDown();
    }

//    @Test
//    public void getTurnoverCommissionValueIT() throws Exception {
//        testUtils.createDefaultTurnoverCommissionValue();
//
//        final TurnoverCommissionValue expectedTurnoverCommissionValue = turnoverCommissionValueRepository
//                .findByAssetTypeName(testUtils.getTurnoverCommissionValueDTO().getAssetTypeName());
//        final var response = testUtils.perform(
//                        get("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH + ID_PATH,
//                                expectedTurnoverCommissionValue.getId())
//                ).andExpect(status().isOk())
//                .andReturn()
//                .getResponse();
//        final TurnoverCommissionValue turnoverCommissionValueFromResponse = fromJson(response.getContentAsString(),
//                new TypeReference<>() {});
//
//        assertEquals(expectedTurnoverCommissionValue.getId(),
//                turnoverCommissionValueFromResponse.getId());
//        assertEquals(expectedTurnoverCommissionValue.getCommissionSystem(),
//                turnoverCommissionValueFromResponse.getCommissionSystem());
//        assertEquals(expectedTurnoverCommissionValue.getAccount(),
//                turnoverCommissionValueFromResponse.getAccount());
//        assertEquals(expectedTurnoverCommissionValue.getAssetTypeName(),
//                turnoverCommissionValueFromResponse.getAssetTypeName());
//        assertEquals(expectedTurnoverCommissionValue.getCommissionPercentValue(),
//                turnoverCommissionValueFromResponse.getCommissionPercentValue());
//        assertNotNull(turnoverCommissionValueFromResponse.getCreatedAt());
//    }

//    @Test
//    public void getAccountsIT() throws Exception {
//        testUtils.createDefaultAccount();
//
//        final var response = testUtils.perform(
//                        get("/data" + ACCOUNT_CONTROLLER_PATH)
//                )
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse();
//
//        final List<Account> allAccounts = fromJson(response.getContentAsString(), new TypeReference<>() {
//        });
//
//        assertThat(allAccounts).hasSize(1);
//    }

    @Test
    public void createTurnoverCommissionValueIT() throws Exception {
        testUtils.createDefaultAccount();

        final TurnoverCommissionValueDTO turnoverCommissionValueDTO = new TurnoverCommissionValueDTO(
                CommissionSystem.TURNOVER,
                accountRepository.findByOrganisationWhereAccountOpened(
                        testUtils.getAccountDTO().getOrganisationWhereAccountOpened()
                ).getId(),
                "assetTypeName",
                0.01F
        );

        final var response = testUtils.perform(
                        post("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH)
                                .content(asJson(turnoverCommissionValueDTO))
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        final TurnoverCommissionValue turnoverCommissionValueFromResponse = fromJson(response.getContentAsString(),
                new TypeReference<>() {});

        assertNotNull(turnoverCommissionValueFromResponse.getId());
        assertEquals(turnoverCommissionValueFromResponse.getCommissionSystem(),
                turnoverCommissionValueDTO.getCommissionSystem());
        assertEquals(turnoverCommissionValueFromResponse.getAccount().getId(),
                turnoverCommissionValueDTO.getAccountID());
        assertEquals(turnoverCommissionValueFromResponse.getAssetTypeName(),
                turnoverCommissionValueDTO.getAssetTypeName());
        assertEquals(turnoverCommissionValueFromResponse.getCommissionPercentValue(),
                turnoverCommissionValueDTO.getCommissionPercentValue());
        assertNotNull(turnoverCommissionValueFromResponse.getCreatedAt());
    }

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
//                        .content(asJson(testUtils.getAnotherBankButSimilarAccountNumberAccountDTO()))
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
//        final var response = testUtils.perform(put("/data" + ACCOUNT_CONTROLLER_PATH + ID_PATH,
//                        createdAccountId)
//                        .content(asJson(testUtils.getSecondAccountDTO())).contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse();
//        final Account accountFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
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

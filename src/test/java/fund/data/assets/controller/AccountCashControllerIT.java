package fund.data.assets.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.dto.financial_entities.AccountCashDTO;
import fund.data.assets.model.financial_entities.AccountCash;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.AccountCashRepository;
import fund.data.assets.repository.RussianAssetsOwnerRepository;
import fund.data.assets.utils.enums.AssetCurrency;

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
import static fund.data.assets.controller.AccountCashController.ACCOUNT_CASH_CONTROLLER_PATH;
import static fund.data.assets.controller.RussianAssetsOwnerController.RUSSIAN_OWNERS_CONTROLLER_PATH;

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
public class AccountCashControllerIT {
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private AccountCashRepository accountCashRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RussianAssetsOwnerRepository russianAssetsOwnerRepository;

    @AfterEach
    public void clearRepositories() {
        testUtils.tearDown();
    }

    @Test
    public void getCashIT() throws Exception {
        testUtils.createDefaultAccountCash();

        AccountCash expectedAccountCash = accountCashRepository.findAll().get(0);
        var response = testUtils.perform(get("/data" + ACCOUNT_CASH_CONTROLLER_PATH + ID_PATH,
                        expectedAccountCash.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        AccountCash accountCashFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {});

        assertEquals(expectedAccountCash.getId(), accountCashFromResponse.getId());
        assertEquals(expectedAccountCash.getAccount().getId(), accountCashFromResponse.getAccount().getId());
        assertEquals(expectedAccountCash.getAssetCurrency(), accountCashFromResponse.getAssetCurrency());
        assertEquals(expectedAccountCash.getAssetsOwner().getId(), accountCashFromResponse.getAssetsOwner().getId());
        assertEquals(expectedAccountCash.getAmount(), accountCashFromResponse.getAmount());
        assertNotNull(accountCashFromResponse.getCreatedAt());
        assertNotNull(accountCashFromResponse.getUpdatedAt());
    }

    @Test
    public void getAllCashIT() throws Exception {
        testUtils.createDefaultAccountCash();

        var response = testUtils.perform(get("/data" + ACCOUNT_CASH_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        List<AccountCash> allAccountCashes = fromJson(response.getContentAsString(), new TypeReference<>() {});

        assertThat(allAccountCashes).hasSize(1);
    }

    @Test
    public void createCashIT() throws Exception {
        testUtils.createDefaultAccount();
        testUtils.createDefaultRussianAssetsOwner();

        AccountCashDTO accountCashDTO = new AccountCashDTO(
                accountRepository.findAll().get(0).getId(),
                AssetCurrency.RUSRUB,
                russianAssetsOwnerRepository.findAll().get(0).getId(),
                0.00F
        );
        var response = testUtils.perform(
                        post("/data" + ACCOUNT_CASH_CONTROLLER_PATH)
                                .content(asJson(accountCashDTO))
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        AccountCash accountCashFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {});

        assertNotNull(accountCashFromResponse.getId());
        assertEquals(accountCashDTO.getAccountID(), accountCashFromResponse.getAccount().getId());
        assertEquals(accountCashDTO.getAssetCurrency(), accountCashFromResponse.getAssetCurrency());
        assertEquals(accountCashDTO.getAssetsOwnerID(), accountCashFromResponse.getAssetsOwner().getId());
        assertEquals(accountCashDTO.getAmountChangeValue(), accountCashFromResponse.getAmount());
        assertNotNull(accountCashFromResponse.getCreatedAt());
        assertNotNull(accountCashFromResponse.getUpdatedAt());
    }

    @Test
    public void createNotValidCashIT() throws Exception {
        testUtils.createDefaultAccount();
        testUtils.createDefaultRussianAssetsOwner();

        testUtils.perform(post("/data" + ACCOUNT_CASH_CONTROLLER_PATH)
                .content(asJson(new AccountCashDTO()))
                .contentType(APPLICATION_JSON));
        assertThat(accountCashRepository.findAll()).hasSize(0);

        float amountChangeValue = 10.00F;

        AccountCashDTO accountCashDTO = new AccountCashDTO(
                accountRepository.findAll().get(0).getId(),
                AssetCurrency.RUSRUB,
                russianAssetsOwnerRepository.findAll().get(0).getId(),
                -amountChangeValue
        );
        Assertions.assertThrows(ServletException.class,
                () -> testUtils.perform(post("/data" + ACCOUNT_CASH_CONTROLLER_PATH)
                        .content(asJson(accountCashDTO)).contentType(APPLICATION_JSON)));
        assertThat(accountCashRepository.findAll()).hasSize(0);

        accountCashDTO.setAmountChangeValue(amountChangeValue);
        testUtils.perform(post("/data" + ACCOUNT_CASH_CONTROLLER_PATH)
                .content(asJson(accountCashDTO))
                .contentType(APPLICATION_JSON));
        accountCashDTO.setAmountChangeValue(amountChangeValue * 2);
        testUtils.perform(post("/data" + ACCOUNT_CASH_CONTROLLER_PATH)
                .content(asJson(accountCashDTO))
                .contentType(APPLICATION_JSON));
        assertThat(accountCashRepository.findAll()).hasSize(1);
        assertEquals(amountChangeValue * 3, accountCashRepository.findAll().get(0).getAmount());
    }

    @Test
    public void depositAndWithdrawCashAmountIT() throws Exception {
        testUtils.createDefaultAccountCash();

        float amountChangeValue = 15.00F;
        AccountCashDTO accountCashDTO = new AccountCashDTO(
                accountRepository.findAll().get(0).getId(),
                AssetCurrency.RUSRUB,
                russianAssetsOwnerRepository.findAll().get(0).getId(),
                amountChangeValue
        );
        testUtils.perform(post("/data" + ACCOUNT_CASH_CONTROLLER_PATH)
                .content(asJson(accountCashDTO))
                .contentType(APPLICATION_JSON));
        testUtils.perform(post("/data" + ACCOUNT_CASH_CONTROLLER_PATH)
                .content(asJson(accountCashDTO))
                .contentType(APPLICATION_JSON));
        assertEquals(amountChangeValue * 2, accountCashRepository.findAll().get(0).getAmount());

        amountChangeValue = -10.00F;
        accountCashDTO.setAmountChangeValue(amountChangeValue);
        testUtils.perform(post("/data" + ACCOUNT_CASH_CONTROLLER_PATH)
                .content(asJson(accountCashDTO))
                .contentType(APPLICATION_JSON));
        var response = testUtils.perform(
                post("/data" + ACCOUNT_CASH_CONTROLLER_PATH)
                        .content(asJson(accountCashDTO))
                        .contentType(APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        AccountCash accountCashFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {});

        assertNotNull(accountCashFromResponse.getId());
        assertEquals(accountCashDTO.getAccountID(), accountCashFromResponse.getAccount().getId());
        assertEquals(accountCashDTO.getAssetCurrency(), accountCashFromResponse.getAssetCurrency());
        assertEquals(accountCashDTO.getAssetsOwnerID(), accountCashFromResponse.getAssetsOwner().getId());
        assertEquals(-amountChangeValue, accountCashRepository.findAll().get(0).getAmount());
        assertNotNull(accountCashFromResponse.getCreatedAt());
        assertNotNull(accountCashFromResponse.getUpdatedAt());
    }

    @Test
    public void notValidWithdrawCashAmountIT() throws Exception {
        testUtils.createDefaultAccountCash();

        AccountCashDTO accountCashDTOWithNegativeAmount = new AccountCashDTO(
                accountRepository.findAll().get(0).getId(),
                AssetCurrency.RUSRUB,
                russianAssetsOwnerRepository.findAll().get(0).getId(),
                -10.00F
        );
        Assertions.assertThrows(ServletException.class,
                () -> testUtils.perform(post("/data" + ACCOUNT_CASH_CONTROLLER_PATH)
                        .content(asJson(accountCashDTOWithNegativeAmount))
                        .contentType(APPLICATION_JSON)));
        assertEquals(0.00F, accountCashRepository.findAll().get(0).getAmount());
    }

    @Test
    public void deleteCashByDeleteAccountOrOwnerIT() throws Exception {
        testUtils.createDefaultAccountCash();
        assertThat(accountCashRepository.findAll()).hasSize(1);

        Long createdAccountId = accountRepository.findAll().get(0).getId();
        testUtils.perform(delete("/data" + ACCOUNT_CONTROLLER_PATH + ID_PATH, createdAccountId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        assertThat(accountCashRepository.findAll()).hasSize(0);

        testUtils.createDefaultAccountCash();
        assertThat(accountCashRepository.findAll()).hasSize(1);

        Long createdOwnerId = russianAssetsOwnerRepository.findAll().get(0).getId();
        testUtils.perform(delete("/data" + RUSSIAN_OWNERS_CONTROLLER_PATH + ID_PATH, createdOwnerId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        assertThat(accountCashRepository.findAll()).hasSize(0);
    }
}

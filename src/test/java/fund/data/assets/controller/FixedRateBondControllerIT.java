package fund.data.assets.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.model.assets.exchange.FixedRateBond;
import fund.data.assets.repository.FixedRateBondRepository;

import fund.data.assets.utils.AutoSelector;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;
import org.junit.jupiter.api.AfterEach;
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

import static fund.data.assets.controller.FixedRateBondController.*;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForTests.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class FixedRateBondControllerIT {
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private FixedRateBondRepository fixedRateBondRepository;

    @AfterEach
    public void clearRepositories() {
        testUtils.tearDown();
    }

    @Test
    public void getFixedRateBondIT() throws Exception {
//        testUtils.createDefaultAccount();
//
//        final Account expectedAccount = accountRepository.findByOrganisationWhereAccountOpened(
//                testUtils.getAccountDTO().getOrganisationWhereAccountOpened());
//        final var response = testUtils.perform(
//                        get("/data" + ACCOUNT_CONTROLLER_PATH + ID_PATH, expectedAccount.getId())
//                ).andExpect(status().isOk())
//                .andReturn()
//                .getResponse();
//        final Account accountFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
//        });
//
//        assertEquals(expectedAccount.getId(), accountFromResponse.getId());
//        assertEquals(expectedAccount.getOrganisationWhereAccountOpened(),
//                accountFromResponse.getOrganisationWhereAccountOpened());
//        assertEquals(expectedAccount.getAccountNumber(),
//                accountFromResponse.getAccountNumber());
//        assertEquals(expectedAccount.getAccountOpeningDate(),
//                accountFromResponse.getAccountOpeningDate());
//        assertNotNull(accountFromResponse.getCreatedAt());
    }

    @Test
    public void getFixedRateBondsIT() throws Exception {
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
    }

    @Test
    public void createFixedRateBondIT() throws Exception {
//        TaxSystem expectedTaxSystem = TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE;
//        CommissionSystem expectedCommissionSystem = CommissionSystem.TURNOVER;
//        String fixedRateBondName = FixedRateBond.class.getTypeName();
//        Float expectedTotalCommissionForPurchase
//        final var response = testUtils.perform(
//                        post("/data" + ASSETS_CONTROLLER_PATH + EXCHANGE_ASSETS_CONTROLLER_PATH
//                                + FIXED_RATE_BOND_CONTROLLER_PATH)
//                                .content(asJson(testUtils.getFixedRateBondDTO()))
//                                .contentType(APPLICATION_JSON)
//                )
//                .andExpect(status().isCreated())
//                .andReturn()
//                .getResponse();
//        final FixedRateBond fixedRateBondFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
//        });
//
//        assertNotNull(fixedRateBondFromResponse.getId());
//
//        assertEquals(fixedRateBondFromResponse.getAssetCurrency(),
//                testUtils.getFixedRateBondDTO().getAssetCurrency());
//        assertEquals(fixedRateBondFromResponse.getAssetTypeName(),
//                fixedRateBondName);
//        assertEquals(fixedRateBondFromResponse.getAssetTitle(),
//                testUtils.getFixedRateBondDTO().getAssetTitle());
//        assertEquals(fixedRateBondFromResponse.getAssetCount(),
//                testUtils.getFixedRateBondDTO().getAssetCount());
//        assertEquals(fixedRateBondFromResponse.getAssetTaxSystem(),
//                expectedTaxSystem);
//
//        assertEquals(fixedRateBondFromResponse.getISIN(),
//                testUtils.getFixedRateBondDTO().getISIN());
//        assertEquals(fixedRateBondFromResponse.getAssetIssuerTitle(),
//                testUtils.getFixedRateBondDTO().getAssetIssuerTitle());
//        assertEquals(fixedRateBondFromResponse.getLastAssetBuyDate(),
//                testUtils.getFixedRateBondDTO().getLastAssetBuyDate());
//        assertEquals(fixedRateBondFromResponse.getAssetCommissionSystem(),
//                expectedCommissionSystem);
//
//
//        assertEquals(fixedRateBondFromResponse.getBondParValue(),
//                testUtils.getFixedRateBondDTO().getBondParValue());
//        assertEquals(fixedRateBondFromResponse.getBondPurchaseMarketPrice(),
//                testUtils.getFixedRateBondDTO().getBondPurchaseMarketPrice());
//        assertEquals(fixedRateBondFromResponse.getBondAccruedInterest(),
//                testUtils.getFixedRateBondDTO().getBondAccruedInterest());
//        assertEquals(fixedRateBondFromResponse.getBondCouponValue(),
//                testUtils.getFixedRateBondDTO().getBondCouponValue());
//        assertEquals(fixedRateBondFromResponse.getExpectedBondCouponPaymentsCount(),
//                testUtils.getFixedRateBondDTO().getExpectedBondCouponPaymentsCount());
//        assertEquals(fixedRateBondFromResponse.getBondMaturityDate(),
//                testUtils.getFixedRateBondDTO().getBondMaturityDate());
//        assertNotNull(fixedRateBondFromResponse.getCreatedAt());
    }

    @Test
    public void deleteFixedRateBondIT() throws Exception {
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
    }
}

package fund.data.assets.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.dto.TurnoverCommissionValueDTO;
import fund.data.assets.dto.common.PercentFloatValueDTO;
import fund.data.assets.model.financial_entities.TurnoverCommissionValue;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.utils.enums.CommissionSystem;

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
import static fund.data.assets.TestUtils.TEST_ASSET_TYPE_NAME;
import static fund.data.assets.TestUtils.TEST_COMMISSION_PERCENT_VALUE;
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;
import static fund.data.assets.controller.AccountController.ID_PATH;
import static fund.data.assets.controller.TurnoverCommissionValueController.TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringConfigForTests.class, webEnvironment = RANDOM_PORT)
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
    public void clearRepositories() {
        testUtils.tearDown();
    }

    @Test
    public void getTurnoverCommissionValueIT() throws Exception {
        testUtils.createDefaultTurnoverCommissionValue();

        final TurnoverCommissionValue expectedTurnoverCommissionValue = turnoverCommissionValueRepository
                .findAll().get(0);
        final var response = testUtils.perform(
                        get("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH + ID_PATH,
                                expectedTurnoverCommissionValue.getId())
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final TurnoverCommissionValue turnoverCommissionValueFromResponse = fromJson(response.getContentAsString(),
                new TypeReference<>() {});

        assertEquals(expectedTurnoverCommissionValue.getId(),
                turnoverCommissionValueFromResponse.getId());
        assertEquals(expectedTurnoverCommissionValue.getCommissionSystem(),
                turnoverCommissionValueFromResponse.getCommissionSystem());
        assertEquals(expectedTurnoverCommissionValue.getAccount().getId(),
                turnoverCommissionValueFromResponse.getAccount().getId());
        assertEquals(expectedTurnoverCommissionValue.getAssetTypeName(),
                turnoverCommissionValueFromResponse.getAssetTypeName());
        assertEquals(expectedTurnoverCommissionValue.getCommissionPercentValue(),
                turnoverCommissionValueFromResponse.getCommissionPercentValue());
        assertNotNull(turnoverCommissionValueFromResponse.getCreatedAt());
    }

    @Test
    public void getTurnoverCommissionValuesIT() throws Exception {
        testUtils.createDefaultTurnoverCommissionValue();

        final var response = testUtils.perform(
                        get("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TurnoverCommissionValue> allTurnoverCommissionValues = fromJson(response.getContentAsString(),
                new TypeReference<>() {});

        assertThat(allTurnoverCommissionValues).hasSize(1);
    }

    @Test
    public void createTurnoverCommissionValueIT() throws Exception {
        testUtils.createDefaultAccount();

        final TurnoverCommissionValueDTO newTurnoverCommissionValueDTO = testUtils.getTurnoverCommissionValueDTO();

        newTurnoverCommissionValueDTO.setAccountID(accountRepository.findByOrganisationWhereAccountOpened(
                testUtils.getAccountDTO().getOrganisationWhereAccountOpened()).getId());

        final var response = testUtils.perform(
                        post("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH)
                                .content(asJson(newTurnoverCommissionValueDTO))
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        final TurnoverCommissionValue turnoverCommissionValueFromResponse = fromJson(response.getContentAsString(),
                new TypeReference<>() {});

        assertNotNull(turnoverCommissionValueFromResponse.getId());
        assertEquals(turnoverCommissionValueFromResponse.getCommissionSystem(),
                CommissionSystem.TURNOVER);
        assertEquals(turnoverCommissionValueFromResponse.getAccount().getId(),
                newTurnoverCommissionValueDTO.getAccountID());
        assertEquals(turnoverCommissionValueFromResponse.getAssetTypeName(),
                newTurnoverCommissionValueDTO.getAssetTypeName());
        assertEquals(turnoverCommissionValueFromResponse.getCommissionPercentValue(),
                newTurnoverCommissionValueDTO.getCommissionPercentValue());
        assertNotNull(turnoverCommissionValueFromResponse.getCreatedAt());
    }

    @Test
    public void createNotValidTurnoverCommissionValueIT() throws Exception {
        testUtils.perform(post("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH)
                .content(asJson(testUtils.getNotValidTurnoverCommissionValueDTO()))
                .contentType(APPLICATION_JSON));

        assertThat(turnoverCommissionValueRepository.findAll()).hasSize(0);

        testUtils.createDefaultTurnoverCommissionValue();

        final TurnoverCommissionValueDTO newTurnoverCommissionValueDTO = testUtils.getTurnoverCommissionValueDTO();

        newTurnoverCommissionValueDTO.setAccountID(accountRepository.findByOrganisationWhereAccountOpened(
                testUtils.getAccountDTO().getOrganisationWhereAccountOpened()).getId());
        newTurnoverCommissionValueDTO.setCommissionPercentValue(TEST_COMMISSION_PERCENT_VALUE
                + TEST_COMMISSION_PERCENT_VALUE);

        Assertions.assertThrows(ServletException.class,
                () -> testUtils.perform(post("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH)
                        .content(asJson(newTurnoverCommissionValueDTO))
                        .contentType(APPLICATION_JSON)));
        assertThat(turnoverCommissionValueRepository.findAll()).hasSize(1);
    }

    @Test
    public void updateTurnoverCommissionValueIT() throws Exception {
        testUtils.createDefaultTurnoverCommissionValue();

        Long createdTurnoverCommissionId = turnoverCommissionValueRepository.findAll().get(0).getId();
        final PercentFloatValueDTO percentFloatValueDTO = testUtils.getPercentFloatValueDTO();

        final var response = testUtils.perform(put("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH
                        + ID_PATH, createdTurnoverCommissionId)
                        .content(asJson(percentFloatValueDTO)).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final TurnoverCommissionValue turnoverCommissionValueFromResponse = fromJson(response.getContentAsString(),
                new TypeReference<>() {});

        assertNotNull(turnoverCommissionValueFromResponse.getId());
        assertEquals(turnoverCommissionValueFromResponse.getCommissionSystem(),
                CommissionSystem.TURNOVER);
        assertEquals(turnoverCommissionValueFromResponse.getAccount().getId(),
                testUtils.getTurnoverCommissionValueDTO().getAccountID());
        assertEquals(turnoverCommissionValueFromResponse.getAssetTypeName(),
                testUtils.getTurnoverCommissionValueDTO().getAssetTypeName());
        assertEquals(turnoverCommissionValueFromResponse.getCommissionPercentValue(),
                percentFloatValueDTO.getPercentValue());
        assertNotNull(turnoverCommissionValueFromResponse.getCreatedAt());
        assertNotNull(turnoverCommissionValueFromResponse.getUpdatedAt());
        assertNotEquals(turnoverCommissionValueFromResponse.getCreatedAt(),
                turnoverCommissionValueFromResponse.getUpdatedAt());
    }

    @Test
    public void notValidUpdateTurnoverCommissionValueIT() throws Exception {
        testUtils.createDefaultTurnoverCommissionValue();

        Long createdTurnoverCommissionId = turnoverCommissionValueRepository.findAll().get(0).getId();
        testUtils.perform(put("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH + ID_PATH,
                createdTurnoverCommissionId)
                .content(asJson(testUtils.getNotValidTurnoverCommissionValueDTO()))
                .contentType(APPLICATION_JSON));

        assertNotEquals(turnoverCommissionValueRepository.findAll().get(0).getAccount().getId(),
                testUtils.getNotValidTurnoverCommissionValueDTO().getAccountID());
        assertNotEquals(turnoverCommissionValueRepository.findAll().get(0).getAssetTypeName(),
                testUtils.getNotValidTurnoverCommissionValueDTO().getAssetTypeName());
        assertNotEquals(turnoverCommissionValueRepository.findAll().get(0).getCommissionPercentValue(),
                testUtils.getNotValidTurnoverCommissionValueDTO().getCommissionPercentValue());
    }

    @Test
    public void deleteTurnoverCommissionValueIT() throws Exception {
        testUtils.createDefaultTurnoverCommissionValue();

        Long createdTurnoverCommissionValueId = turnoverCommissionValueRepository.findByAccountAndAssetTypeName(
                accountRepository.findAll().get(0), TEST_ASSET_TYPE_NAME).getId();

        testUtils.perform(delete("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH + ID_PATH,
                        createdTurnoverCommissionValueId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNull(turnoverCommissionValueRepository.findByAccountAndAssetTypeName(
                accountRepository.findAll().get(0), TEST_ASSET_TYPE_NAME));
    }
}

package fund.data.assets.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.dto.TurnoverCommissionValueDTO;
import fund.data.assets.dto.common.PercentFloatValueDTO;
import fund.data.assets.exception.NotValidPercentValueInputFormatException;
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

import static fund.data.assets.TestUtils.*;
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;
import static fund.data.assets.controller.AccountController.ID_PATH;
import static fund.data.assets.controller.TurnoverCommissionValueController.TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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

        TurnoverCommissionValue expectedTurnoverCommissionValue = turnoverCommissionValueRepository.findAll().get(0);
        var response = testUtils.perform(
                        get("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH + ID_PATH,
                                expectedTurnoverCommissionValue.getId())
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        TurnoverCommissionValue turnoverCommissionValueFromResponse = fromJson(response.getContentAsString(),
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

        var response = testUtils.perform(
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

        TurnoverCommissionValueDTO validTurnoverCommissionValueDTO = testUtils.getTurnoverCommissionValueDTO();

        validTurnoverCommissionValueDTO.setAccountID(accountRepository.findByOrganisationWhereAccountOpened(
                testUtils.getAccountDTO().getOrganisationWhereAccountOpened()).getId());

        var response = testUtils.perform(
                        post("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH)
                                .content(asJson(validTurnoverCommissionValueDTO))
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        TurnoverCommissionValue turnoverCommissionValueFromResponse = fromJson(response.getContentAsString(),
                new TypeReference<>() {});

        assertNotNull(turnoverCommissionValueFromResponse.getId());
        assertEquals(turnoverCommissionValueFromResponse.getCommissionSystem(),
                CommissionSystem.TURNOVER);
        assertEquals(turnoverCommissionValueFromResponse.getAccount().getId(),
                validTurnoverCommissionValueDTO.getAccountID());
        assertEquals(turnoverCommissionValueFromResponse.getAssetTypeName(),
                validTurnoverCommissionValueDTO.getAssetTypeName());
        assertEquals(turnoverCommissionValueFromResponse.getCommissionPercentValue(),
                TEST_COMMISSION_PERCENT_VALUE_FLOAT);
        assertNotNull(turnoverCommissionValueFromResponse.getCreatedAt());
    }

    //TODO Добавь кейс с некорректным значением процента
    @Test
    public void createNotValidTurnoverCommissionValueIT() throws Exception {
        testUtils.perform(post("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH)
                .content(asJson(testUtils.getNotValidTurnoverCommissionValueDTO()))
                .contentType(APPLICATION_JSON));

        assertThat(turnoverCommissionValueRepository.findAll()).hasSize(0);

        testUtils.createDefaultTurnoverCommissionValue();

        TurnoverCommissionValueDTO validTurnoverCommissionValueDTO = testUtils.getTurnoverCommissionValueDTO();

        validTurnoverCommissionValueDTO.setAccountID(accountRepository.findByOrganisationWhereAccountOpened(
                testUtils.getAccountDTO().getOrganisationWhereAccountOpened()).getId());
        validTurnoverCommissionValueDTO.setCommissionPercentValue(TEST_COMMISSION_PERCENT_VALUE
                + TEST_COMMISSION_PERCENT_VALUE);

        Assertions.assertThrows(ServletException.class,
                () -> testUtils.perform(post("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH)
                        .content(asJson(validTurnoverCommissionValueDTO))
                        .contentType(APPLICATION_JSON)));
        assertThat(turnoverCommissionValueRepository.findAll()).hasSize(1);
    }

    @Test
    public void updateTurnoverCommissionValueIT() throws Exception {
        testUtils.createDefaultTurnoverCommissionValue();

        Long createdTurnoverCommissionId = turnoverCommissionValueRepository.findAll().get(0).getId();
        PercentFloatValueDTO percentFloatValueDTO = testUtils.getPercentFloatValueDTO();
        var response = testUtils.perform(put("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH
                        + ID_PATH, createdTurnoverCommissionId)
                        .content(asJson(percentFloatValueDTO)).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        TurnoverCommissionValue turnoverCommissionValueFromResponse = fromJson(response.getContentAsString(),
                new TypeReference<>() {});

        assertNotNull(turnoverCommissionValueFromResponse.getId());
        assertEquals(turnoverCommissionValueFromResponse.getCommissionSystem(),
                CommissionSystem.TURNOVER);
        assertEquals(turnoverCommissionValueFromResponse.getAccount().getId(),
                testUtils.getTurnoverCommissionValueDTO().getAccountID());
        assertEquals(turnoverCommissionValueFromResponse.getAssetTypeName(),
                testUtils.getTurnoverCommissionValueDTO().getAssetTypeName());
        assertEquals(turnoverCommissionValueFromResponse.getCommissionPercentValue(),
                TEST_FORMATTED_PERCENT_VALUE_FLOAT);
        assertNotNull(turnoverCommissionValueFromResponse.getCreatedAt());
        assertNotNull(turnoverCommissionValueFromResponse.getUpdatedAt());
        assertNotEquals(turnoverCommissionValueFromResponse.getCreatedAt(),
                turnoverCommissionValueFromResponse.getUpdatedAt());
    }

    @Test
    public void notValidUpdateTurnoverCommissionValueIT() throws Exception {
        testUtils.createDefaultTurnoverCommissionValue();

        PercentFloatValueDTO notValidPercentFloatValueDTO = testUtils.getPercentFloatValueDTO();

        notValidPercentFloatValueDTO.setPercentValue(TEST_STRING_FORMAT_PERCENT_VALUE + "111");

        Long createdTurnoverCommissionId = turnoverCommissionValueRepository.findAll().get(0).getId();
        assertThatThrownBy(() -> testUtils.perform(put("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH
                        + ID_PATH, createdTurnoverCommissionId)
                .content(asJson(notValidPercentFloatValueDTO))
                .contentType(APPLICATION_JSON))
        )
                .isInstanceOf(ServletException.class)
                .hasMessageContaining(NotValidPercentValueInputFormatException.MESSAGE);

        assertEquals(turnoverCommissionValueRepository.findAll().get(0).getCommissionPercentValue(),
                TEST_COMMISSION_PERCENT_VALUE_FLOAT);
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

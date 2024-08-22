package fund.data.assets.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.model.asset.exchange.FixedRateBondPackage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static fund.data.assets.TestUtils.asJson;
import static fund.data.assets.TestUtils.fromJson;
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;
import static fund.data.assets.controller.FixedRateBondPackageController.FIXED_RATE_BOND_CONTROLLER_PATH;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringConfigForTests.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class FixedRateBondControllerIT {
    @Autowired
    private TestUtils testUtils;

    @AfterEach
    public void clearRepositories() {
        testUtils.tearDown();
    }

    @Test
    public void firstBuyFixedRateBondIT() throws Exception {
        final FirstBuyFixedRateBondDTO firstBuyFixedRateBondDTO = testUtils.getFirstBuyFixedRateBondDTO();

        var response = testUtils.perform(
                post("/data" + FIXED_RATE_BOND_CONTROLLER_PATH)
                        .content(asJson(firstBuyFixedRateBondDTO))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        FixedRateBondPackage fixedRateBondPackageFromResponse = fromJson(response.getContentAsString(),
                new TypeReference<>() {});

        assertNotNull(fixedRateBondPackageFromResponse.getId());
        assertEquals(firstBuyFixedRateBondDTO.getAssetCurrency(), fixedRateBondPackageFromResponse.getAssetCurrency());

        //TODO Допиши здесь проверки для этой сущности!

        assertNotNull(fixedRateBondPackageFromResponse.getCreatedAt());
        assertNotNull(fixedRateBondPackageFromResponse.getUpdatedAt());

        //TODO дополнительно проверь, как дела в смеэных сущностях, как минимум, уменьшились ли деньги!
    }
}

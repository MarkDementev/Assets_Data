package fund.data.assets.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.model.asset.exchange.FixedRateBondPackage;
import fund.data.assets.model.asset.relationship.FinancialAssetRelationship;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.text.DecimalFormat;

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
        DecimalFormat decimalFormat = new DecimalFormat( "#.####" );

        /*
        Сначала проверяются поля самого FixedRateBond.
         */
        assertNotNull(fixedRateBondPackageFromResponse.getId());
        assertEquals(firstBuyFixedRateBondDTO.getAssetCurrency(), fixedRateBondPackageFromResponse.getAssetCurrency());
        assertEquals(FixedRateBondPackage.class.getTypeName(), fixedRateBondPackageFromResponse.getAssetTypeName());
        assertEquals(firstBuyFixedRateBondDTO.getAssetTitle(), fixedRateBondPackageFromResponse.getAssetTitle());
        assertEquals(firstBuyFixedRateBondDTO.getAssetCount(), fixedRateBondPackageFromResponse.getAssetCount());
        assertEquals(TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE, fixedRateBondPackageFromResponse.getAssetTaxSystem());
        assertNotNull(fixedRateBondPackageFromResponse.getAssetRelationship());
        assertEquals(fixedRateBondPackageFromResponse.getAssetRelationship().getClass(),
                FinancialAssetRelationship.class);
        assertNotNull(fixedRateBondPackageFromResponse.getCreatedAt());
        assertNotNull(fixedRateBondPackageFromResponse.getUpdatedAt());
        assertEquals(firstBuyFixedRateBondDTO.getISIN(), fixedRateBondPackageFromResponse.getISIN());
        assertEquals(firstBuyFixedRateBondDTO.getAssetIssuerTitle(),
                fixedRateBondPackageFromResponse.getAssetIssuerTitle());
        assertNotNull(fixedRateBondPackageFromResponse.getLastAssetBuyDate());
        assertEquals(CommissionSystem.TURNOVER, fixedRateBondPackageFromResponse.getAssetCommissionSystem());
        assertEquals(firstBuyFixedRateBondDTO.getBondParValue(), fixedRateBondPackageFromResponse.getBondParValue());
        assertEquals(firstBuyFixedRateBondDTO.getPurchaseBondParValuePercent(),
                fixedRateBondPackageFromResponse.getPurchaseBondParValuePercent());
        assertEquals(firstBuyFixedRateBondDTO.getBondAccruedInterest(),
                fixedRateBondPackageFromResponse.getBondAccruedInterest());
        /*
        По дефолту в тесте используется размер комиссии в размере 1% от суммы покупаемого пакета бумаг, эта сумма
        равна 30000, потому размер комиссии равен 300.
         */
        assertEquals(300.00F, fixedRateBondPackageFromResponse.getTotalCommissionForPurchase());
        assertEquals(30300.00F, fixedRateBondPackageFromResponse.getTotalAssetPurchasePriceWithCommission());
        assertEquals(firstBuyFixedRateBondDTO.getBondCouponValue(),
                fixedRateBondPackageFromResponse.getBondCouponValue());
        assertEquals(firstBuyFixedRateBondDTO.getExpectedBondCouponPaymentsCount(),
                fixedRateBondPackageFromResponse.getExpectedBondCouponPaymentsCount());
        assertEquals(firstBuyFixedRateBondDTO.getBondMaturityDate(),
                fixedRateBondPackageFromResponse.getBondMaturityDate());
        assertEquals(10.00F, fixedRateBondPackageFromResponse.getSimpleYieldToMaturity());
        assertEquals(7.6238F, Float.parseFloat(decimalFormat.format(
                fixedRateBondPackageFromResponse.getMarkDementevYieldIndicator())));

        /*
        Потом проверяются поля assetRelationship внутри FixedRateBond.
         */
        //TODO проверь внутренности assetRelationship
        assertNotNull(fixedRateBondPackageFromResponse.getAssetRelationship().getAssetId());

        /*
        Потом ещё что-то? Например, что суммы на денежных счетах изменились правильно?
         */
        //TODO дополнительно проверь, как дела в смежных сущностях, как минимум, уменьшились ли деньги!
    }
}

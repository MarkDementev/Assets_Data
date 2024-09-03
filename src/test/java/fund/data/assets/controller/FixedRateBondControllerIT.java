package fund.data.assets.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.model.asset.exchange.FixedRateBondPackage;
import fund.data.assets.model.asset.relationship.FinancialAssetRelationship;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.model.owner.AssetsOwner;
import fund.data.assets.model.owner.RussianAssetsOwner;
import fund.data.assets.repository.AccountCashRepository;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.RussianAssetsOwnerRepository;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.text.DecimalFormat;

import java.util.Map;
import java.util.TreeMap;

import static fund.data.assets.TestUtils.asJson;
import static fund.data.assets.TestUtils.fromJson;
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;
import static fund.data.assets.config.SpringConfigForTests.postgres;
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
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RussianAssetsOwnerRepository russianAssetsOwnerRepository;
    @Autowired
    private AccountCashRepository accountCashRepository;
    @Autowired
    private TurnoverCommissionValueRepository turnoverCommissionValueRepository;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @AfterEach
    public void clearRepositories() {
        testUtils.tearDown();
    }

    @Test
    public void firstBuyFixedRateBondCheckBondFieldsIT() throws Exception {
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

        assertNotNull(fixedRateBondPackageFromResponse.getId());
        assertEquals(firstBuyFixedRateBondDTO.getAssetCurrency(), fixedRateBondPackageFromResponse.getAssetCurrency());
        assertEquals(FixedRateBondPackage.class.getTypeName(), fixedRateBondPackageFromResponse.getAssetTypeName());
        assertEquals(firstBuyFixedRateBondDTO.getAssetTitle(), fixedRateBondPackageFromResponse.getAssetTitle());
        assertEquals(firstBuyFixedRateBondDTO.getAssetCount(), fixedRateBondPackageFromResponse.getAssetCount());
        assertEquals(TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE, fixedRateBondPackageFromResponse.getAssetTaxSystem());
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
    }

    @Test
    public void firstBuyFixedRateBondCheckRelationshipFieldsIT() throws Exception {
        final FirstBuyFixedRateBondDTO firstBuyFixedRateBondDTO = testUtils.getFirstBuyFixedRateBondDTO();
        Map<String, Float> assetOwnersWithAssetCountsMapFromDTO
                = firstBuyFixedRateBondDTO.getAssetOwnersWithAssetCounts();
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

        assertNotNull(fixedRateBondPackageFromResponse.getAssetRelationship());
        assertEquals(fixedRateBondPackageFromResponse.getAssetRelationship().getClass(),
                FinancialAssetRelationship.class);

        FinancialAssetRelationship financialAssetRelationship
                = (FinancialAssetRelationship) fixedRateBondPackageFromResponse.getAssetRelationship();

        assertNotNull(fixedRateBondPackageFromResponse.getAssetRelationship().getId());
        assertEquals(fixedRateBondPackageFromResponse.getId(), financialAssetRelationship.getAssetId());
        assertNotNull(financialAssetRelationship.getAssetOwnersWithAssetCounts());

        for (Map.Entry<String, Float> element : financialAssetRelationship.getAssetOwnersWithAssetCounts().entrySet()) {
            String elementKey = element.getKey();
            Float elementValue = element.getValue();

            assertEquals(elementValue, assetOwnersWithAssetCountsMapFromDTO.get(elementKey));
        }
        assertNotNull(financialAssetRelationship.getCreatedAt());
        assertNotNull(financialAssetRelationship.getUpdatedAt());
        assertEquals(accountRepository.findAll().get(0).getId(), financialAssetRelationship.getAccount().getId());
    }

    @Test
    public void firstBuyFixedRateBondCheckAccountCashesIT() throws Exception {
        final FirstBuyFixedRateBondDTO firstBuyFixedRateBondDTO = testUtils.getFirstBuyFixedRateBondDTO();
        Map<String, Float> assetOwnersWithAssetCountsMapFromDTO
                = firstBuyFixedRateBondDTO.getAssetOwnersWithAssetCounts();
        Map<String, Float> correctAccountCashAmounts = new TreeMap<>();

        /*
        Наполнили мапу айдишниками оунеров и значениями количества денег на нужных аккаунтах.
         */
        for (Map.Entry<String, Float> element : assetOwnersWithAssetCountsMapFromDTO.entrySet()) {
            Account account = accountRepository.findById(firstBuyFixedRateBondDTO.getAccountID()).orElseThrow();
            String assetsOwnerID = element.getKey();
            AssetsOwner assetsOwner = russianAssetsOwnerRepository
                    .findById(Long.parseLong(assetsOwnerID)).orElseThrow();
            Float accountCashAmount = accountCashRepository
                    .findByAccountAndAssetCurrencyAndAssetsOwner(account, firstBuyFixedRateBondDTO.getAssetCurrency(),
                            assetsOwner).getAmount();

            correctAccountCashAmounts.put(assetsOwnerID, accountCashAmount);
        }

        /*
        Вручную уменьшили значения в этой мапе, чтобы потом сверить их с теми, что получатся при запросе.
         */
        for (Map.Entry<String, Float> element : assetOwnersWithAssetCountsMapFromDTO.entrySet()) {
            String assetsOwnerID = element.getKey();
            Float amountToChangeValue = element.getValue();
            Float newValueToPreviousCreatedMap = correctAccountCashAmounts.get(assetsOwnerID)
                    - amountToChangeValue * firstBuyFixedRateBondDTO.getBondParValue()
                    - amountToChangeValue * firstBuyFixedRateBondDTO.getBondParValue()
                        * turnoverCommissionValueRepository.findAll().get(0).getCommissionPercentValue();

            correctAccountCashAmounts.put(assetsOwnerID, newValueToPreviousCreatedMap);
        }

        testUtils.perform(post("/data" + FIXED_RATE_BOND_CONTROLLER_PATH)
                        .content(asJson(firstBuyFixedRateBondDTO))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        /*
        Проходимся по мапе с корректными значениями, и сверяем со значениями в репозитории, чтобы понять, корректно ли
        изменились значения там после выполнения запроса на создание пакета облигаций.
         */
        for (Map.Entry<String, Float> element : correctAccountCashAmounts.entrySet()) {
            Account account = accountRepository.findById(firstBuyFixedRateBondDTO.getAccountID()).orElseThrow();
            AssetCurrency assetCurrency = firstBuyFixedRateBondDTO.getAssetCurrency();
            Long accountCashOwnerID = Long.valueOf(element.getKey());
            RussianAssetsOwner assetsOwner = russianAssetsOwnerRepository.findById(accountCashOwnerID).orElseThrow();

            assertEquals(element.getValue(), accountCashRepository.findByAccountAndAssetCurrencyAndAssetsOwner(account,
                            assetCurrency, assetsOwner).getAmount());
        }
    }
}

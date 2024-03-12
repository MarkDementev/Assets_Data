package fund.data.assets.utils;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.dto.TurnoverCommissionValueDTO;
import fund.data.assets.model.financial_entities.TurnoverCommissionValue;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.service.impl.AccountServiceImpl;
import fund.data.assets.service.impl.TurnoverCommissionValueServiceImpl;
import fund.data.assets.utils.enums.CommissionSystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static fund.data.assets.TestUtils.TEST_ASSET_TYPE_NAME;
import static fund.data.assets.TestUtils.TEST_COMMISSION_PERCENT_VALUE;
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForTests.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class CommissionCalculatorTest {
    public static final Integer TEST_ASSET_COUNT = 10;
    public static final Float TEST_BOND_PURCHASE_MARKET_PRICE = 1000.0F;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private TurnoverCommissionValueServiceImpl turnoverCommissionValueService;
    @Autowired
    private CommissionCalculator commissionCalculator;

    @BeforeEach
    public void prepareAccountAndTurnoverCommissionValue() throws Exception {
        testUtils.createDefaultTurnoverCommissionValue();
    }

    @AfterEach
    public void clearRepositories() {
        testUtils.tearDown();
    }

    @ParameterizedTest
    @EnumSource(CommissionSystem.class)
    public void testCalculateTotalCommissionForPurchaseNotImplementedCommissionSystem(
            CommissionSystem commissionSystem) {
        if (commissionSystem.equals(CommissionSystem.TURNOVER)) {
            Assertions.assertDoesNotThrow(
                    () -> commissionCalculator.calculateTotalCommissionForPurchase(
                            commissionSystem,
                            accountService.getAccounts().get(0),
                            TEST_ASSET_TYPE_NAME,
                            TEST_ASSET_COUNT,
                            TEST_BOND_PURCHASE_MARKET_PRICE)
            );
        } else {
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> commissionCalculator.calculateTotalCommissionForPurchase(
                            commissionSystem,
                            accountService.getAccounts().get(0),
                            TEST_ASSET_TYPE_NAME,
                            TEST_ASSET_COUNT,
                            TEST_BOND_PURCHASE_MARKET_PRICE));
        }
    }

    @ParameterizedTest
    @CsvSource(value = {"0.01F, 100.0F", "0.1F, 1000.0F"})
    public void testCalculateTotalCommissionForPurchase(Float commissionPercentValue, Float inputCorrectResult) {
        TurnoverCommissionValue turnoverCommissionValue = turnoverCommissionValueService
                .getTurnoverCommissionValues().get(0);

        turnoverCommissionValue.setCommissionPercentValue(commissionPercentValue);
        Assertions.assertEquals(commissionCalculator.calculateTotalCommissionForPurchase(
                CommissionSystem.TURNOVER,
                accountService.getAccounts().get(0),
                TEST_ASSET_TYPE_NAME,
                TEST_ASSET_COUNT,
                TEST_BOND_PURCHASE_MARKET_PRICE
                ), inputCorrectResult);
    }
}

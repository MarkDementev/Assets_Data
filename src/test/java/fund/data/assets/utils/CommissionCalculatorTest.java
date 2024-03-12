package fund.data.assets.utils;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.dto.TurnoverCommissionValueDTO;
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
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForTests.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class CommissionCalculatorTest {
    public static final Integer TEST_ASSET_COUNT = 10;
    public static final Float TEST_BOND_PURCHASE_MARKET_PRICE = 1000.0F;
    public static final String CSV_SOURCE_TEST_VALUE_FIRST = "0.02F, 200.0F";
    public static final String CSV_SOURCE_TEST_VALUE_SECOND = "0.2F, 2000.0F";
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
    @CsvSource(value = {CSV_SOURCE_TEST_VALUE_FIRST, CSV_SOURCE_TEST_VALUE_SECOND})
    public void testCalculateTotalCommissionForPurchase(Float commissionPercentValue, Float inputCorrectResult) {
        Long turnoverCommissionValueIDToUpdate = turnoverCommissionValueService
                .getTurnoverCommissionValues().get(0).getId();
        final TurnoverCommissionValueDTO turnoverCommissionValueDTOToUpdate = new TurnoverCommissionValueDTO(
                CommissionSystem.TURNOVER,
                turnoverCommissionValueIDToUpdate,
                TEST_ASSET_TYPE_NAME,
                commissionPercentValue
        );

        turnoverCommissionValueService.updateTurnoverCommissionValue(turnoverCommissionValueIDToUpdate,
                turnoverCommissionValueDTOToUpdate);
        Assertions.assertEquals(commissionCalculator.calculateTotalCommissionForPurchase(
                CommissionSystem.TURNOVER,
                accountService.getAccounts().get(0),
                TEST_ASSET_TYPE_NAME,
                TEST_ASSET_COUNT,
                TEST_BOND_PURCHASE_MARKET_PRICE
                ), inputCorrectResult);
    }
}

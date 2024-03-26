package fund.data.assets.utils;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.dto.common.PercentFloatValueDTO;
import fund.data.assets.service.impl.AccountServiceImpl;
import fund.data.assets.service.impl.TurnoverCommissionValueServiceImpl;
import fund.data.assets.utils.enums.CommissionSystem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.function.Supplier;

import static fund.data.assets.TestUtils.TEST_ASSET_TYPE_NAME;
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = SpringConfigForTests.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class CommissionCalculatorTest {
    public static final Integer TEST_ASSET_COUNT = 10;
    public static final Float TEST_DIRTY_BOND_PRICE_IN_CURRENCY = 1000.0F;
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
        Supplier<Float> calculation = new Supplier<>() {
            @Override
            public Float get() {
                return commissionCalculator.calculateTotalCommissionForPurchase(
                        commissionSystem,
                        accountService.getAccounts().get(0),
                        TEST_ASSET_TYPE_NAME,
                        TEST_ASSET_COUNT,
                        TEST_DIRTY_BOND_PRICE_IN_CURRENCY);
            }
        };

        if (commissionSystem.equals(CommissionSystem.TURNOVER)) {
            Assertions.assertDoesNotThrow(() -> calculation.get());
        } else if (commissionSystem.equals(CommissionSystem.NOT_IMPLEMENTED)) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> calculation.get());
        }
    }

    @ParameterizedTest
    @CsvSource(value = {CSV_SOURCE_TEST_VALUE_FIRST, CSV_SOURCE_TEST_VALUE_SECOND})
    public void testCalculateTotalCommissionForPurchase(Float commissionPercentValue, Float inputCorrectResult) {
        Long turnoverCommissionValueIDToUpdate = turnoverCommissionValueService
                .getTurnoverCommissionValues().get(0).getId();

        final PercentFloatValueDTO percentFloatValueDTO = testUtils.getPercentFloatValueDTO();
        percentFloatValueDTO.setPercentValue(commissionPercentValue);
        turnoverCommissionValueService.updateTurnoverCommissionValue(turnoverCommissionValueIDToUpdate,
                percentFloatValueDTO);

        Assertions.assertEquals(commissionCalculator.calculateTotalCommissionForPurchase(
                CommissionSystem.TURNOVER,
                accountService.getAccounts().get(0),
                TEST_ASSET_TYPE_NAME,
                TEST_ASSET_COUNT,
                TEST_DIRTY_BOND_PRICE_IN_CURRENCY
                ), inputCorrectResult);
    }
}

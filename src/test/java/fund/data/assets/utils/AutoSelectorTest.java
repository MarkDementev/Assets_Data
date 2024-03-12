package fund.data.assets.utils;

import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.model.asset.exchange.FixedRateBond;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;
import static fund.data.assets.utils.AutoSelector.COMMISSION_SYSTEM_CHOOSE;
import static fund.data.assets.utils.AutoSelector.TAX_SYSTEM_CHOOSE;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForTests.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class AutoSelectorTest {
    @ParameterizedTest
    @ArgumentsSource(testSelectAssetOperationsCostSystemProvider.class)
    public void testSelectAssetOperationsCostSystem(AssetCurrency assetCurrency, String assetTypeName,
                                                    String costSystemToChoose, Enum<? extends Enum<?>> correctReturn) {
        Assertions.assertEquals(
                AutoSelector.selectAssetOperationsCostSystem(assetCurrency, assetTypeName, costSystemToChoose),
                correctReturn
        );
    }

    static class testSelectAssetOperationsCostSystemProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(AssetCurrency.RUSRUB, FixedRateBond.class.getTypeName(), COMMISSION_SYSTEM_CHOOSE,
                            CommissionSystem.TURNOVER
                    ),
                    Arguments.of(AssetCurrency.RUSRUB, FixedRateBond.class.getTypeName(), TAX_SYSTEM_CHOOSE,
                            TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE
                    )
            );
        }
    }

    //Ещё один тест с выбросом исключений надо!
}

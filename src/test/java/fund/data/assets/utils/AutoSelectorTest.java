package fund.data.assets.utils;

import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.model.asset.exchange.FixedRateBondPackage;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;
import static fund.data.assets.utils.AutoSelector.COMMISSION_SYSTEM_CHOOSE;
import static fund.data.assets.utils.AutoSelector.TAX_SYSTEM_CHOOSE;
import static fund.data.assets.utils.AutoSelector.NO_TAX_SYSTEM_CHOOSE;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = SpringConfigForTests.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class AutoSelectorTest {
    public static final String TEST_WRONG_CHOOSE = "TEST_WRONG_CHOOSE";
    public static final String TEST_WRONG_ASSET_TYPE = "TEST_WRONG_ASSET_TYPE";
    public static final AssetCurrency TEST_WRONG_CURRENCY = AssetCurrency.NOT_IMPLEMENTED;

    @ParameterizedTest
    @ArgumentsSource(testSelectAssetOperationsCostSystemProvider.class)
    public void testSelectAssetOperationsCostSystem(AssetCurrency assetCurrency, String assetTypeName,
                                                    String costSystemToChoose, Enum<? extends Enum<?>> correctReturn) {
        Assertions.assertEquals(
                correctReturn,
                AutoSelector.selectAssetOperationsCostSystem(assetCurrency, assetTypeName, costSystemToChoose)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(testSelectAssetOperationsCostSystemThrowsExceptionsProvider.class)
    public void testSelectAssetOperationsCostSystemThrowsExceptions(AssetCurrency assetCurrency, String assetTypeName,
                                                                    String costSystemToChoose) {
        Supplier<Enum<? extends Enum<?>>> testMethodSupplier = new Supplier<>() {
            @Override
            public Enum<? extends Enum<?>> get() {
                return AutoSelector.selectAssetOperationsCostSystem(assetCurrency, assetTypeName, costSystemToChoose);
            }
        };

        if (assetCurrency.equals(TEST_WRONG_CURRENCY)
                || assetTypeName.equals(TEST_WRONG_ASSET_TYPE)
                || costSystemToChoose.equals(TEST_WRONG_CHOOSE)) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> testMethodSupplier.get());
        }
    }

    static class testSelectAssetOperationsCostSystemProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(AssetCurrency.RUSRUB, FixedRateBondPackage.class.getTypeName(), COMMISSION_SYSTEM_CHOOSE,
                            CommissionSystem.TURNOVER
                    ),
                    Arguments.of(AssetCurrency.RUSRUB, FixedRateBondPackage.class.getTypeName(), TAX_SYSTEM_CHOOSE,
                            TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE
                    ),
                    Arguments.of(AssetCurrency.RUSRUB, FixedRateBondPackage.class.getTypeName(), NO_TAX_SYSTEM_CHOOSE,
                            TaxSystem.NO_TAX
                    )
            );
        }
    }

    static class testSelectAssetOperationsCostSystemThrowsExceptionsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(AssetCurrency.RUSRUB, FixedRateBondPackage.class.getTypeName(), TEST_WRONG_CHOOSE
                    ),
                    Arguments.of(AssetCurrency.RUSRUB, TEST_WRONG_ASSET_TYPE, TAX_SYSTEM_CHOOSE
                    ),
                    Arguments.of(TEST_WRONG_CURRENCY, FixedRateBondPackage.class.getTypeName(), NO_TAX_SYSTEM_CHOOSE
                    )
            );
        }
    }
}

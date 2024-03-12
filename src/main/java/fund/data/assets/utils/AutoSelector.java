package fund.data.assets.utils;

import fund.data.assets.model.asset.exchange.FixedRateBond;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import static fund.data.assets.utils.enums.AssetCurrency.RUSRUB;

@Component
@RequiredArgsConstructor
public class AutoSelector {
    public static final String NOT_IMPLEMENTED_CURRENCY = "Sorry, this currency is not yet supported by the fund.";
    public static final String NOT_IMPLEMENTED_ASSET_TYPE = "Sorry, this asset type tax system is not yet" +
            " supported by the fund.";
    public static final String WRONG_COST_SYSTEM_TO_CHOOSE_WARNING = "There is programmers error - this method uses" +
            " only COMMISSION_SYSTEM_CHOOSE and TAX_SYSTEM_CHOOSE by costSystemToChoose variable!";
    public static final String COMMISSION_SYSTEM_CHOOSE = "COMMISSION_SYSTEM";
    public static final String TAX_SYSTEM_CHOOSE = "TAX_SYSTEM";
    public static final String NO_TAX_SYSTEM_CHOOSE = "NO_TAX_SYSTEM";

    public static Enum<? extends Enum<?>> selectAssetOperationsCostSystem(AssetCurrency assetCurrency,
                                                                          String assetTypeName,
                                                                          String costSystemToChoose) {
        if (assetCurrency.equals(RUSRUB)) {
            if (assetTypeName.equals(FixedRateBond.class.getTypeName())) {
                switch (costSystemToChoose) {
                    case COMMISSION_SYSTEM_CHOOSE:
                        return CommissionSystem.TURNOVER;
                    case TAX_SYSTEM_CHOOSE:
                        return TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE;
                    case NO_TAX_SYSTEM_CHOOSE:
                        return TaxSystem.NO_TAX;
                    default:
                        throw new RuntimeException(WRONG_COST_SYSTEM_TO_CHOOSE_WARNING);
                }
            }
            throw new IllegalArgumentException(NOT_IMPLEMENTED_ASSET_TYPE);
        }
        throw new IllegalArgumentException(NOT_IMPLEMENTED_CURRENCY);
    }
}

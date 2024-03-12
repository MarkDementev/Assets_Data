package fund.data.assets.utils;

import fund.data.assets.model.assets.exchange.FixedRateBond;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;

import static fund.data.assets.utils.enums.AssetCurrency.RUSRUB;

public class AutoSelector {
    public static String NOT_IMPLEMENTED_CURRENCY = "Sorry, this currency is not yet supported by the fund.";
    public static String NOT_IMPLEMENTED_ASSET_TYPE_TAX_SYSTEM = "Sorry, this asset type tax system is not yet" +
            " supported by the fund.";

    public static TaxSystem selectTaxSystem(AssetCurrency assetCurrency, String assetTypeName) {
        if (assetCurrency.getTitle().equals(RUSRUB.getTitle())) {
            if (assetTypeName.equals(FixedRateBond.class.getTypeName())) {
                return TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE;
            }
            throw new IllegalArgumentException(NOT_IMPLEMENTED_ASSET_TYPE_TAX_SYSTEM);
        }
        throw new IllegalArgumentException(NOT_IMPLEMENTED_CURRENCY);
    }

    public static CommissionSystem selectCommissionSystem(AssetCurrency assetCurrency, String assetTypeName,
                                                          String accountOrganisation) {

    }
}

package fund.data.assets.utils;

import fund.data.assets.utils.enums.CommissionSystem;

import static fund.data.assets.utils.enums.CommissionSystem.TURNOVER;

public class CommissionCalculator {
    public static final String NOT_IMPLEMENTED_COMMISSION_SYSTEM_TO_CALCULATE = "Sorry, this commission system" +
            " is not yet supported by the fund.";

    public static Float calculateTotalCommissionForPurchase(CommissionSystem commissionSystem, Integer assetCount,
                                                            Float bondPurchaseMarketPrice) {
        if (commissionSystem.equals(TURNOVER)) {
            Float commissionPercentValue = findCommissionPercentValue();

            return assetCount * bondPurchaseMarketPrice * commissionPercentValue;
        } else {
            throw new IllegalArgumentException(NOT_IMPLEMENTED_COMMISSION_SYSTEM_TO_CALCULATE);
        }
    }

    private static Float findCommissionPercentValue() {
        //Здесь надо находить в БД флоат комиссии исходя из типа актива и счёта, где находится актив.
        return null;
    }
}

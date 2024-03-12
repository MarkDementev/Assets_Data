package fund.data.assets.utils;

import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.utils.enums.CommissionSystem;

import org.springframework.beans.factory.annotation.Autowired;

import static fund.data.assets.utils.enums.CommissionSystem.TURNOVER;

//Надо юнит-тесты прописать для всего тут.
public class CommissionCalculator {
    public static final String NOT_IMPLEMENTED_COMMISSION_SYSTEM_TO_CALCULATE = "Sorry, this commission system" +
            " is not yet supported by the fund.";
    private final TurnoverCommissionValueRepository turnoverCommissionValueRepository;

    public CommissionCalculator(@Autowired TurnoverCommissionValueRepository turnoverCommissionValueRepository) {
        this.turnoverCommissionValueRepository = turnoverCommissionValueRepository;
    }

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
        //Нужно внедрить AssetRelationship, чтобы получать оттуда счёт!!!
//        Float commissionPercentValue = turnoverCommissionValueRepository
        return null;
    }
}

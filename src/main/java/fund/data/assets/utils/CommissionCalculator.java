package fund.data.assets.utils;

import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.utils.enums.CommissionSystem;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fund.data.assets.utils.enums.CommissionSystem.TURNOVER;

@Component
@RequiredArgsConstructor
public class CommissionCalculator {
    public static final String NOT_IMPLEMENTED_COMMISSION_SYSTEM_TO_CALCULATE = "Sorry, this commission system" +
            " is not yet supported by the fund.";
    @Autowired
    private TurnoverCommissionValueRepository turnoverCommissionValueRepository;

    public Float calculateTotalCommissionForPurchase(CommissionSystem commissionSystem,
                                                            Account account, String assetTypeName,
                                                            Integer assetCount, Float bondPurchaseMarketPrice) {
        if (commissionSystem.equals(TURNOVER)) {
            Float commissionPercentValue = findTurnoverCommissionValue(account, assetTypeName);

            return assetCount * bondPurchaseMarketPrice * commissionPercentValue;
        } else {
            throw new IllegalArgumentException(NOT_IMPLEMENTED_COMMISSION_SYSTEM_TO_CALCULATE);
        }
    }

    private Float findTurnoverCommissionValue(Account account, String assetTypeName) {
        return turnoverCommissionValueRepository.findByAccountAndAssetTypeName(account, assetTypeName)
                .getCommissionPercentValue();
    }
}

package fund.data.assets.utils;

import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.utils.enums.CommissionSystem;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fund.data.assets.utils.enums.CommissionSystem.TURNOVER;

/**
 * Класс для расчёта общего размера комиссии с оборота по активу при конкретной транзакции.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Component
@AllArgsConstructor
public class CommissionCalculator {
    public static final String NOT_IMPLEMENTED_COMMISSION_SYSTEM_TO_CALCULATE = "Sorry, this commission system" +
            " is not yet supported by the fund.";
    @Autowired
    private TurnoverCommissionValueRepository turnoverCommissionValueRepository;

    /**
     * Метод рассчитывает общий размер комиссии с оборота по активу при конкретной транзакции.
     * @param commissionSystem Система комиссии, необходимо, чтобы была только CommissionSystem.TURNOVER.
     * @param account Счёт, где проводится операция.
     * @param assetTypeName Тип актива, с которым проводится операция.
     * @param assetCount Количество единиц актива, с которым проводится операция.
     * @param dirtyBondPriceInCurrency "Грязная" цена облигации = (рыночная цена * номинал + НКД).
     * @return Возвращает общий размер комиссии с оборота по активу - число с плавающей запятой.
     * @throws IllegalArgumentException Если в качестве аргумента commissionSystem не CommissionSystem.TURNOVER.
     * @since 0.0.1-alpha
     */
    public Float calculateTotalCommissionForPurchase(CommissionSystem commissionSystem,
                                                            Account account, String assetTypeName,
                                                            Integer assetCount, Float dirtyBondPriceInCurrency) {
        if (commissionSystem.equals(TURNOVER)) {
            Float commissionPercentValue = findTurnoverCommissionValue(account, assetTypeName);

            return assetCount * dirtyBondPriceInCurrency * commissionPercentValue;
        } else {
            throw new IllegalArgumentException(NOT_IMPLEMENTED_COMMISSION_SYSTEM_TO_CALCULATE);
        }
    }

    /**
     * Находит комиссию - размер процента с оборота.
     * @param account Счёт, на котором проводится операция.
     * @param assetTypeName Тип актива, с которым проводится операция.
     * @return Возвращает процента с оборота - число с плавающей запятой.
     * @since 0.0.1-alpha
     */
    private Float findTurnoverCommissionValue(Account account, String assetTypeName) {
        return turnoverCommissionValueRepository.findByAccountAndAssetTypeName(account, assetTypeName)
                .getCommissionPercentValue();
    }
}

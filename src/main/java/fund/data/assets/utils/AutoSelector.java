package fund.data.assets.utils;

import fund.data.assets.model.asset.exchange.FixedRateBondPackage;
import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import static fund.data.assets.utils.enums.AssetCurrency.RUSRUB;

/**
 * Класс для автоматического подбора типа системы налогообложения актива и системы расчёта брокерской комиссии.
 * @version 0.1-b
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Component
@RequiredArgsConstructor
public class AutoSelector {
    public static final String NOT_IMPLEMENTED_CURRENCY = "Sorry, this currency is not yet supported by the fund.";
    public static final String NOT_IMPLEMENTED_ASSET_TYPE = "Sorry, this asset type tax system is not yet"
            + " supported by the fund.";
    public static final String WRONG_COST_SYSTEM_TO_CHOOSE_WARNING = "There is programmers error - this method uses"
            + " only COMMISSION_SYSTEM_CHOOSE and TAX_SYSTEM_CHOOSE by costSystemToChoose variable!";
    public static final String COMMISSION_SYSTEM_CHOOSE = "COMMISSION_SYSTEM";
    public static final String TAX_SYSTEM_CHOOSE = "TAX_SYSTEM";
    public static final String NO_TAX_SYSTEM_CHOOSE = "NO_TAX_SYSTEM";

    /**
     * Автоматически подбирает тип системы налогообложения и системы расчёта брокерской комиссии.
     * Этот класс и этот метод - временные, до внедрения иных модулей для налогообложения и комиссий.
     * @param assetCurrency Валюта актива.
     * @param assetTypeName Тип актива.
     * @param costSystemToChoose Техническое значение - указатель, какой тип системы возвращать, т.к. метод полиморфный.
     * @return Возвращает либо CommissionSystem, либо TaxSystem.
     * @throws IllegalArgumentException Если в качестве аргументов assetCurrency/assetTypeName не поддерживаемые
     * пока что системой фонда валюта актива/тип актива. Либо если в качестве аргумента costSystemToChoose не
     * COMMISSION_SYSTEM_CHOOSE или TAX_SYSTEM_CHOOSE.
     * @since 0.1-b
     */
    public static Enum<? extends Enum<?>> selectAssetOperationsCostSystem(AssetCurrency assetCurrency,
                                                                          String assetTypeName,
                                                                          String costSystemToChoose) {
        if (assetCurrency.equals(RUSRUB)) {
            if (assetTypeName.equals(FixedRateBondPackage.class.getSimpleName())) {
                switch (costSystemToChoose) {
                    case COMMISSION_SYSTEM_CHOOSE:
                        return CommissionSystem.TURNOVER;
                    case TAX_SYSTEM_CHOOSE:
                        return TaxSystem.EQUAL_COUPON_DIVIDEND_TRADE;
                    case NO_TAX_SYSTEM_CHOOSE:
                        return TaxSystem.NO_TAX;
                    default:
                        throw new IllegalArgumentException(WRONG_COST_SYSTEM_TO_CHOOSE_WARNING);
                }
            }
            throw new IllegalArgumentException(NOT_IMPLEMENTED_ASSET_TYPE);
        }
        throw new IllegalArgumentException(NOT_IMPLEMENTED_CURRENCY);
    }
}

package fund.data.assets.utils.enums;

/**
 * Система налогообложения актива.
 * В РФ единообразная и плоская система налогообложения дохода с активов, потому вместо Entity используется Enum.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public enum TaxSystem {
    NO_TAX("Asset without any taxation"),
    EQUAL_COUPON_DIVIDEND_TRADE("Equal taxation of all sources of income");

    private final String title;

    TaxSystem(String title) {
        this.title = title;
    }
}

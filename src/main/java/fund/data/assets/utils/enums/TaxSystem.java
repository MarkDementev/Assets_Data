package fund.data.assets.utils.enums;

public enum TaxSystem {
    NO_TAX("Asset without any taxation"),
    EQUAL_COUPON_DIVIDEND_TRADE("Equal taxation of all sources of income");

    private final String title;

    TaxSystem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

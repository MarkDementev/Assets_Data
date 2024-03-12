package fund.data.assets.utils.enums;

public enum AssetCurrency {
    NOT_IMPLEMENTED("Not implemented currency - for tests use only for now"),
    RUSRUB("Russian ruble");

    private final String title;

    AssetCurrency(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

package fund.data.assets.utils.enums;

public enum AssetCurrency {
    RUSRUB("Russian ruble");

    private final String title;

    AssetCurrency(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

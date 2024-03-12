package fund.data.assets;

public enum Currency {
    RUSRUB("Russian ruble");

    private final String title;

    Currency(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

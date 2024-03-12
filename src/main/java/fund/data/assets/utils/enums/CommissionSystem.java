package fund.data.assets.utils.enums;

public enum CommissionSystem {
    TURNOVER("Commission charged on turnover");

    private final String title;

    CommissionSystem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

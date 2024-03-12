package fund.data.assets.utils.enums;

public enum CommissionSystem {
    TURNOVER("Commission charged on turnover"), NOT_IMPLEMENTED("Not implemented commission for tests use");

    private final String title;

    CommissionSystem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

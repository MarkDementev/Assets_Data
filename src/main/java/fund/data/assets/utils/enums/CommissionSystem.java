package fund.data.assets.utils.enums;

public enum CommissionSystem {
    NOT_IMPLEMENTED("Not implemented commission - for tests use only for now"),
    TURNOVER("Commission charged on turnover");

    private final String title;

    CommissionSystem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

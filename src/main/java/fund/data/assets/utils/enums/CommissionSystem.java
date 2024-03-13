package fund.data.assets.utils.enums;

/**
 * Система расчёта брокерской комиссии.
 * В РФ почти всегда для определения системы расчёта размера брокерской комиссии используется уплата процента
 * с оборота по ценной бумаге. Потому вместо Entity используется Enum.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public enum CommissionSystem {
    NOT_IMPLEMENTED("Not implemented commission - for tests use only for now"),
    TURNOVER("Commission charged on turnover");

    private final String title;

    CommissionSystem(String title) {
        this.title = title;
    }
}

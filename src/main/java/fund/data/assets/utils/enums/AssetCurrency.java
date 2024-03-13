package fund.data.assets.utils.enums;

/**
 * Валюта актива.
 * Перечень валют, для инвестирования в которых есть доступ в РФ, крайне ограничен. Потому вместо Entity
 * используется Enum c String для названия валюты.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public enum AssetCurrency {
    NOT_IMPLEMENTED("Not implemented currency - for tests use only for now"),
    RUSRUB("Russian ruble");

    private final String title;

    AssetCurrency(String title) {
        this.title = title;
    }
}

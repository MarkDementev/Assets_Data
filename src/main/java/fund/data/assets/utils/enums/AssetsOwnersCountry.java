package fund.data.assets.utils.enums;

/**
 * Поддерживаемые фондом гражданства инвесторов-собственников активов.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public enum AssetsOwnersCountry {
    RUS("Russian assets owners"),
    USA("American assets owners");

    private final String title;

    AssetsOwnersCountry(String title) {
        this.title = title;
    }
}

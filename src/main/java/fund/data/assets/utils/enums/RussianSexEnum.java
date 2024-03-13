package fund.data.assets.utils.enums;

/**
 * Энумератор - ограничитель для выбора пола при создания сущности российского гражданина - владельца актива.
 * В РФ по закону принято писать в паспорте только один из двух полов, потому внедрён этот энумератор.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public enum RussianSexEnum {
    MAN("Man"),
    WOMAN("Woman");

    private final String title;

    RussianSexEnum(String title) {
        this.title = title;
    }
}

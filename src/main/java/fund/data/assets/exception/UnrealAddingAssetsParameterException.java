package fund.data.assets.exception;

/**
 * Исключение - наследник IllegalArgumentException.
 * Нужно для случаев, когда используются аргументы метода, не соответствующие предметной области - финансам,
 * хотя могут быть абсолютно корректными с точки зрения написания программы.
 * @since 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public class UnrealAddingAssetsParameterException extends IllegalArgumentException {
    public UnrealAddingAssetsParameterException(String message) {
        super(message);
    }
}

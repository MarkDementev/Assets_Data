package fund.data.assets.exception;

/**
 * Исключение - наследник IllegalArgumentException.
 * Нужно для случаев, когда необходимо напомнить оператору ввода процентной величины о формате ввода.
 * Вводится либо число без запятой (например 100), либо с запятой, и необходимо ввести по четвёртый знак после
 * запятой (например, 15,2555). Данные вводы будут означать 100% и 15,2555% соответственно.
 * @since 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public class NotValidPercentValueInputFormatException extends IllegalArgumentException {
    public static final String MESSAGE = "Wrong input format! Correct formats are a natural number or" +
            " natural number with four decimal places.";

    public NotValidPercentValueInputFormatException() {
        super(MESSAGE);
    }
}

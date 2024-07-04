package fund.data.assets.exception;

import fund.data.assets.model.financial_entities.AccountCash;

import fund.data.assets.dto.AccountCashDTO;

/**
 * Исключение - наследник IllegalArgumentException.
 * Указывает на ошибку при создании {@link AccountCash}.
 * Вызывается, если при создании сущности в {@link AccountCashDTO} поле amountChangeValue имеет отрицательное значение.
 * @since 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public class NegativeValueNotExistAccountCashException extends IllegalArgumentException {
    public static final String MESSAGE = "Wrong amount change value! Negative value of this field is not appropriate "
            + "to AccountCash entity creation!";

    public NegativeValueNotExistAccountCashException() {
        super(MESSAGE);
    }
}

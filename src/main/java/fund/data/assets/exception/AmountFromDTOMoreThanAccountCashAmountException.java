package fund.data.assets.exception;

import fund.data.assets.dto.financial_entities.AccountCashDTO;
import fund.data.assets.model.financial_entities.AccountCash;

/**
 * Исключение - наследник IllegalArgumentException.
 * Указывает на ошибку при изменении размера денежных средств на счёте {@link AccountCash}.
 * Вызывается, если при уменьшении размера счёта поле amountChangeValue в {@link AccountCashDTO} имеет значение
 * меньшее, чем в изменяемом {@link AccountCash}.
 * @since 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public class AmountFromDTOMoreThanAccountCashAmountException extends IllegalArgumentException {
    public static final String MESSAGE = "Wrong amount change value! Amount change value is less than the" +
            " account cash amount by ";

    public AmountFromDTOMoreThanAccountCashAmountException(Float amountFromDTO, Float accountCashAmount) {
        super(MESSAGE + (Math.abs(amountFromDTO) - Math.abs(accountCashAmount)));
    }
}

package fund.data.assets.service;

import fund.data.assets.dto.CashDTO;
import fund.data.assets.model.financial_entities.Cash;

import java.util.List;

/**
 * Сервис для обслуживания денежных средств собственников активов на счетах.
 * Обслуживаемая сущность - {@link fund.data.assets.model.financial_entities.Cash}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public interface CashService {
    Cash getCash(Long id);
    List<Cash> getAllCash();
    Cash depositOrWithdrawCashAmount(CashDTO cashDTO);
}

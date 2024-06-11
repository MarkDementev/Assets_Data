package fund.data.assets.service;

import fund.data.assets.dto.AccountCashDTO;
import fund.data.assets.model.financial_entities.AccountCash;

import java.util.List;

/**
 * Сервис для обслуживания денежных средств собственников активов на счетах.
 * Обслуживаемая сущность - {@link AccountCash}.
 * Сущность жёстко привязана к активу и к собственнику, потому в данном сервисе отсутствует отдельный метод для
 * удаления сущности, удаление реализовано опосредованно, посредством удаления аккаунта или собственника денежных
 * средств.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public interface AccountCashService {
    AccountCash getCash(Long id);
    List<AccountCash> getAllCash();
    AccountCash depositOrWithdrawCashAmount(AccountCashDTO accountCashDTO);
}

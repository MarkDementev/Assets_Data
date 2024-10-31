package fund.data.assets.aspect;

import fund.data.assets.config.LoggerConfig;
import fund.data.assets.dto.financial_entities.AccountCashDTO;
import fund.data.assets.exception.EntityWithIDNotFoundException;
import fund.data.assets.logging.FinancialTransactionsFilter;
import fund.data.assets.model.financial_entities.AccountCash;
import fund.data.assets.repository.AccountCashRepository;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.RussianAssetsOwnerRepository;

import lombok.RequiredArgsConstructor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * Для обеспечения возможности ручной проверки проведённых транзакций, логируется состояние счёта с денежными
 * средствами до и после её проведения.
 * @version 0.5-a
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Component
@RequiredArgsConstructor
@Aspect
public class FinancialTransactionAspect {
    private final Logger logger = LoggerConfig.getLogger();
    @Autowired
    private final AccountCashRepository accountCashRepository;
    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final RussianAssetsOwnerRepository russianAssetsOwnerRepository;

    @Pointcut("execution(public * fund.data.assets.service.impl.AccountCashServiceImpl" +
            ".createAccountCashOrChangeAmount(..))")
    public void accountCashPointCut() {}

    @Around("accountCashPointCut() && args(accountCashDTO)")
    public AccountCash logAfterFinancialTransaction(ProceedingJoinPoint pJP, AccountCashDTO accountCashDTO)
            throws Throwable {
        boolean isMethodProceeded = false;
        String[] startLogArr;
        String[] endLogArr = new String[4];
        Long accountIDFromDTO = accountCashDTO.getAccountID();
        Long assetsOwnerIDFromDTO = accountCashDTO.getAssetsOwnerID();
        Optional<AccountCash> accountCash = Optional.ofNullable(
                accountCashRepository.findByAccountAndAssetCurrencyAndAssetsOwner(
                    accountRepository.findById(accountIDFromDTO).orElseThrow(
                        () -> new EntityWithIDNotFoundException("AccountCash", accountIDFromDTO)),
                    accountCashDTO.getAssetCurrency(),
                    russianAssetsOwnerRepository.findById(assetsOwnerIDFromDTO).orElseThrow(
                        () -> new EntityWithIDNotFoundException("AssetsOwner", assetsOwnerIDFromDTO))
                )
        );

        if (accountCash.isPresent()) {
            startLogArr = new String[4];
            fillLogArr(startLogArr, accountCash.get());
            startLogArr[3] = accountCash.get().getAmount().toString()
                    + " diff by " + accountCashDTO.getAmountChangeValue();
        } else {
            startLogArr = new String[2];
            startLogArr[0] = "AccountCashRepository is empty at start";
            startLogArr[1] = "Account cash is creating with amount = " + accountCashDTO.getAmountChangeValue();
        }
        AccountCash returnValue = (AccountCash) pJP.proceed();

        //TODO придумай проверку, что транзакция прошла успешно
//        if () {
            isMethodProceeded = true;
//        }

        if (isMethodProceeded) {
            AccountCash accountCashCorrected = accountCashRepository.findAll().stream()
                    .sorted(Comparator.comparing(AccountCash::getUpdatedAt).reversed())
                    .findFirst()
                    .orElseThrow();

            fillLogArr(endLogArr, accountCashCorrected);

            endLogArr[3] = accountCashCorrected.getAmount().toString();
        }

        if (isMethodProceeded) {
            logger.warn(FinancialTransactionsFilter.KEY_TO_CONTAINS_START + "{}", Arrays.toString(startLogArr));
            logger.warn(FinancialTransactionsFilter.KEY_TO_CONTAINS_END + "{}", Arrays.toString(endLogArr));
        }
        return returnValue;
    }

    private void fillLogArr(String[] logArr, AccountCash accountCash) {
        String separator = " ";
        logArr[0] = accountCash.getId().toString();
        logArr[1] = accountCash.getAssetsOwner().getName()
                + separator + accountCash.getAssetsOwner().getSurname()
                + separator + accountCash.getAssetsOwner().getEmail();
        logArr[2] = accountCash.getAssetCurrency().toString();
    }
}

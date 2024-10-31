package fund.data.assets.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Для обеспечения возможности ручной проверки проведённых транзакций, они логируются в отдельный файл.
 * @version 0.5-a
 * @author MarkDementev a.k.a JavaMarkDem
 */
public class FinancialTransactionsFilter extends Filter<ILoggingEvent> {
    public static final String KEY_TO_CONTAINS_START = "FT-start / ";
    public static final String KEY_TO_CONTAINS_END = "FT-end / ";

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage().contains(KEY_TO_CONTAINS_START)
                || event.getMessage().contains(KEY_TO_CONTAINS_END)) {
            return FilterReply.ACCEPT;
        } else {
            return FilterReply.DENY;
        }
    }
}

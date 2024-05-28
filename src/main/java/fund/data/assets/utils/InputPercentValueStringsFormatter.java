package fund.data.assets.utils;

import fund.data.assets.exception.NotValidPercentValueInputFormatException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для форматирования введённого пользователем процентного значения из String во Float.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Component
@RequiredArgsConstructor
public class InputPercentValueStringsFormatter {
    /**
     * Метод проверяет соответствие введённого значения одному из двух возможных форматов и
     * преобразовывает корректное значение из String во Float.
     * @param inputString Строка с размером процентного значения.
     * @return Возвращает введённое значение в формате Float для дальнейшего использования.
     * @throws NotValidPercentValueInputFormatException Если введённое значение некорректно.
     * @since 0.0.1-alpha
     */
    public static Float getCheckedAndFormatted(String inputString) {
        String inputStringWithReplacedCommaByPoint = inputString.replace(",", ".");

        Pattern simplePercentStringPattern = Pattern.compile("^[1-9][0-9]*$");
        Pattern complexPercentStringPattern = Pattern.compile("^[1-9][0-9]*\\.[0-9]{3}[1-9]$");

        Matcher simplePercentStringMatcher = simplePercentStringPattern.matcher(inputStringWithReplacedCommaByPoint);
        Matcher complexPercentStringMatcher = complexPercentStringPattern.matcher(inputStringWithReplacedCommaByPoint);

        if (simplePercentStringMatcher.matches() || complexPercentStringMatcher.matches()) {
            return Float.parseFloat(inputStringWithReplacedCommaByPoint) / 100;
        } else {
            throw new NotValidPercentValueInputFormatException();
        }
    }
}

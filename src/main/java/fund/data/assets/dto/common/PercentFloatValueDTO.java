package fund.data.assets.dto.common;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO общего назначения для упрощения внесения данных о процентной величине в систему.
 * Вместо того чтобы прописывать значения в Float, можно вносить в String.
 * Нужно, так как процентные ставки обычно указываются в формате, например, 14%, а вот для расчётов уже придётся
 * превращать это в 0,14. Потому ввод будет осуществляться в виде String "14", и потом преобразовываться в иной нужный
 * формат в иной части кода.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PercentFloatValueDTO {
    @NotNull
    private String percentValue;
}

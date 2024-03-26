package fund.data.assets.dto.common;

import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO общего назначения для упрощения внесения данных о процентной величине в систему.
 * Вместо того чтобы прописывать значения в Float, можно вносить в String, и данные преобразуются внутри DTO.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@NoArgsConstructor
public class PercentFloatValueDTO {
    @NotNull
    private Float percentValue;

    public PercentFloatValueDTO(String stringFormatPercentValue) {
        //TODO Добавь валидацию правильности ввода
//        if () {
//
//        }
        this.percentValue = Float.parseFloat(stringFormatPercentValue) * 0.01F;
    }
}

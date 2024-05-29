package fund.data.assets.dto.owner;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.openapitools.jackson.nullable.JsonNullable;

/**
 * DTO для обслуживания обновления контактных данных о владельце активов с гражданством РФ.
 * Поддерживает частичное обновление данных сущности.
 * Обслуживаемая сущность - {@link fund.data.assets.model.asset.owner.RussianAssetsOwner}.
 * Сервис сущности - {@link fund.data.assets.service.impl.RussianAssetsOwnerServiceImpl}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactDataRussianAssetsOwnerDTO {
    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private JsonNullable<String> email;

    @NotNull
    @Pattern(regexp = "^9[0-9]{9}$")
    private JsonNullable<String> mobilePhoneNumber;
}

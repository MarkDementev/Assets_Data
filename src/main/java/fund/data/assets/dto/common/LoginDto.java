package fund.data.assets.dto.common;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 0.6-a
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    @NotBlank
    private String login;

    @NotBlank
    private String password;
}

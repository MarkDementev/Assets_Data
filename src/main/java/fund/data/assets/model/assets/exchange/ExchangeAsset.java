package fund.data.assets.model.assets.exchange;

import fund.data.assets.model.AssetType;

import jakarta.persistence.MappedSuperclass;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class ExchangeAsset extends AssetType {
    @NotNull
    @Size(min = 12, max = 12)
    @Pattern(regexp = "[a-z]{2}[0-9]{10}")
    private String iSIN;

    //валюта эмиссии

    //название актива

    //название эмитента

    //стоимость при покупке
}

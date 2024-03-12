package fund.data.assets.model.assets.exchange;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fixed rate bonds")
@Getter
@Setter
public class FixedRateBond extends ExchangeAsset {
    //размер купона

    //как-то учти частоту выплат

    //дата погашения

    /*
    НЕ ЗАБУДЬ ДОСОЗДАТЬ СУЩНОСТЬ ОУНЕРОВ И ДОПИСАТЬ ASSET!
     */

    /*
    НАДО-БЫ ФАБРИКУ СОЗДАТЬ ДЛЯ УПРОЩЕНИЯ СОЗДАНИЯ СУЩНОСТЕЙ!?
     */

    public FixedRateBond(//add needed!) {
        super.setAssetTypeName(FixedRateBond.class.getTypeName());
    }
}

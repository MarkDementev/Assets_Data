package fund.data.assets.service;

import fund.data.assets.dto.owner.NewRussianAssetsOwnerDTO;
import fund.data.assets.dto.owner.PersonalDataRussianAssetsOwnerDTO;
import fund.data.assets.dto.owner.ContactDataRussianAssetsOwnerDTO;
import fund.data.assets.model.asset.owner.RussianAssetsOwner;

import java.util.List;

/**
 * Сервис для обслуживания владельца активов с гражданством РФ.
 * Обслуживаемая сущность - {@link fund.data.assets.model.asset.owner.RussianAssetsOwner}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public interface RussianAssetsOwnerService {
    RussianAssetsOwner getRussianAssetsOwner(Long id);
    List<RussianAssetsOwner> getRussianAssetsOwners();
    RussianAssetsOwner createRussianAssetsOwner(NewRussianAssetsOwnerDTO newRussianAssetsOwnerDTO);
    RussianAssetsOwner updateRussianAssetsOwnerPersonalData(
            Long id, PersonalDataRussianAssetsOwnerDTO personalDataRussianAssetsOwnerDTO);
    RussianAssetsOwner updateRussianAssetsOwnerContactData(
            Long id, ContactDataRussianAssetsOwnerDTO contactDataRussianAssetsOwnerDTO);
    void deleteRussianAssetsOwner(Long id);
}

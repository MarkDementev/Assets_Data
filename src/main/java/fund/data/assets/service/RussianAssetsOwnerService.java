package fund.data.assets.service;

import fund.data.assets.dto.NewRussianAssetsOwnerDTO;
//import fund.data.assets.dto.PersonalDataRussianAssetsOwnerDTO;
//import fund.data.assets.dto.NewAssetDataRussianAssetsOwnerDTO;
import fund.data.assets.model.asset.user.RussianAssetsOwner;

import java.util.List;

/**
 * Сервис для обслуживания владельца активов с гражданством РФ.
 * Обслуживаемая сущность - {@link fund.data.assets.model.asset.user.RussianAssetsOwner}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public interface RussianAssetsOwnerService {
    RussianAssetsOwner getRussianAssetsOwner(Long id);
    List<RussianAssetsOwner> getRussianAssetsOwners();
    RussianAssetsOwner createRussianAssetsOwner(NewRussianAssetsOwnerDTO newRussianAssetsOwnerDTO);
//    RussianAssetsOwner updatePersonalDataOfRussianAssetsOwner(
//            Long id, PersonalDataRussianAssetsOwnerDTO personalDataRussianAssetsOwnerDTO);
//    RussianAssetsOwner addNewAssetToRussianAssetsOwner(
//            Long id, NewAssetDataRussianAssetsOwnerDTO russianAssetsOwnerDTO);
    void deleteRussianAssetsOwner(Long id);
}

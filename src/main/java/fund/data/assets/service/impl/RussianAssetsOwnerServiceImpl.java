package fund.data.assets.service.impl;

import fund.data.assets.model.asset.user.RussianAssetsOwner;
import fund.data.assets.repository.RussianAssetsOwnerRepository;
import fund.data.assets.service.RussianAssetsOwnerService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для обслуживания владельца активов с гражданством РФ.
 * Обслуживаемая сущность - {@link fund.data.assets.model.asset.user.RussianAssetsOwner}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Service
@RequiredArgsConstructor
public class RussianAssetsOwnerServiceImpl implements RussianAssetsOwnerService {
    private final RussianAssetsOwnerRepository russianAssetsOwnerRepository;

    @Override
    public RussianAssetsOwner getRussianAssetsOwner(Long id) {
        return russianAssetsOwnerRepository.findById(id).orElseThrow();
    }

    @Override
    public List<RussianAssetsOwner> getRussianAssetsOwners() {
        return russianAssetsOwnerRepository.findAll();
    }

    @Override
    public RussianAssetsOwner createRussianAssetsOwner(NewRussianAssetsOwnerDTO newRussianAssetsOwnerDTO) {
        return null;
    }

    @Override
    public RussianAssetsOwner updatePersonalDataOfRussianAssetsOwner(
            Long id, PersonalDataRussianAssetsOwnerDTO personalDataRussianAssetsOwnerDTO) {
        return null;
    }

    @Override
    public RussianAssetsOwner addNewAssetToRussianAssetsOwner(
            Long id, NewAssetDataRussianAssetsOwnerDTO russianAssetsOwnerDTO) {
        return null;
    }

    @Override
    public void deleteRussianAssetsOwner(Long id) {
        russianAssetsOwnerRepository.deleteById(id);
    }
}

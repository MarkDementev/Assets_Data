package fund.data.assets.service.impl;

import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.model.asset.exchange.FixedRateBondPackage;
import fund.data.assets.repository.FixedRateBondRepository;
import fund.data.assets.service.FixedRateBondService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Реализация сервиса для обслуживания облигаций с фиксированным купоном.
 * Обслуживаемая сущность - {@link FixedRateBondPackage}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Service
@RequiredArgsConstructor
public class FixedRateBondServiceImpl implements FixedRateBondService {
    private final FixedRateBondRepository fixedRateBondRepository;

    @Override
    public FixedRateBondPackage getFixedRateBond(Long id) {
        return fixedRateBondRepository.findById(id).orElseThrow();
    }

    @Override
    public List<FixedRateBondPackage> getFixedRateBonds() {
        return fixedRateBondRepository.findAll();
    }

    @Override
    public FixedRateBondPackage firstBuyFixedRateBond(FirstBuyFixedRateBondDTO firstBuyFixedRateBondDTO) {
        isAssetOwnersWithAssetCountsValid(firstBuyFixedRateBondDTO.getAssetCount(),
                firstBuyFixedRateBondDTO.getAssetOwnersWithAssetCounts());
        //Пропиши связь с AccountCash
        //Пропиши связь с assetRelationship

        return null;
    }

    /**
     * Сумма выльюс в assetOwnersWithAssetCounts, приведённая к Integer, должна быть равна значению assetCount.
     */
    private void isAssetOwnersWithAssetCountsValid(Integer assetCount,
                                                   Map<String, Double> assetOwnersWithAssetCounts) {
        Double mapValuesSum = 0.0;

        for (Map.Entry<String, Double> mapElement : assetOwnersWithAssetCounts.entrySet()) {
            mapValuesSum += mapElement.getValue();
        }
        Integer mapValuesSumToInteger = mapValuesSum.intValue();

        if ((Math.ceil(mapValuesSum) != Math.floor(mapValuesSum))
                || (!mapValuesSumToInteger.equals(assetCount))) {
            throw new IllegalArgumentException("Field assetOwnersWithAssetCounts in DTO is not correct - sum of values"
                    + " are not equals field assetCount!");
        }
    }
}

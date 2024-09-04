package fund.data.assets.service;

import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.model.asset.exchange.FixedRateBondPackage;

import java.util.List;

/**
 * Сервис для обслуживания пакетов облигаций с фиксированным купоном.
 * Обслуживаемая сущность - {@link FixedRateBondPackage}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public interface FixedRateBondService {
    FixedRateBondPackage getFixedRateBond(Long id);
    List<FixedRateBondPackage> getFixedRateBonds();
    FixedRateBondPackage firstBuyFixedRateBond(FirstBuyFixedRateBondDTO firstBuyFixedRateBondDTO);
    void sellAllPackage(Long id);
}

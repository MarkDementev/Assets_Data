package fund.data.assets.service;

import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.model.asset.exchange.FixedRateBond;

import java.util.List;

/**
 * Сервис для обслуживания облигаций с фиксированным купоном.
 * Обслуживаемая сущность - {@link fund.data.assets.model.asset.exchange.FixedRateBond}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public interface FixedRateBondService {
    FixedRateBond getFixedRateBond(Long id);
    List<FixedRateBond> getFixedRateBonds();
    FixedRateBond firstBuyFixedRateBond(FirstBuyFixedRateBondDTO firstBuyFixedRateBondDTO);
}

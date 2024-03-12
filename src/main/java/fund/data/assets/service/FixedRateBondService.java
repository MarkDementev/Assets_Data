package fund.data.assets.service;

import fund.data.assets.model.asset.exchange.FixedRateBond;
import fund.data.assets.dto.FixedRateBondDTO;

import java.util.List;

public interface FixedRateBondService {
    FixedRateBond getFixedRateBond(Long id);
    List<FixedRateBond> getFixedRateBonds();
    FixedRateBond createFixedRateBond(FixedRateBondDTO fixedRateBondDTO);
//    FixedRateBond updateFixedRateBond(Long id, FixedRateBondDTO fixedRateBondDTO);
    void deleteFixedRateBond(Long id);
}

package fund.data.assets.service.impl;

import fund.data.assets.model.asset.exchange.FixedRateBond;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.repository.FixedRateBondRepository;
import fund.data.assets.service.FixedRateBondService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Реализация сервиса для обслуживания облигаций с фиксированным купоном.
 * Обслуживаемая сущность - {@link fund.data.assets.model.asset.exchange.FixedRateBond}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Service
@RequiredArgsConstructor
public class FixedRateBondServiceImpl implements FixedRateBondService {
    private final FixedRateBondRepository fixedRateBondRepository;

    @Override
    public FixedRateBond getFixedRateBond(Long id) {
        return fixedRateBondRepository.findById(id).orElseThrow();
    }

    @Override
    public List<FixedRateBond> getFixedRateBonds() {
        return fixedRateBondRepository.findAll();
    }

//    @Override
//    public FixedRateBond firstBuyFixedRateBond(FixedRateBondDTO fixedRateBondDTO) {
//        AtomicReference<FixedRateBond> atomicNewFixedRateBond = new AtomicReference<>(new FixedRateBond());
//
//        return null;
//    }
}

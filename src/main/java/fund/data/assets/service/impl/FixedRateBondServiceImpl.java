package fund.data.assets.service.impl;

import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.model.asset.exchange.FixedRateBond;
import fund.data.assets.model.asset.relationship.AssetRelationship;
import fund.data.assets.repository.FixedRateBondRepository;
import fund.data.assets.service.FixedRateBondService;

import fund.data.assets.utils.enums.AssetCurrency;
import fund.data.assets.utils.enums.CommissionSystem;
import fund.data.assets.utils.enums.TaxSystem;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
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

    @Override
    public FixedRateBond firstBuyFixedRateBond(FirstBuyFixedRateBondDTO firstBuyFixedRateBondDTO) {
        //Пропиши связь с AccountCash

        AtomicReference<FixedRateBond> atomicNewFixedRateBond = new AtomicReference<>(new FixedRateBond());

        return null;
    }
}

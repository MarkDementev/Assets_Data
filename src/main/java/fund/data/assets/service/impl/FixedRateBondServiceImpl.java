package fund.data.assets.service.impl;

import fund.data.assets.model.asset.exchange.FixedRateBond;
import fund.data.assets.repository.FixedRateBondRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FixedRateBondServiceImpl {
    private final FixedRateBondRepository fixedRateBondRepository;

//    @Override
    public FixedRateBond getFixedRateBond(Long id) {
        return fixedRateBondRepository.findById(id).orElseThrow();
    }

//    @Override
    public List<FixedRateBond> getFixedRateBonds() {
        return fixedRateBondRepository.findAll();
    }

//    @Override
//    public FixedRateBond createFixedRateBond(FixedRateBondDTO fixedRateBondDTO) {
//        String iSIN = fixedRateBondDTO.getISIN();
//        String assetIssuerTitle = fixedRateBondDTO.getAssetIssuerTitle();
//        LocalDate lastAssetBuyDate = fixedRateBondDTO.getLastAssetBuyDate();
//        AssetCurrency assetCurrency = fixedRateBondDTO.getAssetCurrency();
//        String assetTitle = fixedRateBondDTO.getAssetTitle();
//        Integer assetCount = fixedRateBondDTO.getAssetCount();
//        Integer bondParValue = fixedRateBondDTO.getBondParValue();
//        Float bondPurchaseMarketPrice = fixedRateBondDTO.getBondPurchaseMarketPrice();
//        Float bondAccruedInterest = fixedRateBondDTO.getBondAccruedInterest();
//        Float bondCouponValue = fixedRateBondDTO.getBondCouponValue();
//        Integer expectedBondCouponPaymentsCount = fixedRateBondDTO.getExpectedBondCouponPaymentsCount();
//        LocalDate bondMaturityDate = fixedRateBondDTO.getBondMaturityDate();
//
//        FixedRateBond newFixedRateBond = new FixedRateBond(iSIN, assetIssuerTitle, lastAssetBuyDate,
//                assetCurrency, assetTitle, assetCount, bondParValue, bondPurchaseMarketPrice, bondAccruedInterest,
//                bondCouponValue, expectedBondCouponPaymentsCount, bondMaturityDate);
//
//        return fixedRateBondRepository.save(newFixedRateBond);
//    }

//    @Override
    public void deleteFixedRateBond(Long id) {
        fixedRateBondRepository.findById(id);
    }
}

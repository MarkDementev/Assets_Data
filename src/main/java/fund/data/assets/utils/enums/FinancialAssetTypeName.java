package fund.data.assets.utils.enums;

import fund.data.assets.model.asset.exchange.FixedRateBondPackage;
import fund.data.assets.model.asset.relationship.FinancialAssetRelationship;

/**
 * Список типов активов, поддерживаемый фондом, для работы с которыми используется {@link FinancialAssetRelationship}.
 * @version 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
public enum FinancialAssetTypeName {
    FRB(FixedRateBondPackage.class.getTypeName());

    private final String title;

    FinancialAssetTypeName(String title) {
        this.title = title;
    }

    public static boolean isInFinancialAssetTypeNameEnum(String assetTypeName) {
        for (FinancialAssetTypeName element : FinancialAssetTypeName.values()) {
            if (element.title.equals(assetTypeName)) {
                return true;
            }
        }
        return false;
    }
}

package fund.data.assets.model.assets;

import fund.data.assets.model.financial_entities.Account;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "all assets on all accounts with owners")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssetRelationship {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

//    private Map<Long, Float> ownersWithOwnershipPercentages;

//    @ManyToOne
//    @JoinColumn(name = "assetType_id", nullable = false)
//    private AssetType assetType;
}

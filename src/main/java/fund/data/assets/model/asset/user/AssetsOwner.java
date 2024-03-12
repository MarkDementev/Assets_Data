package fund.data.assets.model.asset.user;

import fund.data.assets.model.asset.relationship.AssetRelationship;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

/**
 * Сущность - абстрактная заготовка собственника активов без специфичных для каждой страны
 * паспортных данных для его идентификации. Имя и фамилия указаны у всех, но не отчество и т.п. элементы.
 * @since 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@Getter
@Setter
public class AssetsOwner {
//    public abstract class AssetsOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    /**
     * Обращаясь к AssetsOwner, с вероятностью в 95% нам важны не его иные параметры, а активы на балансе.
     * Потому fetch - это сразу FetchType.EAGER, а не FetchType.LAZY.
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "assetsOwner")
    private List<AssetRelationship> assetRelationships;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public AssetsOwner(String name, String surname, List<AssetRelationship> assetRelationships) {
        this.name = name;
        this.surname = surname;
        this.assetRelationships = assetRelationships;
    }
}
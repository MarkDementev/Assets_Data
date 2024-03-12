package fund.data.assets.model.financial_entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.CascadeType;
//import jakarta.persistence.FetchType;
import jakarta.persistence.Column;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
//import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "in-fund and out-fund accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank
    private String organisationWhereAccountOpened;

    @NotBlank
    @Column(unique = true)
    private String accountNumber;

    @NotNull
    private LocalDate accountOpeningDate;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    /*
    check - does cascade and fetch works correctly?
    */
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<AssetRelationship> assetRelationships;
}

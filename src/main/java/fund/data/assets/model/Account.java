package fund.data.assets.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Set;

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

    /*
        check - does JsonFormat work correctly! May be need to add timezone?
        check size validation to!
     */
    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Size(min = 10, max = 10)
    private Instant accountOpeningDate;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    /*
    check - does cascade and fetch works correctly?
    */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AssetRelationship> assetRelationships;
}

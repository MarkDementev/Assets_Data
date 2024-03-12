package fund.data.assets.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "in-fund and out-fund accounts")
@AllArgsConstructor
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @NotBlank
    private String organisationWhereAccountOpened;

    @NotBlank
    private String accountNumber;

    /*
        check - does in work correctly! May be need to add timezone?
        check size validation to!
     */
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
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
    private Set<Asset> assets;
}

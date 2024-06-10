package fund.data.assets.validation.validator;

import fund.data.assets.model.owner.RussianAssetsOwner;
import fund.data.assets.validation.annotation.UniqueEncryptedEmail;
import fund.data.assets.repository.RussianAssetsOwnerRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class UniqueEncryptedEmailValidator implements ConstraintValidator<UniqueEncryptedEmail, String> {
    @Autowired
    RussianAssetsOwnerRepository russianAssetsOwnerRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        List<RussianAssetsOwner> russianAssetsOwnerList = russianAssetsOwnerRepository.findAll();

        for (RussianAssetsOwner owner : russianAssetsOwnerList) {
            if (owner.getEmail().equals(email)) {
                return false;
            }
        }
        return true;
    }
}

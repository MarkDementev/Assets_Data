package fund.data.assets.validation.validator;

import fund.data.assets.model.asset.owner.RussianAssetsOwner;
import fund.data.assets.repository.RussianAssetsOwnerRepository;
import fund.data.assets.service.RussianAssetsOwnerService;
import fund.data.assets.validation.annotation.UniqueEncryptedMobilePhone;

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
public class UniqueEncryptedMobilePhoneValidator implements ConstraintValidator<UniqueEncryptedMobilePhone, String> {
    @Autowired
    RussianAssetsOwnerRepository russianAssetsOwnerRepository;
    @Autowired
    RussianAssetsOwnerService russianAssetsOwnerService;

    @Override
    public boolean isValid(String mobilePhone, ConstraintValidatorContext context) {
        List<RussianAssetsOwner> russianAssetsOwnerList = russianAssetsOwnerRepository.findAll();

        for (RussianAssetsOwner owner : russianAssetsOwnerList) {
            if (owner.getMobilePhoneNumber().equals(russianAssetsOwnerService
                    .addRussianNumberPrefixPhoneNumber(mobilePhone))) {
                return false;
            }
        }
        return true;
    }
}

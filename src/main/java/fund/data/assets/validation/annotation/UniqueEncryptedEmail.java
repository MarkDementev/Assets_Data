package fund.data.assets.validation.annotation;

import fund.data.assets.validation.validator.UniqueEncryptedEmailValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = UniqueEncryptedEmailValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
@Documented
public @interface UniqueEncryptedEmail {
    String message() default "This Email address is not unique in this table!";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}

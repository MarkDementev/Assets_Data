package fund.data.assets.utils.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import org.springframework.stereotype.Component;

/**
 * Конвертер строк для ряда полей в классах AssetsOwner и RussianAssetsOwner.
 * Позволяет зашифровать важную информацию с помощью jasypt.
 * AssetsOwner - {@link fund.data.assets.model.asset.owner.AssetsOwner}.
 * RussianAssetsOwner - {@link fund.data.assets.model.asset.owner.RussianAssetsOwner}.
 * @since 0.0.1-alpha
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Component
@Converter
public class StringCryptoConverter implements AttributeConverter<String, String> {
    //TODO - Ближе к деплою, придумай, как хранить пароль и алгоритм ВНЕ кода проекта, и как его получать оттуда.
    private final String password = "password";
    private final String algorithm = "PBEWithMD5AndDES";
    private StandardPBEStringEncryptor encryptor;

    public StringCryptoConverter() {
        this.encryptor = new StandardPBEStringEncryptor();

        this.encryptor.setPassword(password);
        this.encryptor.setAlgorithm(algorithm);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return encryptor.decrypt(dbData);
    }
}

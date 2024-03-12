package fund.data.assets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FundAssetsDataApplication {
	public static void main(String[] args) {
		SpringApplication.run(FundAssetsDataApplication.class, args);
	}

	/*
    НЕ ЗАБУДЬ ДОСОЗДАТЬ СУЩНОСТЬ ОУНЕРОВ И ДОПИСАТЬ ASSET!
     */

    /*
    НАДО-БЫ ФАБРИКУ СОЗДАТЬ ДЛЯ УПРОЩЕНИЯ СОЗДАНИЯ СУЩНОСТЕЙ!?
    Видимо, сделать это надо на этапе написания Controller. Напиши единую фабрику для всех биржевых активов.
     */
}

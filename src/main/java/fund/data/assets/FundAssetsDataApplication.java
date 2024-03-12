package fund.data.assets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FundAssetsDataApplication {
	public static void main(String[] args) {
		SpringApplication.run(FundAssetsDataApplication.class, args);
	}

	/*
    Замени модуль с AutoSelector на работу с БД!
     */

	/*
	НАЛОГИ - ТОЖЕ НА ВЫРОСТ НАДО СДЕЛАТЬ!
	 */

	/*
	Протестируй методы внутри FixedRateBond
	 */



	/*
    НАДО-БЫ ФАБРИКУ СОЗДАТЬ ДЛЯ УПРОЩЕНИЯ СОЗДАНИЯ СУЩНОСТЕЙ!?
    Видимо, сделать это надо на этапе написания Controller. Напиши единую фабрику для всех биржевых активов.
     */

	/*
	Перенеси ASSETS_CONTROLLER_PATH и EXCHANGE_ASSETS_CONTROLLER_PATH в другое/другие места
	 */

	//Продумай, как прописать архитектуру в контроллере и сервисе касаемо того, как именно будет меняться бонд

	/*
    НЕ ЗАБУДЬ ДОСОЗДАТЬ СУЩНОСТЬ ОУНЕРОВ И ДОПИСАТЬ ASSET!
     */

	/*
	Протестируй валидацию внутри сущностей!
	 */
}

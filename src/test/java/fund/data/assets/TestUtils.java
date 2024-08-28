package fund.data.assets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fund.data.assets.dto.asset.exchange.FirstBuyFixedRateBondDTO;
import fund.data.assets.dto.financial_entities.AccountDTO;
import fund.data.assets.dto.financial_entities.AccountCashDTO;
import fund.data.assets.dto.financial_entities.TurnoverCommissionValueDTO;
import fund.data.assets.dto.common.PercentFloatValueDTO;
import fund.data.assets.dto.owner.NewRussianAssetsOwnerDTO;
import fund.data.assets.dto.owner.ContactDataRussianAssetsOwnerDTO;
import fund.data.assets.dto.owner.PersonalDataRussianAssetsOwnerDTO;
import fund.data.assets.model.asset.exchange.FixedRateBondPackage;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.AccountCashRepository;
import fund.data.assets.repository.RussianAssetsOwnerRepository;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.repository.FinancialAssetRelationshipRepository;
import fund.data.assets.repository.FixedRateBondRepository;

import org.openapitools.jackson.nullable.JsonNullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;

import java.util.HashMap;
import java.util.Map;

import static fund.data.assets.controller.AccountController.ACCOUNT_CONTROLLER_PATH;
import static fund.data.assets.controller.AccountCashController.ACCOUNT_CASH_CONTROLLER_PATH;
import static fund.data.assets.controller.RussianAssetsOwnerController.RUSSIAN_OWNERS_CONTROLLER_PATH;
import static fund.data.assets.controller.TurnoverCommissionValueController.TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH;
import static fund.data.assets.utils.enums.AssetCurrency.RUSRUB;
import static fund.data.assets.utils.enums.RussianSexEnum.MAN;
import static fund.data.assets.utils.enums.RussianSexEnum.WOMAN;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {
    public static final String TEST_COMMISSION_PERCENT_VALUE = "1";
    public static final String TEST_ASSET_TYPE_NAME = "assetTypeName";
    public static final String TEST_STRING_FORMAT_PERCENT_VALUE = "20,1234";
    public static final Float TEST_FORMATTED_PERCENT_VALUE_FLOAT = 0.201234F;
    public static final Float TEST_COMMISSION_PERCENT_VALUE_FLOAT = 0.01F;
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TurnoverCommissionValueRepository turnoverCommissionValueRepository;
    @Autowired
    private RussianAssetsOwnerRepository russianAssetsOwnerRepository;
    @Autowired
    private AccountCashRepository accountCashRepository;
    @Autowired
    private FixedRateBondRepository fixedRateBondRepository;
    @Autowired
    private FinancialAssetRelationshipRepository financialAssetRelationshipRepository;

    private final AccountDTO accountDTO = new AccountDTO(
            "defaultBank",
            "1q2w3e4r5t",
            LocalDate.of(2024, 2, 22)
    );

    private final AccountDTO secondAccountDTO = new AccountDTO(
            "UPDATEDDefaultBank",
            "UPDATED1q2w3e4r5t",
            LocalDate.of(2024, 2, 22)
    );

    private final AccountDTO notValidAccountDTO = new AccountDTO(
            " ",
            " ",
            null
    );

    private final AccountDTO anotherBankButSameAccountNumberAccountDTO = new AccountDTO(
            "anotherBank",
            "1q2w3e4r5t",
            LocalDate.of(2024, 2, 22)
    );

    private final TurnoverCommissionValueDTO turnoverCommissionValueDTO = new TurnoverCommissionValueDTO(
            null,
            TEST_ASSET_TYPE_NAME,
            TEST_COMMISSION_PERCENT_VALUE
    );

    private final TurnoverCommissionValueDTO notValidTurnoverCommissionValueDTO = new TurnoverCommissionValueDTO(
            null,
            " ",
            null
    );

    private final PercentFloatValueDTO percentFloatValueDTO = new PercentFloatValueDTO(
            TEST_STRING_FORMAT_PERCENT_VALUE
    );

    private final NewRussianAssetsOwnerDTO russianAssetsOwnerDTO = new NewRussianAssetsOwnerDTO(
        "name", "surname", "25.05.1995", "Email_sur@mail.ru", "patronymic", MAN,
            "9888888888", "2424", "111111", "placeOfBirth",
            "placeOfPassportGiven", "24.08.2021", "377-777"
    );

    private final NewRussianAssetsOwnerDTO secondRussianAssetsOwnerDTO = new NewRussianAssetsOwnerDTO(
            "ANOTHER_name", "ANOTHER_surname", "26.05.1995", "Email_mur@mail.ru",
            "ANOTHER_patronymic", WOMAN, "9888888887", "2425",
            "111112", "ANOTHER_placeOfBirth", "ANOTHER_placeOfPassportGiven",
            "25.08.2021", "377-778"
    );

    private final NewRussianAssetsOwnerDTO notValidRussianAssetsOwnerDTO = new NewRussianAssetsOwnerDTO(
            "", "", "25,05,1995", "Email_surmail.ru", "",
            null, "988888888", "242", "11111", "",
            "", "24,08,2021", "377-77"
    );

    private final ContactDataRussianAssetsOwnerDTO validRussianAssetsOwnerContactDataDTO =
            new ContactDataRussianAssetsOwnerDTO(
                    JsonNullable.of("NewEmail_sur@mail.ru"),
                    JsonNullable.of("9888888889"));

    private final PersonalDataRussianAssetsOwnerDTO validRussianAssetsOwnerPersonalDataDTO =
            new PersonalDataRussianAssetsOwnerDTO(
                    JsonNullable.of("NewName"),
                    JsonNullable.of("NewSurname"),
                    JsonNullable.of("NewPatronymic"),
                    JsonNullable.of("4444"),
                    JsonNullable.of("999999"),
                    JsonNullable.of("NewPlaceOfPassportGiven"),
                    JsonNullable.of("31.01.1999"),
                    JsonNullable.of("999-999"));

    private final ContactDataRussianAssetsOwnerDTO notValidRussianAssetsOwnerContactDataDTO =
            new ContactDataRussianAssetsOwnerDTO(
                    JsonNullable.of("NewEmail_surmail.ru"),
                    JsonNullable.of("988888888"));

    private final PersonalDataRussianAssetsOwnerDTO notValidRussianAssetsOwnerPersonalDataDTO =
            new PersonalDataRussianAssetsOwnerDTO(
                    JsonNullable.of(""),
                    JsonNullable.of(""),
                    JsonNullable.of(""),
                    JsonNullable.of("444"),
                    JsonNullable.of("99999"),
                    JsonNullable.of(""),
                    JsonNullable.of("31,01,1999"),
                    JsonNullable.of("999-99"));

    public void tearDown() {
        fixedRateBondRepository.deleteAll();
        financialAssetRelationshipRepository.deleteAll();
        accountCashRepository.deleteAll();
        russianAssetsOwnerRepository.deleteAll();
        turnoverCommissionValueRepository.deleteAll();
        accountRepository.deleteAll();
    }

    public AccountDTO getAccountDTO() {
        return accountDTO;
    }

    public AccountDTO getSecondAccountDTO() {
        return secondAccountDTO;
    }

    public AccountDTO getNotValidAccountDTO() {
        return notValidAccountDTO;
    }

    public AccountDTO getAnotherBankButSameAccountNumberAccountDTO() {
        return anotherBankButSameAccountNumberAccountDTO;
    }

    public TurnoverCommissionValueDTO getTurnoverCommissionValueDTO() {
        return turnoverCommissionValueDTO;
    }

    public TurnoverCommissionValueDTO getNotValidTurnoverCommissionValueDTO() {
        return notValidTurnoverCommissionValueDTO;
    }

    public PercentFloatValueDTO getPercentFloatValueDTO() {
        return percentFloatValueDTO;
    }

    public NewRussianAssetsOwnerDTO getNewRussianAssetsOwnerDTO() {
        return russianAssetsOwnerDTO;
    }

    public NewRussianAssetsOwnerDTO getNotValidNewRussianAssetsOwnerDTO() {
        return notValidRussianAssetsOwnerDTO;
    }

    public ContactDataRussianAssetsOwnerDTO getValidRussianAssetsOwnerContactDataDTO() {
        return validRussianAssetsOwnerContactDataDTO;
    }

    public PersonalDataRussianAssetsOwnerDTO getValidRussianAssetsOwnerPersonalDataDTO() {
        return validRussianAssetsOwnerPersonalDataDTO;
    }

    public ContactDataRussianAssetsOwnerDTO getNotValidRussianAssetsOwnerContactDataDTO() {
        return notValidRussianAssetsOwnerContactDataDTO;
    }

    public PersonalDataRussianAssetsOwnerDTO getNotValidRussianAssetsOwnerPersonalDataDTO() {
        return notValidRussianAssetsOwnerPersonalDataDTO;
    }

    public FirstBuyFixedRateBondDTO getFirstBuyFixedRateBondDTO() throws Exception {
        Map<String, Float> assetOwnersWithAssetCounts = new HashMap<>();

        createDefaultRussianAssetsOwner();
        createSecondDefaultRussianAssetsOwner();
        assetOwnersWithAssetCounts.put(String.valueOf(russianAssetsOwnerRepository.findAll().get(0).getId()), 10.0F);
        assetOwnersWithAssetCounts.put(String.valueOf(russianAssetsOwnerRepository.findAll().get(1).getId()), 20.0F);
        createDefaultAccount();

        TurnoverCommissionValueDTO newTurnoverCommissionValueDTO = new TurnoverCommissionValueDTO(
                accountRepository.findByOrganisationWhereAccountOpened(
                        getAccountDTO().getOrganisationWhereAccountOpened()).getId(),
                FixedRateBondPackage.class.getTypeName(),
                TEST_COMMISSION_PERCENT_VALUE
        );
        createTurnoverCommissionValue(newTurnoverCommissionValueDTO);

        AccountCashDTO firstAccountCashDTO = new AccountCashDTO(
                accountRepository.findAll().get(0).getId(),
                RUSRUB,
                russianAssetsOwnerRepository.findAll().get(0).getId(),
                10150.0F
        );
        AccountCashDTO secondAccountCashDTO = new AccountCashDTO(
                accountRepository.findAll().get(0).getId(),
                RUSRUB,
                russianAssetsOwnerRepository.findAll().get(1).getId(),
                20300.0F
        );
        createAccountCash(firstAccountCashDTO);
        createAccountCash(secondAccountCashDTO);

        return new FirstBuyFixedRateBondDTO(
                RUSRUB,
                "assetTitle",
                30,
                assetOwnersWithAssetCounts,
                accountRepository.findAll().get(0).getId(),
                "RU000A103NZ8",
                "assetIssuerTitle",
                LocalDate.of(2024, 2, 23),
                1000,
                100.00F,
                0.00F,
                100.00F,
                1,
                LocalDate.of(2025, 2, 22)
        );
    }

    public ResultActions createDefaultAccount() throws Exception {
        return createAccount(accountDTO);
    }

    public ResultActions createDefaultSecondAccount() throws Exception {
        return createAccount(secondAccountDTO);
    }

    public ResultActions createDefaultTurnoverCommissionValue() throws Exception {
        createDefaultAccount();

        TurnoverCommissionValueDTO newTurnoverCommissionValueDTO = new TurnoverCommissionValueDTO(
                accountRepository.findByOrganisationWhereAccountOpened(
                        getAccountDTO().getOrganisationWhereAccountOpened()).getId(),
                TEST_ASSET_TYPE_NAME,
                TEST_COMMISSION_PERCENT_VALUE
        );
        return createTurnoverCommissionValue(newTurnoverCommissionValueDTO);
    }

    public ResultActions createDefaultRussianAssetsOwner() throws Exception {
        return createRussianAssetsOwner(russianAssetsOwnerDTO);
    }

    public ResultActions createSecondDefaultRussianAssetsOwner() throws Exception {
        return createRussianAssetsOwner(secondRussianAssetsOwnerDTO);
    }

    public ResultActions createDefaultAccountCash() throws Exception {
        createDefaultAccount();
        createDefaultRussianAssetsOwner();

        AccountCashDTO accountCashDTO = new AccountCashDTO(
                accountRepository.findAll().get(0).getId(),
                RUSRUB,
                russianAssetsOwnerRepository.findAll().get(0).getId(),
                0.00F
        );
        return createAccountCash(accountCashDTO);
    }

    public ResultActions createAccount(final AccountDTO accountDTO) throws Exception {
        final var request = post("/data" + ACCOUNT_CONTROLLER_PATH)
                .content(asJson(accountDTO))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions createTurnoverCommissionValue(final TurnoverCommissionValueDTO turnoverCommissionValueDTO)
            throws Exception {
        final var request = post("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH)
                .content(asJson(turnoverCommissionValueDTO))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions createRussianAssetsOwner(final NewRussianAssetsOwnerDTO russianAssetsOwnerDTO)
            throws Exception {
        final var request = post("/data" + RUSSIAN_OWNERS_CONTROLLER_PATH)
                .content(asJson(russianAssetsOwnerDTO))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions createAccountCash(final AccountCashDTO accountCashDTO) throws Exception {
        final var request = post("/data" + ACCOUNT_CASH_CONTROLLER_PATH)
                .content(asJson(accountCashDTO))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }
}

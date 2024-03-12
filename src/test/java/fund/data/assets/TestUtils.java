package fund.data.assets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fund.data.assets.dto.AccountDTO;
import fund.data.assets.dto.TurnoverCommissionValueDTO;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.utils.enums.CommissionSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;

import static fund.data.assets.controller.AccountController.ACCOUNT_CONTROLLER_PATH;
import static fund.data.assets.controller.TurnoverCommissionValueController.TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {
    public static final Float TEST_COMMISSION_PERCENT_VALUE = 0.01F;
    public static final String TEST_ASSET_TYPE_NAME = "assetTypeName";
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TurnoverCommissionValueRepository turnoverCommissionValueRepository;

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

    private final TurnoverCommissionValueDTO notValidTurnoverCommissionValueDTO = new TurnoverCommissionValueDTO(
            null,
            null,
            " ",
            null
    );

    public void tearDown() {
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

    public TurnoverCommissionValueDTO getNotValidTurnoverCommissionValueDTO() {
        return notValidTurnoverCommissionValueDTO;
    }

    public ResultActions createDefaultAccount() throws Exception {
        return createAccount(accountDTO);
    }

    public ResultActions createDefaultSecondAccount() throws Exception {
        return createAccount(secondAccountDTO);
    }

    public ResultActions createDefaultTurnoverCommissionValue() throws Exception {
        createDefaultAccount();

        final TurnoverCommissionValueDTO turnoverCommissionValueDTO = new TurnoverCommissionValueDTO(
                CommissionSystem.TURNOVER,
                accountRepository.findByOrganisationWhereAccountOpened(
                        getAccountDTO().getOrganisationWhereAccountOpened()
                ).getId(),
                TEST_ASSET_TYPE_NAME,
                TEST_COMMISSION_PERCENT_VALUE
        );

        return createTurnoverCommissionValue(turnoverCommissionValueDTO);
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

package fund.data.assets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fund.data.assets.dto.AccountDTO;
import fund.data.assets.dto.FixedRateBondDTO;
import fund.data.assets.dto.TurnoverCommissionValueDTO;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.repository.AccountRepository;
import fund.data.assets.repository.FixedRateBondRepository;
import fund.data.assets.repository.TurnoverCommissionValueRepository;
import fund.data.assets.utils.enums.AssetCurrency;

import fund.data.assets.utils.enums.CommissionSystem;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Instant;
import java.time.LocalDate;

import static fund.data.assets.controller.AccountController.ACCOUNT_CONTROLLER_PATH;
import static fund.data.assets.controller.TurnoverCommissionValueController.TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH;
import static fund.data.assets.utils.FinancialAndAnotherConstants.STANDARD_BOND_PAR_VALUE;
import static fund.data.assets.utils.FinancialAndAnotherConstants.STANDARD_BOND_PURCHASE_MARKET_PRICE;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
//    @Autowired
//    private FixedRateBondRepository fixedRateBondRepository;
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

    private final AccountDTO anotherBankButSimilarAccountNumberAccountDTO = new AccountDTO(
            "anotherBank",
            "1q2w3e4r5t",
            LocalDate.of(2024, 2, 22)
    );

//    private final FixedRateBondDTO fixedRateBondDTO = new FixedRateBondDTO(
//            "qw1234567890",
//            "assetIssuerTitle",
//            LocalDate.MIN,
//            AssetCurrency.RUSRUB,
//            "assetTitle",
//            1,
//            STANDARD_BOND_PAR_VALUE,
//            STANDARD_BOND_PURCHASE_MARKET_PRICE,
//            0.00F,
//            1F,
//            1,
//            LocalDate.now()
//    );

//    private final TurnoverCommissionValueDTO turnoverCommissionValueDTO = new TurnoverCommissionValueDTO(
//            CommissionSystem.TURNOVER,
//            accountToConstructAnotherEntity,
//            "assetTypeName",
//            0.01F
//    );

    public void tearDown() {
        turnoverCommissionValueRepository.deleteAll();
        //        fixedRateBondRepository.deleteAll();
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

    public AccountDTO getAnotherBankButSimilarAccountNumberAccountDTO() {
        return anotherBankButSimilarAccountNumberAccountDTO;
    }

//    public FixedRateBondDTO getFixedRateBondDTO() {
//        return fixedRateBondDTO;
//    }

//    public TurnoverCommissionValueDTO getTurnoverCommissionValueDTO() {
//        return turnoverCommissionValueDTO;
//    }

    public ResultActions createDefaultAccount() throws Exception {
        return createAccount(accountDTO);
    }

    public ResultActions createDefaultSecondAccount() throws Exception {
        return createAccount(secondAccountDTO);
    }

//    public ResultActions createDefaultTurnoverCommissionValue() throws Exception {
//        return createTurnoverCommissionValue(turnoverCommissionValueDTO);
//    }

    public ResultActions createAccount(final AccountDTO accountDTO) throws Exception {
        final var request = post("/data" + ACCOUNT_CONTROLLER_PATH)
                .content(asJson(accountDTO))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

//    public ResultActions createTurnoverCommissionValue(final TurnoverCommissionValueDTO turnoverCommissionValueDTO)
//            throws Exception {
//        final var request = post("/data" + TURNOVER_COMMISSION_VALUE_CONTROLLER_PATH)
//                .content(asJson(turnoverCommissionValueDTO))
//                .contentType(APPLICATION_JSON);
//
//        return perform(request);
//    }

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

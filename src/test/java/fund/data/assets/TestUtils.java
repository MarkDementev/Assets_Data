package fund.data.assets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fund.data.assets.dto.AccountDTO;

import fund.data.assets.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Instant;

import static fund.data.assets.controller.AccountController.ACCOUNT_CONTROLLER_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    private final AccountDTO accountDTO = new AccountDTO(
            "defaultBank",
            "1q2w3e4r5t",
            Instant.now()
    );

    private final AccountDTO secondAccountDTO = new AccountDTO(
            "UPDATEDdefaultBank",
            "UPDATED1q2w3e4r5t",
            Instant.now()
    );

    public void tearDown() {
        accountRepository.deleteAll();
    }

    public AccountDTO getAccountDTO() {
        return accountDTO;
    }

    public AccountDTO getSecondAccountDTO() {
        return secondAccountDTO;
    }

    public ResultActions createDefaultAccount() throws Exception {
        return createAccount(accountDTO);
    }

    public ResultActions createAccount(final AccountDTO accountDTO) throws Exception {
        final var request = post("/data" + ACCOUNT_CONTROLLER_PATH)
                .content(asJson(accountDTO))
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

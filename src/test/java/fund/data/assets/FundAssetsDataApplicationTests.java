package fund.data.assets;

import com.fasterxml.jackson.core.type.TypeReference;
import fund.data.assets.config.SpringConfigForTests;

import fund.data.assets.dto.AccountDTO;
import fund.data.assets.model.Account;
import jakarta.validation.Valid;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fund.data.assets.TestUtils.asJson;
import static fund.data.assets.TestUtils.fromJson;
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;
import static fund.data.assets.controller.AccountController.ACCOUNT_CONTROLLER_PATH;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForTests.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
class FundAssetsDataApplicationTests {
	@Autowired
	private TestUtils testUtils;

//	@AfterEach
//	public void clear() {
//		testUtils.tearDown();
//	}
//
//	@Test
//	public void getAccountIT() throws Exception {
//	}
//
//	@Test
//	public void getAccountsIT() throws Exception {
////		final var response = testUtils.perform(
////						get("/data" + ACCOUNT_CONTROLLER_PATH)
////				)
////				.andExpect(status().isOk())
////				.andReturn()
////				.getResponse();
//	}

	@Test
	public void createAccountIT() throws Exception {
		final var response = testUtils.perform(
						post("/data" + ACCOUNT_CONTROLLER_PATH)
								.content(asJson(testUtils.getAccountDTO()))
								.contentType(APPLICATION_JSON)
				)
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse();
		final Account accountFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
		});

		assertNotNull(accountFromResponse.getId());
	}

//	@Test
//	public void updateAccountIT() throws Exception {
//	}
//
//	@Test
//	public void deleteAccountIT() throws Exception {
//	}
}

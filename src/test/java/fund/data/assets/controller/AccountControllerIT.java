package fund.data.assets.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.dto.common.LoginDto;
import fund.data.assets.exception.EntityWithIDNotFoundException;
import fund.data.assets.model.financial_entities.Account;
import fund.data.assets.repository.AccountRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static fund.data.assets.TestUtils.asJson;
import static fund.data.assets.TestUtils.fromJson;
import static fund.data.assets.config.SecurityConfig.ADMIN_NAME;
import static fund.data.assets.config.SecurityConfig.LOGIN_PATH;
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;
import static fund.data.assets.controller.AccountController.ACCOUNT_CONTROLLER_PATH;
import static fund.data.assets.controller.AccountController.ID_PATH;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @version 0.6-a
 * @author MarkDementev a.k.a JavaMarkDem
 */
@SpringBootTest(classes = SpringConfigForTests.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
class AccountControllerIT {
	@Autowired
	private TestUtils testUtils;
	@Autowired
	private AccountRepository accountRepository;

	@BeforeEach
	public void loginBeforeTest() throws Exception {
		testUtils.login(testUtils.getAdminLoginDto());
	}

	@AfterEach
	public void clearRepositories() {
		testUtils.tearDown();
	}

	@Test
	public void getAccountIT() throws Exception {
		testUtils.createDefaultAccount();

		Account expectedAccount = accountRepository.findByOrganisationWhereAccountOpened(
				testUtils.getAccountDTO().getOrganisationWhereAccountOpened());
		var response = testUtils.perform(
						get("/data" + ACCOUNT_CONTROLLER_PATH + ID_PATH, expectedAccount.getId())
				).andExpect(status().isOk())
				.andReturn()
				.getResponse();
		Account accountFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
		});

		assertEquals(expectedAccount.getId(), accountFromResponse.getId());
		assertEquals(expectedAccount.getOrganisationWhereAccountOpened(),
				accountFromResponse.getOrganisationWhereAccountOpened());
		assertEquals(expectedAccount.getAccountNumber(),
				accountFromResponse.getAccountNumber());
		assertEquals(expectedAccount.getAccountOpeningDate(),
				accountFromResponse.getAccountOpeningDate());
		assertNotNull(accountFromResponse.getCreatedAt());
	}

	@Test
	public void getNotExistsAccountIT() throws Exception {
		testUtils.createDefaultAccount();

		Long notExistsAccountID = accountRepository.findByOrganisationWhereAccountOpened(
				testUtils.getAccountDTO().getOrganisationWhereAccountOpened()).getId();
		notExistsAccountID++;

		Exception exception = testUtils.perform(get("/data" + ACCOUNT_CONTROLLER_PATH + ID_PATH,
						notExistsAccountID))
				.andExpect(status().isNotFound())
				.andReturn().getResolvedException();

        assert exception != null;
        assertEquals(EntityWithIDNotFoundException.class, exception.getClass());
	}

	@Test
	public void getAccountsIT() throws Exception {
		testUtils.createDefaultAccount();

		var response = testUtils.perform(get("/data" + ACCOUNT_CONTROLLER_PATH))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse();

		List<Account> allAccounts = fromJson(response.getContentAsString(), new TypeReference<>() {
		});

		assertThat(allAccounts).hasSize(1);
	}

	@Test
	public void createAccountIT() throws Exception {
		var response = testUtils.perform(post("/data" + ACCOUNT_CONTROLLER_PATH)
						.content(asJson(testUtils.getAccountDTO()))
						.contentType(APPLICATION_JSON),
						ADMIN_NAME)
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse();
		Account accountFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
		});

		assertNotNull(accountFromResponse.getId());
		assertEquals(testUtils.getAccountDTO().getOrganisationWhereAccountOpened(),
				accountFromResponse.getOrganisationWhereAccountOpened());
		assertEquals(testUtils.getAccountDTO().getAccountNumber(),
				accountFromResponse.getAccountNumber());
		assertEquals(testUtils.getAccountDTO().getAccountOpeningDate(),
				accountFromResponse.getAccountOpeningDate());
		assertNotNull(accountFromResponse.getCreatedAt());
		assertNotNull(accountFromResponse.getUpdatedAt());
	}

	@Test
	public void createNotValidAccountIT() throws Exception {
		testUtils.perform(post("/data" + ACCOUNT_CONTROLLER_PATH)
						.content(asJson(testUtils.getNotValidAccountDTO()))
						.contentType(APPLICATION_JSON));
		assertThat(accountRepository.findAll()).hasSize(0);

		testUtils.createDefaultAccount();
		assertThat(accountRepository.findAll()).hasSize(1);

		Exception exception = testUtils.perform(post("/data" + ACCOUNT_CONTROLLER_PATH)
						.content(asJson(testUtils.getAnotherBankButSameAccountNumberAccountDTO()))
						.contentType(APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn().getResolvedException();

        assert exception != null;
        assertEquals(DataIntegrityViolationException.class, exception.getClass());
		assertThat(accountRepository.findAll()).hasSize(1);
	}

	@Test
	public void updateAccountIT() throws Exception {
		testUtils.createDefaultAccount();

		Long createdAccountId = accountRepository.findByOrganisationWhereAccountOpened(
				testUtils.getAccountDTO().getOrganisationWhereAccountOpened()).getId();
		var response = testUtils.perform(
				put("/data" + ACCOUNT_CONTROLLER_PATH + ID_PATH, createdAccountId)
						.content(asJson(testUtils.getSecondAccountDTO())).contentType(APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andReturn()
				.getResponse();
		Account accountFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
		});

		assertEquals(createdAccountId, accountFromResponse.getId());
		assertEquals(testUtils.getSecondAccountDTO().getOrganisationWhereAccountOpened(),
				accountFromResponse.getOrganisationWhereAccountOpened());
		assertEquals(testUtils.getSecondAccountDTO().getAccountNumber(),
				accountFromResponse.getAccountNumber());
		assertEquals(testUtils.getSecondAccountDTO().getAccountOpeningDate(),
				accountFromResponse.getAccountOpeningDate());
		assertNotNull(accountFromResponse.getCreatedAt());
		assertNotNull(accountFromResponse.getUpdatedAt());
		assertNotEquals(accountFromResponse.getCreatedAt(), accountFromResponse.getUpdatedAt());
	}

	@Test
	public void notValidUpdateAccountIT() throws Exception {
		testUtils.createDefaultAccount();

		Long createdAccountId = accountRepository.findByOrganisationWhereAccountOpened(
				testUtils.getAccountDTO().getOrganisationWhereAccountOpened()).getId();
		testUtils.perform(
				put("/data" + ACCOUNT_CONTROLLER_PATH + ID_PATH,
				createdAccountId)
						.content(asJson(testUtils.getNotValidAccountDTO()))
						.contentType(APPLICATION_JSON));

		assertEquals(testUtils.getAccountDTO().getOrganisationWhereAccountOpened(),
				accountRepository.findAll().get(0).getOrganisationWhereAccountOpened());
		assertEquals(testUtils.getAccountDTO().getAccountNumber(),
				accountRepository.findAll().get(0).getAccountNumber());
		assertEquals(testUtils.getAccountDTO().getAccountOpeningDate(),
				accountRepository.findAll().get(0).getAccountOpeningDate());

		testUtils.createDefaultSecondAccount();

		Long createdSecondAccountId = accountRepository.findByOrganisationWhereAccountOpened(
				testUtils.getSecondAccountDTO().getOrganisationWhereAccountOpened()).getId();
		Exception exception = testUtils.perform(put("/data" + ACCOUNT_CONTROLLER_PATH + ID_PATH,
						createdSecondAccountId)
						.content(asJson(testUtils.getAccountDTO()))
						.contentType(APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn().getResolvedException();

		assert exception != null;
		assertEquals(DataIntegrityViolationException.class, exception.getClass());
	}

	@Test
	public void deleteAccountIT() throws Exception {
		testUtils.createDefaultAccount();

		Long createdAccountId = accountRepository.findByOrganisationWhereAccountOpened(
				testUtils.getAccountDTO().getOrganisationWhereAccountOpened()).getId();

		testUtils.perform(
				delete("/data" + ACCOUNT_CONTROLLER_PATH + ID_PATH, createdAccountId))
				.andExpect(status().isOk());

		assertNull(accountRepository.findByOrganisationWhereAccountOpened(
				testUtils.getAccountDTO().getOrganisationWhereAccountOpened()));
	}
}

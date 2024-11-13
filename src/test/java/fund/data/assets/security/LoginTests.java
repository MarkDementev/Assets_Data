package fund.data.assets.security;

import fund.data.assets.TestUtils;
import fund.data.assets.config.SpringConfigForTests;
import fund.data.assets.dto.common.LoginDto;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static fund.data.assets.TestUtils.asJson;
import static fund.data.assets.config.SecurityConfig.LOGIN_PATH;
import static fund.data.assets.config.SecurityConfig.ADMIN_NAME;
import static fund.data.assets.config.SecurityConfig.ADMIN_PASSWORD;
import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @version 0.6-a
 * @author MarkDementev a.k.a JavaMarkDem
 */
@SpringBootTest(classes = SpringConfigForTests.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class LoginTests {
    @Autowired
    TestUtils testUtils;

    @Test
	public void loginTest() throws Exception {
		testUtils.perform(MockMvcRequestBuilders.post(LOGIN_PATH)
                        .content(asJson(testUtils.getAdminLoginDto()))
                        .contentType(APPLICATION_JSON)
                ).andExpect(status().isOk());
	}

    @Test
    public void loginWrongLoginTest() throws Exception {
        testUtils.perform(MockMvcRequestBuilders.post(LOGIN_PATH)
                .content(asJson(new LoginDto(
                        ADMIN_NAME + "!", ADMIN_PASSWORD
                )))
                .contentType(APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void loginWrongPasswordTest() throws Exception {
        testUtils.perform(MockMvcRequestBuilders.post(LOGIN_PATH)
                .content(asJson(new LoginDto(
                        ADMIN_NAME, ADMIN_PASSWORD + "!"
                )))
                .contentType(APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void loginWrongAllTest() throws Exception {
        testUtils.perform(MockMvcRequestBuilders.post(LOGIN_PATH)
                .content(asJson(new LoginDto(
                        ADMIN_NAME + "!", ADMIN_PASSWORD + "!"
                )))
                .contentType(APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void loginNullAllTest() throws Exception {
        testUtils.perform(MockMvcRequestBuilders.post(LOGIN_PATH)
                .content(asJson(new LoginDto(
                    null, null
                )))
                .contentType(APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }
}

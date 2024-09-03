package fund.data.assets.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.PostgreSQLContainer;

import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;

@Configuration
@Profile(TEST_PROFILE)
@ComponentScan(basePackages = "fund.data.assets")
@PropertySource(value = "classpath:/application-test.yml")
public class SpringConfigForTests {
    public static final String TEST_PROFILE = "test";
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}

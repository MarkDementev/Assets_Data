package fund.data.assets.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;

@Configuration
@Profile(TEST_PROFILE)
@ComponentScan(basePackages = "fund.data.assets")
@PropertySource(value = "classpath:/application-test.yml")
public class SpringConfigForTests {
    public static final String TEST_PROFILE = "test";
}

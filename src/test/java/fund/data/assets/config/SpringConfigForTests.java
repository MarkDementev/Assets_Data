package fund.data.assets.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

import static fund.data.assets.config.SpringConfigForTests.TEST_PROFILE;

@Configuration
@Profile(TEST_PROFILE)
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "fund.data.assets")
@PropertySource(value = "classpath:/test_config/application.yml")
public class SpringConfigForTests {
    public static final String TEST_PROFILE = "test";
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }
}

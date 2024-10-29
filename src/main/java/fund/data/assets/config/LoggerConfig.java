package fund.data.assets.config;

import fund.data.assets.FundAssetsDataApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @version 0.4-a
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Configuration
public class LoggerConfig {
    @Bean
    public static Logger getLogger() {
        return LoggerFactory.getLogger(FundAssetsDataApplication.class);
    }
}

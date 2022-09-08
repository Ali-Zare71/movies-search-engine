package de.kaufland.moviesearch.ingestionapiservice.config;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;

import javax.annotation.PostConstruct;
import java.time.ZoneOffset;
import java.util.TimeZone;

@Configuration
@ComponentScan("de")
@PropertySource(value = {"classpath:config.properties"}, encoding = "UTF-8")
public class AppConfig {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}

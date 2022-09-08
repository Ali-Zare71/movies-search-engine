package de.kaufland.moviesearch.indexservice.searchservice.config;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kaufland.moviesearch.indexservice.searchservice.converter.SearchResponseToSearchResultsDtoConverter;
import org.springframework.context.annotation.*;
import org.springframework.format.FormatterRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan("de")
@PropertySource(value = {"classpath:config.properties"}, encoding = "UTF-8")
@EnableScheduling
public class AppConfig implements WebMvcConfigurer {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new SearchResponseToSearchResultsDtoConverter(objectMapper()));
    }
}

package com.mc855.spark.conf;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import java.util.List;

@Configuration
public class SpringMvcConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.ignoreAcceptHeader(true).defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Bean
    @Autowired
    public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {
        ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
        resolver.setContentNegotiationManager(manager);
        return resolver;
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseRegisteredSuffixPatternMatch(true);
        configurer.setUseSuffixPatternMatch(false);
        configurer.setUseTrailingSlashMatch(true);
    }

    @Bean
    MappingJackson2HttpMessageConverter converter() {

        final ObjectMapper objectMapper = new ObjectMapper();

        // ADD CUSTOM JACKSON CONFIGURATION
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);

        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(converter());
    }

}

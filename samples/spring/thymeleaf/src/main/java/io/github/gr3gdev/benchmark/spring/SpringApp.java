package io.github.gr3gdev.benchmark.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@SpringBootApplication
@EnableJpaRepositories("io.github.gr3gdev.*")
@ComponentScan(basePackages = {"io.github.gr3gdev.*"})
@EntityScan("io.github.gr3gdev.*")
public class SpringApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringApp.class, args);
    }

    @Configuration
    static class Config implements WebMvcConfigurer {
        @Bean
        public LocaleResolver localeResolver() {
            final SessionLocaleResolver slr = new SessionLocaleResolver();
            slr.setDefaultLocale(Locale.UK);
            return slr;
        }

        @Bean
        public LocaleChangeInterceptor localeChangeInterceptor() {
            final LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
            lci.setParamName("locale");
            return lci;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(localeChangeInterceptor());
        }
    }
}

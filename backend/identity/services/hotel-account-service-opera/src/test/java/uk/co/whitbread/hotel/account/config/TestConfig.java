package uk.co.whitbread.hotel.account.config;


import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class TestConfig {


    @Bean
    public Validator getValidator() {

        return new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
    }



}

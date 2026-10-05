package uk.co.whitbread.hotelcountries.config;

import feign.auth.BasicAuthRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AEMFeignConfiguration {

    private final FeignProperties feignProperties;

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(
                feignProperties.getAem().getUsername(),
                feignProperties.getAem().getPassword()
        );
    }
}
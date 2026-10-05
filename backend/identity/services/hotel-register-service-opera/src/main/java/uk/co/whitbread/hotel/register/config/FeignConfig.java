package uk.co.whitbread.hotel.register.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;
import uk.co.whitbread.hotel.register.exceptions.client.CustomErrorDecoder;

@Configuration
public class FeignConfig {

  @Bean
  public ErrorDecoder errorDecoder() {
    return new CustomErrorDecoder();
  }
}

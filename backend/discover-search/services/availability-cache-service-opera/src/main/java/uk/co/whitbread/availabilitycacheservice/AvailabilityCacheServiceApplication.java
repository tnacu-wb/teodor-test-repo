package uk.co.whitbread.availabilitycacheservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableCaching
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"}, exclude = {DataRedisAutoConfiguration.class})
@EnableFeignClients
public class AvailabilityCacheServiceApplication {

  static void main(String[] args) {
    SpringApplication.run(AvailabilityCacheServiceApplication.class, args);
  }

}

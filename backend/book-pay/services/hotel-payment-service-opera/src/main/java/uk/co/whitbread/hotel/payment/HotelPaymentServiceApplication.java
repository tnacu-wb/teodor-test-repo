package uk.co.whitbread.hotel.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableFeignClients
@SpringBootApplication
@EnableCaching
@EnableAsync
@ComponentScan(basePackages = {"uk.co.whitbread"})
@RequiredArgsConstructor
public class HotelPaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelPaymentServiceApplication.class, args);
    }
}

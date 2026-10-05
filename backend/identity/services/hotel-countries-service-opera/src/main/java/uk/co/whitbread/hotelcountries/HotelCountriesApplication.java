package uk.co.whitbread.hotelcountries;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@EnableDiscoveryClient
@EnableConfigurationProperties
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
public class HotelCountriesApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelCountriesApplication.class, args);
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("uk.co.whitbread.bart.unified.api", "uk.co.whitbread.hotelcountries.model.aem");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMarshaller(marshaller());
        template.setUnmarshaller(marshaller());
        return template;
    }

    @Bean
    public HttpClientBuilder httpClientBuilder() {
        return HttpClientBuilder.create();
    }

}

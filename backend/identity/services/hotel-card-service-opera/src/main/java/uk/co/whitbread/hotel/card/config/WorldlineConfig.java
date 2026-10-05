package uk.co.whitbread.hotel.card.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class WorldlineConfig {

  @Bean
  public Jaxb2Marshaller worldlineMarshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setPackagesToScan("worldline.mst.bsm.api.b2b.pi.data");

    return marshaller;
  }

  @Bean
  public WebServiceTemplate worldlineWebServiceTemplate() {
    WebServiceTemplate template = new WebServiceTemplate();
    template.setMarshaller(worldlineMarshaller());
    template.setUnmarshaller(worldlineMarshaller());
    return template;
  }

}

package uk.co.whitbread.address.lookup.infrastructure.config;

import static uk.co.whitbread.address.lookup.infrastructure.config.AddressLookupConstants.IGNORE_COOKIES;

import jakarta.validation.Validator;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.ClientHttpRequestMessageSender;
import uk.co.whitbread.address.lookup.domain.logic.AddressLookupInPortImpl;
import uk.co.whitbread.address.lookup.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.address.lookup.domain.ports.primary.AddressLookupInPort;
import uk.co.whitbread.address.lookup.domain.ports.secondary.AddressLookupOutPort;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.AddressLookupOutPortImpl;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.address.QasClient;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.config.QasProperties;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.mapper.AddressFormatQasMapper;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.mapper.AddressSearchQasMapper;

@Configuration
@RequiredArgsConstructor
public class InfrastructureBeanConfig {

  private final QasProperties qasProperties;

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public AddressLookupInPort getAddressLookupInPort(AddressLookupOutPort addressLookupOutPort) {
    return new AddressLookupInPortImpl(addressLookupOutPort);
  }

  @Bean
  public AddressLookupOutPort getAddressLookupOutPort(
      AddressSearchQasMapper addressSearchQasMapper,
      AddressFormatQasMapper addressFormatQasMapper, QasClient qasClient) {
    return new AddressLookupOutPortImpl(addressSearchQasMapper,
        addressFormatQasMapper, qasClient);
  }

  @Bean
  public Jaxb2Marshaller marshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setPackagesToScan("uk.co.whitbread.qas.addresslookup.api");
    return marshaller;
  }

  @Bean
  public WebServiceTemplate webServiceTemplate(WebServiceMessageSender webServiceMessageSender)
      throws SOAPException {
    WebServiceTemplate template = new WebServiceTemplate();
    final MessageFactory msgFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
    final SaajSoapMessageFactory newSoapMessageFactory = new SaajSoapMessageFactory(msgFactory);
    template.setMessageFactory(newSoapMessageFactory);
    template.setMessageSender(webServiceMessageSender);
    template.setMarshaller(marshaller());
    template.setUnmarshaller(marshaller());
    return template;
  }

  @Bean
  public WebServiceMessageSender webServiceMessageSender(
      ClientHttpRequestFactory createRequestFactory) {
    ClientHttpRequestMessageSender sender = new ClientHttpRequestMessageSender();
    sender.setRequestFactory(createRequestFactory);

    return sender;
  }

  @Bean
  public ClientHttpRequestFactory createRequestFactory(
      ConnectionKeepAliveStrategy connectionKeepAliveStrategy) {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(qasProperties.getConnectionManager().getMaxTotal());
    connectionManager.setDefaultMaxPerRoute(
        qasProperties.getConnectionManager().getDefaultMaxPerRoute());
    RequestConfig requestConfig = RequestConfig
        .custom()
        .setCookieSpec(IGNORE_COOKIES)
        .setConnectionRequestTimeout(Timeout.of(qasProperties.getConnectionManagerTimeout(),
                TimeUnit.MILLISECONDS))
        .setConnectTimeout(Timeout.of(qasProperties.getConnectionTimeout(), TimeUnit.MILLISECONDS))
        .build();
    HttpClient httpClient = HttpClientBuilder
        .create()
        .setConnectionManager(connectionManager)
        .setKeepAliveStrategy(connectionKeepAliveStrategy)
        .setDefaultRequestConfig(requestConfig)
        .build();

    return new HttpComponentsClientHttpRequestFactory(httpClient);
  }

}

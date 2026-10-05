package uk.co.whitbread.address.lookup.infrastructure.rest.client.address;

import java.util.Optional;
import javax.xml.namespace.QName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.address.exceptions.ErrorCode;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.address.exceptions.InvalidMonikerIdException;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.address.exceptions.QasServiceException;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.config.QasProperties;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.qas.addresslookup.api.Address;
import uk.co.whitbread.qas.addresslookup.api.QAGetAddress;
import uk.co.whitbread.qas.addresslookup.api.QASearch;
import uk.co.whitbread.qas.addresslookup.api.QASearchResult;

@Component
@Slf4j
@RequiredArgsConstructor
public class QasClient {

  private static final String INVALID_MONIKERID = "InvalidMoniker";
  private final QasProperties qasProperties;
  private final WebServiceTemplate webServiceTemplate;
  private final LoginWebServiceMessageCallback loginWebServiceMessageCallback;

  public QASearchResult search(QASearch searchRequest) {
    log.debug("QAS: Submitting initial search request for postcode {}", searchRequest.getSearch());

    QASearchResult searchResult;

    try {
      searchResult = (QASearchResult) webServiceTemplate.marshalSendAndReceive(
          qasProperties.getUrl(),
          searchRequest,
          loginWebServiceMessageCallback);

    } catch (SoapFaultClientException ex) {
      var exception = new QasServiceException(ErrorCode.DIGITAL_QAS_SERVICE,
          String.format("Unable to get search result for postcode: %s", searchRequest.getSearch()),
          ex);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return searchResult;
  }

  public Address getFormattedAddress(QAGetAddress qaGetAddress) {
    log.debug("QAS: Submitting search request by monikerId {}", qaGetAddress.getMoniker());

    Address address;
    try {
      address = (Address) webServiceTemplate.marshalSendAndReceive(
          qasProperties.getUrl(),
          qaGetAddress,
          loginWebServiceMessageCallback);
    } catch (SoapFaultClientException ex) {
      String faultCode = Optional.of(ex)
          .map(SoapFaultClientException::getFaultCode)
          .map(QName::toString)
          .orElse(null);
      if (INVALID_MONIKERID.equals(faultCode)) {
        var message = String.format("Unable to get search result by monikerId: %s",
            qaGetAddress.getMoniker());
        var exception = new InvalidMonikerIdException(ErrorCode.DIGITAL_INVALID_MONIKER_ID, message,
            ex);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
      var message = "Error while trying to get search result";
      var exception = new QasServiceException(ErrorCode.DIGITAL_QAS_SEARCH_SERVICE, message, ex);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return address;
  }
}

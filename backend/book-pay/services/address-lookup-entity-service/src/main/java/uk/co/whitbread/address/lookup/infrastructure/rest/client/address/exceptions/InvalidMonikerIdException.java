package uk.co.whitbread.address.lookup.infrastructure.rest.client.address.exceptions;

import static java.util.Optional.ofNullable;

import javax.xml.transform.dom.DOMSource;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.w3c.dom.Node;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class InvalidMonikerIdException extends AbstractBadRequestException {

  public InvalidMonikerIdException(ErrorCode errorCode, String debugMessage,
      SoapFaultClientException cause) {
    super(errorCode.getMessage(), debugMessage + extractSoapFaultDetail(cause),
        errorCode.getCode());
  }

  private static String extractSoapFaultDetail(SoapFaultClientException e) {

    return ofNullable(e.getSoapFault())
        .map(SoapFault::getFaultDetail)
        .map(soapFaultDetail -> (DOMSource) soapFaultDetail.getSource())
        .map(DOMSource::getNode)
        .map(Node::getFirstChild)
        .map(Node::getTextContent)
        .orElse(e.getMessage());
  }
}

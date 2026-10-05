package uk.co.whitbread.bart.exceptions;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.w3c.dom.Node;
import uk.co.whitbread.bart.common.ErrorDetails;
import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.MALBartException;

import javax.xml.transform.dom.DOMSource;

import static java.util.Optional.ofNullable;

/**
 * Created by Abu-Taleb on 02/11/2016.
 */
public class BartServiceException extends AbstractMALException implements MALBartException {
    private static final String DEFAULT_ERROR_CODE = "100";

    private String errorCode = DEFAULT_ERROR_CODE;

    public BartServiceException() {
        super();
    }

    public BartServiceException(String message) {
        super(message);
    }

    public BartServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BartServiceException(Throwable cause) {
        super(cause);
    }

    public BartServiceException(SoapFaultClientException cause) {
        super(extractSoapFaultDetail(cause), cause);
    }

    protected BartServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BartServiceException(String methodError, ErrorDetails errorDetail) {
        super(createErrorMessageFromErrorDetail(methodError, errorDetail));
        this.errorCode = methodError;
    }

    public BartServiceException withErrorCode(String errorCode){
        this.errorCode = errorCode;
        return this;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    public static String createErrorMessageFromErrorDetail(String methodError, ErrorDetails errorDetail) {
        return ofNullable(errorDetail)
                .map(ErrorDetails::getErrorMessage)
                .filter(StringUtils::isNotBlank)
                .map(error -> getErrorWithCode(getMethodError(methodError), error))
                .orElse(getErrorWithCode(getMethodError(methodError), "ErrorDetails missing"));
    }

    private static String getMethodError(String methodError) {
        return ofNullable(methodError).orElse(DEFAULT_ERROR_CODE);
    }

    private static String getErrorWithCode(String errorCode, String errorMessage) {
        return errorCode + ": " + errorMessage;
    }

    public static String extractSoapFaultDetail(SoapFaultClientException e) {
        return ofNullable(e.getSoapFault())
                .map(SoapFault::getFaultDetail)
                .map(SoapFaultDetail::getSource)
                .filter(source -> source instanceof DOMSource)
                .map(DOMSource.class::cast)
                .map(DOMSource::getNode)
                .map(Node::getFirstChild)
                .map(Node::getTextContent)
                .orElse(e.getMessage());
    }
}

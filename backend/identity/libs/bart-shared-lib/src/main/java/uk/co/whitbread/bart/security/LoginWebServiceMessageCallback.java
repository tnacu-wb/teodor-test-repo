package uk.co.whitbread.bart.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.StringSource;
import uk.co.whitbread.bart.enums.BartBookingChannelCode;
import uk.co.whitbread.bart.properties.BaseBartProperties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.IOException;
import uk.co.whitbread.bart.unified.api.BookingChannel;

@Component
public class LoginWebServiceMessageCallback implements WebServiceMessageCallback {

    private static final Logger LOG = LoggerFactory.getLogger(LoginWebServiceMessageCallback.class);

    @Autowired
    private BaseBartProperties bartProperties;
    private String languageCode;
    private String countryCode;
    private BartBookingChannelCode bookingChannel;

    public LoginWebServiceMessageCallback(BaseBartProperties bartProperties) {
        this.bartProperties = bartProperties;
    }

    @Override
    public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
        try {
            SoapMessage soapMessage = (SoapMessage) message;
            SoapHeader header = soapMessage.getSoapHeader();
            StringSource headerSource =
                    new StringSource("<Login xmlns=\"http://bartws.micros.com/1.31\">\n" +
                            "<username>" + bartProperties.getUsername() + "</username>\n" +
                            "<password>" + bartProperties.getPassword() + "</password>\n" +
                            "</Login>");
            addHeader(header, headerSource, "Error adding Login headers to BART SOAP call.");

            if (StringUtils.isNotBlank(languageCode) && StringUtils.isNotBlank(countryCode)) {
                StringSource clientHeaderSource =
                    new StringSource("<ClientHeader xmlns=\"http://bartws.micros.com/1.31\">\n" +
                        "<bookingChannel>" + bookingChannel + "</bookingChannel>" +
                        "<languageCode>" + languageCode + "</languageCode>\n" +
                        "<countryCode>" + countryCode + "</countryCode>\n" +
                        "</ClientHeader>");

                addHeader(header, clientHeaderSource, "Error adding Login headers to BART SOAP call.");
            }
        } catch (TransformerException e) {
            LOG.error("Error adding Login header to BART SOAP call.", e);
            throw e;
        }
    }

    public LoginWebServiceMessageCallback withLanguageCode(String languageCode) {
        this.languageCode = languageCode;
        return this;
    }

    public LoginWebServiceMessageCallback withCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public LoginWebServiceMessageCallback withBookingChannel(BartBookingChannelCode bookingChannelCode) {
        this.bookingChannel = bookingChannelCode;
        return this;
    }

    public BartBookingChannelCode getBookingChannel() { return this.bookingChannel; }

    private void addHeader(SoapHeader header, StringSource sourceHeader, String errorMsg)
        throws TransformerException {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(sourceHeader, header.getResult());
        } catch (TransformerException e) {
            LOG.error(errorMsg, e);
            throw e;
        }
    }
}
package uk.co.whitbread.bart.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.StringSource;
import uk.co.whitbread.bart.enums.BartBookingChannelCode;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class BartHeadersMessageCallback implements WebServiceMessageCallback {

    private static final Logger LOG = LoggerFactory.getLogger(LoginWebServiceMessageCallback.class);

    private static final String NAMESPACE = "http://bartws.micros.com/1.13";

    private String username;
    private String password;
    private String languageCode;
    private String countryCode;
    private BartBookingChannelCode bookingChannel;
    private String brand;
    private boolean hasNoBookingChannel;

    public BartHeadersMessageCallback(final String user, final String pass) {
        this.username = user;
        this.password = pass;
    }

    public BartHeadersMessageCallback withBookingChannel(BartBookingChannelCode channel) {
        bookingChannel = channel;
        return this;
    }

    public BartHeadersMessageCallback withBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public BartHeadersMessageCallback withLanguageCode(String languageCode) {
        this.languageCode = languageCode;
        return this;
    }

    public BartHeadersMessageCallback withCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public BartHeadersMessageCallback hasNoBookingChannel(boolean hasNoBookingChannel) {
        this.hasNoBookingChannel = hasNoBookingChannel;
        return this;
    }

    public BartBookingChannelCode getBookingChannel() {
        return bookingChannel;
    }

    @Override
    public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {

        SoapMessage soapMessage = (SoapMessage)message;
        SoapHeader header = soapMessage.getSoapHeader();

        StringSource loginHeader =
                new StringSource("<Login xmlns=\"http://bartws.micros.com/1.31\">\n" +
                        "<username>" + username + "</username>\n" +
                        "<password>" + password + "</password>\n" +
                        "</Login>");

        addHeader(header, loginHeader, "Error adding Login headers to BART SOAP call.");

        if(Objects.isNull(bookingChannel) && StringUtils.isEmpty(brand) &&
            Objects.isNull(languageCode) && StringUtils.isEmpty(countryCode)) {
            return;
        }

        if(BartBookingChannelCode.WEB_DE.equals(bookingChannel)) {
            StringSource clientHeaderSource =
                    new StringSource("<ClientHeader xmlns=\"http://bartws.micros.com/1.31\">\n" +
                            "<languageCode>de</languageCode>\n" +
                            "<countryCode>de</countryCode>\n" +
                            "</ClientHeader>");

            addHeader(header, clientHeaderSource, "Error adding Login headers to BART SOAP call.");
        }

        if((BartBookingChannelCode.CBT.equals(bookingChannel) && StringUtils.isNotBlank(languageCode) && StringUtils.isNotBlank(countryCode))
                || (hasNoBookingChannel && StringUtils.isNotBlank(languageCode) && StringUtils.isNotBlank(countryCode))) {
            StringSource clientHeaderSource =
                    new StringSource("<ClientHeader xmlns=\"http://bartws.micros.com/1.31\">\n" +
                            "<languageCode>" + languageCode + "</languageCode>\n" +
                            "<countryCode>" + countryCode + "</countryCode>\n" +
                            "</ClientHeader>");

            addHeader(header, clientHeaderSource, "Error adding Login headers to BART SOAP call.");
        }

        StringSource sourceHeader = new StringSource(
                String.format("<Source xmlns=\"http://bartws.micros.com/1.31\">%n%s%s</Source>",
                        getElement("bookingChannel", Optional.ofNullable(bookingChannel).map(BartBookingChannelCode::getValue).orElse(null)),
                        getElement("hotelBrand", brand)));
        addHeader(header, sourceHeader, "Error adding Source headers to BART SOAP call.");
    }

    private void addHeader(SoapHeader header, StringSource sourceHeader, String errorMsg) throws TransformerException {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(sourceHeader, header.getResult());
        } catch (TransformerException e) {
            LOG.error(errorMsg, e);
            throw e;
        }
    }

    private String getElement(String elemName, String maybeValue) {
        return Optional.ofNullable(maybeValue)
                .filter(StringUtils::isNotEmpty)
                .map(value -> String.format("<%s>%s</%s>\n", elemName, value, elemName))
                .orElse("");
    }


}

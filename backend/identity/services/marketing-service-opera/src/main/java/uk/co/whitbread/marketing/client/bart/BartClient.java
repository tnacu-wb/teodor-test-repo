package uk.co.whitbread.marketing.client.bart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.co.whitbread.bart.exceptions.BartServiceException;
import uk.co.whitbread.bart.marketing.api.Subscribe;
import uk.co.whitbread.bart.marketing.api.SubscribeResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatus;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusResponse;
import uk.co.whitbread.bart.security.LoginWebServiceMessageCallback;
import uk.co.whitbread.marketing.properties.BartProperties;
import uk.co.whitbread.marketing.utils.BartResponseValidator;

@Slf4j
@RequiredArgsConstructor
@Component
public class BartClient {

    private static final String BART_RETURNED_ERROR_CODE = "023";
    private static final String BART_INVOCATION_ERROR_CODE = "022";

    private final BartProperties bartProperties;
    private final WebServiceTemplate webServiceTemplate;
    private final LoginWebServiceMessageCallback loginServiceCallback;
    private final BartResponseValidator bartResponseValidator;

    public SubscribeResponse subscribeToNewsletters(Subscribe subscribeRequest) {

        SubscribeResponse response;

        try {

            response = (SubscribeResponse) webServiceTemplate.marshalSendAndReceive(
                    bartProperties.getSubscriptionServiceUrl(),
                    subscribeRequest,
                    loginServiceCallback);

        } catch (SoapFaultClientException ex) {
            throw new BartServiceException(ex).withErrorCode(BART_INVOCATION_ERROR_CODE);
        }

        bartResponseValidator.validate(response).ifPresent(error -> {
            throw new BartServiceException(error).withErrorCode(BART_RETURNED_ERROR_CODE);
        });

        return response;
    }

    public SubscriptionStatusResponse retrieveSubscriptionInfo(SubscriptionStatus subscriptionStatusRequest) {

        SubscriptionStatusResponse response;

        try {

            response = (SubscriptionStatusResponse) webServiceTemplate.marshalSendAndReceive(
                    bartProperties.getSubscriptionServiceUrl(),
                    subscriptionStatusRequest,
                    loginServiceCallback);

        } catch (SoapFaultClientException ex) {
            throw new BartServiceException(ex).withErrorCode(BART_INVOCATION_ERROR_CODE);
        }

        bartResponseValidator.validate(response).ifPresent(error -> {
            throw new BartServiceException(error).withErrorCode(BART_RETURNED_ERROR_CODE);
        });
        return response;
    }
}

package uk.co.whitbread.marketing.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.co.whitbread.bart.exceptions.BartServiceException;
import uk.co.whitbread.bart.security.LoginWebServiceMessageCallback;
import uk.co.whitbread.bart.unified.api.SharedDataRequest;
import uk.co.whitbread.bart.unified.api.SharedDataRequestResponse;
import uk.co.whitbread.marketing.mapper.RegionMapper;
import uk.co.whitbread.marketing.model.RegionsResponse;
import uk.co.whitbread.marketing.properties.BartProperties;
import uk.co.whitbread.marketing.utils.BartResponseValidator;
import uk.co.whitbread.marketing.utils.Converter;

@Service
@Deprecated
public class SharedDataService {

    private static final String BART_INVOCATION_ERROR_CODE = "022";
    private static final String BART_RETURNED_ERROR_CODE = "023";

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Autowired
    private BartProperties bartProperties;

    @Autowired
    private RegionMapper regionMapper;

    @Autowired
    private BartResponseValidator bartResponseValidator;


    @Autowired
    private LoginWebServiceMessageCallback loginServiceCallback;


    public RegionsResponse getRegions() {
        try {
            SharedDataRequestResponse sharedDataResponse = (SharedDataRequestResponse) webServiceTemplate.marshalSendAndReceive(
                    bartProperties.getSharedDataServiceUrl(),
                    new SharedDataRequest(),
                    loginServiceCallback);

            bartResponseValidator.validate(sharedDataResponse).ifPresent(error -> {
                throw new BartServiceException(error).withErrorCode(BART_RETURNED_ERROR_CODE);
            });


            return regionMapper.toRegionsResponse(sharedDataResponse);
        } catch (SoapFaultClientException e) {
            throw new BartServiceException(e).withErrorCode(BART_INVOCATION_ERROR_CODE);
        }
    }
}

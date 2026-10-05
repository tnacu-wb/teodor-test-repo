package uk.co.whitbread.marketing.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.bart.marketing.api.ErrorDetails;
import uk.co.whitbread.bart.marketing.api.SubscribeResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusResponse2015;
import uk.co.whitbread.bart.unified.api.ArrayOfRegionRegion;
import uk.co.whitbread.bart.unified.api.SharedDataRequestResponse;
import uk.co.whitbread.bart.unified.api.SharedDataResponse;

import java.io.File;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BartResponseValidatorTest {

    private static final String BART_RETURNED_EMPTY_RESPONSE = "Bart returned empty response.";
    private final BartResponseValidator bartResponseValidator = new BartResponseValidator();
    private JsonMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = TestObjectMapperFactory.create();
    }


    @Test
    void subscription_shouldHandleNullInput() {
        //Given
        SubscribeResponse response = null;

        //When
        Optional<String> errorMessage = bartResponseValidator.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo(BART_RETURNED_EMPTY_RESPONSE)));
    }

    @Test
    void subscription_shouldExtractErrorMessage() {
        //Given
        SubscriptionResponse result = new SubscriptionResponse();
        SubscribeResponse response = new SubscribeResponse();

        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage("errorMessage");

        result.setErrorDetail(errorDetails);
        response.setSubscribeResult(result);
        //When
        Optional<String> errorMessage = bartResponseValidator.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo("errorMessage")));
    }

    @Test
    void subscription_shouldHandleEmptyErrors() {
        //Given
        SubscriptionResponse result = new SubscriptionResponse();
        SubscribeResponse response = new SubscribeResponse();
        response.setSubscribeResult(result);

        //When
        Optional<String> errorMessage = bartResponseValidator.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(false));
    }

    @Test
    void subscription_shouldHandleNullErrorMessages() {
        //Given
        SubscriptionResponse result = new SubscriptionResponse();
        SubscribeResponse response = new SubscribeResponse();


        result.setErrorDetail(new ErrorDetails());
        response.setSubscribeResult(result);
        //When
        Optional<String> errorMessage = bartResponseValidator.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(false));
    }

    @Test
    void subscriptionInfoOrStatus_shouldHandleInnerNullInput() {
        //Given
        SubscriptionStatusResponse response = null;

        //When
        Optional<String> errorMessage = bartResponseValidator.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo(BartResponseValidatorUtils.ERROR_EMPTY_RESPONSE)));
    }

    @Test
    void subscriptionInfoOrStatus_shouldExtractErrorMessage() {
        //Given
        SubscriptionStatusResponse2015 result = new SubscriptionStatusResponse2015();
        SubscriptionStatusResponse response = new SubscriptionStatusResponse();

        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage("errorMessage");

        result.setErrorDetail(errorDetails);
        response.setSubscriptionStatusResult(result);
        //When
        Optional<String> errorMessage = bartResponseValidator.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo("errorMessage")));
    }

    @Test
    void subscriptionInfoOrStatus_shouldHandleEmptyErrors() {
        //Given
        SubscriptionStatusResponse2015 result = new SubscriptionStatusResponse2015();
        SubscriptionStatusResponse response = new SubscriptionStatusResponse();
        response.setSubscriptionStatusResult(result);

        //When
        Optional<String> errorMessage = bartResponseValidator.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(false));
    }

    @Test
    void subscriptionInfoOrStatus_shouldHandleNullErrorMessages() {
        //Given
        SubscriptionStatusResponse2015 result = new SubscriptionStatusResponse2015();
        SubscriptionStatusResponse response = new SubscriptionStatusResponse();


        result.setErrorDetail(new ErrorDetails());
        response.setSubscriptionStatusResult(result);
        //When
        Optional<String> errorMessage = bartResponseValidator.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(false));
    }

    @Test
    void regions_shouldHandeNullInput() {
        SharedDataRequestResponse response = null;

        Optional<String> errorMessage = bartResponseValidator.validate(response);

        assertThat(errorMessage.get(), is(equalTo(BART_RETURNED_EMPTY_RESPONSE)));
    }

    @Test
    void regions_shouldHandeNullRegions() {
        SharedDataRequestResponse response = new SharedDataRequestResponse();
        SharedDataResponse sharedDataResponse = new SharedDataResponse();

        ArrayOfRegionRegion arrayOfRegionRegion = new ArrayOfRegionRegion();
        sharedDataResponse.setRegions(arrayOfRegionRegion);

        response.setSharedDataRequestResult(sharedDataResponse);

        Optional<String> errorMessage = bartResponseValidator.validate(response);

        assertThat(errorMessage.get(), is(equalTo(BART_RETURNED_EMPTY_RESPONSE)));
    }


    @Test
    void regions_shouldHandeEmptyRegions() {
        SharedDataRequestResponse response = new SharedDataRequestResponse();
        SharedDataResponse sharedDataResponse = new SharedDataResponse();

        ArrayOfRegionRegion arrayOfRegionRegion = mock(ArrayOfRegionRegion.class);
        when(arrayOfRegionRegion.getRegion()).thenReturn(Collections.emptyList());
        sharedDataResponse.setRegions(arrayOfRegionRegion);

        response.setSharedDataRequestResult(sharedDataResponse);

        Optional<String> errorMessage = bartResponseValidator.validate(response);

        assertThat(errorMessage.get(), is(equalTo(BART_RETURNED_EMPTY_RESPONSE)));
    }


    @Test
    void regions_shouldValidate() {
        SharedDataRequestResponse response = objectMapper.readValue(new File("src/test/resources/mapping/SharedDataRequestResponse.json"), SharedDataRequestResponse.class);

        Optional<String> errorMessage = bartResponseValidator.validate(response);
        assertThat(errorMessage, is(Optional.empty()));
    }

}

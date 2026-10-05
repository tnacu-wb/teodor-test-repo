package com.whitbread.premierinn.editpaymentmethods;

import static org.mockito.Mockito.when;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.InstanceFactory;
import com.whitbread.premierinn.api.response.customer.CustomerResponse;
import com.whitbread.premierinn.api.response.customer.PaymentCard;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.result.SubmitFormResult;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.remote.ApiThrowable;
import com.whitbread.premierinn.domain.common.Address;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPaymentDetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class SubmitPaymentMethodsFormApiTransformerTest {

    @Mock
    UpdateCustomerPaymentDetails updateCustomerPaymentDetails;
    @Mock
    Customer customer;
    @Mock
    TrackingAnalytics trackingAnalytics;
    @Mock
    LogService crashlyticsLogger;

    private static final String VALID_CARD_NUMBER = "4444333322221111";
    private static final String VALID_CARD_DATE = "12/21";

    private final Response<CustomerResponse> customerUpdateSuccess = Response.success(InstanceFactory.create(CustomerResponse.class,
            "apiTest/customer-update-success.json"));
    private final Response<CustomerResponse> customerServerErrorResponse = Response.error(500, ResponseBody.create(null, ""));
    private SubmitPaymentMethodsFormApiTransformer submitPaymentMethodsFormApiTransformer;

    @Before
    public void setup() {
        submitPaymentMethodsFormApiTransformer = new SubmitPaymentMethodsFormApiTransformer(
                updateCustomerPaymentDetails, customer, trackingAnalytics,
                crashlyticsLogger);
    }

    @Test
    public void testUpdateCustomerSuccess() {
        String cardholderName = "Mark O'Meara";

        PaymentCard expectedPaymentCard = PaymentCard.builder().cardHolderName(cardholderName)
                .cardNumber(VALID_CARD_NUMBER).cardType("VI").expiryDate(VALID_CARD_DATE).build();

        when(customer.getAddress()).thenReturn(new Address("line 1", "line 2", "", "", "", "EC1N2TD",
                "", "GB"));
        when(updateCustomerPaymentDetails.invoke(new UpdateCustomerPaymentDetails.Params(
                new com.whitbread.premierinn.domain.customer.entity.PaymentCard(
                        expectedPaymentCard.cardNumber(),
                        expectedPaymentCard.cardType(),
                        expectedPaymentCard.cardHolderName(),
                        expectedPaymentCard.expiryDate(),
                        null
                ), customer.getAddress()))).thenReturn(Completable.complete());

        List<Input> inputList = new ArrayList<>();
        inputList.add(Input.builder().id(R.id.til_card_details_form_card_number).value(VALID_CARD_NUMBER)
                .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED)).build());

        inputList.add(Input.builder()
                .id(R.id.til_card_details_form_name_on_card).value(cardholderName)
                .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED)).build());

        inputList.add(Input.builder().id(R.id.til_card_details_form_expiry_date)
                .validationTypes(EnumSet.of(Input.ValidationType.CARD_DATE)).value(VALID_CARD_DATE).build());

        Observable.just(inputList)
                .compose(submitPaymentMethodsFormApiTransformer)
                .test()
                .assertValues(SubmitFormResult.inFlight(), SubmitFormResult.serverSuccess());

    }

    @Test
    public void testUpdateCustomerFailed() {
        String cardholderName = "Mark O'Meara";

        PaymentCard expectedPaymentCard = PaymentCard.builder().cardHolderName(cardholderName)
                .cardNumber(VALID_CARD_NUMBER).cardType("VI").expiryDate(VALID_CARD_DATE).build();

        when(customer.getAddress()).thenReturn(new Address("line 1", "line 2", "", "", "", "EC1N2TD",
                "", "GB"));
        when(updateCustomerPaymentDetails.invoke(new UpdateCustomerPaymentDetails.Params(
                new com.whitbread.premierinn.domain.customer.entity.PaymentCard(
                        expectedPaymentCard.cardNumber(),
                        expectedPaymentCard.cardType(),
                        expectedPaymentCard.cardHolderName(),
                        expectedPaymentCard.expiryDate(),
                        null
                ), customer.getAddress()))).thenReturn(Completable.error(new ApiThrowable.Http(500, null)));


        List<Input> inputList = new ArrayList<>();
        inputList.add(Input.builder().id(R.id.til_card_details_form_card_number).value(VALID_CARD_NUMBER)
                .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED)).build());

        inputList.add(Input.builder()
                .id(R.id.til_card_details_form_name_on_card).value(cardholderName)
                .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED)).build());

        inputList.add(Input.builder().id(R.id.til_card_details_form_expiry_date)
                .validationTypes(EnumSet.of(Input.ValidationType.CARD_DATE)).value(VALID_CARD_DATE).build());

        Observable.just(inputList)
                .compose(submitPaymentMethodsFormApiTransformer)
                .test()
                .assertValues(SubmitFormResult.inFlight(), SubmitFormResult.serverError());
    }
}

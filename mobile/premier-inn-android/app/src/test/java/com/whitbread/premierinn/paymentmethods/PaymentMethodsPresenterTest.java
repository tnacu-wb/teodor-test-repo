package com.whitbread.premierinn.paymentmethods;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.whitbread.premierinn.api.response.InstanceFactory;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.data.common.DomainMappers;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager;
import com.whitbread.premierinn.data.remote.AccountApiContract;
import com.whitbread.premierinn.data.remote.ApiThrowable;
import com.whitbread.premierinn.domain.authentication.NoLongerValidCredentials;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer;
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPaymentDetails;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import com.whitbread.premierinn.utils.RxJavaTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodsPresenterTest {

    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();
    @Mock GetCustomer getCustomer;
    @Mock UpdateCustomerPaymentDetails updateCustomerPaymentDetails;
    @Mock PaymentMethodsPresenter.View view;
    @Mock TrackingAnalytics trackingAnalytics;
    @Mock GetStringResource getStringResource;
    @Captor ArgumentCaptor<UpdateCustomerPaymentDetails.Params> paymentCardCaptor;

    @Mock SimplePersistenceManager persistenceManager;

    private PaymentMethodsPresenter presenter;
    private AccountApiContract.CustomerResponse validCustomer;
    private AccountApiContract.CustomerResponse validCustomerNoPaymentCard;
    private Relay<Object> onCardClickSubject = PublishRelay.create();


    @Before
    public void setup() {
        presenter = new PaymentMethodsPresenter(
                getCustomer,
                updateCustomerPaymentDetails,
                new CompositeDisposable(),
                new CompositeDisposable(),
                trackingAnalytics,
                getStringResource,
                persistenceManager
        );
        presenter.initParams(onCardClickSubject);

        validCustomer = InstanceFactory.create(AccountApiContract.CustomerResponse.class, "apiTest/customer-success.json");
        validCustomerNoPaymentCard = InstanceFactory.create(AccountApiContract.CustomerResponse.class,
                "apiTest/customer-success-no-payment-card.json");

        when(view.onDeleteCardConfirmed()).thenReturn(Observable.never());
        when(view.onDeleteClick()).thenReturn(Observable.never());
        when(view.onReplaceCardClick()).thenReturn(Observable.never());
        when(getStringResource.invoke(any())).thenReturn("some text");
        when(updateCustomerPaymentDetails.invoke(any(UpdateCustomerPaymentDetails.Params.class))).thenReturn(Completable.never());
    }

    @Test
    public void testValidCardDetailsDisplayed() {
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(validCustomer)));

        presenter.attachView(view);

        verify(view).setCardExpiryText("01/19");
        verify(view).setCardHolderText("Test");
        verify(view).setCardNumberLastDigits("1111");
    }

    @Test
    public void testCustomerNoPaymentPreference() {
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(validCustomerNoPaymentCard)));

        presenter.attachView(view);

        verify(view).showError();
    }

    @Test
    public void testDialogShownOnDeleteClick() {
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(validCustomer)));
        when(view.onDeleteClick()).thenReturn(Observable.just(Unit.INSTANCE));

        presenter.attachView(view);

        verify(view).showDeleteCardPrompt();
    }

    @Test
    public void testBroughtToEditOnReplaceCardClick() {
        final Customer customer = DomainMappers.toDomain(validCustomer);
        when(getCustomer.invoke()).thenReturn(Single.just(customer));
        when(view.onReplaceCardClick()).thenReturn(Observable.just(Unit.INSTANCE));

        presenter.attachView(view);

        verify(view).editCardDetails(customer);
    }

    @Test
    public void testErrorShownOnDeletionFailure() {
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(validCustomer)));
        when(view.onDeleteCardConfirmed()).thenReturn(Observable.just(new Object()));
        when(updateCustomerPaymentDetails.invoke(any(UpdateCustomerPaymentDetails.Params.class)))
                .thenReturn(Completable.error(new ApiThrowable.Http(500, null)));

        presenter.attachView(view);

        verify(view).showDeletionError();
    }

    @Test
    public void testBroughtBackToMyAccountAfterDeletion() {
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(validCustomer)));
        when(view.onDeleteCardConfirmed()).thenReturn(Observable.just(new Object()));
        when(updateCustomerPaymentDetails.invoke(any(UpdateCustomerPaymentDetails.Params.class)))
                .thenReturn(Completable.complete());

        presenter.attachView(view);

        verify(view).returnToPreviousActivity();
    }

    @Test
    public void testEmptyPaymentCardUpdatedOnDeletion() {
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(validCustomer)));
        when(view.onDeleteCardConfirmed()).thenReturn(Observable.just(new Object()));
        presenter.attachView(view);

        verify(updateCustomerPaymentDetails).invoke(paymentCardCaptor.capture());
        assertEquals("", paymentCardCaptor.getValue().getCard().getNumber());
        assertEquals("", paymentCardCaptor.getValue().getCard().getHoldersFullName());
        assertEquals("", paymentCardCaptor.getValue().getCard().getCardType());
        assertEquals("", paymentCardCaptor.getValue().getCard().getExpiryDate());
    }

    @Test
    public void getCustomerFails_startLoginActivityExpected() {
        when(getCustomer.invoke()).thenReturn(Single.error(new NoLongerValidCredentials(new Exception())));

        presenter.attachView(view);

        verify(view).showForceLoginMessage();
        verify(view).startLogInActivity();
    }
}

package uk.co.whitbread.payments.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.mapper.PaymentMapper;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.threec.InitialiseRequest;
import uk.co.whitbread.payments.properties.*;
import uk.co.whitbread.payments.service.EMerchantService;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitialiseRequestMapper {

    private static final String DEFAULT_PAY_ON_ARRIVAL_AMOUNT = "0";
    private static final String DEFAULT_ZERO_VALUE_AUTH = "0";
    private static final String TOKEN_INJECTION_ACTION = "T";
    private static final String NOT_APPLICABLE = "N/A";
    private static final String DIGITAL_WALLET = "Digital Wallet";

    private final WebhookProperties webhookProperties;
    private final RedirectProperties redirectProperties;
    private final PaymentMapper mapper;
    private final EMerchantService eMerchantService;
    private final CustomMapper fraudCustomMapper;
    private final CardholderStateMapper stateMapper;
    private final PaymentRedirectProperties paymentRedirectProperties;

    public InitialiseRequest mapInitialiseRequest(PaymentRequest paymentRequest, ProviderAccount account,
        String paymentId, String cardTemplate) {
        InitialiseRequest initialiseRequest = mapper.toInitialiseRequest(paymentRequest);
        initialiseRequest.setTrxMerchantReference(paymentId);
        //Set the amount according to booking type. PAY_ON_ARRIVAL transactions are always set to 0.
        of(paymentRequest)
                .map(PaymentRequest::getBooking)
                .map(Booking::getType)
                .ifPresent(bookingType -> {
                    String amount = valueOf(paymentRequest.getPayment().getAmount().getMinorUnits());
                    if (BookingType.PAY_ON_ARRIVAL.name().equals(bookingType)) {
                        initialiseRequest.setTrxAuthenticationAmountValue(amount);
                        amount = DEFAULT_PAY_ON_ARRIVAL_AMOUNT;
                    }
                    initialiseRequest.setTrxAmountValue(amount);
                });
        //Set the required token injection action if a card token is present in the request
        of(paymentRequest)
                .map(PaymentRequest::getPayment)
                .map(Payment::getCard)
                .map(Card::getToken)
                .filter(token -> !token.isEmpty())
                .ifPresentOrElse(token -> expectSavedCard(initialiseRequest, token, account, cardTemplate),
                        () -> expectNewCard(initialiseRequest, account, cardTemplate));
        initialiseRequest.setServiceAction(account.getConfiguration().getServiceAction());
        // get e-merchant details
        EMerchantDetails eMerchantDetails = eMerchantService.getEMerchantDetails(paymentRequest.getPaymentSubType(), paymentRequest.getHotelCode());
        initialiseRequest.setSecurityEMerchantId(eMerchantDetails.getUsername());
        initialiseRequest.setSecurityValidationCode(eMerchantDetails.getPassword());

        initialiseRequest.setFraudMode(account.getConfiguration().getFraudMode());
        initialiseRequest.setCardOnFileIndicator(account.getConfiguration().getCardOnFileIndicator());
        initialiseRequest.setLanguage(paymentRequest.getLanguage());
        initialiseRequest.setPostUrlSuccess(format(webhookProperties.getSuccess(), paymentId));
        initialiseRequest.setPostUrlFailure(format(webhookProperties.getFailure(), paymentId));
        initialiseRequest.setCardholderAddressCity(NOT_APPLICABLE);
        initialiseRequest.setCardholderAddressState(stateMapper.getCardholderState(paymentRequest.getCountryCode()));

        if (StringUtils.equalsIgnoreCase(paymentRequest.getPayment().getType(), PaymentType.WALLET_GOOGLE.name()) &&
            StringUtils.equalsIgnoreCase(paymentRequest.getBooking().getChannel(), ChannelType.APPS_ANDROID.toString())) {
            initialiseRequest.setRedirectApproved(paymentRedirectProperties.getSuccess());
            initialiseRequest.setRedirectDeclined(paymentRedirectProperties.getFailure());
        } else {
            initialiseRequest.setRedirectApproved(format(redirectProperties.getSuccess(), paymentId));
            initialiseRequest.setRedirectDeclined(format(redirectProperties.getFailure(), paymentId));
        }

        if (StringUtils.equalsIgnoreCase(paymentRequest.getPayment().getType(), PaymentType.WALLET_APPLE.name())
            || StringUtils.equalsIgnoreCase(paymentRequest.getPayment().getType(), PaymentType.WALLET_GOOGLE.name())) {
            initialiseRequest.setWalletType(DIGITAL_WALLET);
        }

        fraudCustomMapper.mapForInitialise(account, initialiseRequest, paymentRequest);
        return initialiseRequest;
    }

    public InitialiseRequest mapInitialiseRequest(SaveCardRequest saveCardRequest, ProviderAccount account, String paymentId, String cardTemplate) {

        var initRequest = new InitialiseRequest();
        initRequest.setTrxMerchantReference(paymentId);
        initRequest.setServiceAction(account.getConfiguration().getServiceAction());
        EMerchantDetails eMerchantDetails = eMerchantService.getEMerchantDetailsByCountry(saveCardRequest.getCountry());
        initRequest.setSecurityEMerchantId(eMerchantDetails.getUsername());
        initRequest.setSecurityValidationCode(eMerchantDetails.getPassword());
        initRequest.setPostUrlSuccess(format(webhookProperties.getSuccess(), paymentId));
        initRequest.setPostUrlFailure(format(webhookProperties.getFailure(), paymentId));
        initRequest.setRedirectApproved(format(redirectProperties.getSuccess(), paymentId));
        initRequest.setRedirectDeclined(format(redirectProperties.getFailure(), paymentId));
        initRequest.setCardholderAddressLine1(saveCardRequest.getBillingAddress().getLine1());
        initRequest.setCardholderAddressLine2(saveCardRequest.getBillingAddress().getLine2());
        initRequest.setCardholderAddressLine3(saveCardRequest.getBillingAddress().getLine3());
        initRequest.setCardholderAddressLine4(saveCardRequest.getBillingAddress().getLine4());
        initRequest.setCardholderAddressPostalCode(saveCardRequest.getBillingAddress().getPostalCode());
        initRequest.setCardholderAddressCountry(saveCardRequest.getBillingAddress().getCountryCode());
        initRequest.setTrxOptions(account.getConfiguration().getNewCardTrxOption());
        initRequest.setLanguage(saveCardRequest.getLanguage());
        initRequest.setTrxAmountValue(DEFAULT_ZERO_VALUE_AUTH);
        initRequest.setCardOnFileIndicator(account.getConfiguration().getCardOnFileIndicator());
        initRequest.setFraudMode(account.getConfiguration().getFraudMode());
        expectNewCard(initRequest, account, cardTemplate);

        return initRequest;
    }

    public InitialiseRequest mapInitialiseAuthorizeScaRequest(ProviderAccount account,
        String paymentId, String language, String country) {

        var initRequest = new InitialiseRequest();
        initRequest.setTrxMerchantReference(paymentId);
        initRequest.setServiceAction(account.getConfiguration().getServiceAction());
        EMerchantDetails eMerchantDetails = eMerchantService.getEMerchantDetailsByCountry(country);
        initRequest.setSecurityEMerchantId(eMerchantDetails.getUsername());
        initRequest.setSecurityValidationCode(eMerchantDetails.getPassword());
        initRequest.setPostUrlSuccess(format(webhookProperties.getSuccess(), paymentId));
        initRequest.setPostUrlFailure(format(webhookProperties.getFailure(), paymentId));
        initRequest.setRedirectApproved(format(redirectProperties.getSuccess(), paymentId));
        initRequest.setRedirectDeclined(format(redirectProperties.getFailure(), paymentId));
        initRequest.setTrxOptions(account.getConfiguration().getNewCardTrxOption());
        initRequest.setLanguage(language);
        initRequest.setTrxAmountValue(DEFAULT_ZERO_VALUE_AUTH);
        initRequest.setCardOnFileIndicator(account.getConfiguration().getCardOnFileIndicator());
        initRequest.setFraudMode(account.getConfiguration().getFraudMode());
        expectNewCard(initRequest, account, account.getConfiguration().getNewCardTemplate());

        return initRequest;
    }

    void expectSavedCard(InitialiseRequest initialiseRequest, String token, ProviderAccount account,
        String cardTemplate) {
        initialiseRequest.setTemplateId(ofNullable(cardTemplate)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Missing iPage template for saved cards",
                        ErrorCodes.TEMPLATE_ERROR)));
        initialiseRequest.setTrxOptions(account.getConfiguration().getSavedCardTrxOption());
        initialiseRequest.setToken(token);
        initialiseRequest.setTokenInjectionAction(TOKEN_INJECTION_ACTION);
    }

    void expectNewCard(InitialiseRequest initialiseRequest, ProviderAccount account, String cardTemplate) {
        initialiseRequest.setTemplateId(ofNullable(cardTemplate)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Missing iPage template for new cards",
                        ErrorCodes.TEMPLATE_ERROR)));
        initialiseRequest.setTrxOptions(account.getConfiguration().getNewCardTrxOption());
    }
}
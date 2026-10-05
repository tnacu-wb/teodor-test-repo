package uk.co.whitbread.payments.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.config.PaypalConfig;
import uk.co.whitbread.payments.mapper.PaymentMapper;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionRequest;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionResponseBody;
import uk.co.whitbread.payments.properties.ProviderAccount;
import uk.co.whitbread.payments.service.EMerchantService;

import static java.lang.String.format;
import static uk.co.whitbread.payments.model.PaymentSubType.ECOMM;
import static uk.co.whitbread.payments.model.PaymentSubType.MOTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoCardReadTransactionRequestMapper {

    private static final String DEFAULT_PAY_ON_ARRIVAL_AMOUNT = "0";
    private static final String NOT_APPLICABLE = "N/A";
    private static final String EFT_AUTHORIZATION = "EftAuthorization";
    private static final String REQUEST_TYPE = "payrequestnocardread";
    private static final String TOKEN_OPTION_FLAG = "P";
    private final PaymentMapper paymentMapper;
    private final EMerchantService eMerchantService;
    private final CustomMapper fraudCustomMapper;
    private final CardholderStateMapper stateMapper;
    private final PaypalConfig paypalConfig;
    private static final String PAYPAL_USER_DATA_4 = "paypal";

    public NoCardReadTransactionRequest populateNoCardReadRequest(RefundRequest refundRequest, ProviderAccount account, String requestId) {
        NoCardReadTransactionRequest.Request request = new NoCardReadTransactionRequest.Request();
        NoCardReadTransactionRequest.Params params = new NoCardReadTransactionRequest.Params();

        var card = refundRequest.getRefund().getCard();
        request.setType(REQUEST_TYPE);
        request.setVersion(account.getConfiguration().getVersion());
        // get e-merchant details
        EMerchantDetails eMerchantDetails;
        if(ChannelType.WEB.name().equalsIgnoreCase(refundRequest.getBooking().getChannel())){
            eMerchantDetails = eMerchantService.getEMerchantDetails(ECOMM.name(), refundRequest.getHotelCode());
        }else{
            eMerchantDetails = eMerchantService.getEMerchantDetails(MOTO.name(), refundRequest.getHotelCode());
        }
        request.setCredentials(NoCardReadTransactionRequest.Credentials.builder()
                .validationId(eMerchantDetails.getUsername())
                .validationCode(eMerchantDetails.getPassword()).build());
        applyCardExpiryAndToken(params, card);
        params.setOptionFlags(TOKEN_OPTION_FLAG);
        params.setCardOnFileIndicator(account.getConfiguration().getCardOnFileIndicator());
        params.setAmount(String.valueOf(-refundRequest.getRefund().getAmount().getMinorUnits()));
        params.setCurrency(refundRequest.getRefund().getAmount().getCurrency());
        params.setTransactionReference(requestId);
        params.setBookingReference(refundRequest.getBooking().getReference());
        request.setParams(params);

        NoCardReadTransactionRequest noCardReadTransactionRequest = new NoCardReadTransactionRequest();
        noCardReadTransactionRequest.setRequest(request);
        return noCardReadTransactionRequest;
    }

    public NoCardReadTransactionRequest populateNoCardReadRequest(
        PaymentRequest paymentRequest, ProviderAccount account, String paymentId) {

      NoCardReadTransactionRequest req = baseRequest(paymentRequest);
      var request = req.getRequest();
      var params = request.getParams();
      var card = paymentRequest.getPayment().getCard();

      request.setType(account.getConfiguration().getServiceAction());
      request.setVersion(account.getConfiguration().getVersion());
      applyCredentials(request, paymentRequest);

      applyCommonParams(params, paymentRequest, account, paymentId);

      params.setExpiryDate(format("%s%s", card.getExpiryMonth(), card.getExpiryYear()));
      params.setToken(card.getToken());

      if (BookingType.PAY_ON_ARRIVAL.name().equals(paymentRequest.getBooking().getType())
          && EFT_AUTHORIZATION.equals(request.getType())) {
        params.setAmount(DEFAULT_PAY_ON_ARRIVAL_AMOUNT);
      }

      var mit = paymentRequest.getPayment().getMit();
      if (isMitPresent(mit)) {
        params.setCardOnFileIndicator(paypalConfig.getPaypalSensitiveData().getMitCofIndicator());
        params.setTransInitiator(account.getConfiguration().getTransInitiator());
        params.setScaTransRef(mit.getScaReference());
        params.setMitType(mit.getType());
      }

      fraudCustomMapper.mapForNoCardRead(account, params, paymentRequest);
      return req;
    }


    public NoCardReadTransactionRequest populatePaypalMitRequest(
        PaypalForwardAPITransactionResponseBody paypalResponse,
        PaymentRequest paymentRequest,
        ProviderAccount account,
        String paymentId) {

      NoCardReadTransactionRequest req = baseRequest(paymentRequest);
      var request = req.getRequest();
      var params = request.getParams();

      request.setType(paypalConfig.getPaypalSensitiveData().getType());
      request.setVersion(paypalConfig.getPaypalSensitiveData().getVersion());
      applyCredentials(request, paymentRequest);

      applyCommonParams(params, paymentRequest, account, paymentId);

      params.setExpiryDate(paypalResponse.getResponse().getParams().getExpiry());
      params.setToken(paypalResponse.getResponse().getParams().getToken());
      params.setAmount(String.valueOf(paymentRequest.getPayment().getAmount().getMinorUnits()));
      params.setCurrency(paymentRequest.getPayment().getAmount().getCurrency());

      // PayPal MIT logic (inline, no helper)
      params.setCardOnFileIndicator(paypalConfig.getPaypalSensitiveData().getMitCofIndicator());
      params.setTransInitiator(paypalConfig.getPaypalSensitiveData().getTransInitiator());
      params.setScaTransRef(paypalResponse.getResponse().getParams().getScaReference());
      params.setMitType(null);
      params.setWalletType(PAYPAL_USER_DATA_4);

      fraudCustomMapper.mapForNoCardRead(account, params, paymentRequest);
      return req;
    }


    public NoCardReadTransactionRequest populateMitCcRequest(
        PaymentRequest paymentRequest, ProviderAccount account, String paymentId) {

      NoCardReadTransactionRequest req = baseRequest(paymentRequest);
      var request = req.getRequest();
      var params = request.getParams();
      var card = paymentRequest.getPayment().getCard();
      applyCardExpiryAndToken(params, card);

      request.setType(account.getConfiguration().getServiceAction());
      request.setVersion(account.getConfiguration().getVersion());
      applyCredentials(request, paymentRequest);

      applyCommonParams(params, paymentRequest, account, paymentId);

      params.setAmount(String.valueOf(paymentRequest.getPayment().getAmount().getMinorUnits()));
      params.setTransInitiator(account.getConfiguration().getTransInitiator());

      fraudCustomMapper.mapForNoCardRead(account, params, paymentRequest);
      return req;
    }


    private boolean isMitPresent(final Mit mit) {
          return mit != null;
      }

    private NoCardReadTransactionRequest baseRequest(PaymentRequest paymentRequest) {
      return paymentMapper.toNoCardReadTransactionRequest(paymentRequest);
    }

    private void applyCredentials(NoCardReadTransactionRequest.Request request,
                                  PaymentRequest paymentRequest) {

      EMerchantDetails eMerchantDetails =
          eMerchantService.getEMerchantDetails(paymentRequest.getPaymentSubType(),
              paymentRequest.getHotelCode());

      request.setCredentials(NoCardReadTransactionRequest.Credentials.builder()
          .validationId(eMerchantDetails.getUsername())
          .validationCode(eMerchantDetails.getPassword())
          .build());
    }

    private void applyCommonParams(NoCardReadTransactionRequest.Params params,
                                   PaymentRequest paymentRequest,
                                   ProviderAccount account,
                                   String paymentId) {

      params.setTransactionReference(paymentId);
      params.setOptionFlags(TOKEN_OPTION_FLAG);
      params.setCardOnFileIndicator(account.getConfiguration().getCardOnFileIndicator());
      params.setSettlementReference(paymentRequest.getPayment().getSettlementReference());
      params.setBookingReference(paymentRequest.getBooking().getReference());
      params.setCardholderCity(NOT_APPLICABLE);
      params.setCardholderState(stateMapper.getCardholderState(paymentRequest.getCountryCode()));
    }

    private void applyCardExpiryAndToken(NoCardReadTransactionRequest.Params params, Card card) {
      params.setExpiryDate(format("%s%s", card.getExpiryMonth(), card.getExpiryYear()));
      params.setToken(card.getToken());
    }

}
package uk.co.whitbread.payments.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.config.PaypalConfig;
import uk.co.whitbread.payments.config.PaypalForwardAPIRequestConfig;
import uk.co.whitbread.payments.model.EMerchantDetails;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionRequest;
import uk.co.whitbread.payments.properties.ProviderAccount;
import uk.co.whitbread.payments.service.EMerchantService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaypalTransactionRequestMapper {

    private final PaypalForwardAPIRequestConfig paypalForwardAPIRequestConfig;
    private final PaypalConfig paypalConfig;
    private final CustomMapper fraudCustomMapper;
    private final EMerchantService eMerchantService;
    private final CardholderStateMapper stateMapper;
    private static final String PAYPAL_USER_DATA_4 = "paypal";
    private static final String PAYPAL_EXPIRE_AT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String PAYPAL_CARD_EXPIRY_DATE_MM_YY_PATH = "/body/Request/Params/CardExpiryDateMMYY";
    private static final String NOT_APPLICABLE = "N/A";

    public uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionRequest populatePaypalRequest(PaymentRequest paymentRequest, ProviderAccount account, String paymentId) {

        PaypalForwardAPITransactionRequest paypalForwardAPITransactionRequest = PaypalForwardAPITransactionRequest.builder().build();
        fraudCustomMapper.mapForPaypal(account, paypalForwardAPITransactionRequest, paymentRequest);
        EMerchantDetails eMerchantDetails = eMerchantService.getEMerchantDetails(paymentRequest.getPaymentSubType(), paymentRequest.getHotelCode());

        ArrayList<PaypalForwardAPITransactionRequest.Transformation> transformationList = getTransformations();

        //Added the below PayPal CardNumber to hardcode the Barclays card in UAT/SIT (Non-Production environments) - Start
        if(paypalConfig.getPaypalTestCard().getEnablePaypalTestCard()) {
            boolean testCardHotelCode = paypalConfig.getPaypalTestCard().getTestCardHotelCodes().stream()
                    .anyMatch(code -> code.equalsIgnoreCase(eMerchantDetails.getUsername().substring(eMerchantDetails.getUsername().lastIndexOf("-") + 1)));
            if (testCardHotelCode) {
                transformationList.forEach(transformations -> {
                    if (transformations.getPath().equalsIgnoreCase("/body/Request/Params/CardNumber")) {
                        transformations.setValue(paypalConfig.getPaypalTestCard().getTestCardNumber());
                    } else if (transformations.getPath().equalsIgnoreCase(PAYPAL_CARD_EXPIRY_DATE_MM_YY_PATH)) {
                        List<List<String>> expiryValue =new ArrayList<>();
                        List<List<String>> expiryList = (List<List<String>>) transformations.getValue();
                        String testCardExpiryDateMMYY = paypalConfig.getPaypalTestCard().getTestCardExpiryDateMMYY();
                        List<String> expiryMMYY = new ArrayList<>();
                        expiryMMYY.add(0, expiryList.get(1).get(0));
                        expiryMMYY.add(1, testCardExpiryDateMMYY.substring(0,testCardExpiryDateMMYY.length()-2));
                        expiryMMYY.add(2, testCardExpiryDateMMYY.substring(testCardExpiryDateMMYY.length()-2));

                        expiryValue.add(0, expiryList.get(0));
                        expiryValue.add(1, expiryMMYY);
                        transformations.setValue(expiryValue);
                    } else if (transformations.getPath().equalsIgnoreCase("/body/Request/Params/CVV2")) {
                        transformations.setValue(paypalConfig.getPaypalTestCard().getTestCVV2());
                    } else if (transformations.getPath().equalsIgnoreCase("/body/Request/Params/CardholderStreetAddress1")) {
                        transformations.setValue(paypalConfig.getPaypalTestCard().getTestCardholderStreetAddress1());
                    } else if (transformations.getPath().equalsIgnoreCase("/body/Request/Params/CardholderCity")) {
                        transformations.setValue(paypalConfig.getPaypalTestCard().getTestCardholderCity());
                    } else if (transformations.getPath().equalsIgnoreCase("/body/Request/Params/CardholderZipCode")) {
                        transformations.setValue(paypalConfig.getPaypalTestCard().getTestCardholderZipCode());
                    }
                });
            }
        }
        //Added the above PayPal CardNumber to hardcode the Barclays card in UAT/SIT (Non-Production environments) - end

        // PayPal Inline Config is added for only Non-Production
        if(paypalConfig.getPaypalConfigData().getEnablePaypalConfigData()){
            return PaypalForwardAPITransactionRequest.builder()
                    .data(getForwardAPIData(paymentRequest, paymentId))
                    .config(getConfig(transformationList))
                    .tsp(PaypalForwardAPITransactionRequest.Tsp.builder()
                            .currencyCode(paymentRequest.getPayment().getAmount().getCurrency())
                            .expireAt(getCurrentDate()).build())
                    .url(isNull(paypalConfig.getUrl()) ? "" : paypalConfig.getUrl())
                    .method(isNull(paypalConfig.getMethod()) ? "" : paypalConfig.getMethod())
                    .sensitiveData(getSensitiveData(paypalForwardAPITransactionRequest, eMerchantDetails))
                    .build();
        }else {
            return PaypalForwardAPITransactionRequest.builder()
                    .name(isNull(paypalConfig.getPaypalConfigData().getPaypalProdConfigName()) ?"": paypalConfig.getPaypalConfigData().getPaypalProdConfigName())
                    .data(getForwardAPIData(paymentRequest, paymentId))
                    .tsp(PaypalForwardAPITransactionRequest.Tsp.builder()
                            .currencyCode(paymentRequest.getPayment().getAmount().getCurrency())
                            .expireAt(getCurrentDate()).build())
                    .url(isNull(paypalConfig.getUrl()) ? "" : paypalConfig.getUrl())
                    .method(isNull(paypalConfig.getMethod()) ? "" : paypalConfig.getMethod())
                    .sensitiveData(getSensitiveData(paypalForwardAPITransactionRequest, eMerchantDetails))
                    .build();
        }
    }

    private String getCurrentDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(PAYPAL_EXPIRE_AT_DATE_FORMAT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, paypalConfig.getExpireAt());
        return dateFormat.format(cal.getTime());
    }

    public uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionRequest.SensitiveData getSensitiveData(PaypalForwardAPITransactionRequest paypalForwardAPITransactionRequest, EMerchantDetails eMerchantDetails) {
        var sensitiveData = paypalForwardAPITransactionRequest.getSensitiveData();
        sensitiveData.setType(isNull(paypalConfig.getPaypalSensitiveData().getZeroAuthType())?"":paypalConfig.getPaypalSensitiveData().getZeroAuthType());
        sensitiveData.setVersion(isNull(paypalConfig.getPaypalSensitiveData().getVersion())?"":paypalConfig.getPaypalSensitiveData().getVersion());
        sensitiveData.setValidationID(isNull(eMerchantDetails.getUsername())?"":eMerchantDetails.getUsername());
        sensitiveData.setValidationCode(isNull(eMerchantDetails.getPassword())?"":eMerchantDetails.getPassword());
        sensitiveData.setValidationCodeHash(isNull(paypalConfig.getPaypalSensitiveData().getValidationCodeHash())?"":paypalConfig.getPaypalSensitiveData().getValidationCodeHash());
        sensitiveData.setOptionFlags(isNull(paypalConfig.getPaypalSensitiveData().getOptionFlags())?"":paypalConfig.getPaypalSensitiveData().getOptionFlags());
        sensitiveData.setCofIndicator(isNull(paypalConfig.getPaypalSensitiveData().getCofIndicator())?"":paypalConfig.getPaypalSensitiveData().getCofIndicator());
        sensitiveData.setTransInitiator(isNull(paypalConfig.getPaypalSensitiveData().getTransInitiator())?"":paypalConfig.getPaypalSensitiveData().getTransInitiator());
        sensitiveData.setMitType("");
        sensitiveData.setScaTransRef("");
        sensitiveData.setPaypalString(PAYPAL_USER_DATA_4);
        return sensitiveData;
    }

    public uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionRequest.ForwardAPIData getForwardAPIData(PaymentRequest paymentRequest, String paymentId) {
        return PaypalForwardAPITransactionRequest.ForwardAPIData.builder()
                .amount(String.valueOf(paypalConfig.getAmount()))
                .currency(paymentRequest.getPayment().getAmount().getCurrency())
                .cardholderStreetAddress1(paymentRequest.getPayment().getBilling().getAddress().getLine1())
                .cardholderStreetAddress2(paymentRequest.getPayment().getBilling().getAddress().getLine2())
                .cardholderStreetAddress3(paymentRequest.getPayment().getBilling().getAddress().getLine3())
                .cardholderStreetAddress4(paymentRequest.getPayment().getBilling().getAddress().getLine4())
                .cardholderState(stateMapper.getCardholderState(paymentRequest.getCountryCode()))
                .cardholderZipCode(paymentRequest.getPayment().getBilling().getAddress().getPostalCode())
                .cardholderNameFirst(paymentRequest.getPayment().getBilling().getFirstName())
                .cardholderNameLast(paymentRequest.getPayment().getBilling().getLastName())
                .cardholderEmail(paymentRequest.getPayment().getBilling().getEmail())
                .cardholderTelephone(paymentRequest.getPayment().getBilling().getTelephone())
                .cardholderCountry(paymentRequest.getPayment().getBilling().getAddress().getCountryCode())
                .cardholderCity(NOT_APPLICABLE)
                .paymentId(paymentId).build();
    }

    private uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionRequest.Config getConfig(ArrayList<PaypalForwardAPITransactionRequest.Transformation> transformationList) {

        List<String> configMethods = new ArrayList<>();
        configMethods.add(isNull(paypalConfig.getMethod()) ? "" : paypalConfig.getMethod());
        List<String> configTypes = new ArrayList<>();
        configTypes.add(isNull(paypalConfig.getPaypalConfigData().getType()) ? "" : paypalConfig.getPaypalConfigData().getType());

        return PaypalForwardAPITransactionRequest.Config.builder()
                .transformations(transformationList)
                .methods(configMethods)
                .name(isNull(paypalConfig.getPaypalConfigData().getName()) ? "" : paypalConfig.getPaypalConfigData().getName())
                .url(isNull(paypalConfig.getPaypalConfigData().getUrl()) ? "" : paypalConfig.getPaypalConfigData().getUrl())
                .requestFormat(PaypalForwardAPITransactionRequest.RequestFormat.builder()
                        .body(isNull(paypalConfig.getPaypalConfigData().getBody()) ? "" : paypalConfig.getPaypalConfigData().getBody())
                        .build())
                .types(configTypes).build();
    }

    private ArrayList<PaypalForwardAPITransactionRequest.Transformation> getTransformations() {
        ArrayList<PaypalForwardAPITransactionRequest.Transformation> transformationList = new ArrayList<>();
        paypalForwardAPIRequestConfig.getPaypalForwardAPIRequest().forEach(response -> {
            PaypalForwardAPITransactionRequest.Transformation transformation;

            if (response.getPath().equalsIgnoreCase(PAYPAL_CARD_EXPIRY_DATE_MM_YY_PATH)) {
                transformation = PaypalForwardAPITransactionRequest.Transformation.builder()
                        .path(isNull(response.getPath()) ? "" : response.getPath())
                        .value(isNull(response.getValue()) ? "" : response.getValue()).build();
            } else {
                transformation = PaypalForwardAPITransactionRequest.Transformation.builder()
                        .path(isNull(response.getPath()) ? "" : response.getPath())
                        .value(isNull(response.getValue().get(0).get(0)) ? "" : response.getValue().get(0).get(0)).build();
            }
            transformationList.add(transformation);
        });
        return transformationList;
    }
}

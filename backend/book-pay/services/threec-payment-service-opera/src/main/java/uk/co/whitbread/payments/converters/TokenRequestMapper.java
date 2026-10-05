package uk.co.whitbread.payments.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.payments.model.CreateTokenRequest;
import uk.co.whitbread.payments.model.UpdateTokenRequest;
import uk.co.whitbread.payments.service.EMerchantService;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenRequestMapper {

    private static final String NOT_APPLICABLE = "N/A";
    private final EMerchantService eMerchantService;
    private final CardholderStateMapper stateMapper;

    public MultiValueMap<String, String> mapCreateTokenRequest(CreateTokenRequest createTokenRequest) {
        var formData = new LinkedMultiValueMap<String, String>();
        var cardHolder = ofNullable(createTokenRequest.getCardHolder());
        var eMerchantDetails = eMerchantService.getTokenisedEMerchantDetails();
        formData.add("eMerchantID", eMerchantDetails.getUsername());
        formData.add("ValidationCode", eMerchantDetails.getPassword());
        formData.add("TokenSchemeID", "");
        formData.add("TokenExpiryYYMM", "");
        formData.add("CardNumber", createTokenRequest.getCardNumber());
        formData.add("CardExpiryYYMM", format("%s%s", createTokenRequest.getExpiryYear(), createTokenRequest.getExpiryMonth()));
        formData.add("CardIssueYYMM", "");
        formData.add("CardIssueNo", "");
        formData.add("CardHolderAddress1", cardHolder.isPresent() && StringUtils.isNotEmpty(cardHolder.get().getAddress().getLine1()) ? cardHolder.get().getAddress().getLine1() : "");
        formData.add("CardHolderCity", NOT_APPLICABLE);
        formData.add("CardHolderState", stateMapper.getCardholderState(createTokenRequest.getCountryCode()));
        formData.add("CardHolderPostalCode", cardHolder.isPresent() && StringUtils.isNotEmpty(cardHolder.get().getAddress().getPostalCode()) ? cardHolder.get().getAddress().getPostalCode() : "");
        formData.add("CardHolderFirstName", cardHolder.isPresent() && StringUtils.isNotEmpty(cardHolder.get().getCardHolderName()) ? cardHolder.get().getCardHolderName() : "");
        formData.add("CardHolderLastName", "");
        formData.add("MerchantRef", createTokenRequest.getRequestId());
        formData.add("UserData1", "");
        formData.add("UserData2", "");
        formData.add("Online", "True");
        formData.add("OptionFlags", "");
        return formData;
    }

    public MultiValueMap<String, String> mapUpdateTokenRequest(UpdateTokenRequest updateTokenRequest) {
        var formData = new LinkedMultiValueMap<String, String>();
        var address = updateTokenRequest.getCardHolderAddress();
        // All fields must be sent - if empty they will be ignored by 3CP, and it will leave existing data as is.
        var eMerchantDetails = eMerchantService.getTokenisedEMerchantDetails();
        formData.add("eMerchantID", eMerchantDetails.getUsername());
        formData.add("ValidationCode", eMerchantDetails.getPassword());
        formData.add("TokenNo", updateTokenRequest.getToken());
        formData.add("TokenExpiryYYMM", "");
        formData.add("CardNumber", "");
        formData.add("CardExpiryYYMM", "");
        formData.add("CardIssueYYMM", "");
        formData.add("CardIssueNo", "");
        formData.add("CardHolderAddress1", address.getLine1());
        formData.add("CardHolderCity", "");
        formData.add("CardHolderState", stateMapper.getCardholderState(address.getCountryCode()));
        formData.add("CardHolderPostalCode", address.getPostalCode());
        formData.add("CardHolderFirstName", updateTokenRequest.getCardHolderFirstName());
        formData.add("CardHolderLastName", updateTokenRequest.getCardHolderLastName());
        formData.add("MerchantRef", updateTokenRequest.getRequestId());
        formData.add("UserData1", "");
        formData.add("UserData2", "");
        formData.add("Online", "True");
        formData.add("OptionFlags", "");
        return formData;
    }
}
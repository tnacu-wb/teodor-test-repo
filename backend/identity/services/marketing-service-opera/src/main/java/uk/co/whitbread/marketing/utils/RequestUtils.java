package uk.co.whitbread.marketing.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import uk.co.whitbread.marketing.exception.ValidationException;
import uk.co.whitbread.marketing.model.BrandCode;
import uk.co.whitbread.marketing.model.ContactType;
import uk.co.whitbread.marketing.model.EditSubscription;
import uk.co.whitbread.marketing.model.MarketingSubscriptionRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.model.newsletter.ContactSubType;
import uk.co.whitbread.marketing.model.newsletter.PreferencesEditRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.ConfirmDoubleOptInRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.UnsubscribeRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.UpdatePreferencesRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.co.whitbread.marketing.model.newsletter.ContactType.email;
import static uk.co.whitbread.marketing.model.newsletter.ContactType.phone;

@Component
public class RequestUtils {

    private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
    private final List<String> brandCodes = Stream.of(BrandCode.values()).map(Enum::name).collect(Collectors.toList());
    private final Pattern emailPattern = Pattern.compile(EMAIL_REGEX);

    private static final String CHANNEL_ID_REGEX = "[A-Za-z0-9_-]+";
    private final Pattern channelIdPattern = Pattern.compile(CHANNEL_ID_REGEX);

    public void validateRequest(MarketingSubscriptionRequest request) {
        if (ObjectUtils.isEmpty(request.getEmailAddress()) && ObjectUtils.isEmpty(request.getCustomerId())) {
            throw new ValidationException("EmailAddress or CustomerId must be present in request");
        }
    }

    public String getCustomerId(String email, String customerId) {
        return StringUtils.hasLength(customerId) ? customerId : email;
    }

    public void validateEditRequest(NewsletterPreferencesEditRequest request) {
        request.getSubscriptionData().forEach(this::validateEditSubscriptionData);

    }

    private void validateEditSubscriptionData(EditSubscription subscription) {
        validateBrandCodes(subscription.getBrandCodes());
        if (ObjectUtils.isEmpty(subscription.getContactChannelValue()) && ObjectUtils.isEmpty(subscription.getContactChannelId())) {
            throw new ValidationException("Either contactChannelValue or contactChannelId must be present");
        }

        if (subscription.getContactChannelType().equals(ContactType.Telephone) && ObjectUtils.isEmpty(subscription.getContactChannelSubType())) {
            throw new ValidationException("ContactChannelSubType must not be null or empty when ContactChannelType is \"Telephone\"");
        }
    }

    public void validateEditPermissions(PreferencesEditRequest editRequest, String contactValue,
                                        uk.co.whitbread.marketing.model.newsletter.ContactType contactType) {
        validateBrandCodes(editRequest.getBrandCodes());
        validatePhone(contactType, editRequest.getContactSubType());
        validateEmail(contactValue, contactType, editRequest.getContactSubType());
    }

    public void validateUpdatePreferences(UpdatePreferencesRequest updatePreferencesRequest, String contactValue,
                                          uk.co.whitbread.marketing.model.newsletter.ContactType contactType) {
        validateBrandCodes(updatePreferencesRequest.getBrandCodes());
        validatePhone(contactType, updatePreferencesRequest.getContactSubType());
        validateEmail(contactValue, contactType, updatePreferencesRequest.getContactSubType());
        validateCustomerLanguage(updatePreferencesRequest.getCustomer().getLanguage());
    }

    public void validateConfirmDoubleOptIn(ConfirmDoubleOptInRequest confirmDoubleOptInRequest, String contactChannelId,
                                           uk.co.whitbread.marketing.model.newsletter.ContactType contactType) {
        validateBrandCodes(confirmDoubleOptInRequest.getBrandCodes());
        validateChannelId(contactChannelId);
        validatePhone(contactType, confirmDoubleOptInRequest.getContactSubType());
    }

    public void validateUnsubscribeRequest(UnsubscribeRequest unsubscribeRequest, String contactChannelId,
                                           uk.co.whitbread.marketing.model.newsletter.ContactType contactType) {
        validateBrandCodes(unsubscribeRequest.getBrandCodes());
        validateChannelId(contactChannelId);
        validatePhone(contactType, unsubscribeRequest.getContactSubType());
    }

    private void validateEmail(String contactValue, uk.co.whitbread.marketing.model.newsletter.ContactType contactType, ContactSubType contactSubType) {
        if (email.equals(contactType) &&
                !emailPattern.matcher(contactValue)
                        .matches()) {
            throw new ValidationException("Invalid email");
        }
    }

    private void validateCustomerLanguage(String language) {
        if (!Arrays.asList(Locale.getISOLanguages()).contains(language.toLowerCase())) {
            throw new ValidationException("Invalid Customer Language");
        }
    }

    private void validatePhone(uk.co.whitbread.marketing.model.newsletter.ContactType contactType, ContactSubType contactSubType) {
        if (phone.equals(contactType) && Objects.isNull(contactSubType)) {
            throw new ValidationException("ContactSubType must not be null when ContactType is \"phone\"");
        }
    }

    public void validateBrandCodes(String... reqBrandCodes) {
        if (reqBrandCodes.length <= 0) {
            throw new ValidationException("At least one brand code must be present");
        }
        if (!brandCodes.containsAll(Arrays.asList(reqBrandCodes))) {
            throw new ValidationException("Invalid Brand code.Ensure brand codes supplied are valid");
        }
    }


    public void validateGetPreferencesRequest(NewsletterPreferencesGetRequest request) {
        validateBrandCodes(request.getBrandCodes());
        if (ObjectUtils.isEmpty(request.getContactChannel().getContactChannelId()) && ObjectUtils.isEmpty(request.getContactChannel().getContactChannelValue())) {
            throw new ValidationException("Either contactChannelValue or contactChannelId must be present");
        }

    }

    public void validateGetPreferencesRequest(PreferencesGetRequest request) {
        validateBrandCodes(request.getBrandCodes());
        if (email.equals(request.getContactType()) &&
                !emailPattern.matcher(request.getContactValue())
                        .matches()) {
            throw new ValidationException("Invalid email");
        }
    }

    public void validateGetPreferencesRequestWithContactChannelId(PreferencesGetRequest request) {
        validateBrandCodes(request.getBrandCodes());
    }

    public void validateChannelId(String channelId) {
        if (ObjectUtils.isEmpty(channelId) ||
                !channelIdPattern.matcher(channelId)
                        .matches()) {
            throw new ValidationException("Invalid channelId");
        }
    }
}

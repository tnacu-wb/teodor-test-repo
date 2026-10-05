package uk.co.whitbread.marketing.utils;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.marketing.client.permissionmanagement.model.ContactChannel;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementConfirmDoubleOptIn;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementGetRequest;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementGetResponse;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementUnsubscribeRequest;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementUpdateRequest;
import uk.co.whitbread.marketing.client.permissionmanagement.model.SubscriptionData;
import uk.co.whitbread.marketing.model.BrandPermission;
import uk.co.whitbread.marketing.model.CustomerCountryOfResidence;
import uk.co.whitbread.marketing.model.newsletter.ContactType;
import uk.co.whitbread.marketing.model.newsletter.Permission;
import uk.co.whitbread.marketing.model.newsletter.PreferencesAnonymousGetRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesAnonymousGetResponse;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetResponse;
import uk.co.whitbread.marketing.model.permissionmanagement.SourceDetails;
import uk.co.whitbread.marketing.model.permissionmanagement.UpdatePreferencesRequest;
import uk.co.whitbread.marketing.properties.PermissionManagementApiProperties;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionManagementTransformer {

    private static final String SOURCE_SYSTEM_DELIM = "_";
    private static final String LANGUAGE_DE = "de";
    private static final String LANGUAGE_EN = "en";
    private static final String COUNTRY_DE = "de";
    private static final String COUNTRY_GB = "gb";
    private final PermissionManagementApiProperties permissionManagementApiProperties;

    public static <T> Collection<T> emptyIfNull(Collection<T> collection) {
        return collection == null ? Collections.emptyList() : collection;
    }

    public PermissionManagementGetRequest transformToPermissionManagementGetRequest(PreferencesGetRequest request) {
        var contactChannel = ContactChannel.builder()
                .contactChannelValue(request.getContactValue())
                .contactChannelId(request.getContactChannelId())
                .contactChannelType(request.getContactType().getType())
                .build();
        return PermissionManagementGetRequest.builder()
                .contactChannel(contactChannel)
                .brandCodes(request.getBrandCodes())
                .sourceSystem(permissionManagementApiProperties.getSourceSystem())
                .build();
    }

    public PermissionManagementGetRequest toPermissionManagementGetRequest(
        PreferencesAnonymousGetRequest request) {

        var contactChannel = ContactChannel.builder()
            .contactChannelType(ContactType.email.getType())
            .contactChannelValue(request.getEmailAddress())
            .build();
        return PermissionManagementGetRequest.builder()
            .contactChannel(contactChannel)
            .brandCodes(new String[]{request.getBrandCode()})
            .sourceSystem(permissionManagementApiProperties.getSourceSystem())
            .build();
    }

    public PermissionManagementUpdateRequest transformToPermissionManagementUpdatePermissionsRequest(UpdatePreferencesRequest request, ContactType contactType, String contactTypeValue) {
        var brandCodes = Arrays.stream(request.getBrandCodes()).filter("PINN"::equals).collect(Collectors.toList());
        //todo remove me after front end fix https://whitbreadis.atlassian.net/browse/DNRQ-20459
        var permissionManagementUpdateRequestBuilder = PermissionManagementUpdateRequest.builder()
                .brandCode(brandCodes.toArray(String[]::new))
                .countryOfResidence(request.getCustomer().getCountryOfResidence())
                .title(request.getCustomer().getTitle())
                .firstName(request.getCustomer().getFirstName())
                .lastName(request.getCustomer().getLastName())
                .nationality(request.getCustomer().getNationality())
                .optIn(request.isOptIn())
                .secondPartyContent(request.isSecondPartyOptIn())
                .thiryPartyContent(request.isThirdPartyVendorsOptIn())
                .transactionType("PIBookingOptIn")
                .sourceSystem(permissionManagementApiProperties.getSourceSystem())
                .secondOptInReq(request.isDoubleOptIn())
                .sourceLanguage(Locale.forLanguageTag(request.getCustomer().getLanguage()).getDisplayLanguage())
                .transactionId(UUID.randomUUID().toString())
                .userId(request.getCustomer().getUserId());
        if (ContactType.email.equals(contactType)) {
            permissionManagementUpdateRequestBuilder.email(contactTypeValue);
        } else if (ContactType.phone.equals(contactType)) {
            //todo set phone when altius make available
        } else if (ContactType.email_contact_channel_id.equals(contactType)) {
            permissionManagementUpdateRequestBuilder.emailContactChannelId(contactTypeValue);
        }
        ofNullable(request.getSourceDetails()).ifPresent(
                sourceDetails -> {
                    permissionManagementUpdateRequestBuilder.sourceSystem(constructSourceSystem(sourceDetails));
                });

        return permissionManagementUpdateRequestBuilder.build();
    }

    public String constructSourceSystem(SourceDetails sourceDeatils) {
        String[] sourceValues = {sourceDeatils.getJourney(), sourceDeatils.getChannel(), sourceDeatils.getLocale()};
        return String.join(SOURCE_SYSTEM_DELIM, sourceValues);
    }

    public PermissionManagementConfirmDoubleOptIn transformToPermissionManagementConfirmDoubleOptInRequest(uk.co.whitbread.marketing.model.permissionmanagement.ConfirmDoubleOptInRequest request, ContactType contactType, String contactChannelId) {
        var brandCodes = Arrays.stream(request.getBrandCodes()).filter("PINN"::equals).collect(Collectors.toList());
        //todo remove me after front end fix https://whitbreadis.atlassian.net/browse/DNRQ-20459
        var subscriptionData = SubscriptionData.builder()
                .brandCodes(brandCodes.toArray(String[]::new))
                .contactChannelId(contactChannelId)
                .contactChannelType(contactType.getType());
        if (request.getContactSubType() != null) {
            subscriptionData.contactChannelSubType(request.getContactSubType().getType());
        }
        return PermissionManagementConfirmDoubleOptIn.builder()
                .customerId(request.getCustomerId())
                .sourceSystem(permissionManagementApiProperties.getSourceSystem())
                .subscriptionData(new uk.co.whitbread.marketing.client.permissionmanagement.model.SubscriptionData[]{subscriptionData.build()})
                .build();
    }

    public PermissionManagementUnsubscribeRequest transformToPermissionManagementUnsubscribeRequest(uk.co.whitbread.marketing.model.permissionmanagement.UnsubscribeRequest request, ContactType contactType, String contactChannelId) {
        var subscriptionData = SubscriptionData.builder()
                .brandCodes(request.getBrandCodes())
                .contactChannelId(contactChannelId)
                .contactChannelType(contactType.getType());
        if (request.getContactSubType() != null) {
            subscriptionData.contactChannelSubType(request.getContactSubType().getType());
        }
        return PermissionManagementUnsubscribeRequest.builder()
                .customerId(request.getCustomerId())
                .sourceSystem(permissionManagementApiProperties.getSourceSystem())
                .subscriptionData(new uk.co.whitbread.marketing.client.permissionmanagement.model.SubscriptionData[]{subscriptionData.build()})
                .build();
    }

    public PreferencesGetResponse convertPermissionManagementResponseToGetResponse(
        PermissionManagementGetResponse permissionManagementGetResponse,
        String countryOfResidence, String language) {

        if (StringUtils.isEmpty(countryOfResidence)) {
            countryOfResidence = COUNTRY_GB;
        }
        if (StringUtils.isEmpty(language)) {
            language = LANGUAGE_EN;
        }

        var firstBrandPermission = Optional.ofNullable(
                permissionManagementGetResponse.getBrandPermissions())
            .stream()
            .flatMap(List::stream)
            .findFirst()
            .orElse(new BrandPermission());

        var suppressMarketingCheckbox = shouldSuppressMarketingCheckbox(language,
            firstBrandPermission.isOptIn(), countryOfResidence,
            permissionManagementGetResponse.getCustomerLinks());

        return PreferencesGetResponse.builder()
            .deleted(permissionManagementGetResponse.isDeleted())
            .valid(permissionManagementGetResponse.isIsValid())
            .loyaltyAccounts(permissionManagementGetResponse.getLoyaltyAccounts())
            .permissions(convertBrandPermissions(permissionManagementGetResponse.getBrandPermissions(), suppressMarketingCheckbox))
            .contactChannelId(permissionManagementGetResponse.getContactChannelId())
            .contactChannelValue(permissionManagementGetResponse.getContactChannelValue())
            .build();
    }

    public PreferencesAnonymousGetResponse toPreferencesAnonymousGetResponse(
        PermissionManagementGetResponse permissionManagementGetResponse,
        String countryOfResidence, String language) {

        if (StringUtils.isEmpty(countryOfResidence)) {
            countryOfResidence = COUNTRY_GB;
        }
        if (StringUtils.isEmpty(language)) {
            language = LANGUAGE_EN;
        }

        var firstBrandPermission = Optional.ofNullable(
                permissionManagementGetResponse.getBrandPermissions())
            .stream()
            .flatMap(List::stream)
            .findFirst()
            .orElse(new BrandPermission());

        var suppressMarketingCheckbox = shouldSuppressMarketingCheckbox(language,
            firstBrandPermission.isOptIn(), countryOfResidence,
            permissionManagementGetResponse.getCustomerLinks());

        return PreferencesAnonymousGetResponse.builder()
            .optIn(firstBrandPermission.isOptIn())
            .secondOptIn(firstBrandPermission.isSecondOptIn())
            .secondOptInReq(firstBrandPermission.isSecondOptInReq())
            .secondPartyOptIn(firstBrandPermission.getContentPermission().isSecondParty())
            .thirdPartyVendorsOptIn(firstBrandPermission.getContentPermission().isThirdParty())
            .suppressMarketingCheckbox(suppressMarketingCheckbox)
            .build();
    }

    protected List<Permission> convertBrandPermissions(List<uk.co.whitbread.marketing.model.BrandPermission> brandPermissions,
        boolean suppressMarketingCheckbox) {
        return emptyIfNull(brandPermissions)
                .stream()
                .map(brandPermission -> Permission
                        .builder()
                        .brandCode(brandPermission.getBrandCode())
                        .brand(brandPermission.getBrand())
                        .optIn(brandPermission.isOptIn())
                        .secondPartyOptIn(brandPermission.getContentPermission().isSecondParty())
                        .thirdPartyVendorsOptIn(brandPermission.getContentPermission().isThirdParty())
                        .secondOptInReq(brandPermission.isSecondOptInReq())
                        .secondOptIn(brandPermission.isSecondOptIn())
                        .suppressMarketingCheckbox(suppressMarketingCheckbox)
                        .build())
                .collect(toList());
    }

    private static boolean shouldSuppressMarketingCheckbox(String language, boolean optIn,
        String countryOfResidence, List<CustomerCountryOfResidence> customerLinks) {
        if (LANGUAGE_DE.equals(language)) {
            return !optIn;
        }
        else if (LANGUAGE_EN.equals(language)) {
            boolean isDE = Stream.concat(Stream.of(countryOfResidence), customerLinks == null ?
                Stream.empty() : customerLinks.stream()
                .map(CustomerCountryOfResidence::getCountryOfResidence)
                .map(StringUtils::lowerCase)
            ).anyMatch(COUNTRY_DE::equals);

            return optIn != isDE;
        }

        // Default: do not suppress checkbox
        return false;
    }
}

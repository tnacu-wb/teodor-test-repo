package uk.co.whitbread.marketing.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import uk.co.whitbread.bart.marketing.api.ArrayOfSubscriptionElementSubscriptionElement;
import uk.co.whitbread.bart.marketing.api.Subscribe;
import uk.co.whitbread.bart.marketing.api.SubscribeResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionElement;
import uk.co.whitbread.bart.marketing.api.SubscriptionRequest2015;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatus;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusRequest;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusResponse2015;
import uk.co.whitbread.marketing.client.customerhub.model.ContactChannelData;
import uk.co.whitbread.marketing.client.customerhub.model.ContentPermissionData;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesUpdateRequest;
import uk.co.whitbread.marketing.client.customerhub.model.EditSubscriptionData;
import uk.co.whitbread.marketing.client.customerhub.model.SubscriptionData;
import uk.co.whitbread.marketing.mapper.SubscriptionMapper;
import uk.co.whitbread.marketing.model.EditSubscription;
import uk.co.whitbread.marketing.model.MarketingSubscriptionInfoRequest;
import uk.co.whitbread.marketing.model.MarketingSubscriptionInfoResponse;
import uk.co.whitbread.marketing.model.MarketingSubscriptionRequest;
import uk.co.whitbread.marketing.model.MarketingSubscriptionResponse;
import uk.co.whitbread.marketing.model.NewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetResponse;
import uk.co.whitbread.marketing.model.NewsletterPreferencesUpdateRequest;
import uk.co.whitbread.marketing.model.RegionSubscription;
import uk.co.whitbread.marketing.model.newsletter.ContactType;
import uk.co.whitbread.marketing.model.newsletter.Permission;
import uk.co.whitbread.marketing.model.newsletter.PreferencesEditRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetResponse;
import uk.co.whitbread.marketing.model.permissionmanagement.UpdatePreferencesRequest;
import uk.co.whitbread.marketing.properties.CustomerHubPropertiesLegacy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PROTECTED;

@Slf4j
@Component
@AllArgsConstructor
public class Converter {

    private SubscriptionMapper subscriptionMapper;

    @Getter(PROTECTED)
    private CustomerHubPropertiesLegacy customerHubPropertiesLegacy;

    public static <T> Collection<T> emptyIfNull(Collection<T> collection) {
        return collection == null ? Collections.emptyList() : collection;
    }

    public SubscriptionStatus convert(MarketingSubscriptionInfoRequest request) {
        SubscriptionStatus bartRequest = new SubscriptionStatus();
        SubscriptionStatusRequest innerBartRequest = subscriptionMapper.toSubscriptionStatusRequest(request);
        bartRequest.setSubscriptionStatusRequest(innerBartRequest);
        return bartRequest;
    }

    public MarketingSubscriptionInfoResponse convert(SubscriptionStatusResponse response) {
        MarketingSubscriptionInfoResponse marketingSubscriptionInfoResponse = subscriptionMapper.toSubscriptionStatusRequest(response.getSubscriptionStatusResult());
        List<RegionSubscription> regionSubscriptions = convertRegionsArray(response);
        if (!CollectionUtils.isEmpty(regionSubscriptions)) {
            marketingSubscriptionInfoResponse.setRegionSubscriptions(regionSubscriptions);
        }
        return marketingSubscriptionInfoResponse;
    }

    public Subscribe convert(MarketingSubscriptionRequest request) {
        Subscribe bartRequest = new Subscribe();
        SubscriptionRequest2015 innerBartRequest = subscriptionMapper.toSubscriptionRequest2015(request);
        innerBartRequest.setRegions(convertRegionsToArrayOfSubscriptionElements(request));
        if (innerBartRequest.isIsBusinessTravel() == null) {
            innerBartRequest.setIsBusinessTravel(false);
        }
        bartRequest.setSubscriptionRequest(innerBartRequest);
        return bartRequest;
    }

    public MarketingSubscriptionResponse convert(SubscribeResponse response) {
        return subscriptionMapper.toMarketingSubscriptionRequest(response.getSubscribeResult());
    }

    private ArrayOfSubscriptionElementSubscriptionElement convertRegionsToArrayOfSubscriptionElements(
            MarketingSubscriptionRequest request) {

        List<SubscriptionElement> subscriptionElements = request.getRegionSubscriptions()
                .stream()
                .map(this::convertToSubscriptionElement)
                .collect(Collectors.toList());

        ArrayOfSubscriptionElementSubscriptionElement bartRegions = new ArrayOfSubscriptionElementSubscriptionElement();
        bartRegions.getSubscriptionElement().addAll(subscriptionElements);

        return bartRegions;
    }

    private SubscriptionElement convertToSubscriptionElement(RegionSubscription regionSubscription) {
        SubscriptionElement element = new SubscriptionElement();
        element.setRegionId(regionSubscription.getRegionId());
        element.setSubscribed(regionSubscription.getSubscribed());

        return element;
    }

    private List<RegionSubscription> convertRegionsArray(SubscriptionStatusResponse response) {
        return Optional.ofNullable(response)
                .map(SubscriptionStatusResponse::getSubscriptionStatusResult)
                .map(SubscriptionStatusResponse2015::getRegions)
                .map(ArrayOfSubscriptionElementSubscriptionElement::getSubscriptionElement)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private RegionSubscription convert(SubscriptionElement subscriptionElement) {
        RegionSubscription regionSubscription = new RegionSubscription();
        regionSubscription.setRegionId(subscriptionElement.getRegionId());
        regionSubscription.setSubscribed(subscriptionElement.isSubscribed());
        return regionSubscription;
    }

    public CustomerHubNewsletterPreferencesUpdateRequest convertToCustomerHubNewsletterPrefUpdateReq(String customerId, NewsletterPreferencesUpdateRequest updateRequest) {

        return CustomerHubNewsletterPreferencesUpdateRequest.builder()
                .customerId(customerId)
                .sourceSystem(getCustomerHubPropertiesLegacy().getSourceSystem())
                .updateDateTime(LocalDateTime.now())
                .subscriptionData(mapSubscriptionData(updateRequest))
                .correlationId(updateRequest.getCorrelationId())
                .build();
    }

    protected List<SubscriptionData> mapSubscriptionData(NewsletterPreferencesUpdateRequest updateRequest) {
        return updateRequest.getSubscriptions().stream()
                .map(subscription ->
                        SubscriptionData.builder()
                                .contactChannelType(subscription.getContactType().name())
                                .contactChannelValue(subscription.getContactValue())
                                .contactChannelPermission(subscription.getSubscribe())
                                .brandCode(subscription.getBrandCode().name())
                                .build())
                .collect(Collectors.toList());
    }

    public CustomerHubNewsletterPreferencesEditRequest convertToCustomerHubNewsletterPrefEditReq(NewsletterPreferencesEditRequest editRequest) {

        return CustomerHubNewsletterPreferencesEditRequest.builder()
                .customerId(editRequest.getCustomerId())
                .sourceSystem(getCustomerHubPropertiesLegacy().getSourceSystem())
                .permissionDateTime(LocalDateTime.now())
                .userId(editRequest.getUserId())
                .subscriptionData(mapEditSubscriptionData(editRequest))
                .build();
    }

    public NewsletterPreferencesEditRequest convertToCustomerHubNewsletterPrefEditReq(UpdatePreferencesRequest request, String contactValue, ContactType contactType) {
        var editSubscription = EditSubscription.builder()
                .brandCodes(request.getBrandCodes())
                .contactChannelType(uk.co.whitbread.marketing.model.ContactType.valueOf(contactType.getType()))
                .contactChannelSubType(request.getContactSubType() == null ? null : request.getContactSubType().getType())
                .contactChannelPermission(request.isOptIn())
                .contactChannelValue(contactValue)
                .contentPermission(new EditSubscription.ContentPermission(request.isSecondPartyOptIn(), request.isThirdPartyVendorsOptIn()))
                .build();
        return NewsletterPreferencesEditRequest.builder()
                .customerId(request.getCustomer().getCustomerId())
                .userId(request.getCustomer().getUserId())
                .subscriptionData(Collections.singletonList(editSubscription))
                .build();
    }

    protected List<EditSubscriptionData> mapEditSubscriptionData(NewsletterPreferencesEditRequest editRequest) {
        return editRequest.getSubscriptionData().stream()
                .map(subscription -> removeContactChannelValueIfIdPresent(subscriptionMapper.toEditSubscriptionData(subscription)))
                .collect(Collectors.toList());
    }

    public CustomerHubNewsletterPreferencesGetRequest convertToCustomerHubNewsletterPreferencesGetRequest(NewsletterPreferencesGetRequest request) {
        return CustomerHubNewsletterPreferencesGetRequest.builder()
                .requestId(request.getRequestId())
                .requestedDateTime(LocalDateTime.now())
                .brandCodes(request.getBrandCodes())
                .sourceSystem(getCustomerHubPropertiesLegacy().getSourceSystem())
                .ContactChannel(removeContactChannelValueIfIdPresent(subscriptionMapper.toContactChannelData(request.getContactChannel())))
                .build();

    }

    private ContactChannelData removeContactChannelValueIfIdPresent(ContactChannelData contactChannel) {
        if (!ObjectUtils.isEmpty(contactChannel.getContactChannelValue()) && !ObjectUtils.isEmpty(contactChannel.getContactChannelId())) {
            log.info("Received Get Preferences request with ContactChannelValue and ContactChannelId {} ", contactChannel.getContactChannelId());
            contactChannel.setContactChannelValue(null);
        }
        return contactChannel;
    }

    // Removing ContactChannelValue if ContactChannelValue and ContactChannelId both present.
    // Customer Hub doesn't accept requests which holds both parameters
    private EditSubscriptionData removeContactChannelValueIfIdPresent(EditSubscriptionData subscription) {
        if (!ObjectUtils.isEmpty(subscription.getContactChannelValue()) && !ObjectUtils.isEmpty(subscription.getContactChannelId())) {
            log.info("Received Edit Preferences request with ContactChannelValue and ContactChannelId {} ", subscription.getContactChannelId());
            subscription.setContactChannelValue(null);
        }

        return subscription;
    }

    public CustomerHubNewsletterPreferencesGetRequest convertToCustomerHubPreferencesGetRequest(PreferencesGetRequest request) {
        ContactChannelData contactChannelData = new ContactChannelData();
        contactChannelData.setContactChannelType(request.getContactType().getType());
        contactChannelData.setContactChannelValue(request.getContactValue());
        return CustomerHubNewsletterPreferencesGetRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .requestedDateTime(LocalDateTime.now())
                .brandCodes(request.getBrandCodes())
                .sourceSystem(customerHubPropertiesLegacy.getSourceSystem())
                .ContactChannel(contactChannelData)
                .build();
    }

    public PreferencesGetResponse convertCustomerHubToPreferencesGetResponse(NewsletterPreferencesGetResponse newsletterPreferences) {
        return PreferencesGetResponse.builder()
                .deleted(newsletterPreferences.isDeleted())
                .valid(newsletterPreferences.isIsValid())
                .loyaltyAccounts(newsletterPreferences.getLoyaltyAccounts())
                .permissions(convertBrandPermissions(newsletterPreferences.getBrandPermissions()))
                .build();
    }

    protected List<Permission> convertBrandPermissions(List<uk.co.whitbread.marketing.model.BrandPermission> brandPermissions) {
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
                        .build())
                .collect(toList());
    }

    protected List<EditSubscriptionData> mapEditSubscriptionData(PreferencesEditRequest editRequest,
                                                                 String contactValue, String contactType) {
        EditSubscriptionData editSubscriptionData = new EditSubscriptionData();
        editSubscriptionData.setBrandCodes(editRequest.getBrandCodes());
        editSubscriptionData.setContactChannelValue(contactValue);
        editSubscriptionData.setContactChannelType(contactType);
        String contactSubChannelType = editRequest.getContactSubType() == null ? null : editRequest.getContactSubType().getType();
        editSubscriptionData.setContactChannelSubType(contactSubChannelType);
        editSubscriptionData.setContactChannelPermission(editRequest.isOptIn());
        ContentPermissionData contentPermissionData = new ContentPermissionData();
        contentPermissionData.setSecondParty(editRequest.isSecondPartyOptIn());
        contentPermissionData.setThirdParty(editRequest.isThirdPartyVendorsOnpIn());
        editSubscriptionData.setContentPermission(contentPermissionData);
        return Collections.singletonList(editSubscriptionData);
    }

    public CustomerHubNewsletterPreferencesEditRequest convertToCustomerHubPreferencesEditRequest(
            PreferencesEditRequest editRequest,
            String contactValue,
            String contactType) {
        return CustomerHubNewsletterPreferencesEditRequest.builder()
                .sourceSystem(getCustomerHubPropertiesLegacy().getSourceSystem())
                .permissionDateTime(LocalDateTime.now())
                .userId(editRequest.getUserId())
                .subscriptionData(mapEditSubscriptionData(editRequest, contactValue, contactType))
                .build();
    }

}

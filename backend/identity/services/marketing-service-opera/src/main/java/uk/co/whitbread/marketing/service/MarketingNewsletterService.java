package uk.co.whitbread.marketing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.bart.marketing.api.Subscribe;
import uk.co.whitbread.bart.marketing.api.SubscribeResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatus;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusResponse;
import uk.co.whitbread.marketing.client.bart.BartClient;
import uk.co.whitbread.marketing.model.MarketingSubscriptionInfoRequest;
import uk.co.whitbread.marketing.model.MarketingSubscriptionInfoResponse;
import uk.co.whitbread.marketing.model.MarketingSubscriptionRequest;
import uk.co.whitbread.marketing.model.MarketingSubscriptionResponse;
import uk.co.whitbread.marketing.model.RegionSubscription;
import uk.co.whitbread.marketing.utils.Converter;
import uk.co.whitbread.marketing.utils.RegionSubscriptionUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Deprecated
public class MarketingNewsletterService {

    private final Converter converter;
    private final RegionSubscriptionUtils regionSubscriptionUtils;
    private final BartClient bartClient;

    public MarketingSubscriptionResponse subscribeToNewsletters(MarketingSubscriptionRequest request) {

        log.debug("Called MarketingNewsletterService.subscribeToNewsletters");

        final List<RegionSubscription> regionSubscriptions = regionSubscriptionUtils.populateSubscriptionsFromRegions(request);
        request.setRegionSubscriptions(regionSubscriptions);

        final Subscribe subscriptionRequest = converter.convert(request);

        final SubscribeResponse response = bartClient.subscribeToNewsletters(subscriptionRequest);
        return converter.convert(response);

    }

    public MarketingSubscriptionInfoResponse getSubscriptionInfo(String emailAddress) {

        log.debug("Called MarketingNewsletterService.getSubscriptionInfo");

        MarketingSubscriptionInfoResponse marketingSubscriptionInfoResponse =
                getMarketingSubscriptionInfoResponse(emailAddress, true);

        List<String> regions = regionSubscriptionUtils.extractRegions(marketingSubscriptionInfoResponse.getRegionSubscriptions());
        marketingSubscriptionInfoResponse.setRegions(regions);

        return marketingSubscriptionInfoResponse;
    }

    public MarketingSubscriptionInfoResponse getSubscriptionStatus(String emailAddress) {

        log.debug("Called MarketingNewsletterService.getSubscriptionStatus");

        MarketingSubscriptionInfoResponse marketingSubscriptionInfoResponse =
                getMarketingSubscriptionInfoResponse(emailAddress, false);

        marketingSubscriptionInfoResponse.setEmailAddress(emailAddress);

        return marketingSubscriptionInfoResponse;
    }

    private MarketingSubscriptionInfoResponse getMarketingSubscriptionInfoResponse(String emailAddress, boolean allUserInfo) {

        final SubscriptionStatus subscriptionStatusRequest = converter.convert(new MarketingSubscriptionInfoRequest(emailAddress, allUserInfo));

        SubscriptionStatusResponse response = bartClient.retrieveSubscriptionInfo(subscriptionStatusRequest);

        return converter.convert(response);
    }
}

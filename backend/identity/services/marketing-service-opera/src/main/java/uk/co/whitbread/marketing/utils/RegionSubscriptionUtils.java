package uk.co.whitbread.marketing.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.marketing.model.MarketingSubscriptionRequest;
import uk.co.whitbread.marketing.model.Region;
import uk.co.whitbread.marketing.model.RegionSubscription;
import uk.co.whitbread.marketing.model.RegionsResponse;
import uk.co.whitbread.marketing.service.SharedDataService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RegionSubscriptionUtils {

    private final SharedDataService sharedDataService;

    public List<String> extractRegions(List<RegionSubscription> regionSubscriptions) {
        return Optional.ofNullable(regionSubscriptions).orElse(Collections.emptyList())
                .stream()
                .filter(RegionSubscription::getSubscribed)
                .map(RegionSubscription::getRegionId)
                .collect(Collectors.toList());
    }

    public List<RegionSubscription> populateSubscriptionsFromRegions(MarketingSubscriptionRequest request) {
        if (!CollectionUtils.isEmpty(request.getRegionSubscriptions())) {
            return request.getRegionSubscriptions();
        }

        List<String> regions = Optional.ofNullable(request.getRegions()).orElse(Collections.emptyList());

        return getActiveRegions().stream().map(activeRegion -> map(activeRegion, regions)).collect(Collectors.toList());
    }

    private RegionSubscription map(String activeRegionId, List<String> regions) {
        RegionSubscription regionSubscription = new RegionSubscription();
        regionSubscription.setRegionId(activeRegionId);
        regionSubscription.setSubscribed(regions.contains(activeRegionId));
        return regionSubscription;
    }

    private List<String> getActiveRegions() {
        RegionsResponse regions = sharedDataService.getRegions();

        return Optional.ofNullable(regions).map(RegionsResponse::getRegions)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Region::isActive)
                .map(Region::getId)
                .collect(Collectors.toList());
    }
}

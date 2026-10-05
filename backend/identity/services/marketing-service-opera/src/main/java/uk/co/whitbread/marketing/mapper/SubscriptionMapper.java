package uk.co.whitbread.marketing.mapper;

import java.util.Collections;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.co.whitbread.bart.marketing.api.SubscriptionRequest2015;
import uk.co.whitbread.bart.marketing.api.SubscriptionResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusRequest;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusResponse2015;
import uk.co.whitbread.marketing.client.customerhub.model.ContactChannelData;
import uk.co.whitbread.marketing.client.customerhub.model.EditSubscriptionData;
import uk.co.whitbread.marketing.model.ContactChannel;
import uk.co.whitbread.marketing.model.EditSubscription;
import uk.co.whitbread.marketing.model.MarketingSubscriptionInfoRequest;
import uk.co.whitbread.marketing.model.MarketingSubscriptionInfoResponse;
import uk.co.whitbread.marketing.model.MarketingSubscriptionRequest;
import uk.co.whitbread.marketing.model.MarketingSubscriptionResponse;

@Mapper(componentModel = "spring", uses = {
    RegionMapper.class
})
public interface SubscriptionMapper {

  @Mapping(target = "sessionID", source = "sessionId")
  @Mapping(target = "isBusinessTravel", source = "businessClient")
  @Mapping(target = "receiveRestaurantInfo", source = "restaurantNewsletter")
  @Mapping(ignore = true, target = "regions")
  SubscriptionRequest2015 toSubscriptionRequest2015(MarketingSubscriptionRequest marketingSubscriptionRequest);

  @Mapping(target = "success", source = "successful")
  MarketingSubscriptionResponse toMarketingSubscriptionRequest(SubscriptionResponse subscriptionResponse);

  @Mapping(target = "emailID", source = "emailAddress")
  @Mapping(target = "fullSearch", source = "allUserInfo")
  SubscriptionStatusRequest toSubscriptionStatusRequest(MarketingSubscriptionInfoRequest marketingSubscriptionInfoRequest);

  @Mapping(target = "subscribedStatus", source = "subscribed")
  @Mapping(target = "sessionId", source = "sessionID")
  @Mapping(target = "businessClient", source = "isBusinessTravel")
  @Mapping(target = "restaurantNewsletter", source = "receiveRestaurantInfo")
  @Mapping(ignore = true, target = "regions")
  MarketingSubscriptionInfoResponse toSubscriptionStatusRequest(SubscriptionStatusResponse2015 marketingSubscriptionInfoRequest);

  @AfterMapping
  default void setMarketingSubscriptionInfoResponseDetails(SubscriptionStatusResponse2015 source, @MappingTarget MarketingSubscriptionInfoResponse target) {
    target.setRegions(Collections.emptyList());
  }

  EditSubscriptionData toEditSubscriptionData(EditSubscription editSubscription);

  ContactChannelData toContactChannelData(ContactChannel contactChannel);
}

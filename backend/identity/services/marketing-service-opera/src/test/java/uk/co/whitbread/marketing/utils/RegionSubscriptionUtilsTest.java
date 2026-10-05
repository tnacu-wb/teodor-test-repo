package uk.co.whitbread.marketing.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.marketing.model.MarketingSubscriptionRequest;
import uk.co.whitbread.marketing.model.Region;
import uk.co.whitbread.marketing.model.RegionSubscription;
import uk.co.whitbread.marketing.model.RegionsResponse;
import uk.co.whitbread.marketing.service.SharedDataService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class RegionSubscriptionUtilsTest {

  @Mock
  private SharedDataService mockSharedDataService;

  @InjectMocks
  private RegionSubscriptionUtils underTest;

  @Test
  void shouldIgnoreRegionsWhenRegionSubscriptionsArePresent() {
    //Given
    RegionSubscription regionSubscription = new RegionSubscription();
    regionSubscription.setRegionId("1");
    regionSubscription.setSubscribed(true);

    MarketingSubscriptionRequest request = new MarketingSubscriptionRequest();
    request.setEmailAddress("test@gmail.com");
    request.setRegionSubscriptions(Collections.singletonList(regionSubscription));

    //When
    List<RegionSubscription> regionSubscriptions = underTest.populateSubscriptionsFromRegions(
        request);

    //Then
    assertThat(regionSubscriptions).contains(regionSubscription);
  }

  @Test
  void shouldPopulateRegionSubscriptionsFromRegions() {
    //Given
    MarketingSubscriptionRequest request = new MarketingSubscriptionRequest();
    request.setEmailAddress("test@gmail.com");
    request.setRegionSubscriptions(null);
    request.setRegions(Arrays.asList("1", "3", "5"));

    RegionsResponse regionsResponse = new RegionsResponse();
    regionsResponse.setRegions(Arrays.asList(
        new Region("1", null, true),
        new Region("2", null, true),
        new Region("3", null, true),
        new Region("4", null, true),
        new Region("5", null, true)
    ));
    doReturn(regionsResponse).when(mockSharedDataService).getRegions();

    //When
    List<RegionSubscription> regionSubscriptions = underTest.populateSubscriptionsFromRegions(
        request);

    //Then
    assertThat(regionSubscriptions).contains(
        new RegionSubscription("1", true),
        new RegionSubscription("2", false),
        new RegionSubscription("3", true),
        new RegionSubscription("4", false),
        new RegionSubscription("5", true)
    );
  }

  @Test
  void shouldExtractRegions() {
    //Given
    List<RegionSubscription> regionSubscriptions = Arrays.asList(
        new RegionSubscription("1", true),
        new RegionSubscription("2", false),
        new RegionSubscription("3", true),
        new RegionSubscription("4", false),
        new RegionSubscription("5", true)
    );

    //When
    List<String> regions = underTest.extractRegions(regionSubscriptions);

    //Then

    assertThat(regions).contains("1", "3", "5");

  }
}

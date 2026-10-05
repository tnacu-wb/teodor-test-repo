package uk.co.whitbread.content.utils;

import java.util.List;
import uk.co.whitbread.content.domain.model.apps.homepage.in.AppsHomepageRequest;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageCard;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageContentCard;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageDestinationCard;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageHeading;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageLogo;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageResponse;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsNotification;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out.AppsHomepageCardDto;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out.AppsHomepageContentCardDto;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out.AppsHomepageDestinationCardDto;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out.AppsHomepageLogoDto;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out.AppsHomepageResponseDto;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out.AppsNotificationDto;

public class AppsHomepageUtils {

  public static AppsHomepageRequest createHomepageAppsRequest() {
    return AppsHomepageRequest.builder()
        .country("gb")
        .language("en")
        .channel("PI")
        .build();
  }

  public static AppsHomepageResponse mock_HomepageApps() {
    AppsHomepageDestinationCard destinationCard = mockHomepageAppsDestinationCard();
    AppsHomepageContentCard content = mock_HomepageAppsContentCard();
    AppsHomepageCard promoCard = mock_HomepageAppsCard();
    return AppsHomepageResponse.builder()
        .logo(AppsHomepageLogo.builder().imagePath("logo").build())
        .heading(AppsHomepageHeading.builder().title("title").build())
        .destinationCards(List.of(destinationCard))
        .contentCards(List.of(content))
        .promoCards(List.of(promoCard))
        .notification(AppsNotification.builder().message("mess").type("type").build())
        .build();
  }

  public static AppsHomepageContentCard mock_HomepageAppsContentCard() {
    return AppsHomepageContentCard.builder()
        .imagePath("imagePath")
        .title("title")
        .imageTag("tag")
        .openLinkInApp(true)
        .linkPath("linkPath")
        .subtitle("subtitle")
        .order(2)
        .build();
  }

  public static AppsHomepageCard mock_HomepageAppsCard() {
    return AppsHomepageCard.builder()
        .imagePath("imagePath")
        .title("title")
        .imageTag("tag")
        .openLinkInApp(true)
        .linkPath("linkPath")
        .subtitle("subtitle")
        .order(2)
        .build();
  }

  public static AppsHomepageResponseDto mockHomepageAppsDto() {
    AppsHomepageDestinationCardDto destinationCard = mockHomepageAppsDestinationCardDto();
    AppsHomepageContentCardDto content = mockHomepageAppsContentCardDto();
    AppsHomepageCardDto promoCard = mockHomepageAppsCardDto();
    return AppsHomepageResponseDto.builder()
        .logo(AppsHomepageLogoDto.builder().imagePath("logo").build())
        .heading("title")
        .destinationCards(List.of(destinationCard))
        .contentCards(List.of(content))
        .promoCards(List.of(promoCard))
        .notification(AppsNotificationDto.builder().type("type").message("message").build())
        .build();
  }


  public static AppsHomepageCardDto mockHomepageAppsCardDto() {
    return AppsHomepageCardDto.builder()
        .title("title")
        .imagePath("imagePath")
        .imageTag("tag")
        .openLinkInApp(true)
        .subtitle("subtitle")
        .linkPath("linkPath")
        .order(2)
        .trackingId("id")
        .build();
  }

  public static AppsHomepageDestinationCardDto mockHomepageAppsDestinationCardDto() {
    return AppsHomepageDestinationCardDto.builder()
        .title("title")
        .imagePath("imagePath")
        .imageTag("tag")
        .openLinkInApp(true)
        .subtitle("subtitle")
        .linkPath("linkPath")
        .order(2)
        .trackingId("id")
        .latitude("lat")
        .longitude("long")
        .build();
  }

  public static AppsHomepageContentCardDto mockHomepageAppsContentCardDto() {
    return AppsHomepageContentCardDto.builder()
        .title("title")
        .imagePath("imagePath")
        .imageTag("tag")
        .openLinkInApp(true)
        .subtitle("subtitle")
        .linkPath("linkPath")
        .order(2)
        .trackingId("id")
        .build();
  }

  public static AppsHomepageResponse mockHomepageApps() {
    AppsHomepageDestinationCard destinationCard = mockHomepageAppsDestinationCard();
    AppsHomepageContentCard content = mockHomepageAppsContentCard();
    AppsHomepageCard promoCard = mockHomepageAppsCard();
    return AppsHomepageResponse.builder()
        .logo(AppsHomepageLogo.builder().imagePath("logo").build())
        .heading(AppsHomepageHeading.builder().title("title").build())
        .destinationCards(List.of(destinationCard))
        .contentCards(List.of(content))
        .promoCards(List.of(promoCard))
        .build();
  }

  public static AppsHomepageCard mockHomepageAppsCard() {
    return AppsHomepageCard.builder()
        .imagePath("imagePath")
        .title("title")
        .imageTag("tag")
        .openLinkInApp(true)
        .linkPath("linkPath")
        .subtitle("subtitle")
        .order(2)
        .trackingId("id")
        .build();
  }

  public static AppsHomepageDestinationCard mockHomepageAppsDestinationCard() {
    return AppsHomepageDestinationCard.builder()
        .imagePath("imagePath")
        .title("title")
        .imageTag("tag")
        .openLinkInApp(true)
        .linkPath("linkPath")
        .subtitle("subtitle")
        .order(2)
        .trackingId("id")
        .latitude("lat")
        .longitude("long")
        .build();
  }

  public static AppsHomepageContentCard mockHomepageAppsContentCard() {
    return AppsHomepageContentCard.builder()
        .imagePath("imagePath")
        .title("title")
        .imageTag("tag")
        .openLinkInApp(true)
        .linkPath("linkPath")
        .subtitle("subtitle")
        .order(2)
        .trackingId("id")
        .build();
  }
}
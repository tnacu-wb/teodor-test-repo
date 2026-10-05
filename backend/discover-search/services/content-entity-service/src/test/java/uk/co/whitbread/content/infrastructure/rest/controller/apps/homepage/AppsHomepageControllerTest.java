package uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static uk.co.whitbread.content.utils.AppsHomepageUtils.mockHomepageApps;
import static uk.co.whitbread.content.utils.AppsHomepageUtils.mockHomepageAppsDto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.apps.homepage.in.AppsHomepageRequest;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageResponse;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.in.AppsHomepageRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out.AppsHomepageCardDto;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out.AppsHomepageDestinationCardDto;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out.AppsHomepageResponseDto;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.mapper.ControllerAppsHomepageMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out.AppsHomepageContentCardDto;

@ExtendWith(MockitoExtension.class)
class AppsHomepageControllerTest {

  @InjectMocks
  AppsHomepageController homepageController;

  @Mock
  private ContentInPort contentInPort;

  @Mock
  ControllerAppsHomepageMapper mapper;

  @Test
  void getHomepageApps__ShouldReturnOk() {
    //Arrange
    AppsHomepageRequestDto request = AppsHomepageRequestDto.builder().channel("PI").language("en")
        .country("gb").build();
    AppsHomepageResponse homepageAppsResponse = mockHomepageApps();
    AppsHomepageRequest requestModel = AppsHomepageRequest.builder().country("gb").language("en")
        .channel("PI").build();
    Mockito.when(mapper.toDomainModel(request))
        .thenReturn(requestModel);
    Mockito.when(contentInPort.getAppsHomepage(requestModel)).thenReturn(mockHomepageApps());
    Mockito.when(mapper.toDto(homepageAppsResponse)).thenReturn(mockHomepageAppsDto());

    //Act
    final ResponseEntity<AppsHomepageResponseDto> response = homepageController.getHomepageApps(
        request);

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    final AppsHomepageResponseDto dto = response.getBody();
    assertThat(dto, notNullValue());
    assertEquals("Expected ImagePath is logo", "logo", dto.getLogo().getImagePath());
    assertEquals("Expected Title is title", "title", dto.getHeading());
    assertHomepageAppsDestinationCardDto(dto.getDestinationCards().get(0));
    assertHomepageAppsCardDto(dto.getPromoCards().get(0));
    assertHomepageAppsContentCardDto(dto.getContentCards().get(0));
    assertEquals("Expected notification's message", "message",
        dto.getNotification().getMessage());
    assertEquals("Expected notification's type", "type",
        dto.getNotification().getType());
  }

  private static void assertHomepageAppsCardDto(AppsHomepageCardDto dto) {
    assertEquals("Expected destinationCards imagePath is imagePath", "imagePath",
        dto.getImagePath());
    assertEquals("Expected destinationCards imagePath is imageTag", "tag",
        dto.getImageTag());
    assertEquals("Expected destinationCards linkPath is linkPath", "linkPath",
        dto.getLinkPath());
    assertEquals("Expected destinationCards subtitle is subtitle", "subtitle",
        dto.getSubtitle());
    assertEquals("Expected destinationCards order is order", 2,
        dto.getOrder());
    assertEquals("Expected destinationCards trackingId is trackingId", "id",
        dto.getTrackingId());
  }

  private static void assertHomepageAppsDestinationCardDto(AppsHomepageDestinationCardDto dto) {
    assertEquals("Expected destinationCards imagePath is imagePath", "imagePath",
        dto.getImagePath());
    assertEquals("Expected destinationCards imagePath is imageTag", "tag",
        dto.getImageTag());
    assertEquals("Expected destinationCards linkPath is linkPath", "linkPath",
        dto.getLinkPath());
    assertEquals("Expected destinationCards subtitle is subtitle", "subtitle",
        dto.getSubtitle());
    assertEquals("Expected destinationCards order is order", 2,
        dto.getOrder());
    assertEquals("Expected destinationCards trackingId is trackingId", "id",
        dto.getTrackingId());
    assertEquals("Expected Latitude", "lat", dto.getLatitude());
    assertEquals("Expected Longitude", "long", dto.getLongitude());
  }

  private static void assertHomepageAppsContentCardDto(AppsHomepageContentCardDto dto) {
    assertEquals("Expected destinationCards imagePath is imagePath", "imagePath",
        dto.getImagePath());
    assertEquals("Expected destinationCards imagePath is imageTag", "tag",
        dto.getImageTag());
    assertEquals("Expected destinationCards linkPath is linkPath", "linkPath",
        dto.getLinkPath());
    assertEquals("Expected destinationCards subtitle is subtitle", "subtitle",
        dto.getSubtitle());
    assertEquals("Expected destinationCards order is order", 2,
        dto.getOrder());
    assertEquals("Expected destinationCards trackingId is trackingId", "id",
        dto.getTrackingId());
  }

}
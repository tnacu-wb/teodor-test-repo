package uk.co.whitbread.content.infrastructure.rest.client.meals;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_MEALS_INFO_EXCEPTION;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.meals.in.MealsRequest;
import uk.co.whitbread.content.domain.model.meals.out.MealsInfoResponse;
import uk.co.whitbread.content.domain.model.meals.out.UpsellItems;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.meals.adapter.MealsAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.meals.mapper.MealsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.meals.mapper.MealsResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.meals.model.in.MealsInfoResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.meals.model.in.UpsellItemsAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.meals.model.out.MealsRequestAemDto;

@ExtendWith(MockitoExtension.class)
class MealsOutPortImplTests {

  @InjectMocks
  private MealsOutPortImpl mealsOutPort;

  @Mock
  private MealsAemClient aemClient;

  @Mock
  private MealsRequestMapper mealsRequestMapper;

  @Mock
  private MealsResponseMapper mealsResponseMapper;

  @Test
  void getMeals__ShouldReturnOK() {
    //Arrange
    when(mealsRequestMapper.toDto(any())).thenReturn((createMealsInfoRequestAemDto()));

    when(aemClient.getMealsInfo(createMealsInfoRequestAemDto())).thenReturn(
        mockMealsInfoResponseAemDto());

    when(mealsResponseMapper.toModel(any())).thenReturn(mockMealsInfoResponse());

    //Act
    var mealsAEMResponse = mealsOutPort.getMealsInfo(createMealsInfoRequest());

    //Assert
    assertThat(mealsAEMResponse, notNullValue());
    var mealsResponse = mealsAEMResponse.getUpsellItems().get(0);
    assertThat(mealsResponse.getCode(), is("BFADCT"));
    assertThat(mealsResponse.getName(), is("Continental breakfast"));
    assertThat(mealsResponse.getDescription(), is("description"));
    assertThat(mealsResponse.getImages().get(0), is("/content/dam/images.png"));
    assertThat(mealsResponse.getShow(), is(true));
    assertThat(mealsResponse.getFreeBreakfastOption(), is(false));
    assertThat(mealsResponse.getFreeBreakfastTrigger(), is(false));

  }

  @Test
  void getMeals__ShouldReturnException() {
    //Arrange
    var expectedMessage = "Unable to get meals info.";
    when(aemClient.getMealsInfo(any())).thenThrow(
        new AemResponseException(AEM_MEALS_INFO_EXCEPTION, expectedMessage, new Exception()));
    var request = new MealsRequest("null", "null", "null");
    //Act
    var actual = assertThrows(AemResponseException.class, () ->
        mealsOutPort.getMealsInfo(request)
    );

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_MEALS_INFO_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(AEM_MEALS_INFO_EXCEPTION.getCode()));
  }

  private MealsInfoResponseAemDto mockMealsInfoResponseAemDto() {
    var upsellItems = new UpsellItemsAemDto().builder()
        .code("BFADCT")
        .name("Continental breakfast")
        .description("description")
        .images(Collections.singletonList("/content/dam/images.png"))
        .attachments(Collections.emptyList())
        .show(true)
        .freeBreakfastOption(false)
        .freeBreakfastCode("BFAD")
        .freeBreakfastTrigger(false)
        .build();

    return MealsInfoResponseAemDto.builder()
        .upsellItems(Collections.singletonList(upsellItems))
        .build();

  }

  private MealsInfoResponse mockMealsInfoResponse() {
    var upsellItems = UpsellItems.builder()
        .code("BFADCT")
        .name("Continental breakfast")
        .description("description")
        .images(Collections.singletonList("/content/dam/images.png"))
        .attachments(Collections.emptyList())
        .show(true)
        .freeBreakfastOption(false)
        .freeBreakfastCode("BFAD")
        .freeBreakfastTrigger(false)
        .build();

    return MealsInfoResponse.builder()
        .upsellItems(Collections.singletonList(upsellItems))
        .build();

  }

  private MealsRequest createMealsInfoRequest() {
    return MealsRequest.builder()
        .country("gb")
        .language("en")
        .hotelId("GLASTA")
        .build();
  }

  private MealsRequestAemDto createMealsInfoRequestAemDto() {
    return MealsRequestAemDto.builder()
        .country("gb")
        .language("en")
        .hotelId("GLASTA")
        .build();
  }

}

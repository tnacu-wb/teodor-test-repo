package uk.co.whitbread.content.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.meals.in.MealsRequest;
import uk.co.whitbread.content.domain.model.meals.out.MealsInfoResponse;
import uk.co.whitbread.content.domain.model.meals.out.UpsellItems;
import uk.co.whitbread.content.domain.ports.secondary.MealsOutPort;

@ExtendWith(MockitoExtension.class)
class MealsInPortImplTest {

  @InjectMocks
  private MealsInPortImpl mealsInPort;

  @Mock
  private MealsOutPort mealsOutPort;


  @Test
  void getMealsInfo__ShouldReturnOk() {
    //Arrange
    when(this.mealsOutPort.getMealsInfo(any())).thenReturn(mockMealsInfoResponse());

    //Act
    final var mealsAemResponse = mealsInPort.getMealsInfo(createMealsInfoRequest());

    //Assert
    assertThat(mealsAemResponse, notNullValue());
    var mealsResponse = mealsAemResponse.getUpsellItems().get(0);
    assertThat(mealsResponse.getCode(), is("BFADCT"));
    assertThat(mealsResponse.getName(), is("Continental breakfast"));
    assertThat(mealsResponse.getDescription(), is("description"));
    assertThat(mealsResponse.getImages().get(0), is("/content/dam/images.png"));
    assertThat(mealsResponse.getShow(), is(true));
    assertThat(mealsResponse.getFreeBreakfastOption(), is(false));
    assertThat(mealsResponse.getFreeBreakfastTrigger(), is(false));

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

}

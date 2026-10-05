package uk.co.whitbread.content.infrastructure.rest.client.meals;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_MEALS_INFO_EXCEPTION;

import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.meals.adapter.MealsAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.meals.model.in.MealsInfoResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.meals.model.in.UpsellItemsAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.meals.model.out.MealsRequestAemDto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class MealsAemClientTests {

  @InjectMocks
  private MealsAemClient aemClient;

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Test
  void getMealsInfo__ShouldReturnOK() {

    //Arrange
    MealsRequestAemDto mealsRequestDto = createMealsInfoRequest();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    initWebClient();
    when(responseSpec.bodyToMono(MealsInfoResponseAemDto.class)).thenReturn(
        mockMealsInfoResponse());

    //Act
    final var mealsAEMResponse =
        aemClient.getMealsInfo(mealsRequestDto);

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
  void getMealsInfo_ShouldReturnException() {
    MealsRequestAemDto mealsRequestDto = createMealsInfoRequest();
    initWebClient();
    var exception = new AemResponseException(
        AEM_MEALS_INFO_EXCEPTION,
        "message",
        new Exception());
    when(responseSpec.onStatus(any(), any())).thenThrow(exception);

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () ->  aemClient.getMealsInfo(mealsRequestDto));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_MEALS_INFO_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("message"));
    assertThat(actual.getErrorCode(), is(AEM_MEALS_INFO_EXCEPTION.getCode()));
  }

  private void initWebClient() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
  }

  private Mono<MealsInfoResponseAemDto> mockMealsInfoResponse() {
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

    var mealsResponseAemDto = MealsInfoResponseAemDto.builder()
        .upsellItems(Collections.singletonList(upsellItems))
        .build();

    return Mono.just(mealsResponseAemDto);
  }

  private MealsRequestAemDto createMealsInfoRequest() {
    return MealsRequestAemDto.builder()
        .country("gb")
        .language("en")
        .hotelId("GLASTA")
        .build();
  }
}


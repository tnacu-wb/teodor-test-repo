package uk.co.whitbread.content.infrastructure.rest.client.note.business;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_BUSINESS_NOTE_EXCEPTION;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.adapter.BusinessNotesAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.model.out.BusinessNotesRequestAemDto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class BusinessNotesAemClientTests {

  @InjectMocks
  private BusinessNotesAemClient aemClient;

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
  void getBusinessNotes_ShouldReturnOK() throws IOException {

    BusinessNotesRequestAemDto businessNotesRequest = createBusinessNotesInfoRequest();
    initWebClient();
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
    })).thenReturn(Mono.just(createBusinessNotesInfoResponse()));

    final var businessNotesResponse =
        aemClient.getBusinessNotes(businessNotesRequest);

    assertThat(businessNotesResponse, notNullValue());
    assertThat(businessNotesResponse.getBusinessNotes().size(), is(2));
    assertThat(businessNotesResponse.getHeaders().size(), is(2));
    assertThat(businessNotesResponse.getFooters().size(), is(1));
    var businessNote = businessNotesResponse.getBusinessNotes().get(0);
    var header = businessNotesResponse.getHeaders().get(0);
    var footer = businessNotesResponse.getFooters().get(0);
    assertThat(header.getId(), is("spendAuthorisation"));
    assertThat(header.getLang(), is("en"));
    assertThat(header.getValue(), is("Spend authorisations."));
    assertThat(footer.getId(), is("emailAuthorisation"));
    assertThat(footer.getLang(), is("en"));
    assertThat(footer.getValue(),
        is("Email central payment authorisation not required for this reservation."));
    assertThat(businessNote.getId(), is("carParking"));
    assertThat(businessNote.getLang(), is("en"));
    assertThat(businessNote.getAllow(), is("Parking is authorised."));
    assertThat(businessNote.getDeny(), is("Parking is not authorised."));
  }

  @Test
  void getBusinessNotes_ShouldReturnException() {
    BusinessNotesRequestAemDto businessNotesRequest = createBusinessNotesInfoRequest();
    initWebClient();
    var exception = new AemResponseException(
        AEM_BUSINESS_NOTE_EXCEPTION,
        "message",
        new Exception());
    when(responseSpec.onStatus(any(), any())).thenThrow(exception);

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> aemClient.getBusinessNotes(businessNotesRequest));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_BUSINESS_NOTE_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("message"));
    assertThat(actual.getErrorCode(), is(AEM_BUSINESS_NOTE_EXCEPTION.getCode()));
  }

  private void initWebClient() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
  }

  private Map<String, String> createBusinessNotesInfoResponse() throws IOException {
    HashMap<String, String> response = new HashMap<>();

    response.put("businessNotes.carParking.allow", "Parking is authorised.");
    response.put("businessNotes.carParking.deny", "Parking is not authorised.");
    response.put("businessNotes.ultimateWifi.allow", "Wi-Fi Access is authorised.");
    response.put("businessNotes.ultimateWifi.deny", "Wi-Fi Access is not authorised.");
    response.put("header.spendAuthorisation.value", "Spend authorisations.");
    response.put("header.electronicAuthorisation.value",
        "Business Account electronic authorisation received to charge Room each day to the card securing this reservation.");
    response.put("footer.emailAuthorisation.value",
        "Email central payment authorisation not required for this reservation.");
    return response;
  }

  private BusinessNotesRequestAemDto createBusinessNotesInfoRequest() {
    BusinessNotesRequestAemDto businessNotesRequestAemDto = new BusinessNotesRequestAemDto();
    businessNotesRequestAemDto.setLang("en");
    return businessNotesRequestAemDto;
  }
}


package uk.co.whitbread.basket.infrastructure.rest.client.content;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNote;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNotesResponse;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.model.content.out.Note;
import uk.co.whitbread.basket.generated.models.content.BusinessNotesResponseDto;
import uk.co.whitbread.basket.generated.models.content.HotelPaymentInformationDto;
import uk.co.whitbread.basket.infrastructure.rest.client.content.mapper.ContentResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.content.service.ContentClient;

@ExtendWith(MockitoExtension.class)
class ContentOutPortImplTest {

  public static final String HOTEL_CODE = "DUNGOU";
  public static final String COUNTRY = "gb";
  public static final String LANGUAGE = "en";
  public static final HotelPaymentInformation PAYMENT_INFO = new HotelPaymentInformation();

  @InjectMocks
  ContentOutPortImpl contentOutPort;

  @Mock
  private ContentClient contentClient;

  @Mock
  private ContentResponseMapper contentResponseMapper;

  @BeforeEach
  public void before() {
    contentOutPort = new ContentOutPortImpl(contentClient,
        contentResponseMapper);
  }

  @Test
  void testGetHotelPaymentDetails_success() {
    when(contentClient.getHotelPaymentDetails(HOTEL_CODE, COUNTRY, LANGUAGE)).thenReturn(new HotelPaymentInformationDto());
    when(contentResponseMapper.toModel(new HotelPaymentInformationDto())).thenReturn(PAYMENT_INFO);

    //ACT
    var response = contentOutPort.getHotelPaymentDetails(HOTEL_CODE, COUNTRY, LANGUAGE);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response, equalTo(PAYMENT_INFO));

    verify(contentClient).getHotelPaymentDetails(HOTEL_CODE, COUNTRY, LANGUAGE);
    verify(contentResponseMapper).toModel(new HotelPaymentInformationDto());
  }

  @Test
  void testGetBusinessNotes_success() {
    when(contentClient.getBusinessNotes(LANGUAGE)).thenReturn(new BusinessNotesResponseDto());
    when(contentResponseMapper.toModel(new BusinessNotesResponseDto())).thenReturn(createBusinessNotesResponse());

    //ACT
    var response = contentOutPort.getBusinessNotes(LANGUAGE);

    //Assert
    assertThat(response, notNullValue());
    assertTrue(response.getFooters().get(0).getId().equalsIgnoreCase("footer.emailAuthorisation"));

    verify(contentClient).getBusinessNotes(LANGUAGE);
    verifyNoMoreInteractions(contentClient);

  }

  @Test
  void testGetBusinessNotes_Error() {
    when(contentClient.getBusinessNotes(LANGUAGE)).thenReturn(new BusinessNotesResponseDto());
    when(contentResponseMapper.toModel(new BusinessNotesResponseDto())).thenReturn(createBusinessNotesResponse());

    //ACT
    var response = contentOutPort.getBusinessNotes(LANGUAGE);

    //Assert
    assertThat(response, notNullValue());
    assertTrue(response.getFooters().get(0).getId().equalsIgnoreCase("footer.emailAuthorisation"));

    verify(contentClient).getBusinessNotes(LANGUAGE);
    verifyNoMoreInteractions(contentClient);

  }

  private BusinessNotesResponse createBusinessNotesResponse() {
    Note header = Note.builder()
        .id("header.spendAuthorisation")
        .value("Spend Authorisations")
        .lang("en").build();

    BusinessNote businessNote1 = BusinessNote.builder()
        .deny("Parking is not authorized")
        .allow("Parking is authorized")
        .id("businessAllowance.parking")
        .lang("en")
        .build();


    BusinessNote businessNote2 = BusinessNote.builder()
        .deny("Dinner is not authorized")
        .allow("Dinner at {price} per guest per night is authorized")
        .id("businessAllowance.dinner")
        .lang("en")
        .build();

    BusinessNote businessNote3 = BusinessNote.builder()
        .deny("Wi-Fi Access is not authorized")
        .allow("Wi-Fi Access is authorized")
        .id("businessAllowance.ultimateWifi")
        .lang("en")
        .build();

    Note footer = Note.builder()
        .id("footer.emailAuthorisation")
        .value("Email central payment authorisation not required for this reservation.")
        .lang("en")
        .build();

    return BusinessNotesResponse.builder()
        .headers(List.of(header))
        .businessNotes(List.of(businessNote1, businessNote2, businessNote3))
        .footers(List.of(footer))
        .build();
  }
}

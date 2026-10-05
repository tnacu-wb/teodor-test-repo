package uk.co.whitbread.content.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.logic.mapper.SeoMapper;
import uk.co.whitbread.content.domain.model.booking.in.BookingInformationRequest;
import uk.co.whitbread.content.domain.model.booking.out.BookingFlowStep;
import uk.co.whitbread.content.domain.model.booking.out.BookingInformation;
import uk.co.whitbread.content.domain.model.hotel.in.HotelInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformation;
import uk.co.whitbread.content.domain.model.hotel.out.TopSectionImage;
import uk.co.whitbread.content.domain.model.index.header.data.in.IndexHeaderDataRequest;
import uk.co.whitbread.content.domain.model.index.header.data.in.LocalizationRequest;
import uk.co.whitbread.content.domain.model.index.header.data.out.Content;
import uk.co.whitbread.content.domain.model.index.header.data.out.Favicon;
import uk.co.whitbread.content.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.content.domain.model.index.header.data.out.Seo;
import uk.co.whitbread.content.domain.model.searchresults.data.out.SearchResultsData;
import uk.co.whitbread.content.domain.model.seo.exception.MissingRequiredFieldException;
import uk.co.whitbread.content.domain.model.seo.exception.UnsupportedPageException;
import uk.co.whitbread.content.domain.model.seo.in.SeoRequest;
import uk.co.whitbread.content.domain.model.seo.out.SeoResponse;
import uk.co.whitbread.content.domain.ports.secondary.BookingOutPort;
import uk.co.whitbread.content.domain.ports.secondary.ContentOutPort;

@ExtendWith(MockitoExtension.class)
class SeoInPortImplTest {

  private static final String COUNTRY = "en";
  private static final String LANGUAGE = "gb";
  private static final String HOTEL_ID = "hotelId";
  private static final String HOME_PAGE = "home";
  private static final String HDP = "hdp";
  private static final String SRP = "srp";
  private static final String BOOKING_FLOW_ID = "booking-a1";
  private static final String GDP_TITLE = "Guest details page";
  private static final String CONFIRMATION_PAGE_TITLE = "Confirmation";

  @InjectMocks
  private SeoInPortImpl seoInPort;

  @Mock
  private ContentOutPort contentOutPort;

  @Mock
  private BookingOutPort bookingOutPort;

  @Mock
  private SeoMapper seoMapper;

  @Test
  void getGeneralPagesSeoInformation_success() {
    when(contentOutPort.getIndexHeaderData(mockIndexHeaderDataRequest())).thenReturn(mockIndexHeaderData());
    when(seoMapper.toGeneralPageModel(mockIndexHeaderData()))
        .thenReturn(SeoResponse.builder().pageTitle(HOME_PAGE).build());
    //Act
    var aemResponse = seoInPort.getSeoInformation(mockGeneralPageRequest());

    assertThat(aemResponse, notNullValue());
  }

  @Test
  void getBookingFlowPagesSeoInformation_success() {
    when(contentOutPort.getIndexHeaderData(mockIndexHeaderDataRequest())).thenReturn(mockIndexHeaderData());
    when(bookingOutPort.getBookingInformation(mockBookingInformationRequest())).thenReturn(mockBookingInformation());
    when(seoMapper.toBookingFlowPagesSeoModel(mockIndexHeaderData(), CONFIRMATION_PAGE_TITLE))
        .thenReturn(SeoResponse.builder().pageTitle(CONFIRMATION_PAGE_TITLE).build());
    //Act
    var aemResponse = seoInPort.getSeoInformation(mockBookingFlowPageRequest());

    assertThat(aemResponse, notNullValue());
  }

  @Test
  void getBookingFlowGdpPageSeoInformation_success() {
    when(contentOutPort.getIndexHeaderData(mockIndexHeaderDataRequest())).thenReturn(mockIndexHeaderData());
    when(bookingOutPort.getBookingInformation(mockBookingInformationRequest())).thenReturn(mockBookingInformation());
    when(seoMapper.toBookingFlowPagesSeoModel(mockIndexHeaderData(), GDP_TITLE))
        .thenReturn(SeoResponse.builder().pageTitle(GDP_TITLE).build());
    //Act
    var aemResponse = seoInPort.getSeoInformation(mockGdpFlowPageRequest());

    assertThat(aemResponse, notNullValue());
  }

  @Test
  void getSeoInformation_throwsUnsupportedPageException() {

    assertThrows(UnsupportedPageException.class,
        () -> seoInPort.getSeoInformation(SeoRequest.builder().page("page").build()));
  }

  @Test
  void getHdpInformation_throwsMissingHotelIdRequestParam() {

    assertThrows(MissingRequiredFieldException.class,
        () -> seoInPort.getSeoInformation(SeoRequest.builder().page("hdp").build()));
  }

  @Test
  void getSrpSeoInformation_success() {
    when(contentOutPort.getIndexHeaderData(mockIndexHeaderDataRequest())).thenReturn(mockIndexHeaderData());
    when(contentOutPort.getSearchResultsData(mockLocalizationRequest())).thenReturn(mockSearchResultData());
    when(seoMapper.toSrpSeoModel(mockIndexHeaderData(), mockSearchResultData()))
        .thenReturn(SeoResponse.builder().pageTitle(SRP).build());
    //Act
    var aemResponse = seoInPort.getSeoInformation(mockSrpSeoRequest());

    assertThat(aemResponse, notNullValue());
  }

  @Test
  void getHdpSeoInformation_success() {
    when(contentOutPort.getIndexHeaderData(mockIndexHeaderDataRequest())).thenReturn(mockIndexHeaderData());
    when(contentOutPort.getHotelInformation(mockHotelInformationRequest(), false)).thenReturn(mockHotelInformationResponse());
    when(seoMapper.toHdpSeoModel(mockIndexHeaderData(), mockHotelInformationResponse()))
        .thenReturn(SeoResponse.builder().pageTitle(HDP).build());
    //Act
    var aemResponse = seoInPort.getSeoInformation(mockHdpSeoRequest());

    assertThat(aemResponse, notNullValue());
  }

  private IndexHeaderDataRequest mockIndexHeaderDataRequest() {
    return IndexHeaderDataRequest.builder()
        .language(LANGUAGE)
        .country(COUNTRY)
        .build();
  }

  private HotelInformationRequest mockHotelInformationRequest() {
    return HotelInformationRequest.builder()
        .hotelId(HOTEL_ID)
        .language(LANGUAGE)
        .country(COUNTRY)
        .build();
  }

  private SeoRequest mockHdpSeoRequest() {
    return SeoRequest.builder()
        .country(COUNTRY)
        .hotelId(HOTEL_ID)
        .language(LANGUAGE)
        .page(HDP)
        .build();
  }

  private SeoRequest mockSrpSeoRequest() {
    return SeoRequest.builder()
        .country(COUNTRY)
        .language(LANGUAGE)
        .page(SRP)
        .build();
  }

  private SeoRequest mockBookingFlowPageRequest() {
    return SeoRequest.builder()
        .country(COUNTRY)
        .bookingFlowId(BOOKING_FLOW_ID)
        .hotelId(HOTEL_ID)
        .language(LANGUAGE)
        .page("confirmation")
        .build();
  }

  private SeoRequest mockGdpFlowPageRequest() {
    return SeoRequest.builder()
        .country(COUNTRY)
        .bookingFlowId(BOOKING_FLOW_ID)
        .hotelId(HOTEL_ID)
        .language(LANGUAGE)
        .page("gdp")
        .build();
  }

  private SeoRequest mockGeneralPageRequest() {
    return SeoRequest.builder()
        .country(COUNTRY)
        .bookingFlowId(BOOKING_FLOW_ID)
        .hotelId(HOTEL_ID)
        .language(LANGUAGE)
        .page("home")
        .build();
  }

  private IndexHeaderData mockIndexHeaderData() {
    return IndexHeaderData.builder()
        .content(Content.builder()
            .seo(Seo.builder().build())
            .favicon(Favicon.builder().build())
            .build())
        .build();
  }

  private HotelInformation mockHotelInformationResponse() {
    return HotelInformation.builder()
        .hotelId(HOTEL_ID)
        .topSectionImages(List.of(TopSectionImage.builder().build()))
        .pageDescription("pageDescription")
        .pageTitle(HDP)
        .build();
  }

  private LocalizationRequest mockLocalizationRequest() {
    return LocalizationRequest.builder()
        .country(COUNTRY)
        .language(LANGUAGE)
        .build();
  }

  private SearchResultsData mockSearchResultData() {
    return SearchResultsData.builder()
        .content(uk.co.whitbread.content.domain.model.searchresults.data.out.Content.builder()
        .seo(uk.co.whitbread.content.domain.model.searchresults.data.out.Seo.builder()
            .pageDescription("pageDescription")
            .pageTitle("Search result page")
            .cardImageUrl("cardImg")
            .build())
        .build()).build();
  }

  private BookingInformationRequest mockBookingInformationRequest() {
    return BookingInformationRequest.builder()
        .bookingFlowId(BOOKING_FLOW_ID)
        .hotelId(HOTEL_ID)
        .country(COUNTRY)
        .language(LANGUAGE)
        .build();
  }

  private BookingInformation mockBookingInformation() {
    return BookingInformation.builder()
        .bookingFlowSteps(List.of(
            BookingFlowStep.builder()
                .id("guestDetails")
                .title("Guest details page")
            .build(),
                BookingFlowStep.builder()
                .id("confirmation")
                .title("Confirmation")
                .build()))
        .build();
  }

}

package uk.co.whitbread.content.domain.logic;

import static uk.co.whitbread.content.domain.model.ErrorCode.DIGITAL_SEO_MISSING_BOOKING_FLOW_ID;
import static uk.co.whitbread.content.domain.model.ErrorCode.DIGITAL_SEO_MISSING_HOTEL_ID_REQUIRED_FIELD;
import static uk.co.whitbread.content.domain.model.ErrorCode.DIGITAL_SEO_MISSING_REQUIRED_FIELD;
import static uk.co.whitbread.content.domain.model.ErrorCode.DIGITAL_SEO_UNSUPORTED_PAGE_EXCEPTION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.domain.logic.mapper.SeoMapper;
import uk.co.whitbread.content.domain.model.booking.in.BookingInformationRequest;
import uk.co.whitbread.content.domain.model.booking.out.BookingFlowStep;
import uk.co.whitbread.content.domain.model.hotel.in.HotelInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformation;
import uk.co.whitbread.content.domain.model.index.header.data.in.IndexHeaderDataRequest;
import uk.co.whitbread.content.domain.model.index.header.data.in.LocalizationRequest;
import uk.co.whitbread.content.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.content.domain.model.searchresults.data.out.SearchResultsData;
import uk.co.whitbread.content.domain.model.seo.exception.MissingRequiredFieldException;
import uk.co.whitbread.content.domain.model.seo.exception.UnsupportedPageException;
import uk.co.whitbread.content.domain.model.seo.in.SeoRequest;
import uk.co.whitbread.content.domain.model.seo.out.SeoResponse;
import uk.co.whitbread.content.domain.ports.primary.SeoInPort;
import uk.co.whitbread.content.domain.ports.secondary.BookingOutPort;
import uk.co.whitbread.content.domain.ports.secondary.ContentOutPort;

@Slf4j
@RequiredArgsConstructor
public class SeoInPortImpl implements SeoInPort {

  private final ContentOutPort contentOutPort;
  private final BookingOutPort bookingOutPort;

  private final SeoMapper seoMapper;

  @SuppressWarnings("squid:S1301")
  @Override
  public SeoResponse getSeoInformation(SeoRequest seoRequest) {
    return switch (seoRequest.getPage().toLowerCase()) {
      case "hdp" -> getHdpInformation(seoRequest);
      case "srp" -> getSrpInformation(seoRequest);
      case "ancillaries", "payment", "gdp", "confirmation" ->
          getBookingFlowPagesInformation(seoRequest);
      case "home", "amend", "dashboard", "cyb", "register", "cyt" ->
          getGeneralPagesInformation(seoRequest);
      default -> {
        var ex = new UnsupportedPageException(DIGITAL_SEO_UNSUPORTED_PAGE_EXCEPTION,
            String.format("SEO Information not supported for page: %s", seoRequest.getPage()));
        ExceptionLogger.log(log, ex);
        throw ex;
      }
    };
  }

  private SeoResponse getHdpInformation(SeoRequest seoRequest) {
    if (StringUtils.isBlank(seoRequest.getHotelId())) {
      var ex = new MissingRequiredFieldException(DIGITAL_SEO_MISSING_REQUIRED_FIELD,
          "hotelId mandatory for this page");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    HotelInformation hotelInformation = contentOutPort.getHotelInformation(
        HotelInformationRequest.builder().hotelId(seoRequest.getHotelId())
            .country(seoRequest.getCountry()).language(seoRequest.getLanguage())
            .build(), false);

    IndexHeaderData indexHeaderData = contentOutPort.getIndexHeaderData(
        IndexHeaderDataRequest.builder().country(seoRequest.getCountry()).language(
            seoRequest.getLanguage()).build());

    return seoMapper.toHdpSeoModel(indexHeaderData, hotelInformation);
  }

  private SeoResponse getSrpInformation(SeoRequest seoRequest) {
    SearchResultsData searchResultsData = contentOutPort.getSearchResultsData(LocalizationRequest
        .builder().country(seoRequest.getCountry()).language(seoRequest.getLanguage()).build());

    IndexHeaderData indexHeaderData = contentOutPort.getIndexHeaderData(IndexHeaderDataRequest.builder()
        .country(seoRequest.getCountry()).language(seoRequest.getLanguage()).build());

    return seoMapper.toSrpSeoModel(indexHeaderData, searchResultsData);
  }

  private SeoResponse getGeneralPagesInformation(SeoRequest seoRequest) {
    IndexHeaderData indexHeaderData = contentOutPort.getIndexHeaderData(
        IndexHeaderDataRequest.builder().country(seoRequest.getCountry()).language(
            seoRequest.getLanguage()).build());

    return seoMapper.toGeneralPageModel(indexHeaderData);
  }

  private SeoResponse getBookingFlowPagesInformation(SeoRequest seoRequest) {
    IndexHeaderData indexHeaderData = contentOutPort.getIndexHeaderData(
        IndexHeaderDataRequest.builder().country(seoRequest.getCountry()).language(
            seoRequest.getLanguage()).build());

    var title = getBookingFlowPagesTitle(seoRequest);

    return seoMapper.toBookingFlowPagesSeoModel(indexHeaderData, title);
  }

  private String getBookingFlowPagesTitle(SeoRequest seoRequest) {
    if (StringUtils.isBlank(seoRequest.getHotelId())) {
      var ex = new MissingRequiredFieldException(DIGITAL_SEO_MISSING_HOTEL_ID_REQUIRED_FIELD,
          "hotelId mandatory for this page");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    if (StringUtils.isBlank(seoRequest.getBookingFlowId())) {
      var ex = new MissingRequiredFieldException(DIGITAL_SEO_MISSING_BOOKING_FLOW_ID,
          "bookingFlowId mandatory for this page");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    var bookingFlowInformation = bookingOutPort.getBookingInformation(
        BookingInformationRequest.builder().bookingFlowId(seoRequest.getBookingFlowId())
            .country(seoRequest.getCountry()).language(seoRequest.getLanguage())
            .hotelId(seoRequest.getHotelId()).build());

    if (seoRequest.getPage().equalsIgnoreCase("gdp")) {
      return bookingFlowInformation.getBookingFlowSteps().stream()
          .filter(bookingFlowStep -> bookingFlowStep.getId().equalsIgnoreCase("guestDetails"))
          .findFirst().map(BookingFlowStep::getTitle)
          .orElse(null);
    }

    return bookingFlowInformation.getBookingFlowSteps().stream()
        .filter(bookingFlowStep -> bookingFlowStep.getId().equalsIgnoreCase(seoRequest.getPage()))
        .findFirst()
        .map(BookingFlowStep::getTitle)
        .orElse(null);
  }
}

package uk.co.whitbread.dashboard.domain.logic.chain;

import static uk.co.whitbread.dashboard.domain.logic.utils.ActionsUtils.DE_COUNTRY_CODE;
import static uk.co.whitbread.dashboard.domain.logic.utils.ActionsUtils.DE_LANGUAGE_CODE;
import static uk.co.whitbread.dashboard.domain.logic.utils.ActionsUtils.GB_COUNTRY_CODE;
import static uk.co.whitbread.dashboard.domain.logic.utils.ActionsUtils.assignActions;
import static uk.co.whitbread.dashboard.domain.logic.utils.DashboardUtils.getHotelImage;
import static uk.co.whitbread.dashboard.domain.logic.utils.DashboardUtils.isArrivalDateWithinRange;
import static uk.co.whitbread.dashboard.domain.logic.utils.DashboardUtils.setRoomsAndGuests;
import static uk.co.whitbread.dashboard.domain.model.out.DashboardType.UPCOMING_BOOKING;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.CdhReservationSearchDto;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.ResultsDto;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.HotelInfo;
import uk.co.whitbread.dashboard.domain.logic.mapper.AddressMapper;
import uk.co.whitbread.dashboard.domain.logic.mapper.MapMapper;
import uk.co.whitbread.dashboard.domain.logic.utils.DashboardUtils;
import uk.co.whitbread.dashboard.domain.model.RetrieveDashboardChainRequest;
import uk.co.whitbread.dashboard.domain.model.in.ManageBookingResponse;
import uk.co.whitbread.dashboard.domain.model.in.RoomType;
import uk.co.whitbread.dashboard.domain.model.out.Content;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;
import uk.co.whitbread.dashboard.domain.ports.secondary.CdhReservationsOutPort;
import uk.co.whitbread.dashboard.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.dashboard.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.dashboard.domain.ports.secondary.HotelReservationsOutPort;
import uk.co.whitbread.dashboard.domain.properties.DashboardProperties;

@Slf4j
@RequiredArgsConstructor
@Order(1)
@Component
public class UpcomingBookingChain extends DashboardChain {

  private final CdhReservationsOutPort cdhReservationsOutPort;
  private final HotelInfoOutPort hotelInfoOutPort;
  private final ContentOutPort contentOutPort;
  private final HotelReservationsOutPort hotelReservationsOutPort;
  private final DashboardProperties properties;
  private final AddressMapper addressMapper;
  private final MapMapper mapMapper;

  @Override
  protected void get(RetrieveDashboardChainRequest request, DashboardElement response) {

    ResultsDto reservationDetails = getReservationIfRequired(request);

    if (reservationDetails == null) {
      return;
    }

    HotelInfo hotelInfo = hotelInfoOutPort.getHotelInfo(reservationDetails.getHotelCode());

    log.info("Adding Upcoming Booking element.");
    response.setType(UPCOMING_BOOKING);

    Content content = populateContent(request, hotelInfo, reservationDetails);
    setRoomsInfo(request, hotelInfo, content, reservationDetails);
    assignActions(content, properties, getManageBookingResponse(request, reservationDetails),
        request.isBusiness(), request.getLanguage(), reservationDetails.getStatus());
    response.setContent(content);
  }

  private void setRoomsInfo(RetrieveDashboardChainRequest request, HotelInfo hotelInfo, Content content,
      ResultsDto reservationDetails) {
    String country = DE_LANGUAGE_CODE.equalsIgnoreCase(request.getLanguage()) ? DE_COUNTRY_CODE : GB_COUNTRY_CODE;
    RoomType roomTypes = contentOutPort.getRoomTypes(country, request.getLanguage(), hotelInfo.getBrand());
    setRoomsAndGuests(content, reservationDetails, roomTypes);
  }

  private ManageBookingResponse getManageBookingResponse(RetrieveDashboardChainRequest request,
      ResultsDto reservationDetails) {
    var booking = hotelReservationsOutPort.findBooking(request.getSurname(), request.getArrivalDate(),
        request.getConfirmationNumber(), request.getLanguage());

    if (null != booking) {
      return hotelReservationsOutPort.getManageBookingInfo(booking.getBasketReference(),
          reservationDetails.getHotelCode(), booking.getToken(), request.getLanguage(), LocalDateTime.now());
    } else {
      log.info("No booking found for confirmation number: {}", request.getConfirmationNumber());
    }
    return new ManageBookingResponse();
  }

  private ResultsDto getReservationIfRequired(RetrieveDashboardChainRequest request) {
    ResultsDto reservation = null;
    
    boolean shouldFetchBooking = isArrivalDateWithinRange(request.getArrivalDate(),
        properties.getMaxDays());
    
    if (shouldFetchBooking) {
      log.debug("Fetching booking {}", request.getConfirmationNumber());
      
      CdhReservationSearchDto reservationInfoResponse = cdhReservationsOutPort
          .retrieveBooking(request.getConfirmationNumber());
      
      if (reservationInfoResponse != null && CollectionUtils.isNotEmpty(reservationInfoResponse.getResults())) {
        reservation = reservationInfoResponse.getResults().get(0);
      }
    }
    
    return reservation;
  }

  private Content populateContent(RetrieveDashboardChainRequest request,
      HotelInfo infoResponse, ResultsDto reservationDetails) {
    Content content = new Content();
    content.setHotelImage(getHotelImage(infoResponse, properties.getEnvironment()));
    content.setHotelName(infoResponse.getName());
    content.setHotelBrand(infoResponse.getBrand());
    content.setHotelCode(reservationDetails.getHotelCode());
    content.setMap(mapMapper.infoMapToMap(infoResponse.getMap()));
    content.setAddress(addressMapper.infoAddressToAddress(infoResponse.getAddress()));
    content.setConfirmationNumber(request.getConfirmationNumber());

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate departureDate = null;
    LocalDate arrivalDate = null;
    try {
      arrivalDate = reservationDetails.getArrivalDate() != null
          ? LocalDate.parse((splitDateTime(reservationDetails.getArrivalDate())), formatter) : null;
    } catch (DateTimeParseException e) {
      log.error("Error parsing arrival date: {}", e.getMessage());
    }
    try {
      departureDate = reservationDetails.getDepartureDate() != null
          ? LocalDate.parse(splitDateTime(reservationDetails.getDepartureDate()), formatter) : null;
    } catch (DateTimeParseException e) {
      log.error("Error parsing departure date: {}", e.getMessage());
    }
    content.setArrivalDate(arrivalDate);
    content.setDepartureDate(departureDate);
    content.setCheckedIn(DashboardUtils.isCheckedIn(reservationDetails.getStatus()));
    return content;
  }
  
  private String splitDateTime(String date) {
    
    String[] splitDate = date.split("T");
    return splitDate[0];
  }
}

package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch;

import static uk.co.whitbread.cdh.infrastructure.util.Utils.sanitizeInputString;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.domain.ports.primary.CdhReservationSearchInPort;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.mapper.CdhReservationSearchCriteriaDtoMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.mapper.ReservationInvoicesResponseDtoMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.mapper.ReservationSearchDtoMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.in.BookingInvoiceRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.in.CdhReservationSearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out.CdhReservationSearchDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out.ReservationInvoicesResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/cdh")
public class CdhReservationSearchController implements
    CdhReservationSearchControllerApiDocumentation {

  private final CdhReservationSearchCriteriaDtoMapper cdhReservationSearchCriteriaDtoMapper;
  private final CdhReservationSearchInPort cdhReservationSearchInPort;
  private final ReservationSearchDtoMapper reservationSearchDtoMapper;
  private final ReservationInvoicesResponseDtoMapper reservationInvoicesResponseDtoMapper;

  @PostMapping(value = "/reservation/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public CdhReservationSearchDto getReservationSearch(
      @Valid @RequestBody CdhReservationSearchCriteriaDto cdhReservationSearchCriteriaDto) {

    log.info("Request to get reservation details for hotelId: {}",
        sanitizeInputString(cdhReservationSearchCriteriaDto.getHotelCode()));
    var domainReservationSearchCriteria = cdhReservationSearchCriteriaDtoMapper.toModel(
        cdhReservationSearchCriteriaDto);

    var reservationSearchResponse = cdhReservationSearchInPort.getReservationSearch(
        domainReservationSearchCriteria);

    return reservationSearchDtoMapper.toDto(reservationSearchResponse);
  }

  @GetMapping(value = "/reservation/{reservationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public CdhReservationSearchDto getReservationById(
      @PathVariable("reservationId") String reservationId) {
    log.info("Request to get reservation details by Id for a reservation Id: {}",
        sanitizeInputString(reservationId));
    ReservationSearch reservationSearch = cdhReservationSearchInPort.getReservationById(
        reservationId);
    return reservationSearchDtoMapper.toDto(reservationSearch);
  }

  @GetMapping(value = "/reservation/invoices", produces = MediaType.APPLICATION_JSON_VALUE)
  public ReservationInvoicesResponseDto getBookingInvoices(
      @RequestParam("bookingReference") String bookingReference,
      @ParameterObject @Valid BookingInvoiceRequestDto bookingInvoiceRequestDto) {
    log.info("Request to get booking invoices for bookingReference: {}",
        sanitizeInputString(bookingReference));

    var bookingInvoices = cdhReservationSearchInPort.getBookingInvoices(bookingReference,
        bookingInvoiceRequestDto.getAccessedBy(), bookingInvoiceRequestDto.getAccessContext());

    return reservationInvoicesResponseDtoMapper.toDto(bookingInvoices);
  }

}

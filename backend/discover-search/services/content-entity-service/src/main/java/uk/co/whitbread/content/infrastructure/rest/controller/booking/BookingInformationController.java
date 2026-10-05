package uk.co.whitbread.content.infrastructure.rest.controller.booking;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.content.domain.ports.primary.BookingInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper.BookingInformationDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper.BookingInformationRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper.RateInformationDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper.RateInformationRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.in.BookingInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.in.RateInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out.BookingInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out.RateInformationDto;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BookingInformationController implements BookingInformationApiDocumentation {

  private final BookingInPort bookingInPort;
  private final BookingInformationDtoMapper bookingDtoMapper;
  private final BookingInformationRequestDtoMapper bookingInformationRequestDtoMapper;
  private final RateInformationDtoMapper rateInformationDtoMapper;
  private final RateInformationRequestDtoMapper rateInformationRequestDtoMapper;

  @GetMapping(value = BOOKING_INFO_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BookingInformationDto> getBookingInformation(@Valid @ParameterObject
      BookingInformationRequestDto bookingInformationRequestDto) {

    var request = bookingInformationRequestDtoMapper.toDomainModel(bookingInformationRequestDto);
    var bookingInformationDto = bookingDtoMapper.toDto(
        bookingInPort.getBookingInformation(request));
    return ResponseEntity.status(HttpStatus.OK).body(bookingInformationDto);
  }

  @GetMapping(value = BOOKING_INFO_PATH
      + "/rateInformation", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RateInformationDto> getRateInformation(@Valid @ParameterObject
      RateInformationRequestDto rateInformationRequestDto) {
    var request = rateInformationRequestDtoMapper.toDomainModel(rateInformationRequestDto);
    var rateInformationDto = rateInformationDtoMapper.toDto(
        bookingInPort.getRateInformation(request));
    return ResponseEntity.status(HttpStatus.OK).body(rateInformationDto);
  }

  @GetMapping(value = BOOKING_INFO_PATH + "/hotelRateInformation",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RateInformationDto> getHotelRateInformation(@Valid @ParameterObject
      RateInformationRequestDto rateInformationRequestDto) {
    var request = rateInformationRequestDtoMapper.toDomainModel(rateInformationRequestDto);
    var rateInformationDto = rateInformationDtoMapper.toDto(
        bookingInPort.getHotelRateInformation(request));
    return ResponseEntity.status(HttpStatus.OK).body(rateInformationDto);
  }
}

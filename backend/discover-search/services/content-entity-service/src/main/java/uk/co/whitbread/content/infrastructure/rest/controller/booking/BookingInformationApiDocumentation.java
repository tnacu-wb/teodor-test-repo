package uk.co.whitbread.content.infrastructure.rest.controller.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.in.BookingInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.in.RateInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out.BookingInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out.RateInformationDto;

public interface BookingInformationApiDocumentation {

  String BOOKING_INFO_PATH = "/v1/content/booking";

  @Operation(summary = "Retrieves Booking Information")
  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = BOOKING_INFO_PATH,
      operation = @Operation(summary = "Retrieves Booking Information",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
                  description = "Country to retrieve booking information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
                  description = "Language to retrieve booking information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "bookingFlowId", example = "booking-nm-a1",
                  description =
                      "bookingId from bookingFlowItems object from complete.data endpoint for every RATE available",
                  required = true),
          },
          responses = {
              @ApiResponse(responseCode = "200", description = "Success",
                  content = @Content(mediaType = "application/json",
                      schema = @Schema(implementation = BookingInformationDto.class))),
              @ApiResponse(responseCode = "400", description = "Error Occurred",
                  content = {
                      @Content(mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class))}),
              @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponse.class))}),
              @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponse.class))})
          }))
  })
  ResponseEntity<BookingInformationDto> getBookingInformation(
      @Valid @ParameterObject BookingInformationRequestDto bookingInformationRequest);

  @Operation(summary = "Retrieves Rate Information")
  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = BOOKING_INFO_PATH + "/rateInformation",
      operation = @Operation(summary = "Retrieves Rate Information",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
                  description = "Country to retrieve rate information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
                  description = "Language to retrieve rate information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "hotelId", example = "LONEUS",
                  description = "Id of the hotel to retrieve rate information", required = true),
          },
          responses = {
              @ApiResponse(responseCode = "200", description = "Success",
                  content = @Content(mediaType = "application/json",
                      schema = @Schema(implementation = RateInformationDto.class))),
              @ApiResponse(responseCode = "400", description = "Error Occurred",
                  content = {
                      @Content(mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class))}),
              @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponse.class))}),
              @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponse.class))})
          }))
  })
  ResponseEntity<RateInformationDto> getRateInformation(
      @Valid @ParameterObject RateInformationRequestDto rateInformationRequestDto);


  @Operation(summary = "Retrieves Hotel Rate Information from AEM")
  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = BOOKING_INFO_PATH + "/rateHotelInformation",
      operation = @Operation(summary = "Retrieves Hotel Rate Information from AEM",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
                  description = "Country to retrieve rate information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
                  description = "Language to retrieve rate information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "hotelId", example = "LONEUS",
                  description = "Id of the hotel to retrieve rate information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "brand", example = "PI",
                  description = "The brand of the hotel to retrieve the rate information for")
          },
          responses = {
              @ApiResponse(responseCode = "200", description = "Success",
                  content = @Content(mediaType = "application/json",
                      schema = @Schema(implementation = RateInformationDto.class))),
              @ApiResponse(responseCode = "400", description = "Error Occurred",
                  content = {
                      @Content(mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class))}),
              @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponse.class))}),
              @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponse.class))})
          }))
  })
  ResponseEntity<RateInformationDto> getHotelRateInformation(
      @Valid @ParameterObject RateInformationRequestDto rateInformationRequestDto);
}

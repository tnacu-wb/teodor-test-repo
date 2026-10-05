package uk.co.whitbread.booking.infrastructure.rest.controller.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.channel.BookingChannelDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.email.in.ResendConfirmationEmailRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.email.in.ResendInvoiceEmailRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.in.BookingHistoryRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.in.BookingRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.out.BookingResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.in.BookingInfoRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.in.CancelBookingRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out.BookingInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out.CancelBookingResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.in.DownloadBookingInvoicesRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.out.InvoiceDownloadResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.upcoming.in.UpcomingBookingsRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.upcoming.out.UpcomingBookingsResponseDto;

interface BookingApi {

  String BOOKINGS_PATH = "/v1/bookings";
  String BOOKINGS_HISTORY_PATH = BOOKINGS_PATH + "/history";
  String BOOKING_INFORMATION_PATH = BOOKINGS_PATH + "/information";
  String CANCEL_BOOKING_PATH = BOOKINGS_PATH + "/cancel";
  String BOOKINGS_CONFIRMATION_PATH = BOOKINGS_PATH + "/confirmation";
  String BOOKINGS_INVOICE_PATH = BOOKINGS_PATH + "/invoice";
  String BOOKINGS_INVOICE_DOWNLOAD_PATH = BOOKINGS_PATH + "/invoices/download";
  String UPCOMING_BOOKINGS_PATH = BOOKINGS_PATH + "/upcomingBookings";
  String WB_AUTHORIZATION = "WB-Authorization";

  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = BOOKINGS_HISTORY_PATH,
      operation = @Operation(operationId = "Get booking history",
          summary = "Retrieves all bookings for a user",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "business",
                  description = "Type of user"),
              @Parameter(in = ParameterIn.QUERY, name = "companyId", example = "companyId", required = true,
                  description = "CompanyId of customer to retrieve bookings history"),
              @Parameter(in = ParameterIn.QUERY, name = "employeeId", example = "employeeId", required = true,
                  description = "Language to retrieve hotel payment details"),
              @Parameter(in = ParameterIn.QUERY, name = "typeOfBooking",
                  description = "Booking type"),
              @Parameter(in = ParameterIn.QUERY, name = "sortOrder",
                  description = "Sort order of the bookings"),
              @Parameter(in = ParameterIn.QUERY, name = "filterType",
                  description = "Filter type for bookings"),
              @Parameter(in = ParameterIn.QUERY, name = "filterValue",
                  description = "Bookings filter value"),
              @Parameter(in = ParameterIn.QUERY, name = "includeCheckInBookings",
                  description = "Include check in bookings"),
              @Parameter(in = ParameterIn.QUERY, name = "continuationToken",
                  description = "Continuation token for pagination"),
              @Parameter(in = ParameterIn.QUERY, name = "filterValue",
                  description = "Bookings filter value"),
              @Parameter(in = ParameterIn.HEADER, name = WB_AUTHORIZATION,
                  description = "Authorization token"),
              @Parameter(in = ParameterIn.HEADER, name = "bookingChannel",
                  description = "Channel name (Web/Mobile)"),
          },
          responses = {
              @ApiResponse(responseCode = "200",
                  description = "Booking history successfully retrieved",
                  content = @Content(array = @ArraySchema(
                      schema = @Schema(implementation = BookingResponseDto.class)))),
              @ApiResponse(responseCode = "400", description = "Missing or invalid request"),
              @ApiResponse(responseCode = "404", description = "No bookings found with provided criteria"),
              @ApiResponse(responseCode = "500", description = "Internal server error")
          }))
  })
  ResponseEntity<BookingResponseDto> getBookingsHistory(
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      @ModelAttribute BookingChannelDto bookingChannel,
      @ModelAttribute BookingRequestDto request);

  @RouterOperations({@RouterOperation(method = RequestMethod.POST,
      path = BOOKINGS_HISTORY_PATH,
      operation = @Operation(operationId = "Get bookings history",
          summary = "Retrieves all bookings for a user",
          parameters = {
              @Parameter(in = ParameterIn.HEADER, name = WB_AUTHORIZATION,
                  description = "Authorization token")
          },
          requestBody =
          @RequestBody(content = @Content(schema = @Schema(implementation = BookingHistoryRequestDto.class))),
          responses = {
              @ApiResponse(responseCode = "200",
                  description = "Booking history successfully retrieved",
                  content = @Content(array = @ArraySchema(
                      schema = @Schema(implementation = BookingResponseDto.class)))),
              @ApiResponse(responseCode = "400", description = "Missing or invalid request"),
              @ApiResponse(responseCode = "404", description = "No bookings found with provided criteria"),
              @ApiResponse(responseCode = "500", description = "Internal server error")
          }))
  })
  ResponseEntity<BookingResponseDto> retrieveBookingHistory(
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      @RequestBody @Valid BookingHistoryRequestDto bookingHistoryRequestDto);

  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = BOOKING_INFORMATION_PATH,
      operation = @Operation(operationId = "Get booking information",
          summary = "Retrieves available booking information for a hotel",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "business",
                  description = "Type of user"),
              @Parameter(in = ParameterIn.QUERY, name = "hotelId",
                  description = "Hotel id"),
              @Parameter(in = ParameterIn.QUERY, name = "bookingReference",
                  description = "Booking reference"),
              @Parameter(in = ParameterIn.QUERY, name = "arrival",
                  description = "Arrival date"),
              @Parameter(in = ParameterIn.HEADER, name = "bookingChannel",
                  description = "Channel name (Web/Mobile)"),
          },
          responses = {
              @ApiResponse(responseCode = "200",
                  description = "Booking information successfully retrieved",
                  content = @Content(array = @ArraySchema(
                      schema = @Schema(implementation = BookingResponseDto.class)))),
              @ApiResponse(responseCode = "400", description = "Missing or invalid request"),
              @ApiResponse(responseCode = "404", description = "No bookings found with provided criteria"),
              @ApiResponse(responseCode = "500", description = "Internal server error")
          }))
  })
  ResponseEntity<BookingInfoResponseDto> getBookingInformation(
      @ModelAttribute BookingChannelDto bookingChannel,
      @ModelAttribute BookingInfoRequestDto bookingRequestDto
  );

  @RouterOperations({@RouterOperation(method = RequestMethod.POST,
      path = CANCEL_BOOKING_PATH,
      operation = @Operation(operationId = "Cancel booking",
          summary = "Cancels a booking based on booking and hotel reference",
          requestBody =
              @RequestBody(content = @Content(schema = @Schema(implementation = CancelBookingRequestDto.class))),
          responses = {
              @ApiResponse(responseCode = "200",
                  description = "Booking successfully canceled",
                  content = @Content(schema = @Schema(implementation = CancelBookingResponseDto.class))),
              @ApiResponse(responseCode = "400", description = "Missing or invalid request"),
              @ApiResponse(responseCode = "404", description = "No bookings found with provided criteria"),
              @ApiResponse(responseCode = "500", description = "Internal server error")
          }))
  })
  ResponseEntity<CancelBookingResponseDto> cancelBooking(
      @ModelAttribute BookingChannelDto bookingChannelDto,
      @RequestBody CancelBookingRequestDto cancelBookingRequestDto);

  @RouterOperations({@RouterOperation(method = RequestMethod.POST,
      path = BOOKINGS_CONFIRMATION_PATH,
      operation = @Operation(operationId = "sendConfirmationEmail",
          summary = "Sends booking confirmation email",
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
              schema = @Schema(implementation = ResendConfirmationEmailRequestDto.class))),
          responses = {
              @ApiResponse(responseCode = "200", description = "Confirmation email was sent successfully"),
              @ApiResponse(responseCode = "400", description = "Missing or invalid request"),
              @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
              @ApiResponse(responseCode = "500", description = "Internal server error")
          }))
  })
  ResponseEntity<Void> sendBookingConfirmationEmail(
      @RequestBody ResendConfirmationEmailRequestDto bookingConfirmationRequest
  );

  @RouterOperations({@RouterOperation(method = RequestMethod.POST,
      path = BOOKINGS_INVOICE_PATH,
      operation = @Operation(operationId = "sendInvoiceEmail",
          summary = "Sends booking invoice email",
          parameters = {
              @Parameter(in = ParameterIn.HEADER, name = WB_AUTHORIZATION,
                  description = "Authorization token - required only for logged-in users")
          },
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
              schema = @Schema(implementation = ResendInvoiceEmailRequestDto.class))),
          responses = {
              @ApiResponse(responseCode = "200", description = "Invoice email was sent successfully"),
              @ApiResponse(responseCode = "400", description = "Missing or invalid request"),
              @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
              @ApiResponse(responseCode = "500", description = "Internal server error")
          }))
  })
  ResponseEntity<Void> resendBookingInvoiceEmail(
      @RequestHeader(value = WB_AUTHORIZATION, required = false) String authorization,
      @ModelAttribute BookingChannelDto bookingChannelDto,
      @RequestBody ResendInvoiceEmailRequestDto bookingInvoiceRequest);


  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = UPCOMING_BOOKINGS_PATH,
      operation = @Operation(operationId = "getUpcomingBookings",
          summary = "Retrieve upcoming bookings for an InnBusiness user",
          parameters = {
            @Parameter(in = ParameterIn.HEADER, name = WB_AUTHORIZATION, description = "Authorization token"),
            @Parameter(in = ParameterIn.QUERY, name = "language", description = "Channel name (Web/Mobile)"),
            @Parameter(in = ParameterIn.QUERY, name = "country", description = "Country (gb/de)"),
            @Parameter(in = ParameterIn.QUERY, name = "channel", description = "Channel name (BB)"),
            @Parameter(in = ParameterIn.QUERY, name = "subchannel", description = "Subchannel (Web/Mobile)")
          },
          responses = {
            @ApiResponse(responseCode = "200", description = "Upcoming bookings retrieved"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid request"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
          }))
  })
  ResponseEntity<UpcomingBookingsResponseDto> getUpcomingBookingsForInnBusiness(
        @RequestHeader(value = WB_AUTHORIZATION, required = false) String authorization,
        @Valid UpcomingBookingsRequestDto upcomingBookingsRequestDto
  );

  @RouterOperations({@RouterOperation(method = RequestMethod.POST,
      path = BOOKINGS_INVOICE_DOWNLOAD_PATH,
      operation = @Operation(operationId = "downloadBookingInvoices",
          summary = "Generates invoice download URLs for booking references",
          requestBody = @RequestBody(content = @Content(
              schema = @Schema(implementation = DownloadBookingInvoicesRequestDto.class))),
          responses = {
              @ApiResponse(responseCode = "200",
                  description = "The request was fulfilled successfully.",
                  content = @Content(schema =
                      @Schema(implementation = InvoiceDownloadResponseDto.class))),
              @ApiResponse(responseCode = "400",
                  description = "The request parameters did not comply with the expected format"),
              @ApiResponse(responseCode = "401", description = "Invalid or Expired Token"),
              @ApiResponse(responseCode = "404",
                  description = "No invoice found for booking reference | Booking Ref Not Found"),
              @ApiResponse(responseCode = "500",
                  description = "The server is experiencing some errors and cannot fulfill the request.")
          }))
  })
  ResponseEntity<InvoiceDownloadResponseDto> downloadBookingInvoices(
      @RequestHeader(value = WB_AUTHORIZATION, required = false) String authorization,
      @io.swagger.v3.oas.annotations.parameters.RequestBody @Valid DownloadBookingInvoicesRequestDto request);
}

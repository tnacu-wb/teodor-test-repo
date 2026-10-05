package uk.co.whitbread.content.infrastructure.rest.controller.hotel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.HotelInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.HotelShortInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.HotelsInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.SlugRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelInformationExtendedDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelPaymentInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelShortInformationDto;

public interface HotelInformationApiDocumentation {

  String HOTEL_INFO_PATH = "/v1/content";

  @Operation(summary = "Retrieves Hotel Information by Slug")
  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = HOTEL_INFO_PATH + "/hotels",
      operation = @Operation(summary = "Retrieves Hotel Information By Slug",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "slug",
                  example = "england/greater-london/london/london-kings-cross",
                  description = "Hotel Slug", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
                  description = "Country to retrieve booking information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
                  description = "Language to retrieve booking information", required = true)
          },
          responses = {
              @ApiResponse(responseCode = "200", description = "Success", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = HotelInformationDto.class))}),
              @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
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
  ResponseEntity<HotelInformationDto> getHotelInformationBySlug(@Valid SlugRequestDto slugRequestDto,
                                                        @Valid HotelInformationRequestDto hotelInformationRequestDto,
                                                        LocalDate stayStartDate,
                                                        LocalDate stayEndDate);

  @Operation(summary = "Retrieves All Hotels Short Information")
  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = HOTEL_INFO_PATH + "/allhotels",
      operation = @Operation(summary = "Retrieves All Hotels Short Information",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
                  description = "Country to retrieve booking information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
                  description = "Language to retrieve booking information", required = true)
          },
          responses = {
              @ApiResponse(responseCode = "200", description = "Success", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = HotelShortInformationDto.class))}),
              @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
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
  public ResponseEntity<List<HotelShortInformationDto>> getAllHotelsShortInformation(
      @Valid @ParameterObject HotelShortInformationRequestDto hotelShortInformationRequestDto);

  @Operation(summary = "Retrieves Hotels Information by list of HotelIds")
  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = HOTEL_INFO_PATH + "/hotels/information",
      operation = @Operation(summary = "Retrieves Hotels Information for a list of HotelIds",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "hotelIds", example = "TKINPT, LONEUS",
                  description = "Hotel Ids list", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
                  description = "Country to retrieve booking information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
                  description = "Language to retrieve booking information", required = true)
          },
          responses = {
              @ApiResponse(responseCode = "200", description = "Success", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = HotelInformationDto.class))}),
              @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
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
  ResponseEntity<List<HotelInformationDto>> getHotelsInformation(
          @Valid HotelsInformationRequestDto hotelsInformationRequestDto);

  @Operation(summary = "Retrieves Hotel Payment Information")
  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = HOTEL_INFO_PATH + "/hotels/{hotelId}/payment-information",
      operation = @Operation(summary = "Retrieves Hotel Payment Information",
          parameters = {
              @Parameter(in = ParameterIn.PATH, name = "hotelId", example = "TKINPT",
                  description = "Hotel Id", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
                  description = "Country to retrieve booking information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
                  description = "Language to retrieve booking information", required = true)
          },
          responses = {
              @ApiResponse(responseCode = "200", description = "Success", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = HotelPaymentInformationDto.class))}),
              @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
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
  ResponseEntity<HotelPaymentInformationDto> getHotelPaymentInformation(String hotelId,
                                                                        @Valid HotelInformationRequestDto requestDto);

  @Operation(summary = "Trigger Hotel Facilities Cache Update")
  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = HOTEL_INFO_PATH + "/hotels/facilities/updater/trigger",
      operation = @Operation(summary = "Trigger Hotel Facilities Cache Update",
          responses = {
              @ApiResponse(responseCode = "200", description = "Success", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema())}),
              @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
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
  ResponseEntity<Void> triggerUpdateHotelsFacilitiesCache();

  @Operation(summary = "Retrieves Hotel Information")
  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = HOTEL_INFO_PATH + "/hotels/{hotelId}/information",
      operation = @Operation(summary = "Retrieves Hotel Information",
          parameters = {
              @Parameter(in = ParameterIn.PATH, name = "hotelId", example = "TKINPT",
                  description = "Hotel Id", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
                  description = "Country to retrieve booking information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
                  description = "Language to retrieve booking information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "channel", example = "BB",
                  description = "Booking channel"),
              @Parameter(in = ParameterIn.QUERY, name = "subchannel", example = "WEB",
                  description = "Booking subchannel"),
              @Parameter(in = ParameterIn.QUERY, name = "stayStartDate", example = "2026-07-14",
                  description = "Start date of the stay (ISO format: yyyy-MM-dd). "
                      + "Used to filter restaurant menus. Must not be after stayEndDate."),
              @Parameter(in = ParameterIn.QUERY, name = "stayEndDate", example = "2026-07-16",
                  description = "End date of the stay (ISO format: yyyy-MM-dd). "
                      + "Used to filter restaurant menus. Must not be before stayStartDate.")
          },
          responses = {
              @ApiResponse(responseCode = "200", description = "Success", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = HotelInformationExtendedDto.class))}),
              @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
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
  ResponseEntity<HotelInformationExtendedDto> getHotelInformation(
      @NotNull String hotelId,
      @Valid HotelInformationRequestDto hotelInformationRequestDto,
      LocalDate stayStartDate,
      LocalDate stayEndDate);

  @Operation(summary = "Trigger Hotel Opening Soon Cache Update")
  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = HOTEL_INFO_PATH + "/hotels/opening-soon/updater/trigger",
      operation = @Operation(summary = "Trigger Hotel Opening Soon Cache Update",
          responses = {
              @ApiResponse(responseCode = "200", description = "Success", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema())}),
              @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
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
  ResponseEntity<Void> triggerUpdateHotelsOpeningSoonCache();

}

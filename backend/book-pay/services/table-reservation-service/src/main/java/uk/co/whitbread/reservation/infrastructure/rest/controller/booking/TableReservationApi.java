package uk.co.whitbread.reservation.infrastructure.rest.controller.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EventOrEquiryRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.EnquiryResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.EventsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.MenuResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.OccasionsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.SessionResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.OutletResponseDto;

public interface TableReservationApi {

  @Operation(summary = "Slots")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  SessionResponseDto slots(String from, String until, String time, String adult, String children,
      String siteId);

  @Operation(summary = "Outlets")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  OutletResponseDto outlets(String location, String id);

  @Operation(summary = "events")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  EventsResponseDto events(EventOrEquiryRequest eventRequest);

  @Operation(summary = "eventById")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  EventsResponseDto getEvent(String eventId);

  @Operation(summary = "enquiries")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  EnquiryResponseDto enquiry(EventOrEquiryRequest eventOrEquiryRequest);

  @Operation(summary = "enquiryById")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  EnquiryResponseDto getEnquiry(String enquiryId);

  @Operation(summary = "occasions")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Bad Request", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  OccasionsResponseDto occasions(String from, String until, String siteId);

  @Operation(summary = "menu")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Bad Request", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  MenuResponseDto getMenu(String siteId, String from, String until, String time,
      String ocassionId);


}

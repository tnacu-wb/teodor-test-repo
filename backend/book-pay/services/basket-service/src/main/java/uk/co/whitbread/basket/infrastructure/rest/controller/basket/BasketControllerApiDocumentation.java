package uk.co.whitbread.basket.infrastructure.rest.controller.basket;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.AddBasketItemRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.BasketItemsRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.CancelBasketDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ChangeBasketIdContextDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ChangeBasketStatusDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ConfirmItemProcessingRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.CreateBasketRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.CreateBasketRequestReservationDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.PreAuthChargesDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.UpdateAllowancesRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.UpdateBasketItemOccupancyRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.BasketDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.BasketStatusResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.CreateBasketResponseDto;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

public interface BasketControllerApiDocumentation {

  @Operation(summary = "Creates a new empty basket entity, assigns a new, unique, random reference"
      + " or a migrated reference, persists the Basket and returns an empty basket model to the caller.")
  @ApiResponse(responseCode = "201", description = "Created", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = CreateBasketResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<CreateBasketResponseDto> createBasket(
      @RequestBody @Valid CreateBasketRequestDto createBasketRequestDto);

  @Operation(summary = "Returns the content of a Basket resource identified by the supplied unique basket reference.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = BasketDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "{basket-reference}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<BasketDto> getBasket(
      @PathVariable("basket-reference") @NotNull String reference);

  @Operation(summary = "Adds an item to a basket.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = BasketDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "412", description = "Precondition failed", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/{basket-reference}/items", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<BasketDto> addItem(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestHeader("If-Match") @NotNull String ifMatch,
      @RequestBody @Valid AddBasketItemRequestDto addBasketItemRequestDto);

  @Operation(summary = "Updates the business allowances.")
  @ApiResponse(responseCode = "204", description = "Success")
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "412", description = "Precondition failed", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/{basket-reference}/allowances", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> updateAllowances(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestHeader("If-Match") @NotNull String ifMatch,
      @RequestBody @Valid UpdateAllowancesRequestDto updateAllowancesRequestDto);

  @Operation(summary = "Remove an item from an existing basket.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = BasketDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "412", description = "Precondition failed", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @DeleteMapping(value = "/{basket-reference}/items/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<BasketDto> removeItem(
      @PathVariable("basket-reference") @NotNull String reference,
      @PathVariable("itemId") @NotNull String itemId,
      @RequestHeader("If-Match") @NotNull String ifMatch);

  @Operation(summary = "Remove multiple items from an existing basket.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = BasketDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "412", description = "Precondition failed", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @DeleteMapping(value = "/{basket-reference}/items", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<BasketDto> removeItems(
      @PathVariable("basket-reference") @NotNull String reference,
      @Valid @ParameterObject BasketItemsRequestDto basketItemsRequestDto,
      @RequestHeader("If-Match") @NotNull String ifMatch);

  @Operation(summary = "Delete an existing basket.")
  @ApiResponse(responseCode = "204", description = "Removed", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = BasketDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "412", description = "Precondition failed", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @DeleteMapping(value = "/{basket-reference}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> deleteBasket(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestHeader("If-Match") @NotNull String ifMatch);

  @Operation(summary = "Confirm the processing of an item.")
  @ApiResponse(responseCode = "204", description = "Updated", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = BasketDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/{basket-reference}/items/{itemId}/acks", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> confirmItem(
      @PathVariable("basket-reference") @NotNull String reference,
      @PathVariable("itemId") @NotNull String itemId,
      @RequestBody @Valid ConfirmItemProcessingRequestDto confirmItemProcessingRequestDto);

  @Operation(summary =
      "Cancels Basket resource identified by the supplied unique basket reference. "
          + "Basket status is updated to CANCELLED and an email is sent to the user.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/{basket-reference}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> cancelBasket(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestBody CancelBasketDto cancelBasketDto);

  @Operation(summary = "Updates A2C Pre Auth Charges")
  @ApiResponse(responseCode = "204", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  public ResponseEntity<Void> setPreAuthCharges(
      @PathVariable("basket-reference") @NotNull String basketId,
      @RequestBody @Valid PreAuthChargesDto preAuthChargesDto);

  @Operation(summary = "Returns the content of a BasketStatusResponse resource identified "
      + "by the supplied unique basket reference.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = BasketStatusResponseDto.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/{basketReference}/checkStatus", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<BasketStatusResponseDto> checkStatus(
      @PathVariable("basketReference") @NotNull String basketReference);

  @Operation(summary = "Updates Occupancy Supplement flag for the items in the basket.")
  @ApiResponse(responseCode = "202", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<Void> updateItemOccupancy(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestHeader("If-Match") @NotNull String ifMatch,
      @RequestBody @Valid UpdateBasketItemOccupancyRequestDto updateBasketItemOccupancyRequestDto);

  @Operation(summary =
      "Set basket in pre-checked-in status if check-in online was initiated and the basket was completed.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/{basket-reference}/preCheckIn", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<BasketStatusResponseDto> preCheckInBasket(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestParam(value = "isCiol", required = false) Boolean isCiol);

  @Operation(summary =
      "Complete the pre-check-out flow by settings the basket status and alert in opera")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/{basket-reference}/preCheckOut", produces = MediaType.APPLICATION_JSON_VALUE)
  BasketStatusResponseDto preCheckOut(
      @PathVariable("basket-reference") @NotNull String reference);

  @Operation(summary =
      "Update the status of a basket by booking reference")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = BasketDto.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/{bookingRef}/changeStatus", produces = MediaType.APPLICATION_JSON_VALUE)
  BasketDto changeStatus(
      @PathVariable("bookingRef") @Size(min = 5, max = 15) String bookingRef,
      @RequestBody @Valid ChangeBasketStatusDto statusDto);

  @Operation(summary =
      "Update idContext of a basket by booking reference")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = BasketDto.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PutMapping("/{bookingRef}/changeIdContext")
  BasketDto changeIdContext(@PathVariable @Size(min = 5, max = 15) String bookingRef,
      @RequestBody @Valid ChangeBasketIdContextDto idContextDto);

  @Operation(summary = "Creates a migrated reference basket, persists the Basket including "
      + "basketItems and basket item types and returns an empty basket model to the caller.")
  @ApiResponse(responseCode = "201", description = "Created", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = CreateBasketResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<CreateBasketResponseDto> createBasketReservation(
      @RequestBody @Valid CreateBasketRequestReservationDto createBasketRequestReservationDto);
}

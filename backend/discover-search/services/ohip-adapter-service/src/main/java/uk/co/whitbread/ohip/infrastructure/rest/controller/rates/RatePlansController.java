package uk.co.whitbread.ohip.infrastructure.rest.controller.rates;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ohip.domain.ports.primary.RatePlansInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipNotFoundException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.mapper.NegotiatedRatesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.mapper.PromotionCodeResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.mapper.RatePlanInfoResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.mapper.RatePlansResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.NegotiatedRatesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.PromotionResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.RatePlanInfoResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.RatePlansResponseDto;

@RestController
@RequiredArgsConstructor
public class RatePlansController {

  private final RatePlansInPort ratePlansInPort;
  private final RatePlansResponseMapper ratePlansResponseMapper;
  private final NegotiatedRatesResponseMapper negotiatedRatesResponseMapper;
  private final PromotionCodeResponseMapper promotionCodeResponseMapper;
  private final RatePlanInfoResponseMapper ratePlanInfoResponseMapper;

  @Operation(summary = "Retrieves Hotel Rate Plans")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = RatePlansResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(
      value = "/ratePlans", produces = MediaType.APPLICATION_JSON_VALUE)
  public RatePlansResponseDto getRatePlans(
      @RequestParam(value = "ratePlanCodes", required = false) List<String> ratePlans,
      @RequestParam(value = "hotelId") String hotelId) {

    return ratePlansResponseMapper.toDto(ratePlansInPort.getRatePlans(ratePlans, hotelId));
  }

  @Operation(summary = "Retrieves negotiated Rates info for given profile id")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = NegotiatedRatesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(
      value = "/{profileId}/negotiatedRates", produces = MediaType.APPLICATION_JSON_VALUE)
  public NegotiatedRatesResponseDto getNegotiatedRatesForProfileId(@PathVariable final String profileId) {

    return negotiatedRatesResponseMapper.toDto(ratePlansInPort.getNegotiatedRatesForProfileId(profileId));
  }

  @Operation(summary = "Retrieves Rate Plan Info for a given rate plan and hotel")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = RatePlanInfoResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/ratePlanInfo", produces = MediaType.APPLICATION_JSON_VALUE)
  public RatePlanInfoResponseDto getRatePlanInfo(
      @RequestParam(value = "ratePlanCode") String ratePlanCode,
      @RequestParam(value = "hotelId") String hotelId) {

    return ratePlanInfoResponseMapper.toDto(ratePlansInPort.getRatePlanInfo(ratePlanCode, hotelId));
  }

  @Operation(summary = "Retrieves Promotion codes")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = NegotiatedRatesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(
      value = "/promotions", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PromotionResponseDto> getPromotionCode(
      @RequestParam(value = "promotionCodes") List<String> promotionCodes,
      @RequestParam(value = "hotelId") String hotelId) {

    return promotionCodeResponseMapper.toDto(
        ratePlansInPort.getPromotionCode(promotionCodes, hotelId));

  }
}

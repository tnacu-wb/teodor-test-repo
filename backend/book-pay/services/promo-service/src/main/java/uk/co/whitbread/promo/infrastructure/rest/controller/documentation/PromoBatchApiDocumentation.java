package uk.co.whitbread.promo.infrastructure.rest.controller.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in.PromoBatchRequestDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in.PromoKindRequestDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in.RedeemPromoCodeRequestDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PagedResponse;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PromoBatchResponseDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PromoBatchSummaryDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PromoKindResponseDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.RedeemPromoCodeResponseDto;

public interface PromoBatchApiDocumentation {

  @Operation(
      summary = "Create promo batch",
      description = "Creates a new promo batch and starts async promo code generation."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Promo batch created successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = PromoBatchResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Validation error in request payload",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  PromoBatchResponseDto createPromoBatch(
      @Valid @RequestBody PromoBatchRequestDto promoBatchRequestDto);

  @Operation(
      summary = "Get promo batch by id",
      description = "Retrieves a single promo batch summary by its identifier."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Promo batch found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = PromoBatchSummaryDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Promo batch not found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  PromoBatchSummaryDto getPromoBatchById(
      @Parameter(
          name = "batchId",
          in = ParameterIn.PATH,
          description = "Promo batch identifier",
          required = true
      )
      @PathVariable("batchId") UUID batchId);

  @Operation(
      summary = "List promo batches (paged)",
      description = "Returns paginated promo batch summaries, sorted by creation time."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Page of promo batches returned successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = PagedResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid paging or sorting parameters",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  PagedResponse<PromoBatchSummaryDto> getPromoBatchSummary(
      @ParameterObject Pageable pageable);

  @Operation(
      summary = "Mark promo batch as downloaded",
      description = "Marks the given promo batch as downloaded so that passwords are no longer returned."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "Download flag updated successfully"
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid batch id or request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Promo batch not found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error or DB update failure",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  void markPromoBatchAsDownloaded(
      @Parameter(
          name = "batchId",
          in = ParameterIn.PATH,
          description = "Promo batch identifier to mark as downloaded",
          required = true
      )
      @PathVariable("batchId") @Valid UUID batchId);


  @Operation(
      summary = "Validate promo kind by promo code",
      description = "Validates the given promo code and returns its promo kind "
          + "along with Opera promo code and unique promo code status."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Promo code validated successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = PromoKindResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid or malformed promo code",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Promo code not found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  PromoKindResponseDto validatePromoKind(
          @ParameterObject
          @Valid PromoKindRequestDto requestDto
  );


  @Operation(
      summary = "Redeem promo code",
      description = "Redeems a unique promo code against a booking reference and returns the redemption status."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Promo code redeemed or already processed",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = RedeemPromoCodeResponseDto.class)
          )
      ),
      @ApiResponse(
          responseCode = "422",
          description = "Validation error in request payload",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  RedeemPromoCodeResponseDto redeemPromoCode(
      @Valid @RequestBody RedeemPromoCodeRequestDto requestDto
  );
}
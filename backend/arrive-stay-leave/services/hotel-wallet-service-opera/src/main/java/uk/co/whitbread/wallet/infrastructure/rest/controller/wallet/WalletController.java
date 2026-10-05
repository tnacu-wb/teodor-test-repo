package uk.co.whitbread.wallet.infrastructure.rest.controller.wallet;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.wallet.domain.ports.primary.WalletGeneratorInPort;
import uk.co.whitbread.wallet.infrastructure.rest.controller.wallet.mapper.WalletRequestMapper;
import uk.co.whitbread.wallet.infrastructure.rest.controller.wallet.model.in.WalletRequestDto;


@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class WalletController {

  private final WalletGeneratorInPort walletGeneratorInPort;
  private final WalletRequestMapper walletRequestMapper;

  @Operation(summary = "Generate hotel wallet logic")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/octet-stream",
                  schema = @Schema(implementation = byte.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
          @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
          @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/hotel-wallet", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE,
      MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<byte[]> getHotelWallet(
      @ParameterObject @NotNull @Valid WalletRequestDto walletRequestDto) {
    return ResponseEntity.ok(
        walletGeneratorInPort.generateWalletPass(walletRequestMapper.toModel(walletRequestDto)));
  }
}

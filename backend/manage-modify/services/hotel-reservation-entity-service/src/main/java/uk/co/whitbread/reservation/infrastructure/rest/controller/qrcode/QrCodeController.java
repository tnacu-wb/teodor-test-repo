package uk.co.whitbread.reservation.infrastructure.rest.controller.qrcode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.glxn.qrgen.core.exception.QRGenerationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.reservation.infrastructure.rest.client.qrcode.service.QrCodeUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Slf4j
public class QrCodeController {
  
  private final QrCodeUtils qrCodeUtils;
  private static final String CONTROL_CHARACTER_REGEX = "\\p{Cntrl}";
  
  @Operation(summary = "Generates a QR code image",
      description = "Generate a QR code image in the confirmation email a customer receives when making a reservation")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/octet-stream", schema = @Schema(implementation = byte.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/qrcode/{code}", produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<byte[]> generateReservationQrCode(@PathVariable("code") String code) {

    var sanitizedCode = Optional.ofNullable(code)
        .map(c -> c.replaceAll(CONTROL_CHARACTER_REGEX, "")
            .replace("\r", "").replace("\n", ""))
        .orElse("");

    try {
      log.debug("generating reservation QR code for code='{}'", sanitizedCode);
      ByteArrayOutputStream imageStream = qrCodeUtils.generateQrCode(code);
      return buildQrCodeResponse(HttpStatus.OK, imageStream.toByteArray());
    } catch (QRGenerationException e) {
      log.error("Exception white generating QR Code", e);
      return buildQrCodeResponse(HttpStatus.INTERNAL_SERVER_ERROR, new byte[0]);
    }
  }
  
  private ResponseEntity<byte[]> buildQrCodeResponse(HttpStatus status, byte[] imageContent) {
    return ResponseEntity.status(status)
        .contentType(MediaType.IMAGE_PNG)
        .contentLength(imageContent.length)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + qrCodeUtils.getFilename())
        .body(imageContent);
  }
}

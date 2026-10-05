package uk.co.whitbread.refund.processor.infrastructure.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.refund.processor.domain.ports.primary.RefundRequestProcessInPort;
import uk.co.whitbread.refund.processor.infrastructure.rest.controller.mapper.RefundResponseDtoMapper;
import uk.co.whitbread.refund.processor.infrastructure.rest.controller.mapper.TokenRefundDtoMapper;
import uk.co.whitbread.refund.processor.infrastructure.rest.controller.model.in.TokenRefundRequestDto;
import uk.co.whitbread.refund.processor.infrastructure.rest.controller.model.out.RefundResponseDto;

@RestController
@RequestMapping("/v1/rrp")
@RequiredArgsConstructor
public class RefundRequestProcessorController implements RefundRequestProcessorApiDocumentation {

  private final RefundRequestProcessInPort refundRequestProcessInPort;
  private final TokenRefundDtoMapper tokenRefundMapper;
  private final RefundResponseDtoMapper refundResponseMapper;

  @Override
  @PostMapping(value = "/tokenRefund", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RefundResponseDto> refund(
             @RequestBody TokenRefundRequestDto tokenRefundRequestDto) {
    var refundResponse = refundRequestProcessInPort
            .handleTokenRefund(tokenRefundMapper.toDomain(tokenRefundRequestDto));
    return new ResponseEntity<>(refundResponseMapper.toDto(refundResponse), HttpStatus.ACCEPTED);
  }

}

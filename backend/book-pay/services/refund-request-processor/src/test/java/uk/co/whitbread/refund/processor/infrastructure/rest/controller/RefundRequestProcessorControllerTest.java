package uk.co.whitbread.refund.processor.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.whitbread.refund.processor.domain.model.in.Amount;
import uk.co.whitbread.refund.processor.domain.model.in.Booking;
import uk.co.whitbread.refund.processor.domain.model.in.BusinessSite;
import uk.co.whitbread.refund.processor.domain.model.in.Card;
import uk.co.whitbread.refund.processor.domain.model.in.PaymentType;
import uk.co.whitbread.refund.processor.domain.model.in.Refund;
import uk.co.whitbread.refund.processor.domain.model.in.RefundReason;
import uk.co.whitbread.refund.processor.domain.model.in.TokenRefund;
import uk.co.whitbread.refund.processor.domain.model.out.RefundResponse;
import uk.co.whitbread.refund.processor.domain.ports.primary.RefundRequestProcessInPort;
import uk.co.whitbread.refund.processor.infrastructure.rest.controller.mapper.RefundResponseDtoMapper;
import uk.co.whitbread.refund.processor.infrastructure.rest.controller.mapper.TokenRefundDtoMapper;
import uk.co.whitbread.refund.processor.infrastructure.rest.controller.model.in.TokenRefundRequestDto;
import uk.co.whitbread.refund.processor.infrastructure.rest.controller.model.out.RefundResponseDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
public class RefundRequestProcessorControllerTest {

    @Mock
    private RefundRequestProcessInPort refundRequestProcessInPort;

    @Mock
    private TokenRefundDtoMapper tokenRefundMapper;

    @Mock
    private RefundResponseDtoMapper refundResponseMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        this.mockMvc = standaloneSetup(new RefundRequestProcessorController(
            refundRequestProcessInPort, tokenRefundMapper, refundResponseMapper)).build();
    }

    @Test
    void testProcessTokenRefund() throws Exception {
      var request = mockTokenRefundRequestDto();
      var tokenRefund = TokenRefund.builder().requestId("req-123").build();
      var refundResponse = RefundResponse.builder()
          .requestId("req-123")
          .refundId("refund-456")
          .paymentId("pay-789")
          .refunded(true)
          .build();
      var refundResponseDto = RefundResponseDto.builder()
          .requestId("req-123")
          .refundId("refund-456")
          .paymentId("pay-789")
          .refunded(true)
          .build();

      when(tokenRefundMapper.toDomain(any(TokenRefundRequestDto.class))).thenReturn(tokenRefund);
      when(refundRequestProcessInPort.handleTokenRefund(tokenRefund)).thenReturn(refundResponse);
      when(refundResponseMapper.toDto(refundResponse)).thenReturn(refundResponseDto);

      mockMvc.perform(post("/v1/rrp/tokenRefund")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isAccepted())
          .andExpect(jsonPath("$.requestId").value("req-123"))
          .andExpect(jsonPath("$.refundId").value("refund-456"))
          .andExpect(jsonPath("$.paymentId").value("pay-789"))
          .andExpect(jsonPath("$.refunded").value(true));

      verify(tokenRefundMapper).toDomain(any(TokenRefundRequestDto.class));
      verify(refundRequestProcessInPort).handleTokenRefund(tokenRefund);
      verify(refundResponseMapper).toDto(refundResponse);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TokenRefundRequestDto mockTokenRefundRequestDto() {
        return TokenRefundRequestDto
                .builder()
                .hotelCode("LONEUS")
                .refund(Refund
                        .builder()
                        .type(PaymentType.CARD)
                        .amount(Amount
                                .builder()
                                .currency("GBP")
                                .minorUnits(BigDecimal.TEN)
                                .build())
                        .card(Card
                                .builder()
                                .token("4216333880397891103")
                                .expiryYear("26")
                                .expiryMonth("03")
                                .build())
                        .reason(RefundReason.AMEND)
                        .build())
                .booking(Booking
                        .builder()
                        .channel("WEB")
                        .type("PAY_NOW")
                        .journey("REFUND")
                        .businessSite(BusinessSite
                                .builder()
                                .identifier("LONEUS")
                                .type("HOTEL")
                                .build())
                        .build())
                .build();
    }

}

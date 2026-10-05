package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.basket.out.BasketError;
import uk.co.whitbread.basket.domain.model.basket.out.PromoKind;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiExtraItems;
import uk.co.whitbread.basket.domain.model.payments.out.BasketPaymentStatus;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.BookingAllowanceDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketDto {
  @NotEmpty
  private String hotelId;
  @NotEmpty
  private String bookingReference;
  //holds basket id
  @NotEmpty
  private String reference;
  @NotEmpty
  private String createdAt;
  private String lastModifiedAt;
  private String userId;
  private String originalBasketId;
  Map<String, String> linkAmendReservations;
  @NotEmpty
  private BasketStatusDto status;
  private BasketPaymentStatus paymentStatus;
  private Boolean sendMail;
  private String paymentID;
  private String paymentOption;
  private String channel;
  private String subChannel;
  private String lockingTime;
  @NotEmpty
  private Set<String> itemTypes;
  @NotEmpty
  private List<BasketItemDto> items;
  private CcuiExtraItems ccuiExtraItems;
  private List<BookingAllowanceDto> bookingAllowances;
  private Boolean isErroredBooking;
  private BasketError basketError;
  private Boolean isCheckInOnlinePay;
  private Boolean isSecureBooking;
  private String idContext;
  private String promotionCode;
  private PromoKind promoKind;
}
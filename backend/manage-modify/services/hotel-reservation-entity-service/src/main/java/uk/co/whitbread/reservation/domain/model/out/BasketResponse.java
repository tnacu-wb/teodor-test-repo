package uk.co.whitbread.reservation.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketError;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromoKind;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketResponse {

  @NotEmpty
  private String hotelId;
  @NotEmpty
  private String bookingReference;
  @NotEmpty
  private String reference;
  @NotEmpty
  private String createdAt;
  private String lastModifiedAt;
  private String userId;
  @NotEmpty
  private String status;
  private String paymentID;
  private PaymentOption paymentOption;
  private String lockingTime;
  @NotEmpty
  private Set<String> itemTypes;
  @NotEmpty
  private List<BasketItemResponse> items;
  private CcuiExtraItems ccuiExtraItems;
  private Long cleanUpTime;
  private String eTag;
  private String originalBasketId;
  private String channel;
  private String subChannel;
  private boolean isErroredBooking;
  Map<String, String> linkAmendReservations;
  private BasketError basketError;
  private List<BookingAllowance> bookingAllowances;
  private Boolean isCheckInOnlinePay;
  private String idContext;
  private Boolean isSecureBooking;
  private String promotionCode;
  private PromoKind promoKind;
}

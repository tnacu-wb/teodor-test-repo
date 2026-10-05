package uk.co.whitbread.basket.domain.model.basket.out;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiExtraItems;
import uk.co.whitbread.basket.domain.model.payments.out.BasketPaymentStatus;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Basket {

  @NotEmpty
  private String hotelId;
  @NotEmpty
  private String threeLetterHotelId;
  @NotEmpty
  private String sortKey;
  @NotEmpty
  private String reference;
  @NotEmpty
  private String basketId;
  @NotEmpty
  private String createdAt;
  private String lastModifiedAt;
  private String pollingStartedAt;
  private String userId;
  private String originalBasketId;
  private Map<String, String> linkAmendReservations;
  private String emailAddress;
  private Boolean sendMail;
  @NotEmpty
  private String channel;
  private String subChannel;
  @NotEmpty
  private BasketStatus status;
  private BasketPaymentStatus paymentStatus;
  private String paymentID;
  private String paymentOption;
  private String paymentChannel;
  private String lockingTime;
  @NotEmpty
  @Singular
  private Map<String, Set<String>> itemTypes;
  @NotEmpty
  @Singular
  private List<BasketItem> items;
  private Long cleanUpTime;
  private CcuiExtraItems ccuiExtraItems;
  private List<BookingAllowance> bookingAllowances;
  private String totalCost;
  private String currency;
  private BasketError basketError;
  private Boolean retryPayment;
  private Boolean isErroredBooking;
  private String threeDSIndicator;
  private Boolean isCheckInOnlinePay;
  private Boolean isSecureBooking;
  private String idContext;
  private String promotionCode;
  private PromoKind promoKind;
  private String paymentProvider;
}

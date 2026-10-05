package uk.co.whitbread.basket.infrastructure.repository.model;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class BasketEntity {

  @NotEmpty
  private String hotelId;
  @NotEmpty
  private String threeLetterHotelId;
  @NotEmpty
  private String sortKey;
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
  private String paymentChannel;
  private Boolean retryPayment;
  @NotEmpty
  private BasketStatusEntity status;
  private BasketPaymentStatusEntity paymentStatus;
  private String paymentID;
  private String paymentOption;
  private CcuiExtraItemsEntity ccuiExtraItems;
  private List<BookingAllowanceEntity> bookingAllowances;
  private String lockingTime;
  @Singular
  private List<BasketItemTypeEntity> itemTypes;
  @Singular
  private List<BasketItemEntity> items;
  private Long cleanUpTime;
  private String totalCost;
  private String currency;
  private BasketErrorEntity basketError;
  private Boolean isErroredBooking;
  private Boolean isCheckInOnlinePay;
  private Boolean isSecureBooking;
  private String idContext;
  private String promotionCode;
  private PromoKind promoKind;
  private PaymentProvider paymentProvider;

  @DynamoDbPartitionKey
  @DynamoDbAttribute(value = "threeLetterHotelId")
  public String getThreeLetterHotelId() {
    return this.threeLetterHotelId;
  }

  @DynamoDbSortKey
  @DynamoDbAttribute(value = "sortKey")
  public String getSortKey() {
    return this.sortKey;
  }

  @DynamoDbAttribute(value = "hotelId")
  public String getHotelId() {
    return this.hotelId;
  }

  @DynamoDbAttribute(value = "reference")
  @DynamoDbSecondaryPartitionKey(indexNames = {"referenceIndex"})
  public String getReference() {
    return this.reference;
  }
}

package uk.co.whitbread.basket.infrastructure.repository.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class PrepaidDepositEntity {

  @NotEmpty
  private String reservationId;
  @NotEmpty
  private Long paymentNo;
  @NotEmpty
  private byte[] charges;
  @NotEmpty
  private String createdAt;
  private Long cleanUpTime;


  @DynamoDbPartitionKey
  @DynamoDbAttribute(value = "reservationId")
  public String getReservationId() {
    return this.reservationId;
  }

  @DynamoDbSortKey
  @DynamoDbAttribute(value = "paymentNo")
  public Long getPaymentNo() {
    return this.paymentNo;
  }

}

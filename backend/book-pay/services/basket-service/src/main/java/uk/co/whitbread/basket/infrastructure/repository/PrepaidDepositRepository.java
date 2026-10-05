package uk.co.whitbread.basket.infrastructure.repository;

import static java.util.Optional.ofNullable;
import static reactor.core.publisher.Mono.fromFuture;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.infrastructure.repository.config.DynamoDbProperties;
import uk.co.whitbread.basket.infrastructure.repository.exception.BasketNotFoundException;
import uk.co.whitbread.basket.infrastructure.repository.model.PrepaidDepositEntity;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PrepaidDepositRepository {

  private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
  private final DynamoDbProperties dynamoDBProperties;
  private final TableSchema<PrepaidDepositEntity> tableSchema;

  public Mono<PrepaidDepositEntity> save(PrepaidDepositEntity prepaidDepositEntity) {
    DynamoDbAsyncTable<PrepaidDepositEntity> dbAsyncTable =
        dynamoDbEnhancedAsyncClient.table(dynamoDBProperties.getTableNamePrepaidDeposit(),
            tableSchema);

    return fromFuture(dbAsyncTable.putItem(prepaidDepositEntity)
        .thenCompose(a -> dbAsyncTable.getItem(
            PrepaidDepositEntity.builder().reservationId(prepaidDepositEntity.getReservationId())
                .paymentNo(prepaidDepositEntity.getPaymentNo())
                .charges(prepaidDepositEntity.getCharges())
                .build())))
        .flatMap(depositEntity -> ofNullable(depositEntity)
            .map(Mono::just)
            .orElseThrow(() -> {
              var exception = new BasketNotFoundException(
                  ErrorCode.PREP_BOOKING_CHARGES_EXCEPTION,
                  String.format("No PrepaidBookingCharges found with reservation Id: %s",
                      prepaidDepositEntity
                          .getReservationId()));
              ExceptionLogger.log(log, exception);
              return exception;
            }));
  }

  public List<PrepaidDepositEntity> get(String reservationId) {
    DynamoDbAsyncTable<PrepaidDepositEntity> dbAsyncTable =
        dynamoDbEnhancedAsyncClient.table(dynamoDBProperties.getTableNamePrepaidDeposit(),
            tableSchema);

    QueryConditional queryConditional = QueryConditional
        .keyEqualTo(Key.builder().partitionValue(reservationId)
            .build());

    var pagePublisher = dbAsyncTable.query(QueryEnhancedRequest.builder()
        .queryConditional(queryConditional).build());

    final var items = Objects.requireNonNull(Flux.from(pagePublisher).blockFirst()).items();

    if (CollectionUtils.isEmpty(items)) {
      var exception = new BasketNotFoundException(
          ErrorCode.GET_PREP_BOOKING_CHARGES_EXCEPTION,
          String.format("No PrepaidBookingCharges found with reservation Id: %s", reservationId));
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    return items;
  }
}

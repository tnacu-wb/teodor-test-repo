package uk.co.whitbread.basket.infrastructure.repository;

import static java.util.Optional.ofNullable;
import static reactor.core.publisher.Mono.fromFuture;
import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.infrastructure.repository.config.DynamoDbProperties;
import uk.co.whitbread.basket.infrastructure.repository.exception.BasketNotFoundException;
import uk.co.whitbread.basket.infrastructure.repository.exception.InvalidBasketIdException;
import uk.co.whitbread.basket.infrastructure.repository.id.BasketIdService;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketEntity;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BasketRepository {

  private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
  private final DynamoDbProperties dynamoDBProperties;
  private final BasketIdService basketIdService;
  private final TableSchema<BasketEntity> tableSchema;

  private static final int BASKET_ID_LENGTH = 36;

  public Mono<BasketEntity> save(BasketEntity basketEntity) {
    DynamoDbAsyncTable<BasketEntity> basketTable = dynamoDbEnhancedAsyncClient
        .table(dynamoDBProperties.getTableName(), tableSchema);
    return fromFuture(basketTable.putItem(basketEntity)
        .thenCompose(a -> basketTable.getItem(
            BasketEntity.builder().threeLetterHotelId(basketEntity.getThreeLetterHotelId())
                .sortKey(basketEntity.getSortKey())
                .basketId(basketEntity.getBasketId())
                .reference(basketEntity.getReference())
                .build())))
        .flatMap(basket -> ofNullable(basket)
            .map(Mono::just)
            .orElseThrow(() -> {
              var exception = new BasketNotFoundException(
                  ErrorCode.BASKET_REPO_NOT_FOUND_EXCEPTION,
                  String.format("No basket found with reference: %s", basketEntity.getReference()));
              ExceptionLogger.log(log, exception);
              return exception;
            }));
  }

  public Mono<BasketEntity> updateBasket(BasketEntity basketEntity) {
    DynamoDbAsyncTable<BasketEntity> basketTable =
        dynamoDbEnhancedAsyncClient.table(dynamoDBProperties.getTableName(),
            tableSchema);
    return fromFuture(basketTable.updateItem(basketEntity));
  }

  public Optional<BasketEntity> getByReference(final String reference) {

    DynamoDbAsyncTable<BasketEntity> basketTable =
        dynamoDbEnhancedAsyncClient.table(dynamoDBProperties.getTableName(), tableSchema);
    DynamoDbAsyncIndex<BasketEntity> basketsByReference = basketTable.index("referenceIndex");

    final var basketByRefPublisher =
        basketsByReference
            .query(r -> r.queryConditional(keyEqualTo(k -> k.partitionValue(reference))));

    final var items = Objects.requireNonNull(Flux.from(basketByRefPublisher).blockFirst()).items();

    if (CollectionUtils.isEmpty(items)) {
      return Optional.empty();
    }

    var future = basketTable.getItem(BasketEntity.builder()
        .threeLetterHotelId(items.get(0).getThreeLetterHotelId())
        .sortKey(items.get(0).getSortKey())
        .build());

    return future.thenApply(Optional::ofNullable).join();
  }

  public Optional<BasketEntity> getByBasketId(final String basketId) {
    final var hotelId = basketIdService.extractBasketIdHotelId(basketId);
    final var sortKey = basketIdService.extractBasketIdSortKey(basketId);

    validateExtractedBasketIdData(hotelId, sortKey);

    DynamoDbAsyncTable<BasketEntity> basketTable =
        dynamoDbEnhancedAsyncClient.table(dynamoDBProperties.getTableName(), tableSchema);

    var future = basketTable.getItem(BasketEntity.builder()
        .threeLetterHotelId(hotelId)
        .sortKey(sortKey)
        .build());

    return future.thenApply(Optional::ofNullable).join();
  }

  public void deleteBasket(final String partitionKey, final String sortKey) {
    DynamoDbAsyncTable<BasketEntity> basketTable =
        dynamoDbEnhancedAsyncClient.table(dynamoDBProperties.getTableName(), tableSchema);

    Key key = Key.builder()
        .partitionValue(partitionKey)
        .sortValue(sortKey)
        .build();

    basketTable.deleteItem(key).join();
  }

  private void validateExtractedBasketIdData(final String hotelId, final String sortKey) {
    if (Strings.isEmpty(hotelId) || Strings.isEmpty(sortKey)
        || sortKey.length() < BASKET_ID_LENGTH) {
      var exception = new InvalidBasketIdException(ErrorCode.BASKET_INVALID_EXCEPTION,
          "Invalid basketId");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }
}

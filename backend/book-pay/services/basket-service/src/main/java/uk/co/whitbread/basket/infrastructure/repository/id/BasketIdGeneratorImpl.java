package uk.co.whitbread.basket.infrastructure.repository.id;

import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.infrastructure.repository.config.DynamoDbProperties;
import uk.co.whitbread.basket.infrastructure.repository.exception.InvalidBasketIdException;
import uk.co.whitbread.basket.infrastructure.repository.exception.InvalidBasketReferenceException;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketEntity;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Component("digitBasketIdGenerator")
@RequiredArgsConstructor
@Slf4j
public class BasketIdGeneratorImpl implements BasketIdGenerator {

  public static final String ID = "DIGIT";

  private static final SecureRandom rand = new SecureRandom();
  //Standard UUID RFC-4122, 32 chars and 4 dashes
  private static final int BASKET_ID_LENGTH = 36;
  private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
  private final DynamoDbProperties dynamoDBProperties;
  private final TableSchema<BasketEntity> tableSchema;


  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String generateReference(String hotelId) {

    validateHotelId(hotelId);

    DynamoDbAsyncTable<BasketEntity> basketTable =
        dynamoDbEnhancedAsyncClient.table(dynamoDBProperties.getTableName(), tableSchema);

    DynamoDbAsyncIndex<BasketEntity> basketsByReference = basketTable.index("referenceIndex");

    // Prevent replacing an existing basket
    boolean present;
    String reference;

    do {
      int generatedResNo = rand.nextInt(9999999);
      final var tempReference = hotelId.concat(String.format("%07d", generatedResNo));

      final var basketByRefPublisher =
          basketsByReference.query(r -> r.queryConditional(keyEqualTo(k -> k.partitionValue(tempReference))));

      final var items = Objects.requireNonNull(Flux.from(basketByRefPublisher).blockFirst()).items();

      present = !CollectionUtils.isEmpty(items);
      reference = tempReference;
    } while (present);

    return reference;
  }


  @Override
  public String generateSortKey(String hotelId) {
    validateHotelId(hotelId);
    String sortKey;

    DynamoDbAsyncTable<BasketEntity> basketTable =
        dynamoDbEnhancedAsyncClient.table(dynamoDBProperties.getTableName(), tableSchema);

    // Prevent replacing an existing basket
    boolean present;

    do {
      sortKey = UUID.randomUUID().toString();

      var future =
          basketTable.getItem(BasketEntity.builder().threeLetterHotelId(hotelId).sortKey(sortKey).build());
      present = future.thenApply(Optional::ofNullable).join().isPresent();
    } while (present);

    return sortKey;
  }

  @Override
  public String generateBasketId(String hotelId, String sortKey) {
    validateHotelId(hotelId);
    validateSortKey(sortKey);
    return String.format("%s-%s", hotelId, sortKey);
  }

  @Override
  public String extractBasketIdHotelId(String basketId) {
    validateBasketId(basketId);
    return basketId.substring(0, 3);
  }

  @Override
  public String extractBasketIdSortKey(String basketId) {
    validateBasketId(basketId);
    return basketId.substring(4);
  }

  private void validateHotelId(String hotelId) {
    if (hotelId == null || hotelId.length() != 3) {
      var exception = new InvalidBasketReferenceException(ErrorCode.BASKET_REF_INVALID_EXCEPTION,
          "Invalid hotelId");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private void validateSortKey(String sortKey) {
    Pattern uuidRegex =
        Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    if (sortKey == null || sortKey.isEmpty() || !uuidRegex.matcher(sortKey).matches()) {
      var exception = new InvalidBasketReferenceException(
          ErrorCode.BASKET_SORTKEY_INVALID_EXCEPTION,
          "Invalid sortKey");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private void validateBasketId(String basketId) {
    if (basketId == null || basketId.length() < BASKET_ID_LENGTH) {
      var exception = new InvalidBasketIdException(ErrorCode.GENERATED_INVALID_EXCEPTION,
          "Invalid basket id");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }
}

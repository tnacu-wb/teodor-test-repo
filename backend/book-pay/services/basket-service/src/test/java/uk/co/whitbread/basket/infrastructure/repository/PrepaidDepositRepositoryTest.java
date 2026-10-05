package uk.co.whitbread.basket.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import uk.co.whitbread.basket.infrastructure.repository.config.DynamoDbProperties;
import uk.co.whitbread.basket.infrastructure.repository.exception.BasketNotFoundException;
import uk.co.whitbread.basket.infrastructure.repository.model.PrepaidDepositEntity;

@ExtendWith(MockitoExtension.class)
public class PrepaidDepositRepositoryTest {

  @Mock
  private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
  @Mock
  private DynamoDbProperties dynamoDBProperties;
  @Mock
  private TableSchema<PrepaidDepositEntity> tableSchema;

  @InjectMocks
  private PrepaidDepositRepository prepaidDepositRepository;

  @Test
  void testSave() {
    // Arrange
    PrepaidDepositEntity prepaidDepositEntity = PrepaidDepositEntity.builder()
        .reservationId("TST")
        .build();
    var table = mock(DynamoDbAsyncTable.class);
    var future = CompletableFuture.supplyAsync(() -> null);
    when(table.putItem(any(PrepaidDepositEntity.class))).thenReturn(future);
    when(table.getItem(any(PrepaidDepositEntity.class))).thenReturn(CompletableFuture.completedFuture(prepaidDepositEntity));
    when(dynamoDbEnhancedAsyncClient.table(any(), any()))
        .thenReturn(table);

    // Act
    Mono<PrepaidDepositEntity> result = prepaidDepositRepository.save(prepaidDepositEntity);
    PrepaidDepositEntity depositEntity = result.block();

    // Assert
    MatcherAssert.assertThat(depositEntity.getReservationId(), is("TST"));
    verify(table, times(1)).putItem(any(PrepaidDepositEntity.class));
  }

  @Test
  void test_getReservationPrepaidDeposits_success() {
    DynamoDbAsyncTable table = mock(DynamoDbAsyncTable.class);

    final SdkPublisher<Page<PrepaidDepositEntity>> adapt = SdkPublisher.adapt(
        Mono.just(Page.create(List.of(PrepaidDepositEntity.builder().build()))));

    QueryConditional queryConditional = QueryConditional
        .keyEqualTo(Key.builder().partitionValue("reservationId")
            .build());

    when(dynamoDbEnhancedAsyncClient.table(any(), any())).thenReturn(table);

    when(table.query(QueryEnhancedRequest.builder()
        .queryConditional(queryConditional).build())).thenReturn(PagePublisher.create(adapt));

    // Act
    List<PrepaidDepositEntity> result = prepaidDepositRepository.get("reservationId");

    // Assert
    assertThat(result).isNotNull();
  }

  @Test
  void test_getReservationPrepaidDeposits_noChargesFound() {
    DynamoDbAsyncTable table = mock(DynamoDbAsyncTable.class);

    final SdkPublisher<Page<PrepaidDepositEntity>> adapt = SdkPublisher.adapt(
        Mono.just(Page.create(List.of())));

    QueryConditional queryConditional = QueryConditional
        .keyEqualTo(Key.builder().partitionValue("reservationId")
            .build());

    when(dynamoDbEnhancedAsyncClient.table(any(), any())).thenReturn(table);

    when(table.query(QueryEnhancedRequest.builder()
        .queryConditional(queryConditional).build())).thenReturn(PagePublisher.create(adapt));

    assertThrows(BasketNotFoundException.class,
        () -> prepaidDepositRepository.get("reservationId"));
  }

  
}

package uk.co.whitbread.basket.infrastructure.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import uk.co.whitbread.basket.infrastructure.repository.config.DynamoDbProperties;
import uk.co.whitbread.basket.infrastructure.repository.exception.InvalidBasketIdException;
import uk.co.whitbread.basket.infrastructure.repository.id.BasketIdService;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketEntity;

@ExtendWith(MockitoExtension.class)
class BasketRepositoryTest {

    @InjectMocks
    private BasketRepository basketRepository;

    @Mock
    private DynamoDbProperties dynamoDBProperties;

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Mock
    private BasketIdService basketIdService;

    private static final String SORT_KEY = UUID.randomUUID().toString();

    @Test
    void testSave() {
        // Arrange
        final String sortKey = UUID.randomUUID().toString();
        BasketEntity basketEntity = BasketEntity.builder()
            .hotelId("TST")
            .reference("TST1231231")
            .basketId("TST-".concat(sortKey))
            .build();

        var table = mock(DynamoDbAsyncTable.class);
        var future = CompletableFuture.supplyAsync(() -> null);
        when(table.putItem(any(BasketEntity.class))).thenReturn(future);
        when(table.getItem(any(BasketEntity.class))).thenReturn(CompletableFuture.completedFuture(basketEntity));
        when(dynamoDbEnhancedAsyncClient.table(any(), any()))
            .thenReturn(table);

        // Act
        Mono<BasketEntity> result = basketRepository.save(basketEntity);
        BasketEntity basket = result.block();

        // Assert
        assertThat(basket.getHotelId(), is("TST"));
        assertThat(basket.getBasketId(), is("TST-".concat(sortKey)));
        assertThat(basket.getReference(), is("TST1231231"));
        verify(table, times(1)).putItem(any(BasketEntity.class));
    }

    @Test
    void getByBasketId__ShouldReturnOk(){
        // Arrange
        var table = mock(DynamoDbAsyncTable.class);

        when(basketIdService.extractBasketIdHotelId(anyString())).thenReturn("BasketId");
        when(basketIdService.extractBasketIdSortKey(anyString())).thenReturn("BasketIdSortWith36Characters********************************");
        when(dynamoDbEnhancedAsyncClient.table(any(), any())).thenReturn(table);
        when(table.getItem(any(BasketEntity.class))).thenReturn(CompletableFuture.completedFuture(mockBasketEntity()));


        // Act
        var result = basketRepository.getByBasketId("basketId");

        BasketEntity basket = result.get();
        // Assert

        assertThat(basket, notNullValue());
    }

    @Test
    void updateBasket__ShouldReturnOk(){
        //Arrange
        var table = mock(DynamoDbAsyncTable.class);
        when(dynamoDbEnhancedAsyncClient.table(any(), any())).thenReturn(table);
        when(table.updateItem(any(BasketEntity.class))).thenReturn(CompletableFuture.completedFuture(mockBasketEntity()));

        // Act
        var result = basketRepository.updateBasket(BasketEntity.builder().build());
        var basket = result.block();

        // Assert
        assertThat(basket, notNullValue());

    }

    @Test
    void testDeleteBasket_success() {
        // Arrange
        DynamoDbAsyncTable table = mock(DynamoDbAsyncTable.class);
        var future = CompletableFuture.completedFuture(BasketEntity.builder().hotelId("ABCABC").build());
        when(table.deleteItem(any(Key.class))).thenReturn(future);
        when(dynamoDbEnhancedAsyncClient.table(any(), any())).thenReturn(table);

        // Act
        basketRepository.deleteBasket("TST", UUID.randomUUID().toString());

        // Assert
        verify(table, times(1)).deleteItem(any(Key.class));
    }

    @Test
    void testGetByReference_success() {
        // Arrange
        DynamoDbAsyncTable table = mock(DynamoDbAsyncTable.class);
        DynamoDbAsyncIndex basketsByReference= mock(DynamoDbAsyncIndex.class);
        var future = CompletableFuture.completedFuture(null);

        when(dynamoDbEnhancedAsyncClient.table(any(), any())).thenReturn(table);
        when(table.index(any())).thenReturn(basketsByReference);

        final BasketEntity basketEntity =
            BasketEntity.builder().reference("TST1231231").sortKey(SORT_KEY).threeLetterHotelId("TST").build();
        final SdkPublisher<Page<BasketEntity>> adapt = SdkPublisher.adapt(Mono.just(Page.create(List.of(
            basketEntity))));

        when(basketsByReference.query(any(Consumer.class))).thenReturn(adapt);
        when(table.getItem(any(BasketEntity.class))).thenReturn(CompletableFuture.completedFuture(
            basketEntity.toBuilder()
                .basketId("TST-".concat(SORT_KEY))
                .hotelId("Test")
                .channel("PI")
                .sendMail(Boolean.TRUE)
                .build()));

        // Act
        Optional<BasketEntity> result = basketRepository.getByReference("TST1231231");

        // Assert
        assertThat(result.isPresent(), is(true));
        final BasketEntity basket = result.get();
        assertThat(basket.getHotelId(), is("Test"));
        assertThat(basket.getThreeLetterHotelId(), is("TST"));
        assertThat(basket.getReference(), is("TST1231231"));
    }

    @Test
    void testGetByReference_notFound() {
        // Arrange
        DynamoDbAsyncTable table = mock(DynamoDbAsyncTable.class);
        DynamoDbAsyncIndex basketsByReference= mock(DynamoDbAsyncIndex.class);
        var future = CompletableFuture.completedFuture(null);

        when(dynamoDbEnhancedAsyncClient.table(any(), any())).thenReturn(table);
        when(table.index(any())).thenReturn(basketsByReference);

        final SdkPublisher<Page<BasketEntity>> adapt = SdkPublisher.adapt(Mono.just(Page.create(List.of())));

        when(basketsByReference.query(any(Consumer.class))).thenReturn(adapt);

        // Act
        Optional<BasketEntity> result = basketRepository.getByReference("TST1231231");

        // Assert
        assertThat(result.isPresent(), is(false));
    }

    @Test
    void getByBasketId__ShouldReturnBasketNotFoundException(){
        // Arrange
        when(basketIdService.extractBasketIdHotelId(anyString())).thenReturn(null);
        when(basketIdService.extractBasketIdSortKey(anyString())).thenReturn(null);

        // Act
         assertThrows(InvalidBasketIdException.class,
            () ->       basketRepository.getByBasketId("basketId"));
    }

    private BasketEntity mockBasketEntity() {
        return BasketEntity.builder()
                .hotelId("TST")
                .reference("TST1231231")
                .build();
    }
}

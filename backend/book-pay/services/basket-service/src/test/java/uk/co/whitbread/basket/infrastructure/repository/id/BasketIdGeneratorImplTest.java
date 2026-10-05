package uk.co.whitbread.basket.infrastructure.repository.id;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.infrastructure.repository.config.DynamoDbProperties;
import uk.co.whitbread.basket.infrastructure.repository.exception.InvalidBasketIdException;
import uk.co.whitbread.basket.infrastructure.repository.exception.InvalidBasketReferenceException;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketEntity;

@ExtendWith(MockitoExtension.class)
class BasketIdGeneratorImplTest {
    @InjectMocks
    private BasketIdGeneratorImpl basketIdGenerator;

    @Mock
    private DynamoDbProperties dynamoDBProperties;

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;


    @ParameterizedTest
    @ValueSource(strings = {"1", "null"})
    void testValidateBasketId_InvalidBasketIdException(String basketId) {
        //Arrange
        String basketIdParam = basketId.equals("null") ? null : basketId;
        //Act
        var exception = assertThrows(InvalidBasketIdException.class,
            () -> {
                ReflectionTestUtils.invokeMethod(basketIdGenerator, "validateBasketId",
                    basketIdParam);
            });
        //Assert
        String actualMessage = exception.getMessage();
        assertEquals("Invalid basket id", actualMessage);
    }

    @Test
    void testGenerateReference_success() {
        // Arrage
        DynamoDbAsyncTable table = mock(DynamoDbAsyncTable.class);
        DynamoDbAsyncIndex basketsByReference= mock(DynamoDbAsyncIndex.class);
        var future = CompletableFuture.completedFuture(null);

        when(dynamoDbEnhancedAsyncClient.table(any(), any())).thenReturn(table);
        when(table.index(any())).thenReturn(basketsByReference);

        final SdkPublisher<Page<BasketEntity>> adapt = SdkPublisher.adapt(Mono.just(Page.create(List.of())));

        when(basketsByReference.query(any(Consumer.class))).thenReturn(adapt);

        // Act
        final String reference = basketIdGenerator.generateReference("TST");

        // Assert
        assertThat(reference, hasLength(10));
        verify(basketsByReference, times(1)).query(any(Consumer.class));
    }

    @Test
    void testGenerateSortKey_empty() {
        // Arrage

        // Act
        final InvalidBasketReferenceException ex = assertThrows(InvalidBasketReferenceException.class, () -> {
            basketIdGenerator.generateSortKey("");

        });

        // Assert
        assertThat(ex.getGlobalErrTextTemplate(), is(ErrorCode.Constants.INVALID_REFERENCE));
        assertThat(ex.getDebugMessage(), is("Invalid hotelId"));
    }

    @Test
    void testGenerateSortKey_null() {
        // Arrage

        // Act
        final InvalidBasketReferenceException ex = assertThrows(InvalidBasketReferenceException.class, () -> {
            basketIdGenerator.generateSortKey(null);

        });

        // Assert
        assertThat(ex.getGlobalErrTextTemplate(), is(ErrorCode.Constants.INVALID_REFERENCE));
        assertThat(ex.getDebugMessage(), is("Invalid hotelId"));
    }

    @Test
    void testGenerateSortKey_retry_success() {
        // Arrange
        DynamoDbAsyncTable table = mock(DynamoDbAsyncTable.class);
        var future = CompletableFuture.completedFuture(BasketEntity.builder().hotelId("DTSTHT").reference("DTSTHT1231231").build());
        var future2 = CompletableFuture.completedFuture(null);
        when(table.getItem(any(BasketEntity.class))).thenReturn(future).thenReturn(future2);
        when(dynamoDbEnhancedAsyncClient.table(any(), any())).thenReturn(table);

        // Act
        final String sortKey = basketIdGenerator.generateSortKey("TST");

        // Assert
        assertThat(sortKey, hasLength(36));
        verify(table, times(2)).getItem(any(BasketEntity.class));
    }

    @Test
    void testGenerateSortKey_success() {
        // Arrange

        // Act
        final String reference = basketIdGenerator.generateBasketId("TST", "9763ce3e-2636-404f-8a68-9e41c954794f");

        // Assert
        assertThat(reference, is("TST-9763ce3e-2636-404f-8a68-9e41c954794f"));
    }

    @Test
    void testGenerateReference_emptyHotelId() {
        // Arrage

        // Act
        final InvalidBasketReferenceException ex = assertThrows(InvalidBasketReferenceException.class, () -> {
            basketIdGenerator.generateBasketId("", "1231231");

        });

        // Assert
        assertThat(ex.getGlobalErrTextTemplate(), is(ErrorCode.Constants.INVALID_REFERENCE));
        assertThat(ex.getDebugMessage(), is("Invalid hotelId"));
    }

    @Test
    void testGenerateReference_nullHotelId() {
        // Arrage

        // Act
        final InvalidBasketReferenceException ex = assertThrows(InvalidBasketReferenceException.class, () -> {
            basketIdGenerator.generateBasketId(null, "1231231");

        });

        // Assert
        assertThat(ex.getGlobalErrTextTemplate(), is(ErrorCode.Constants.INVALID_REFERENCE));
        assertThat(ex.getDebugMessage(), is("Invalid hotelId"));
    }

    @Test
    void testGenerateReference_emptySortKey() {
        // Arrage

        // Act
        final InvalidBasketReferenceException ex = assertThrows(InvalidBasketReferenceException.class, () -> {
            basketIdGenerator.generateBasketId("TST", "");

        });

        // Assert
        assertThat(ex.getGlobalErrTextTemplate(), is(ErrorCode.Constants.INVALID_REFERENCE));
        assertThat(ex.getDebugMessage(), is("Invalid sortKey"));
    }

    @Test
    void testGenerateReference_nullSortKey() {
        // Arrage

        // Act
        final InvalidBasketReferenceException ex = assertThrows(InvalidBasketReferenceException.class, () -> {
            basketIdGenerator.generateBasketId("TST", null);

        });

        // Assert
        assertThat(ex.getGlobalErrTextTemplate(), is(ErrorCode.Constants.INVALID_REFERENCE));
        assertThat(ex.getDebugMessage(), is("Invalid sortKey"));
    }


    @Test
    void testExtractHotelIdFromBasketId_success() {
        // Arrange
        String id = "TST-".concat(UUID.randomUUID().toString());

        // Act
        final String hotelId = basketIdGenerator.extractBasketIdHotelId(id);

        // Assert
        assertThat(hotelId, is("TST"));
    }
}

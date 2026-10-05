package uk.co.whitbread.basket.infrastructure.queue.processor;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static uk.co.whitbread.basket.infrastructure.queue.processor.BasketOrderMapper.CARD_HOLDER_NAME;
import static uk.co.whitbread.basket.infrastructure.queue.processor.BasketOrderMapper.CARD_NUMBER_LAST_4_DIGITS;
import static uk.co.whitbread.basket.infrastructure.queue.processor.BasketOrderMapper.PAYMENT_ID;
import static uk.co.whitbread.basket.infrastructure.queue.processor.BasketOrderMapper.PAYMENT_OPTION;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.payments.out.BasketRequestAction;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.domain.model.payments.out.CardData;
import uk.co.whitbread.basket.infrastructure.queue.exception.BasketOrderException;

@ExtendWith(MockitoExtension.class)
class BasketOrderMapperTest {
    public static final String BASKET_REFERENCE = "REF-5ed1de47-7712-43c7-92d6-148f530f3f10";
    public static final String BOOKING_REFERENCE = "REF1234567";

    @InjectMocks
    private BasketOrderMapper basketOrderMapper;

    @BeforeEach
    public void setUp() {
        Set<String> fields = new HashSet<>();
        fields.add("hotelId");
        fields.add("paymentID");
        ReflectionTestUtils.setField(basketOrderMapper, "rootFields", fields);
    }

    @Test
    void testExtractField_BasketOrderException() {
        var item = new BasketItem();
        var path = new ArrayDeque<>(asList("sourceId"));
        //Act
        var exception = assertThrows(BasketOrderException.class,
            () -> {
                ReflectionTestUtils.invokeMethod(basketOrderMapper, "extractField", item,
                    path);
            });
        //Assert
        String actualMessage = exception.getMessage();
        assertEquals("Unable to process item", actualMessage);
    }

    @Test
    void testGetBasketField_BasketOrderException() {
        var basket = new Basket();
        var fieldName = "basketId";
        //Act
        var exception = assertThrows(BasketOrderException.class,
            () -> {
                ReflectionTestUtils.invokeMethod(basketOrderMapper, "getBasketField", basket,
                    fieldName);
            });
        //Assert
        String actualMessage = exception.getMessage();
        assertEquals("Unable to process item", actualMessage);
    }

    @Test
    void testGetField_BasketOrderException() {
        var basket = new Basket();
        var item = new BasketItem();
        var exception = assertThrows(BasketOrderException.class,
            () -> ReflectionTestUtils.invokeMethod(basketOrderMapper, "getField", item,
                basket, null));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("Unable to process item"));
    }

    @Test
    void testProcessBasket_success() {
        // Arrange
        final var itemTypes = new HashMap<String, Set<String>>();
        itemTypes.put("STAY", new HashSet<>(asList("sourceId", "details.field1", "details.field2")));
        final var items = new ArrayList<BasketItem>();
        final var item1Details = new HashMap<String, String>();
        item1Details.put("field1", "value1");
        item1Details.put("field2", "value2");
        items.add(mockBasketItem("123", "STAY", item1Details));
        final var basket = mockBasket(itemTypes, items);
        final var bookingConfirmationDetails = mockBookingConfirmationDetails();

        // Act
        final var basketOrders = basketOrderMapper.processBasket(basket, BasketRequestAction.COMMIT.getReqAction(),
                bookingConfirmationDetails, null);

        // Assert
        assertThat(basketOrders.size(), is(1));
        assertThat(basketOrders.get(0).getBasketReference(), is(BASKET_REFERENCE));
        assertThat(basketOrders.get(0).getEventId(), is("STAY#123"));
        assertThat(basketOrders.get(0).getData().get("field1"), is("value1"));
        assertThat(basketOrders.get(0).getData().get("field2"), is("value2"));
        assertThat(basketOrders.get(0).getData().get(CARD_HOLDER_NAME), is("John Smith"));
        assertThat(basketOrders.get(0).getData().get(CARD_NUMBER_LAST_4_DIGITS), is("4567"));
        assertThat(basketOrders.get(0).getData().get(PAYMENT_OPTION), is("PAY_ON_ARRIVAL"));
        assertThat(basketOrders.get(0).getData().get(PAYMENT_ID), is("3CPRef"));
    }

    @Test
    void testProcessBasket_nullItems() {
        // Arrange
        final var basket = mockBasket(null, null);
        final var bookingConfirmationDetails = mock(BookingConfirmationDetails.class);


        // Act
        final BasketOrderException ex = assertThrows(BasketOrderException.class,
                () -> basketOrderMapper.processBasket(basket, BasketRequestAction.COMMIT.getReqAction(), bookingConfirmationDetails, null));

        // Assert
        assertThat(ex.getDebugMessage(), is("No items to process"));
        assertThat(ex.getGlobalErrTextTemplate(), is(ErrorCode.Constants.PROCESSING_EXCEPTION));
    }

    @Test
    void testProcessBasket_nullItemTypes() {
        // Arrange
        final var items = new ArrayList<BasketItem>();
        final var item1Details = new HashMap<String, String>();
        item1Details.put("field1", "value1");
        item1Details.put("field2", "value2");
        items.add(mockBasketItem("123", "STAY", item1Details));

        final var basket = mockBasket(null, items);
        final var bookingConfirmationDetails = mock(BookingConfirmationDetails.class);

        // Act
        final BasketOrderException ex = assertThrows(BasketOrderException.class,
                () -> basketOrderMapper.processBasket(basket, BasketRequestAction.COMMIT.getReqAction(), bookingConfirmationDetails, null));

        // Assert
        assertThat(ex.getDebugMessage(), is("Unable to process item"));
        assertThat(ex.getGlobalErrTextTemplate(), is(ErrorCode.Constants.PROCESSING_EXCEPTION));
    }

    @ParameterizedTest
    @MethodSource(value = "dataSetForItemTypes")
    void testProcessBasket_itemTypeNotFound(String itemType1, String itemType2, String itemType3) {
        // Arrange
        final var itemTypes = new HashMap<String, Set<String>>();
        itemTypes.put("STAY", new HashSet<>(List.of(itemType1, itemType2, itemType3)));
        final var items = new ArrayList<BasketItem>();
        final var item1Details = new HashMap<String, String>();
        item1Details.put("field1", "value1");
        item1Details.put("field2", "value2");
        items.add(mockBasketItem("123", "ABC", item1Details));
        final var basket = mockBasket(itemTypes, items);
        final var bookingConfirmationDetails = mock(BookingConfirmationDetails.class);

        // Act
        final BasketOrderException ex = assertThrows(BasketOrderException.class,
                () -> basketOrderMapper.processBasket(basket, BasketRequestAction.COMMIT.getReqAction(), bookingConfirmationDetails, null));

        // Assert
        assertThat(ex.getDebugMessage(), is("Unable to process item"));
        assertThat(ex.getGlobalErrTextTemplate(), is(ErrorCode.Constants.PROCESSING_EXCEPTION));
    }


    private static Stream<Arguments> dataSetForItemTypes() {
        return Stream.of(
            arguments("sourceId", "details.field1", "details.field2"),
            arguments("", "", "")
        );
    }


    @Test
    void testProcessBasket_fieldNotFound() {
        // Arrange
        final var itemTypes = new HashMap<String, Set<String>>();
        itemTypes.put("STAY", new HashSet<>(asList("sourceId", "otherField", "details.field2")));
        final var items = new ArrayList<BasketItem>();
        final var item1Details = new HashMap<String, String>();
        item1Details.put("field1", "value1");
        item1Details.put("field2", "value2");
        items.add(mockBasketItem("123", "STAY", item1Details));
        final var basket = mockBasket(itemTypes, items);
        final var bookingConfirmationDetails = mock(BookingConfirmationDetails.class);

        // Act
        final BasketOrderException ex = assertThrows(BasketOrderException.class,
                () -> basketOrderMapper.processBasket(basket, BasketRequestAction.COMMIT.getReqAction(), bookingConfirmationDetails, null));

        // Assert
        assertThat(ex.getDebugMessage(), is("Unable to process item . Error while trying to extract the order field name."));
        assertThat(ex.getGlobalErrTextTemplate(), is(ErrorCode.Constants.PROCESSING_EXCEPTION));
    }

    @Test
    void testProcessBasket_fieldNotFoundMap() {
        // Arrange
        final var itemTypes = new HashMap<String, Set<String>>();
        itemTypes.put("STAY", new HashSet<>(asList("sourceId", "details.otherField", "details.field2")));
        final var items = new ArrayList<BasketItem>();
        final var item1Details = new HashMap<String, String>();
        item1Details.put("field1", "value1");
        item1Details.put("field2", "value2");
        items.add(mockBasketItem("123", "STAY", item1Details));
        final var basket = mockBasket(itemTypes, items);
        final var bookingConfirmationDetails = mock(BookingConfirmationDetails.class);

        // Act
        final BasketOrderException ex = assertThrows(BasketOrderException.class,
                () -> basketOrderMapper.processBasket(basket, BasketRequestAction.COMMIT.getReqAction(),
                        bookingConfirmationDetails, null));

        // Assert
        assertThat(ex.getDebugMessage(), is("Unable to process item"));
        assertThat(ex.getGlobalErrTextTemplate(), is(ErrorCode.Constants.PROCESSING_EXCEPTION));
    }

    private Basket mockBasket(Map<String, Set<String>> itemTypes, List<BasketItem> items) {
        final var basket = Basket.builder()
            .reference(BOOKING_REFERENCE)
            .basketId(BASKET_REFERENCE)
            .paymentOption("PAY_ON_ARRIVAL")
            .paymentID("3CPRef")
            .emailAddress("user@whitbread.com")
                .threeDSIndicator("1")
            .build();

        basket.setItemTypes(itemTypes);
        basket.setItems(items);

        return basket;
    }

    private BasketItem mockBasketItem(String sourceId, String itemType, Map<String, String> details) {
        return BasketItem.builder()
            .sourceId(sourceId)
            .type(itemType)
            .details(details)
            .build();
    }

    private CardData mockCardData() {
        return CardData.builder().token("12345").cardHolderName("John Smith").expirationDate("06/80")
            .last4Digits("4567").build();
    }

    private BookingConfirmationDetails mockBookingConfirmationDetails(){
        return BookingConfirmationDetails.builder()
            .cardData(mockCardData())
            .paymentType("VA")
            .cardType("VA")
            .paymentMethod("DVA")
            .build();
    }
}

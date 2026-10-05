package uk.co.whitbread.basket.infrastructure.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.domain.model.basket.in.CreateBasketRequest;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationInfoPaymentType;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationPaymentCardType;
import uk.co.whitbread.basket.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentOption;
import uk.co.whitbread.basket.domain.model.payments.out.BasketPaymentStatus;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.basket.generated.models.ohip.ReservationByBasketRefResponseDto;
import uk.co.whitbread.basket.generated.models.ohip.UdfsRequestDto;
import uk.co.whitbread.basket.infrastructure.repository.exception.BasketNotFoundException;
import uk.co.whitbread.basket.infrastructure.repository.exception.InvalidBasketIdException;
import uk.co.whitbread.basket.infrastructure.repository.id.BasketIdService;
import uk.co.whitbread.basket.infrastructure.repository.mapper.BasketEntityItemTypesMapper;
import uk.co.whitbread.basket.infrastructure.repository.mapper.BasketEntityMapper;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketItemEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketPaymentStatusEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketStatusEntity;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.service.OhipAdapterClient;

@ExtendWith(MockitoExtension.class)
class BasketOutPortTest {
    private final Long cleanUpTimeSeconds = 500L;
    private BasketOutPort basketOutPort;
    @Mock
    private BasketRepository basketRepository;
    @Mock
    private BasketEntityMapper basketEntityMapper;
    @Mock
    private BasketIdService basketIdService;
    @Mock
    private HotelInfoOutPort hotelInfoOutPort;
    @Mock
    private CleanUpTime cleanUpTime;
    @Mock
    private OhipAdapterClient ohipAdapterClient;
    private BasketEntityItemTypesMapper basketEntityItemTypesMapper;

    private final String SORT_KEY = UUID.randomUUID().toString();
    private static final Instant FIXED_INSTANT = Instant.parse("2024-10-21T15:00:00Z");
    private static final long CLEAN_UP_TIME_LONG = 5000;

    @BeforeEach
    public void init() {
        basketEntityItemTypesMapper = Mappers.getMapper(BasketEntityItemTypesMapper.class);
        basketOutPort = new BasketOutPortImpl(basketRepository,
            basketEntityMapper, basketIdService, hotelInfoOutPort, cleanUpTime, ohipAdapterClient);
    }

    @Test
    void testCreateBasket() {
        // Arrange
        when(hotelInfoOutPort.getHotelInfo("ABHotelus")).thenReturn(HotelInfo.builder().threeLetterId("ABC").build());
        when(basketIdService.generateSortKey("ABC")).thenReturn(SORT_KEY);
        when(basketIdService.generateBasketId("ABC", SORT_KEY)).thenReturn("ABC-".concat(SORT_KEY));
        when(basketIdService.generateReference("ABC")).thenReturn("ABC2233445");
        when(basketRepository.save(any(BasketEntity.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        when(basketEntityMapper.toDomainModel(any(BasketEntity.class))).thenAnswer(i -> mockBasket((BasketEntity) i.getArguments()[0]));
        when(cleanUpTime.getCleanUpTime(any(), any())).thenReturn(cleanUpTimeSeconds);

        // Act
        CreateBasketRequest createBasketRequest = CreateBasketRequest.builder()
                .hotelId("ABHotelus")
                .userId("USR")
                .originalBasketId("TST4564564")
                .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
                .idContext("CTX")
                .build();

        Basket basket = basketOutPort.createBasket(createBasketRequest);

        // Assert
        assertThat(basket.getHotelId(), is("ABHotelus"));
        assertThat(basket.getReference(), is("ABC2233445"));
        assertThat(basket.getBasketId(), is("ABC-".concat(SORT_KEY)));
        assertThat(basket.getUserId(), is("USR"));
        assertThat(basket.getStatus().toString(), is(BasketStatus.OPEN.toString()));
        assertThat(basket.getPaymentOption(), is(PaymentOption.PAY_ON_ARRIVAL.name()));
        assertThat(basket.getCreatedAt(), is(notNullValue()));
        assertThat(basket.getCleanUpTime(), is(cleanUpTimeSeconds));
        assertEquals("CTX", basket.getIdContext());
        verify(basketIdService, times(1)).generateSortKey("ABC");
        verify(basketIdService, times(1)).generateBasketId("ABC", SORT_KEY);
    }

    @ParameterizedTest
    @CsvSource({
        "KIOSK, ,",
        "PI, MOBILE",
        "PI, WEB",
        "WEB, ,",
        "WEB, MOBILE"
    })
    void testCreateBasket_ShouldBlockSaveOnlyForKioskOrPiMobileChannel(String channel, String subChannel) {
        // Arrange
        when(hotelInfoOutPort.getHotelInfo("ABHotelus")).thenReturn(HotelInfo.builder().threeLetterId("ABC").build());
        when(basketIdService.generateSortKey("ABC")).thenReturn(SORT_KEY);
        when(basketIdService.generateBasketId("ABC", SORT_KEY)).thenReturn("ABC-".concat(SORT_KEY));
        when(basketIdService.generateReference("ABC")).thenReturn("ABC2233445");
        when(cleanUpTime.getCleanUpTime(any(), any())).thenReturn(cleanUpTimeSeconds);

        BasketEntity savedEntity = BasketEntity.builder()
            .hotelId("ABHotelus")
            .reference("ABC2233445")
            .basketId("ABC-".concat(SORT_KEY))
            .sortKey(SORT_KEY)
            .userId("USR")
            .status(BasketStatusEntity.OPEN)
            .cleanUpTime(cleanUpTimeSeconds)
            .build();

        when(basketRepository.save(any())).thenReturn(Mono.just(savedEntity));
        when(basketEntityMapper.toDomainModel(any(BasketEntity.class))).thenAnswer(i -> mockBasket((BasketEntity) i.getArguments()[0]));

        // Act
        CreateBasketRequest createBasketRequest = CreateBasketRequest.builder()
            .hotelId("ABHotelus")
            .userId("USR")
            .channel(channel)
            .subChannel(subChannel)
            .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
            .build();

        Basket basket = basketOutPort.createBasket(createBasketRequest);

        // Assert
        assertThat(basket.getHotelId(), is("ABHotelus"));
        assertThat(basket.getReference(), is("ABC2233445"));
        verify(basketRepository, times(1)).save(any(BasketEntity.class));
    }

    @Test
    void testCreateBasket_invalidBasketException() {
        // Arrange
        when(hotelInfoOutPort.getHotelInfo("ABHotelus")).thenReturn(
            HotelInfo.builder().threeLetterId("ABC").build());
        when(basketIdService.generateSortKey("ABC")).thenReturn(SORT_KEY);
        when(basketIdService.generateBasketId("ABC", SORT_KEY)).thenReturn("ABC-".concat(SORT_KEY));
        when(basketRepository.getByBasketId(any())).thenReturn(Optional.of(BasketEntity.builder()
            .build()));

        // Act
        CreateBasketRequest createBasketRequest = CreateBasketRequest.builder()
            .migratedResNo("migration")
            .hotelId("ABHotelus")
            .userId("USR")
            .originalBasketId("TST4564564")
            .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
            .build();

        // Assert
        assertThrows(InvalidBasketIdException.class,
            () -> basketOutPort.createBasket(createBasketRequest));
    }

    @Test
    void testGetBasketById_invalidBasketException() {
        // Arrange
        when(basketRepository.getByBasketId(any())).thenReturn(Optional.empty());
        // Act
        assertThrows(InvalidBasketIdException.class, () -> basketOutPort.getBasketById("123"));
    }

    @Test
    void testUpdateBasketPayment_BasketNotFoundException() {
        // Arrange
        var basket = Basket.builder().basketId("123").build();
        when(basketRepository.getByBasketId(any())).thenReturn(Optional.empty());
        // Act
        assertThrows(
            BasketNotFoundException.class,
            () -> basketOutPort.updateBasketPayment(basket));
    }


    @Test
    void testUpdateBasketStatus_BasketNotFoundException() {
        // Arrange
        when(basketRepository.getByBasketId(any())).thenReturn(Optional.empty());
        // Act
        assertThrows(BasketNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                basketOutPort.updateBasketStatus("123", BasketStatus.OPEN, Optional.empty());
            }
        });
    }

    @Test
    void testUpdateBasketStatus_Success() {
        // Arrange
        final var basketEntity = BasketEntity.builder()
            .hotelId("TST")
            .reference("TST1231231")
            .status(BasketStatusEntity.OPEN)
            .build();
        when(basketRepository.getByBasketId(any())).thenReturn(Optional.of(basketEntity));
        when(cleanUpTime.getCleanUpTime(any(), any())).thenReturn(CLEAN_UP_TIME_LONG);
        when(basketRepository.updateBasket(any(BasketEntity.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        ArgumentCaptor<BasketEntity> captor = ArgumentCaptor.forClass(BasketEntity.class);

        // Act
        basketOutPort.updateBasketStatus("TST1231231", BasketStatus.FAILED, Optional.of(FIXED_INSTANT));

        // Assert
        verify(cleanUpTime, times(1)).getCleanUpTime(BasketStatus.FAILED, FIXED_INSTANT);
        verify(basketRepository, times(1)).updateBasket(captor.capture());
        final var basketEntityResult = captor.getValue();
        assertEquals(CLEAN_UP_TIME_LONG, basketEntityResult.getCleanUpTime());
        assertEquals(BasketStatus.FAILED.name(), basketEntityResult.getStatus().toString());
    }

    @Test
    void testUpdatePollingStartedAt_BasketNotFoundException() {
        // Arrange
        when(basketRepository.getByBasketId(any())).thenReturn(Optional.empty());
        // Act
        assertThrows(
            BasketNotFoundException.class,
            () -> basketOutPort.updatePollingStartedAt("123", "123"));
    }

    @Test
    void testCreateBasket_nullStatus() {
        // Arrange
        when(hotelInfoOutPort.getHotelInfo("ABHotelus")).thenReturn(HotelInfo.builder().threeLetterId("ABC").build());
        when(basketIdService.generateSortKey("ABC")).thenReturn(SORT_KEY);
        when(basketIdService.generateBasketId("ABC", SORT_KEY)).thenReturn("ABC-".concat(SORT_KEY));
        when(basketIdService.generateReference("ABC")).thenReturn("ABC2233445");
        when(basketRepository.save(any(BasketEntity.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        when(basketEntityMapper.toDomainModel(any(BasketEntity.class))).thenAnswer(i -> mockBasket((BasketEntity) i.getArguments()[0]));
        when(cleanUpTime.getCleanUpTime(any(), any())).thenReturn(cleanUpTimeSeconds);

        // Act
        CreateBasketRequest createBasketRequest = CreateBasketRequest.builder()
                .hotelId("ABHotelus")
                .userId("USR")
                .basketStatus(null)
                .build();

        Basket basket = basketOutPort.createBasket(createBasketRequest);

        // Assert
        assertThat(basket.getHotelId(), is("ABHotelus"));
        assertThat(basket.getReference(), is("ABC2233445"));
        assertThat(basket.getBasketId(), is("ABC-".concat(SORT_KEY)));
        assertThat(basket.getUserId(), is("USR"));
        assertThat(basket.getStatus().toString(), is(BasketStatus.OPEN.toString()));
        assertThat(basket.getCreatedAt(), is(notNullValue()));
        assertThat(basket.getCleanUpTime(), is(cleanUpTimeSeconds));
        verify(basketIdService, times(1)).generateSortKey(eq("ABC"));
        verify(basketIdService, times(1)).generateBasketId(eq("ABC"), eq(SORT_KEY));
    }

    @Test
    void testGetBasketByReference_success() {
        // Arrange
        final var basketEntity = BasketEntity.builder()
            .hotelId("TST")
            .reference("TST1231231")
            .status(BasketStatusEntity.OPEN)
            .build();

        when(basketRepository.getByReference(any(String.class))).thenReturn(Optional.of(basketEntity));
        when(basketEntityMapper.toDomainModel(any(BasketEntity.class))).thenAnswer(i -> mockBasket((BasketEntity) i.getArguments()[0]));


        // Act
        final var basketResult = basketOutPort.getBasketByReference("123");

        // Assert
        assertThat(basketResult.get().getHotelId(), is("TST"));
        assertThat(basketResult.get().getReference(), is("TST1231231"));
    }

    @Test
    void testGetBasketByReference_notFound() {
        // Arrange
        when(basketRepository.getByReference(any(String.class))).thenReturn(Optional.empty());

        // Act

        // Assert
        assertEquals(basketOutPort.getBasketByReference("123"), Optional.empty());
    }

    @Test
    void testDeleteBasket() {
        // Arrange
        doNothing().when(basketRepository).deleteBasket(any(String.class), any(String.class));

        // Act
        basketOutPort.deleteBasket("TST", "1231231");

        // Assert
        verify(basketRepository, times(1)).deleteBasket(any(String.class), any(String.class));
    }

    @Test
    void testUpdateBasket() {
        final var basket = Basket.builder()
            .hotelId("TST")
            .basketId("TST-".concat(SORT_KEY))
            .status(BasketStatus.OPEN)
            .build();

        // Arrange
        when(basketRepository.updateBasket(any(BasketEntity.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        when(basketEntityMapper.toDomainModel(any(BasketEntity.class))).thenAnswer(i -> mockBasket((BasketEntity) i.getArguments()[0]));
        when(basketEntityMapper.toEntityModel(any(Basket.class))).thenAnswer(i -> mockBasketEntity((Basket) i.getArguments()[0]));

        // Act
        final var basketResult = basketOutPort.updateBasket(basket);

        // Assert
        assertThat(basketResult.getHotelId(), is("TST"));
        assertThat(basketResult.getBasketId(), is("TST-".concat(SORT_KEY)));
        verify(basketRepository, times(1)).updateBasket(any(BasketEntity.class));
    }

    @Test
    void testCreateMigratedBasket() {
        // Arrange
        when(hotelInfoOutPort.getHotelInfo("TST")).thenReturn(HotelInfo.builder().threeLetterId("ABC").build());
        final String sortKey = "9763ce3e-2636-404f-8a68-9e41c954794f";
        when(basketIdService.generateSortKey("ABC")).thenReturn(sortKey);
        when(basketIdService.generateBasketId("ABC", sortKey))
            .thenReturn("ABC-9763ce3e-2636-404f-8a68-9e41c954794f");
        when(basketRepository.save(any(BasketEntity.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        when(basketEntityMapper.toDomainModel(any(BasketEntity.class)))
            .thenAnswer(i -> mockBasket((BasketEntity) i.getArguments()[0]));
        when(cleanUpTime.getCleanUpTime(any(), any())).thenReturn(cleanUpTimeSeconds);

        // Act
        CreateBasketRequest createBasketRequest = CreateBasketRequest.builder()
            .hotelId("TST")
            .userId("USR")
            .migratedResNo("R231231")
            .build();

        Basket basket = basketOutPort.createBasket(createBasketRequest);

        // Assert
        assertThat(basket.getHotelId(), is("TST"));
        assertThat(basket.getReference(), is("R231231"));
        assertThat(basket.getUserId(), is("USR"));
        assertThat(basket.getStatus().toString(), is(BasketStatus.OPEN.toString()));
        assertThat(basket.getCreatedAt(), is(notNullValue()));
        assertThat(basket.getCleanUpTime(), is(cleanUpTimeSeconds));
        verify(basketIdService, times(1)).generateBasketId("ABC", sortKey);
    }

    @Test
    void getBasketsByReferences_shouldReturnListOfBaskets() {
        // Arrange
        List<String> references = Arrays.asList("ref1", "ref2", "ref3");

        BasketEntity entity1 = new BasketEntity();
        BasketEntity entity2 = new BasketEntity();
        Basket basket1 = new Basket();
        Basket basket2 = new Basket();

        when(basketRepository.getByReference("ref1")).thenReturn(Optional.of(entity1));
        when(basketRepository.getByReference("ref2")).thenReturn(Optional.of(entity2));
        when(basketRepository.getByReference("ref3")).thenReturn(Optional.empty());

        when(basketEntityMapper.toDomainModel(entity1)).thenReturn(basket1);
        when(basketEntityMapper.toDomainModel(entity2)).thenReturn(basket2);

        // Act
        List<Basket> result = basketOutPort.getBasketsByReferences(references);

        // Assert
        assertEquals(2, result.size());
        assertEquals(basket1, result.get(0));
        assertEquals(basket2, result.get(1));

        verify(basketRepository, times(3)).getByReference(any());
        verify(basketEntityMapper, times(2)).toDomainModel(any());
    }

    @Test
    void testUpdateCharacterUdfs() {
        // Arrange
        UdfsRequestDto udfsRequestDto = new UdfsRequestDto();
        doNothing().when(ohipAdapterClient).updateCharacterUdfs(any(UdfsRequestDto.class));

        // Act
        basketOutPort.updateCharacterUdfs(udfsRequestDto);

        // Assert
        verify(ohipAdapterClient, times(1)).updateCharacterUdfs(udfsRequestDto);
    }

    @Test
    void getBasketsByReferences_shouldHandleEmptyList() {
        // Arrange
        List<String> references = List.of();

        // Act
        List<Basket> result = basketOutPort.getBasketsByReferences(references);

        // Assert
        assertEquals(0, result.size());
        verify(basketRepository, never()).getByReference(any());
        verify(basketEntityMapper, never()).toDomainModel(any());
    }

  @Test
  void testCreateBasketReservation() {
    // Arrange
    when(hotelInfoOutPort.getHotelInfo("ABHotelus")).thenReturn(HotelInfo.builder().threeLetterId("ABC").build());
    when(basketIdService.generateSortKey("ABC")).thenReturn(SORT_KEY);
    when(basketIdService.generateBasketId("ABC", SORT_KEY)).thenReturn("ABC-".concat(SORT_KEY));
    when(basketIdService.generateReference("ABC")).thenReturn("ABC2233445");
    when(basketRepository.save(any(BasketEntity.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
    when(basketEntityMapper.toDomainModel(any(BasketEntity.class))).thenAnswer(i -> mockBasket((BasketEntity) i.getArguments()[0]));
    when(cleanUpTime.getCleanUpTime(any(), any())).thenReturn(cleanUpTimeSeconds);

    // Act
    CreateBasketRequest createBasketRequest = CreateBasketRequest.builder()
        .hotelId("ABHotelus")
        .userId("USR")
        .originalBasketId("TST4564564")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .idContext("CTX")
        .build();

    Basket basket = basketOutPort.createBasketReservation(createBasketRequest);

    // Assert
    assertThat(basket.getHotelId(), is("ABHotelus"));
    assertThat(basket.getReference(), is("ABC2233445"));
    assertThat(basket.getBasketId(), is("ABC-".concat(SORT_KEY)));
    assertThat(basket.getUserId(), is("USR"));
    assertThat(basket.getStatus().toString(), is(BasketStatus.OPEN.toString()));
    assertThat(basket.getPaymentOption(), is(PaymentOption.PAY_ON_ARRIVAL.name()));
    assertThat(basket.getCreatedAt(), is(notNullValue()));
    assertThat(basket.getCleanUpTime(), is(cleanUpTimeSeconds));
    assertEquals("CTX", basket.getIdContext());
    verify(basketIdService, times(1)).generateSortKey("ABC");
    verify(basketIdService, times(1)).generateBasketId("ABC", SORT_KEY);
  }

    private Basket mockBasket(BasketEntity basketEntity) {
        final var basketItems = basketEntity.getItems().stream().map(
            entityItem -> BasketItem.builder().details(entityItem.getDetails())
                .sourceId(entityItem.getSourceId()).type(entityItem.getType()).build()).toList();

        final var basketItemTypes =
            basketEntityItemTypesMapper.toModel(basketEntity.getItemTypes());

        return Basket.builder()
                .hotelId(basketEntity.getHotelId())
                .reference(basketEntity.getReference())
                .basketId(basketEntity.getBasketId())
                .sortKey(basketEntity.getSortKey())
                .userId(basketEntity.getUserId())
                .status(BasketStatus.valueOf(basketEntity.getStatus().toString()))
                .createdAt(basketEntity.getCreatedAt())
                .lastModifiedAt(basketEntity.getLastModifiedAt())
                .cleanUpTime(basketEntity.getCleanUpTime())
                .items(basketItems)
                .itemTypes(basketItemTypes)
                .lockingTime(basketEntity.getLockingTime())
                .paymentID(basketEntity.getPaymentID())
                .paymentOption(basketEntity.getPaymentOption())
                .idContext("CTX")
                .build();
    }

    private BasketEntity mockBasketEntity(Basket basket) {
        final var basketItems = basket.getItems().stream().map(
            entityItem -> BasketItemEntity.builder().details(entityItem.getDetails())
                .sourceId(entityItem.getSourceId()).type(entityItem.getType()).build()).toList();

        final var basketItemTypes =
            basketEntityItemTypesMapper.toEntityModel(basket.getItemTypes());

        return BasketEntity.builder()
            .hotelId(basket.getHotelId())
            .reference(basket.getReference())
            .basketId(basket.getBasketId())
            .sortKey(basket.getSortKey())
            .userId(basket.getUserId())
            .status(BasketStatusEntity.valueOf(basket.getStatus().toString()))
            .createdAt(basket.getCreatedAt())
            .lastModifiedAt(basket.getLastModifiedAt())
            .cleanUpTime(basket.getCleanUpTime())
            .items(basketItems)
            .itemTypes(basketItemTypes)
            .lockingTime(basket.getLockingTime())
            .paymentID(basket.getPaymentID())
            .build();
    }

    private static String getTimestampStr(String epochTime) {
        return String.valueOf(Instant.parse(epochTime).toEpochMilli());
    }

    @Test
    void testGetPaymentType() {
      String hotelId = "H100";
      Set<String> ids = Set.of("1", "2");
      List<ReservationInfoPaymentType> expected = List.of(new ReservationInfoPaymentType(
        new ReservationPaymentCardType("BU", 2)));
      when(ohipAdapterClient.getPaymentType(hotelId, ids)).thenReturn(expected);
      List<ReservationInfoPaymentType> result = basketOutPort.getPaymentType(hotelId, ids);
      assertEquals(expected, result);
      verify(ohipAdapterClient).getPaymentType(hotelId, ids);
      verifyNoMoreInteractions(ohipAdapterClient);
    }

  @Test
  void testGetReservationDetails() {
    // Arrange
    String hotelId = "HOTEL1";
    String sourceId = "RES123";

    ReservationByBasketRefResponseDto expectedResponse =
        new ReservationByBasketRefResponseDto();

    when(ohipAdapterClient.getReservationDetails(hotelId, sourceId))
        .thenReturn(expectedResponse);

    // Act
    ReservationByBasketRefResponseDto result =
        basketOutPort.getReservationDetails(hotelId, sourceId);

    // Assert
    assertThat(result, is(expectedResponse));

    verify(ohipAdapterClient, times(1))
        .getReservationDetails(hotelId, sourceId);

    verifyNoMoreInteractions(ohipAdapterClient);
  }

  @Test
  void testUpdateBasketWithPaymentStatus_WithNullPaymentStatus() {
    Basket basket = Basket.builder().basketId("123").status(BasketStatus.COMPLETED).build();
    BasketEntity basketEntity = BasketEntity.builder().basketId("123").build();
    when(basketRepository.getByBasketId("123")).thenReturn(Optional.of(basketEntity));
    basketOutPort.updateBasketWithPaymentStatus(basket);
    assertNull(basketEntity.getPaymentStatus());
  }

  @Test
  void testUpdateBasketWithPaymentStatus_WithCompletedPaymentStatus() {
    Basket basket = Basket.builder().basketId("123").status(BasketStatus.COMPLETED)
            .paymentStatus(BasketPaymentStatus.COMPLETED).build();
    BasketEntity basketEntity = BasketEntity.builder().basketId("123").build();
    when(basketRepository.getByBasketId("123")).thenReturn(Optional.of(basketEntity));
    basketOutPort.updateBasketWithPaymentStatus(basket);
    assertEquals(BasketPaymentStatusEntity.COMPLETED, basketEntity.getPaymentStatus());
  }

  @Test
  void testGetPaymentStatus_IllegalArgumentException() {
    Basket basket = Basket.builder().basketId("123").status(BasketStatus.COMPLETED)
            .paymentStatus(BasketPaymentStatus.COMPLETED).build();
    BasketEntity basketEntity = BasketEntity.builder().basketId("123").build();
    when(basketRepository.getByBasketId("123")).thenReturn(Optional.of(basketEntity));
    try (MockedStatic<BasketPaymentStatusEntity> mockedStatic = Mockito.mockStatic(BasketPaymentStatusEntity.class)) {
        mockedStatic.when(() -> BasketPaymentStatusEntity.valueOf("COMPLETED"))
                .thenThrow(new IllegalArgumentException("exception"));
            basketOutPort.updateBasketWithPaymentStatus(basket);
            assertNull(basketEntity.getPaymentStatus());
    }
  }

  @Test
  void testUpdateBasketWithPaymentStatus_BasketNotFoundException() {
    var basket = Basket.builder().basketId("987").build();
    when(basketRepository.getByBasketId(any())).thenReturn(Optional.empty());
    assertThrows(BasketNotFoundException.class, () -> basketOutPort.updateBasketWithPaymentStatus(basket));
  }
}

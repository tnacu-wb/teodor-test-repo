package uk.co.whitbread.ondemandrefreshservice.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.HotelAvailabilitiesBatchOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.config.PostgresIntegrationTestConfig;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RateCategory;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RoomEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.read.HotelJpaRepository;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.read.RateJpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Class not compatible with Kaniko")
@DirtiesContext
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {PostgresIntegrationTestConfig.class})
class HotelAvailabilityDBBatchServiceIT {

  @Autowired
  private HotelAvailabilitiesBatchOutPort dbBatchService;

  @Autowired
  private HotelJpaRepository hotelJpaRepository;

  @Autowired
  private RateJpaRepository rateJpaRepository;

  private static final String HOTEL_CODE = "TKINPT";

  private static final String UNDERSCORE = "_";

  private static final String OPERA = "OPERA";

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private static final Set<String> roomTypes = new HashSet<>(Arrays.asList("DB","SB","TWIN"));

  private List<RatePlanEntity> rates;

  private List<RoomEntity> rooms;

  @BeforeEach
  void setUp() {
    rates = new ArrayList<>();
    rooms = new ArrayList<>();
  }

  @AfterEach
  final void tearDown() {
    clearRatesNRooms();
  }

  @Test
  void insertHotelEntityTest_Success() {
    final HotelEntity hotelToSave = buildMockHotelEntity(10, BigDecimal.valueOf(100));
    dbBatchService.persistHotelEntities(new ArrayList<>(Arrays.asList(hotelToSave)));
    final Optional<HotelEntity> hotelFrmDB = hotelJpaRepository.findById(hotelToSave.getId());
    assertTrue(hotelFrmDB.isPresent());
    final RatePlanEntity ratePlanEntityToSave = hotelToSave.getRates().stream().findFirst().get();
    final Optional<RatePlanEntity> ratePlanEntityFrmDB = rateJpaRepository.findById(ratePlanEntityToSave.getId());
    assertTrue(ratePlanEntityFrmDB.isPresent());
    assertEquals(ratePlanEntityToSave.getAmount().toBigInteger(), ratePlanEntityFrmDB.get().getAmount().toBigInteger());
  }

  @Test
  void updateHotelEntityTest_Success() {
    //1. Save the hotel entity in DB
    final HotelEntity hotelEntityToPersist = buildMockHotelEntity(10, BigDecimal.valueOf(100));
    dbBatchService.persistHotelEntities(new ArrayList<>(Arrays.asList(hotelEntityToPersist)));
    //2. Verify hotel entity saved in DB
    final Optional<HotelEntity> hotelFrmDB = hotelJpaRepository.findById(hotelEntityToPersist.getId());
    assertTrue(hotelFrmDB.isPresent());
    //3. Update the hotel entity to be saved in DB
    clearRatesNRooms();
    final HotelEntity updatedHotelEntityToPersist = buildMockHotelEntity(20, BigDecimal.valueOf(200));
    dbBatchService.persistHotelEntities(new ArrayList<>(Arrays.asList(updatedHotelEntityToPersist)));
    //4. Retrieve updated hotel entity from DB
    final Optional<HotelEntity> hotelEntityFrmDBAfterUpdate = hotelJpaRepository.findById(updatedHotelEntityToPersist.getId());
    assertTrue(hotelEntityFrmDBAfterUpdate.isPresent());
    //5. Retrieve updated rate entity from DB
    final RatePlanEntity updatedRatePlanEntityToPersist = updatedHotelEntityToPersist.getRates().stream().findFirst().get();
    final Optional<RatePlanEntity> ratePlanEntityFrmDBAfterUpdate = rateJpaRepository.findById(updatedRatePlanEntityToPersist.getId());
    //5. Verify rates updated in DB
    assertTrue(ratePlanEntityFrmDBAfterUpdate.isPresent());
    assertEquals(updatedRatePlanEntityToPersist.getAmount().toBigInteger(), ratePlanEntityFrmDBAfterUpdate.get().getAmount().toBigInteger());
  }

  private HotelEntity buildMockHotelEntity(final int roomQty, final BigDecimal amount){
    roomTypes.stream().forEach(roomType -> {
      //create rates for each room type
      Stream.of(RateCategory.values()).forEach(rateCategory -> {
        final RatePlanEntity rate = buildRatePlanEntity(roomType, rateCategory, amount);
        rates.add(rate);
      });
      final RoomEntity room = buildRoomEntity(roomType, rates, roomQty);
      rooms.add(room);
    });
    return buildHotelEntity(rates, rooms);
  }



  private static RoomEntity buildRoomEntity(final String roomType, final List<RatePlanEntity> rates, final int quantity) {
    final String roomId = new StringBuilder(roomType).append(UNDERSCORE)
        .append(HOTEL_CODE).append(UNDERSCORE)
        .append(OPERA).append(UNDERSCORE)
        .append(LocalDate.now().format(formatter)).toString();
    return RoomEntity.builder()
        .id(roomId)
        .quantity(quantity)
        .roomType(roomType)
        .rates(rates).build();
  }

  private static RatePlanEntity buildRatePlanEntity(final String roomType, final RateCategory rateCategory, final BigDecimal amount) {
    final String rateId = new StringBuilder(rateCategory.rateCategory()).append(UNDERSCORE)
        .append(roomType).append(UNDERSCORE)
        .append(HOTEL_CODE).append(UNDERSCORE)
        .append(OPERA).append(UNDERSCORE)
        .append(LocalDate.now().format(formatter)).toString();
    return RatePlanEntity.builder()
        .id(rateId)
        .rateCode(rateCategory.name())
        .rateClassification(rateCategory.rateCategory())
        .currency("G")
        .amount(amount)
        .availability(true)
        .minNights(0)
        .maxNights(0).build();
  }

  private static HotelEntity buildHotelEntity(final List<RatePlanEntity> rates, final List<RoomEntity> rooms) {
    final String hotelId = new StringBuilder(HOTEL_CODE).append(UNDERSCORE)
        .append(OPERA).append(UNDERSCORE)
        .append(LocalDate.now().format(formatter)).toString();
    return HotelEntity.builder()
        .id(hotelId)
        .hotelCode(HOTEL_CODE)
        .pmsSource(OPERA)
        .date(LocalDate.now())
        .rooms(rooms)
        .rates(rates).build();
  }

  private void clearRatesNRooms(){
    rates.clear();
    rooms.clear();
  }

}

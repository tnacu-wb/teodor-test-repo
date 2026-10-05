package uk.co.whitbread.avail.business.events.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.co.whitbread.avail.business.events.AvailabilityBusinessEventApplication;
import uk.co.whitbread.avail.business.events.infrastructure.entity.HotelEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.ProcessedEventEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RoomEntity;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AvailabilityBusinessEventApplication.class},
    properties = {
        "opera.clientId=testUserName",
        "opera.clientSecret=testPassword",
        "SubscribeBusinessEventsColdStartSvc.runner.enabled=false",
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.hibernate.default_schema=avail_cache"
    })
@ContextConfiguration(initializers = {HotelEntityRepositoryIT.Initializer.class})
@DirtiesContext
@Testcontainers
//Ignoring temporarily as docker issues in Jenkins build in QA & UAT env
@Disabled("Class not compatible with Kaniko")
public class HotelEntityRepositoryIT {

  @Autowired
  private HotelEntityRepository hotelEntityRepository;

  @Autowired
  private RateEntityJpaRepository rateEntityJpaRepository;

  @Autowired
  private RoomEntityJpaRepository roomEntityJpaRepository;

  @Autowired
  private ProcessedEventJpaRepository processedEventJpaRepository;

  final String roomType = "TWIN";
  final String appKey = "test_app_key";
  final BigInteger offset = BigInteger.valueOf(123);

  @Container
  public static final JdbcDatabaseContainer postgreSQLContainer =
      new PostgreSQLContainer("latest")
          .withInitScript("schema.sql")
          .withDatabaseName("integration-tests-db")
          .withUsername("sa")
          .withPassword("sa");

  static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
          "spring.datasource.writer.jdbc-url=" + postgreSQLContainer.getJdbcUrl(),
          "spring.datasource.writer.username=" + postgreSQLContainer.getUsername(),
          "spring.datasource.writer.password=" + postgreSQLContainer.getPassword()
      ).applyTo(configurableApplicationContext.getEnvironment());
    }
  }

  //start summary total test cases
  @Test
  @Transactional
  public void summaryTotalNewRecordsShouldBeInsertedSuccessfully() {
    processedEventJpaRepository.deleteAll();
    final String hotelCode = "LONSUT";
    final int year = LocalDate.now().getYear() + 1;
    final LocalDate localDate1 = LocalDate.of(year, 05, 18);
    final String hotelId = hotelCode + "_OPERA_" + localDate1.format(
        DateTimeFormatter.ISO_LOCAL_DATE);

    //Before insertion record should not be there
    Optional<HotelEntity> optionalHotelEntityBeforeInsertion
        = hotelEntityRepository.findById(hotelId);
    assertFalse(optionalHotelEntityBeforeInsertion.isPresent());

    final List<RatePlanEntity> rates = buildRatePlanEntities(hotelId);
    final List<RoomEntity> rooms = buildRoomEntities(hotelId, 16, "PRE");
    final HotelEntity hotelEntity1 =
        buildHotelEntity(hotelId, hotelCode, localDate1, "OPERA", rates, rooms);

    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity(appKey, BigInteger.valueOf(123));

    hotelEntityRepository.updateSummaryTotals(hotelEntity1, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntity = hotelEntityRepository.findById(hotelId);
    assertTrue(optionalHotelEntity.isPresent());
    final HotelEntity hotelEntity = optionalHotelEntity.get();
    final List<RoomEntity> roomEntityList = hotelEntity.getRooms();
    assertEquals(hotelEntity1, hotelEntity);
    assertEquals(hotelEntity1.getRooms().size(), roomEntityList.size());


    Optional<ProcessedEventEntity> optionalProcessedEventEntity =
        processedEventJpaRepository.findById(appKey);

    assertTrue(optionalProcessedEventEntity.isPresent());
    final ProcessedEventEntity processedEventEntityFromDb = optionalProcessedEventEntity.get();
    assertEquals(processedEventEntity, processedEventEntityFromDb);

  }

  @Test
  @Transactional
  public void newRoomsShouldBeAddedToExistingHotelRecord() {
    processedEventJpaRepository.deleteAll();
    final String hotelCode = "LONSUT";
    final int year = LocalDate.now().getYear() + 1;
    final LocalDate localDate1 = LocalDate.of(year, 05, 18);
    final String hotelId = hotelCode + "_OPERA_" + localDate1.format(
        DateTimeFormatter.ISO_LOCAL_DATE);

    //Before insertion record should not be there
    Optional<HotelEntity> optionalHotelEntityBeforeInsertion
        = hotelEntityRepository.findById(hotelId);
    assertFalse(optionalHotelEntityBeforeInsertion.isPresent());

    //insert a HotelEntity now with one room
    final List<RatePlanEntity> rates = buildRatePlanEntities(hotelId);
    final List<RoomEntity> rooms = buildRoomEntities(hotelId, 16, "PRE");
    int numberOfRooms = rooms.size();
    HotelEntity hotelEntityNew =
        buildHotelEntity(hotelId, hotelCode, localDate1, "OPERA", rates, rooms);
    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity(appKey, BigInteger.valueOf(123));

    hotelEntityRepository.updateSummaryTotals(hotelEntityNew, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntityBeforeAddition
        = hotelEntityRepository.findById(hotelId);
    assertTrue(optionalHotelEntityBeforeAddition.isPresent());
    final HotelEntity hotelEntityExistingBeforeAddition = optionalHotelEntityBeforeAddition.get();
    final List<RoomEntity> roomEntityBeforeAdditionList =
        hotelEntityExistingBeforeAddition.getRooms();
    assertEquals(hotelEntityNew, hotelEntityExistingBeforeAddition);
    assertEquals(numberOfRooms, roomEntityBeforeAdditionList.size());

    //add one more room to the same hotel entity
    final RoomEntity newRoom = buildRoomEntity(hotelId, 5, "DB");
    hotelEntityNew.getRooms().add(newRoom);
    numberOfRooms = hotelEntityNew.getRooms().size();

    hotelEntityRepository.updateSummaryTotals(hotelEntityNew, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntity = hotelEntityRepository.findById(hotelId);
    assertTrue(optionalHotelEntity.isPresent());
    final HotelEntity hotelEntity = optionalHotelEntity.get();
    final List<RoomEntity> roomEntityList = hotelEntity.getRooms();
    assertEquals(hotelEntityNew, hotelEntity);
    assertEquals(numberOfRooms, roomEntityList.size());

    Optional<ProcessedEventEntity> optionalProcessedEventEntity =
        processedEventJpaRepository.findById(appKey);

    assertTrue(optionalProcessedEventEntity.isPresent());
    final ProcessedEventEntity processedEventEntityFromDb = optionalProcessedEventEntity.get();
    assertEquals(processedEventEntity, processedEventEntityFromDb);

  }

  @Test
  @Transactional
  public void quantityOfExistingRoomsShouldBeUpdatedOfExistingHotelRecord() {
    processedEventJpaRepository.deleteAll();
    final String hotelCode = "LONSUT";
    final int year = LocalDate.now().getYear() + 1;
    final LocalDate localDate1 = LocalDate.of(year, 05, 18);
    final String hotelId = hotelCode + "_" + localDate1.format(
        DateTimeFormatter.ISO_LOCAL_DATE);

    //Before insertion record should not be there
    Optional<HotelEntity> optionalHotelEntityBeforeInsertion
        = hotelEntityRepository.findById(hotelId);
    assertFalse(optionalHotelEntityBeforeInsertion.isPresent());

    //insert a HotelEntity now with one room
    final List<RatePlanEntity> rates = buildRatePlanEntities(hotelId);
    final List<RoomEntity> rooms = buildRoomEntities(hotelId, 16, "PRE");
    int numberOfRooms = rooms.size();
    HotelEntity hotelEntityNew =
        buildHotelEntity(hotelId, hotelCode, localDate1, "BART", rates, rooms);

    final UUID uuid = UUID.randomUUID();
    final String eventId = uuid.toString();

    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity(appKey, BigInteger.valueOf(123));

    hotelEntityRepository.updateSummaryTotals(hotelEntityNew, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntityBeforeAddition
        = hotelEntityRepository.findById(hotelId);
    assertTrue(optionalHotelEntityBeforeAddition.isPresent());
    final HotelEntity hotelEntityExistingBeforeAddition = optionalHotelEntityBeforeAddition.get();
    final List<RoomEntity> roomEntityBeforeAdditionList =
        hotelEntityExistingBeforeAddition.getRooms();
    assertEquals(hotelEntityNew, hotelEntityExistingBeforeAddition);
    assertEquals(numberOfRooms, roomEntityBeforeAdditionList.size());

    //update the quantity into the room of the same hotel entity
    int newQuantity = 8;
    hotelEntityNew.getRooms().get(0).setQuantity(newQuantity);

    hotelEntityRepository.updateSummaryTotals(
        hotelEntityNew, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntity =
        hotelEntityRepository.findById(hotelEntityNew.getId());

    assertTrue(optionalHotelEntity.isPresent());
    final HotelEntity hotelEntity = optionalHotelEntity.get();
    assertEquals(hotelEntityNew, hotelEntity);
    final List<RoomEntity> roomEntityList = hotelEntity.getRooms();
    assertEquals(1, roomEntityList.size());
    assertEquals(newQuantity, roomEntityList.get(0).getQuantity());

    Optional<ProcessedEventEntity> optionalProcessedEventEntity =
        processedEventJpaRepository.findById(appKey);

    assertTrue(optionalProcessedEventEntity.isPresent());
    final ProcessedEventEntity processedEventEntityFromDb = optionalProcessedEventEntity.get();
    assertEquals(processedEventEntity, processedEventEntityFromDb);

  }

  //End summary total test cases

  //start apply daily rates test cases

  @Test
  @Transactional
  public void applyDailyRatesNewRecordsShouldBeInsertedSuccessfully() {
    processedEventJpaRepository.deleteAll();
    final String hotelCode = "LONSUT";
    final int year = LocalDate.now().getYear() + 1;
    final LocalDate localDate1 = LocalDate.of(year, 05, 18);
    final String hotelId = hotelCode + "_" + localDate1.format(
        DateTimeFormatter.ISO_LOCAL_DATE);

    //Before insertion record should not be there
    Optional<HotelEntity> optionalHotelEntityBeforeInsertion
        = hotelEntityRepository.findById(hotelId);
    assertFalse(optionalHotelEntityBeforeInsertion.isPresent());

    final List<RatePlanEntity> rates = buildRatePlanEntities(hotelId);
    final RoomEntity roomEntity2 = buildRoomEntity(hotelId);
    final RatePlanEntity ratePlanEntity2 =
        buildRatePlanEntity(hotelId, BigDecimal.ZERO, false, "G", 0,
            0, BigDecimal.ZERO, "S", roomEntity2);

    rates.add(ratePlanEntity2);

    final List<RoomEntity> rooms = buildRoomEntities(hotelId, 16, "PRE");
    HotelEntity hotelEntity1 = buildHotelEntity(
        hotelId, hotelCode, localDate1, "OPERA", rates, rooms);

    List<HotelEntity> hotelEntityList = new ArrayList<>();
    hotelEntityList.add(hotelEntity1);

    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity(appKey, offset);

    hotelEntityRepository.updateDailyRates(hotelEntityList, processedEventEntity);


    Optional<HotelEntity> optionalHotelEntity = hotelEntityRepository.findById(hotelId);
    assertTrue(optionalHotelEntity.isPresent());
    final HotelEntity hotelEntity = optionalHotelEntity.get();
    assertEquals(hotelEntity1, hotelEntity);

    final List<RatePlanEntity> rateEntityList = hotelEntity.getRates();
    assertEquals(hotelEntity1.getRates(), rateEntityList);
    assertEquals(hotelEntity1.getRates().size(), rateEntityList.size());
    assertEquals(hotelEntity1.getRates().size(), rateEntityJpaRepository.findAll().size());
    assertEquals(hotelEntity1.getRooms().size(),
        roomEntityJpaRepository.findAll().size());

    Optional<ProcessedEventEntity> optionalProcessedEventEntity =
        processedEventJpaRepository.findById(appKey);

    assertTrue(optionalProcessedEventEntity.isPresent());
    final ProcessedEventEntity processedEventEntityFromDb = optionalProcessedEventEntity.get();
    assertEquals(processedEventEntity, processedEventEntityFromDb);

  }

  @Test
  @Transactional
  public void applyDailyRatesNewRecordsShouldBeInsertedSuccessfullyNew() {
    processedEventJpaRepository.deleteAll();
    final String hotelCode = "TKINPT";
    final int year = LocalDate.now().getYear() + 1;
    final LocalDate localDate1 = LocalDate.of(year, 05, 18);
    final LocalDate localDate2 = LocalDate.of(year, 05, 19);
    final LocalDate localDate3 = LocalDate.of(year, 05, 20);

    final String hotelId1 = hotelCode + "_OPERA_" + localDate1.format(
        DateTimeFormatter.ISO_LOCAL_DATE);
    final String hotelId2 = hotelCode + "_OPERA_" + localDate2.format(
        DateTimeFormatter.ISO_LOCAL_DATE);
    final String hotelId3 = hotelCode + "_OPERA_" + localDate3.format(
        DateTimeFormatter.ISO_LOCAL_DATE);

    //Before insertion record should not be there
    Optional<HotelEntity> optionalHotelEntityBeforeInsertion
        = hotelEntityRepository.findById(hotelId1);
    assertFalse(optionalHotelEntityBeforeInsertion.isPresent());

    optionalHotelEntityBeforeInsertion = hotelEntityRepository.findById(hotelId2);
    assertFalse(optionalHotelEntityBeforeInsertion.isPresent());

    optionalHotelEntityBeforeInsertion = hotelEntityRepository.findById(hotelId3);
    assertFalse(optionalHotelEntityBeforeInsertion.isPresent());

    List<HotelEntity> hotelEntityList = new ArrayList<>();
    final List<RatePlanEntity> rates = new ArrayList<>();
    final RoomEntity roomEntity = buildRoomEntity(hotelId1);
    final RatePlanEntity ratePlanEntity =
        buildRatePlanEntity(
            hotelId1, new BigDecimal(122), false, "G", 0,
            0, BigDecimal.ZERO, "A", roomEntity);
    rates.add(ratePlanEntity);

    final HotelEntity hotelEntity1 =
        buildHotelEntity(hotelId1, hotelCode, localDate1, "OPERA", rates);
    final HotelEntity hotelEntity2 =
        buildHotelEntity(hotelId2, hotelCode, localDate2, "OPERA", rates);
    final HotelEntity hotelEntity3 =
        buildHotelEntity(hotelId3, hotelCode, localDate3, "OPERA", rates);
    hotelEntityList.add(hotelEntity1);
    hotelEntityList.add(hotelEntity2);
    hotelEntityList.add(hotelEntity3);

    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity(appKey, offset);

    hotelEntityRepository.updateDailyRates(hotelEntityList, processedEventEntity);


    Optional<HotelEntity> optionalHotelEntity = hotelEntityRepository.findById(hotelId1);
    assertTrue(optionalHotelEntity.isPresent());
    final HotelEntity hotelEntity = optionalHotelEntity.get();
    assertEquals(hotelEntity1, hotelEntity);
    final List<RatePlanEntity> rateEntityList = hotelEntity.getRates();
    assertEquals(rates, rateEntityList);

    Optional<HotelEntity> optionalHotelEntity2 = hotelEntityRepository.findById(hotelId2);
    assertTrue(optionalHotelEntity2.isPresent());
    final HotelEntity hotelEntity2FromDb = optionalHotelEntity2.get();
    assertEquals(hotelEntity2, hotelEntity2FromDb);
    final List<RatePlanEntity> rateEntityList2FromDb = hotelEntity2FromDb.getRates();
    assertEquals(rates, rateEntityList2FromDb);

    Optional<HotelEntity> optionalHotelEntity3 = hotelEntityRepository.findById(hotelId3);
    assertTrue(optionalHotelEntity3.isPresent());
    final HotelEntity hotelEntity3FromDb = optionalHotelEntity3.get();
    assertEquals(hotelEntity3, hotelEntity3FromDb);
    final List<RatePlanEntity> rateEntityList3FromDb = hotelEntity3FromDb.getRates();
    assertEquals(rates, rateEntityList3FromDb);

    assertEquals(hotelEntity1.getRates().size(), rateEntityJpaRepository.findAll().size());

    Optional<ProcessedEventEntity> optionalProcessedEventEntity =
        processedEventJpaRepository.findById(appKey);

    assertTrue(optionalProcessedEventEntity.isPresent());
    final ProcessedEventEntity processedEventEntityFromDb = optionalProcessedEventEntity.get();
    assertEquals(processedEventEntity, processedEventEntityFromDb);

  }

  private RoomEntity buildRoomEntity(String hotelId) {
    return RoomEntity.builder()
        .id(roomType + "_" + hotelId)
        .roomType(roomType)
        .build();
  }

  @Test
  @Transactional
  public void newRateShouldBeAddedToExistingHotelRecord() {
    processedEventJpaRepository.deleteAll();
    final String hotelCode = "LONSUT";
    final int year = LocalDate.now().getYear() + 1;
    final LocalDate localDate1 = LocalDate.of(year, 05, 18);
    final String hotelId = hotelCode + "_" + localDate1.format(
        DateTimeFormatter.ISO_LOCAL_DATE);

    //Before insertion record should not be there
    Optional<HotelEntity> optionalHotelEntityBeforeInsertion
        = hotelEntityRepository.findById(hotelId);
    assertFalse(optionalHotelEntityBeforeInsertion.isPresent());

    //insert a HotelEntity now with one rate
    final List<RatePlanEntity> rates = buildRatePlanEntities(hotelId);
    final List<RoomEntity> rooms = buildRoomEntities(hotelId, 16, "PRE");
    int numberOfRates = rates.size();
    HotelEntity hotelEntityNew =
        buildHotelEntity(hotelId, hotelCode, localDate1, "OPERA", rates, rooms);

    List<HotelEntity> hotelEntityList = new ArrayList<>();
    hotelEntityList.add(hotelEntityNew);

    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity(appKey, offset);

    hotelEntityRepository.updateDailyRates(hotelEntityList, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntityBeforeAddition
        = hotelEntityRepository.findById(hotelId);

    assertTrue(optionalHotelEntityBeforeAddition.isPresent());

    final HotelEntity hotelEntityExistingBeforeAddition = optionalHotelEntityBeforeAddition.get();
    final List<RoomEntity> roomEntityBeforeAdditionList =
        hotelEntityExistingBeforeAddition.getRooms();
    assertEquals(hotelEntityNew, hotelEntityExistingBeforeAddition);
    assertEquals(numberOfRates, roomEntityBeforeAdditionList.size());

    final RoomEntity roomEntity = buildRoomEntity(hotelId);
    //Add new rate
    final RatePlanEntity newRate
        = buildRatePlanEntity(
        hotelId,
        BigDecimal.TEN,
        false,
        "G", 0, 0, BigDecimal.valueOf(20), "B", roomEntity);
    hotelEntityNew.getRates().add(newRate);
    numberOfRates = hotelEntityNew.getRates().size();

    List<HotelEntity> hotelEntityListNew = new ArrayList<>();
    hotelEntityListNew.add(hotelEntityNew);

    hotelEntityRepository.updateDailyRates(hotelEntityListNew, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntity = hotelEntityRepository.findById(hotelId);
    assertTrue(optionalHotelEntity.isPresent());
    final HotelEntity hotelEntity = optionalHotelEntity.get();
    final List<RatePlanEntity> rateEntityList = hotelEntity.getRates();
    assertEquals(hotelEntityNew, hotelEntity);
    assertEquals(numberOfRates, rateEntityList.size());

    Optional<ProcessedEventEntity> optionalProcessedEventEntity =
        processedEventJpaRepository.findById(appKey);

    assertTrue(optionalProcessedEventEntity.isPresent());
    final ProcessedEventEntity processedEventEntityFromDb = optionalProcessedEventEntity.get();
    assertEquals(processedEventEntity, processedEventEntityFromDb);

  }

  @Test
  @Transactional
  public void amountOfExistingRatesShouldBeUpdatedOfExistingHotelRecord() {
    processedEventJpaRepository.deleteAll();
    final String hotelCode = "LONSUT";
    int year = LocalDate.now().getYear() + 1;
    final LocalDate localDate1 = LocalDate.of(year, 05, 18);
    final String hotelId = hotelCode + "_" + localDate1.format(
        DateTimeFormatter.ISO_LOCAL_DATE);

    //Before insertion record should not be there
    Optional<HotelEntity> optionalHotelEntityBeforeInsertion
        = hotelEntityRepository.findById(hotelId);
    assertFalse(optionalHotelEntityBeforeInsertion.isPresent());

    //insert a HotelEntity now with one room
    final List<RatePlanEntity> rates = buildRatePlanEntities(hotelId);
    final List<RoomEntity> rooms = buildRoomEntities(hotelId, 16, "PRE");
    int numberOfRates = rates.size();
    HotelEntity hotelEntityNew =
        buildHotelEntity(hotelId, hotelCode, localDate1, "OPERA", rates, rooms);

    List<HotelEntity> hotelEntityList = new ArrayList<>();
    hotelEntityList.add(hotelEntityNew);

    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity(appKey, offset);

    hotelEntityRepository.updateDailyRates(hotelEntityList, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntityBeforeAddition
        = hotelEntityRepository.findById(hotelId);
    assertTrue(optionalHotelEntityBeforeAddition.isPresent());
    final HotelEntity hotelEntityExistingBeforeAddition = optionalHotelEntityBeforeAddition.get();
    final List<RatePlanEntity> rateEntityBeforeAdditionList =
        hotelEntityExistingBeforeAddition.getRates();
    assertEquals(hotelEntityNew, hotelEntityExistingBeforeAddition);
    assertEquals(numberOfRates, rateEntityBeforeAdditionList.size());

    //update the quantity into the room of the same hotel entity
    BigDecimal oldAmount = hotelEntityNew.getRates().get(0).getAmount();
    BigDecimal oldPremiumAmount = hotelEntityNew.getRates().get(0).getPremiumAmount();
    BigDecimal newAmount = new BigDecimal(8);
    BigDecimal newPremiumAmount = new BigDecimal(16);
    hotelEntityNew.getRates().get(0).setAmount(newAmount);
    hotelEntityNew.getRates().get(0).setPremiumAmount(newPremiumAmount);

    List<HotelEntity> hotelEntityListNew = new ArrayList<>();
    hotelEntityListNew.add(hotelEntityNew);

    hotelEntityRepository.updateDailyRates(hotelEntityListNew, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntity = hotelEntityRepository.findById(hotelId);
    assertTrue(optionalHotelEntity.isPresent());
    final HotelEntity hotelEntity = optionalHotelEntity.get();
    final List<RatePlanEntity> rateEntityList = hotelEntity.getRates();

    assertEquals(hotelEntityNew, hotelEntity);
    assertEquals(numberOfRates, rateEntityList.size());
    assertEquals(1, rateEntityList.size());

    assertNotEquals(oldAmount, rateEntityList.get(0).getAmount());
    assertNotEquals(oldPremiumAmount, rateEntityList.get(0).getPremiumAmount());

    assertEquals(newAmount, rateEntityList.get(0).getAmount());
    assertEquals(newPremiumAmount, rateEntityList.get(0).getPremiumAmount());

    Optional<ProcessedEventEntity> optionalProcessedEventEntity =
        processedEventJpaRepository.findById(appKey);

    assertTrue(optionalProcessedEventEntity.isPresent());
    final ProcessedEventEntity processedEventEntityFromDb = optionalProcessedEventEntity.get();
    assertEquals(processedEventEntity, processedEventEntityFromDb);

  }
  //end apply daily rates test cases

  //start rate restrictions test cases

  @Test
  @Transactional
  public void rateRestrictionsNewRecordsShouldBeInsertedSuccessfully() {
    processedEventJpaRepository.deleteAll();
    final String hotelCode = "LONSUT";
    int year = LocalDate.now().getYear() + 1;
    final LocalDate localDate1 = LocalDate.of(year, 05, 18);
    final String hotelId = hotelCode + "_" + localDate1.format(
        DateTimeFormatter.ISO_LOCAL_DATE);

    //Before insertion record should not be there
    Optional<HotelEntity> optionalHotelEntityBeforeInsertion
        = hotelEntityRepository.findById(hotelId);
    assertFalse(optionalHotelEntityBeforeInsertion.isPresent());

    final List<RatePlanEntity> rates = buildRatePlanEntities(hotelId);
    final List<RoomEntity> rooms = buildRoomEntities(hotelId, 16, "PRE");
    HotelEntity hotelEntity1 =
        buildHotelEntity(hotelId, hotelCode, localDate1, "OPERA", rates, rooms);
    List<HotelEntity> hotelEntityList = new ArrayList<>();
    hotelEntityList.add(hotelEntity1);

    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity(appKey, offset);

    hotelEntityRepository.
        updateRateRestrictions(hotelEntityList, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntity = hotelEntityRepository.findById(hotelId);
    assertTrue(optionalHotelEntity.isPresent());
    final HotelEntity hotelEntity = optionalHotelEntity.get();
    final List<RatePlanEntity> rateEntityList = hotelEntity.getRates();
    assertEquals(hotelEntity1, hotelEntity);
    assertEquals(hotelEntity1.getRooms().size(), rateEntityList.size());

    Optional<ProcessedEventEntity> optionalProcessedEventEntity =
        processedEventJpaRepository.findById(appKey);

    assertTrue(optionalProcessedEventEntity.isPresent());
    final ProcessedEventEntity processedEventEntityFromDb = optionalProcessedEventEntity.get();
    assertEquals(processedEventEntity, processedEventEntityFromDb);

  }


  @Test
  @Transactional
  public void rateRestrictionsNewRateShouldBeAddedToExistingHotelRecord() {
    processedEventJpaRepository.deleteAll();
    final String hotelCode = "LONSUT";
    int year = LocalDate.now().getYear() + 1;
    final LocalDate localDate1 = LocalDate.of(year, 05, 18);
    final String hotelId = hotelCode + "_" + localDate1.format(
        DateTimeFormatter.ISO_LOCAL_DATE);

    //Before insertion record should not be there
    Optional<HotelEntity> optionalHotelEntityBeforeInsertion
        = hotelEntityRepository.findById(hotelId);
    assertFalse(optionalHotelEntityBeforeInsertion.isPresent());

    //insert a HotelEntity now with one rate
    final List<RatePlanEntity> rates = buildRatePlanEntities(hotelId);
    final List<RoomEntity> rooms = buildRoomEntities(hotelId, 16, "PRE");
    int numberOfRates = rates.size();
    HotelEntity hotelEntityNew =
        buildHotelEntity(hotelId, hotelCode, localDate1, "OPERA", rates, rooms);
    List<HotelEntity> hotelEntityList = new ArrayList<>();
    hotelEntityList.add(hotelEntityNew);

    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity(appKey, offset);

    hotelEntityRepository.
        updateRateRestrictions(hotelEntityList, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntityBeforeAddition
        = hotelEntityRepository.findById(hotelId);
    assertTrue(optionalHotelEntityBeforeAddition.isPresent());

    final HotelEntity hotelEntityExistingBeforeAddition = optionalHotelEntityBeforeAddition.get();
    final List<RoomEntity> roomEntityBeforeAdditionList =
        hotelEntityExistingBeforeAddition.getRooms();
    assertEquals(hotelEntityNew, hotelEntityExistingBeforeAddition);
    assertEquals(numberOfRates, roomEntityBeforeAdditionList.size());


    final RoomEntity roomEntity2 = buildRoomEntity(hotelId);
    //Add new rate
    final RatePlanEntity newRate
        = buildRatePlanEntity(
        hotelId,
        BigDecimal.TEN,
        false,
        "G", 3, 1, BigDecimal.valueOf(20), "B", roomEntity2);
    hotelEntityNew.getRates().add(newRate);
    numberOfRates = hotelEntityNew.getRates().size();

    hotelEntityRepository.
        updateRateRestrictions(hotelEntityList, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntity = hotelEntityRepository.findById(hotelId);
    assertTrue(optionalHotelEntity.isPresent());
    final HotelEntity hotelEntity = optionalHotelEntity.get();
    final List<RatePlanEntity> rateEntityList = hotelEntity.getRates();
    assertEquals(hotelEntityNew, hotelEntity);
    assertEquals(numberOfRates, rateEntityList.size());

    Optional<ProcessedEventEntity> optionalProcessedEventEntity =
        processedEventJpaRepository.findById(appKey);

    assertTrue(optionalProcessedEventEntity.isPresent());
    final ProcessedEventEntity processedEventEntityFromDb = optionalProcessedEventEntity.get();
    assertEquals(processedEventEntity, processedEventEntityFromDb);

  }

  @Test
  @Transactional
  public void minMaxNightsOfExistingRatesShouldBeUpdatedOfExistingHotelRecord() {
    processedEventJpaRepository.deleteAll();
    final String hotelCode = "LONSUT";
    int year = LocalDate.now().getYear() + 1;
    final LocalDate localDate1 = LocalDate.of(year, 05, 18);
    final String hotelId = hotelCode + "_" + localDate1.format(
        DateTimeFormatter.ISO_LOCAL_DATE);

    //Before insertion record should not be there
    Optional<HotelEntity> optionalHotelEntityBeforeInsertion
        = hotelEntityRepository.findById(hotelId);
    assertFalse(optionalHotelEntityBeforeInsertion.isPresent());

    //insert a HotelEntity now with one room
    final List<RatePlanEntity> rates = buildRatePlanEntities(hotelId);
    final List<RoomEntity> rooms = buildRoomEntities(hotelId, 16, "PRE");
    int numberOfRates = rates.size();
    HotelEntity hotelEntityNew =
        buildHotelEntity(hotelId, hotelCode, localDate1, "OPERA", rates, rooms);

    List<HotelEntity> hotelEntityList = new ArrayList<>();
    hotelEntityList.add(hotelEntityNew);

    final ProcessedEventEntity processedEventEntity = buildProcessedEventEntity(appKey, offset);

    hotelEntityRepository.
        updateRateRestrictions(hotelEntityList, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntityBeforeAddition
        = hotelEntityRepository.findById(hotelId);
    assertTrue(optionalHotelEntityBeforeAddition.isPresent());

    final HotelEntity hotelEntityExistingBeforeAddition = optionalHotelEntityBeforeAddition.get();
    final List<RatePlanEntity> rateEntityBeforeAdditionList =
        hotelEntityExistingBeforeAddition.getRates();
    assertEquals(hotelEntityNew, hotelEntityExistingBeforeAddition);
    assertEquals(numberOfRates, rateEntityBeforeAdditionList.size());

    //update the quantity into the room of the same hotel entity
    int oldMinNights = hotelEntityNew.getRates().get(0).getMinNights();
    int oldMaxNights = hotelEntityNew.getRates().get(0).getMaxNights();
    int newMinNights = 3;
    int newMaxNights = 6;
    hotelEntityNew.getRates().get(0).setMinNights(newMinNights);
    hotelEntityNew.getRates().get(0).setMaxNights(newMaxNights);

    List<HotelEntity> hotelEntityListNew = new ArrayList<>();
    hotelEntityListNew.add(hotelEntityNew);

    hotelEntityRepository.
        updateRateRestrictions(hotelEntityListNew, processedEventEntity);

    Optional<HotelEntity> optionalHotelEntity = hotelEntityRepository.findById(hotelId);
    assertTrue(optionalHotelEntity.isPresent());
    final HotelEntity hotelEntity = optionalHotelEntity.get();
    final List<RatePlanEntity> rateEntityList = hotelEntity.getRates();

    assertEquals(hotelEntityNew, hotelEntity);
    assertEquals(numberOfRates, rateEntityList.size());
    assertEquals(1, rateEntityList.size());

    assertNotEquals(oldMinNights, rateEntityList.get(0).getMinNights());
    assertNotEquals(oldMaxNights, rateEntityList.get(0).getMaxNights());

    assertEquals(newMinNights, rateEntityList.get(0).getMinNights());
    assertEquals(newMaxNights, rateEntityList.get(0).getMaxNights());

    Optional<ProcessedEventEntity> optionalProcessedEventEntity =
        processedEventJpaRepository.findById(appKey);

    assertTrue(optionalProcessedEventEntity.isPresent());
    final ProcessedEventEntity processedEventEntityFromDb = optionalProcessedEventEntity.get();
    assertEquals(processedEventEntity, processedEventEntityFromDb);

  }
  //end rate restrictions test cases

  @Test
  public void findByIdForProcessEventEntityShouldReturnEmptyWhenNoRecordsInDb(){
    processedEventJpaRepository.deleteAll();
    assertEquals(Optional.empty(), processedEventJpaRepository.findById(appKey));

  }

  @Test
  public void findByIdForProcessEventEntityShouldReturnExpectedWhenRecordsInDb(){

    processedEventJpaRepository.deleteAll();

    assertEquals(Optional.empty(), processedEventJpaRepository.findById(appKey));

    final ProcessedEventEntity processedEventEntity =
        buildProcessedEventEntity(appKey, offset);

    final ProcessedEventEntity processedEventEntityExpected =
        processedEventJpaRepository.save(processedEventEntity);

    final Optional<ProcessedEventEntity> optionalProcessedEventEntityActual =
        processedEventJpaRepository.findById(appKey);

    assertTrue(optionalProcessedEventEntityActual.isPresent());
    final ProcessedEventEntity processedEventEntityActual =
        optionalProcessedEventEntityActual.get();

    assertEquals(processedEventEntityExpected.getOffset(),
        processedEventEntityActual.getOffset());


  }

  @Test
  public void findByIdForProcessEventEntityShouldReturnUpdatedObj(){

    processedEventJpaRepository.deleteAll();

    assertEquals(Optional.empty(), processedEventJpaRepository.findById(appKey));

    BigInteger newOffset = offset.add(BigInteger.valueOf(10));

    final ProcessedEventEntity processedEventEntity =
        buildProcessedEventEntity(appKey, offset);

    processedEventJpaRepository.save(processedEventEntity);

    final ProcessedEventEntity processedEventEntityUpdated =
        buildProcessedEventEntity(appKey, newOffset);

    processedEventJpaRepository.save(processedEventEntityUpdated);

    final Optional<ProcessedEventEntity> optionalProcessedEventEntityActual =
        processedEventJpaRepository.findById(appKey);

    assertTrue(optionalProcessedEventEntityActual.isPresent());

    final ProcessedEventEntity processedEventEntityActual =
        optionalProcessedEventEntityActual.get();

    assertEquals(processedEventEntityUpdated.getOffset(),
        processedEventEntityActual.getOffset());

    assertEquals(newOffset, processedEventEntityActual.getOffset());
    assertNotEquals(offset, processedEventEntityActual.getOffset());

  }

  @Test
  public void findByIdForProcessEventEntityShouldReturnUpdatedObj1(){

    processedEventJpaRepository.deleteAll();

    final ProcessedEventEntity processedEventEntity =
        buildProcessedEventEntity(appKey, offset);

    processedEventJpaRepository.save(processedEventEntity);

    final ProcessedEventEntity processedEventEntityUpdated =
        buildProcessedEventEntity(appKey, offset.add(BigInteger.ONE));

    processedEventJpaRepository.save(processedEventEntityUpdated);

    final Optional<ProcessedEventEntity> optionalProcessedEventEntityActual =
        processedEventJpaRepository.findById(appKey);

    assertTrue(optionalProcessedEventEntityActual.isPresent());

    final ProcessedEventEntity processedEventEntityActual =
        optionalProcessedEventEntityActual.get();

    assertEquals(processedEventEntityUpdated.getOffset(),
        processedEventEntityActual.getOffset());
    assertNotEquals(offset,
        processedEventEntityActual.getOffset());

  }



  private List<RoomEntity> buildRoomEntities(
      final String hotelId,
      final int quantity,
      final String roomType) {

    final List<RoomEntity> roomEntityList = new ArrayList<>();
    final RoomEntity roomEntity1 = buildRoomEntity(hotelId, quantity, roomType);
    roomEntityList.add(roomEntity1);

    return roomEntityList;
  }

  private RoomEntity buildRoomEntity(
      final String hotelId,
      final int quantity,
      final String roomType
  ) {
    return RoomEntity.builder()
        .id(roomType + "_" + hotelId)
        .quantity(quantity)
        .roomType(roomType)
        .build();
  }

  private List<RatePlanEntity> buildRatePlanEntities(final String hotelId) {

    final List<RatePlanEntity> ratePlanEntityList = new ArrayList<>();
    final RoomEntity roomEntity = buildRoomEntity(hotelId);
    final RatePlanEntity ratePlanEntity =
        buildRatePlanEntity(hotelId, BigDecimal.ZERO, false, "G", 0,
            0, BigDecimal.ZERO, "A", roomEntity);

    ratePlanEntityList.add(ratePlanEntity);
    return ratePlanEntityList;
  }

  private RatePlanEntity buildRatePlanEntity(
      final String hotelId,
      final BigDecimal amount,
      final boolean availability,
      final String currency,
      final int maxNights,
      final int minNights,
      final BigDecimal premiumAmount,
      final String rateClassification,
      final RoomEntity roomEntity
  ) {
    final String rateId = rateClassification + "_" + hotelId + "_" + roomType;
    return RatePlanEntity.builder()
        .id(rateId)
        .amount(amount)
        .availability(availability)
        .currency(currency)
        .maxNights(maxNights)
        .minNights(minNights)
        .premiumAmount(premiumAmount)
        .rateClassification(rateClassification)
        .room(roomEntity)
        .build();
  }

  private HotelEntity buildHotelEntity(
      final String hotelId,
      final String hotelCode,
      final LocalDate date,
      final String pmsSource,
      final List<RatePlanEntity> rates,
      final List<RoomEntity> rooms) {

    return HotelEntity.builder()
        .id(hotelId)
        .hotelCode(hotelCode)
        .date(date)
        .pmsSource(pmsSource)
        .rates(rates)
        .rooms(rooms)
        .build();

  }

  private HotelEntity buildHotelEntity(
      final String hotelId,
      final String hotelCode,
      final LocalDate date,
      final String pmsSource,
      final List<RatePlanEntity> rates) {

    return HotelEntity.builder()
        .id(hotelId)
        .hotelCode(hotelCode)
        .date(date)
        .pmsSource(pmsSource)
        .rates(rates)
        .build();

  }

  private ProcessedEventEntity buildProcessedEventEntity(
      final String appKey, final BigInteger offset) {
    return ProcessedEventEntity.builder()
        .offset(offset)
        .receivedOn(LocalDateTime.now())
        .appKey(appKey)
        .build();
  }

}
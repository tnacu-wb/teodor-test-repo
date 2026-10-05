package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.DistributionHotelAvailResultWithRestrictionSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil;

@ExtendWith(MockitoExtension.class)
class DistributionHotelsPostProcessorSvcTest {

  @Mock
  private LosRestrictionPort<DistributionHotel> losRestrictionPort;

  @Mock
  private CityTaxFeatureUtil cityTaxFeatureUtil;

  @InjectMocks
  private DistributionHotelsPostProcessorSvc service;

  @Test
  void testPerformDistributionHotelsPostProcessWithRestriction_addsHotelNotInDb_andAppliesCityTax() {
    // Setup payload with two hotels, only one in DB
    DistributionPayload payload = mock(DistributionPayload.class);
    when(payload.getHotelCodes()).thenReturn(Arrays.asList("HOTEL1", "HOTEL2"));
    when(payload.getArrival()).thenReturn(LocalDate.now().toString());
    when(payload.getDeparture()).thenReturn(LocalDate.now().plusDays(1).toString());
    when(payload.getRoomQty()).thenReturn(new int[]{1});
    when(payload.getRoomTypes()).thenReturn(new String[][]{{"ROOM1"}, {"ROOM1"}});

    // Setup city tax info
    HotelsCityTaxInfo hotelsCityTaxInfo = mock(HotelsCityTaxInfo.class);
    when(payload.getHotelsCityTaxInfo()).thenReturn(hotelsCityTaxInfo);

    // Setup DB result for HOTEL1 only
    DistributionHotelAvailResultWithRestrictionSet dbResult =
        mock(DistributionHotelAvailResultWithRestrictionSet.class);
    when(dbResult.getHotelCode()).thenReturn("HOTEL1");
    when(dbResult.getAmount()).thenReturn(BigDecimal.valueOf(100));
    when(dbResult.getAmountWithCityTax()).thenReturn(BigDecimal.valueOf(110));
    when(dbResult.getQuantity()).thenReturn(1);
    when(dbResult.getAvailableDate()).thenReturn(LocalDate.now());
    when(dbResult.getRateClassification()).thenReturn("RATE1");
    when(dbResult.getRoomType()).thenReturn("ROOM1");
    when(dbResult.getCurrency()).thenReturn("EUR");

    // City tax feature enabled
    when(cityTaxFeatureUtil.isFeatureEnabled()).thenReturn(true);

    // City tax should apply, but use DB value (shouldCalculateCityTax=false → dbAmountWithCityTax=110)
    try (var mockedStatic = mockStatic(CityTaxUtil.class)) {
      mockedStatic.when(() -> CityTaxUtil.shouldApplyCityTax(anyBoolean(), anyString(), any(), any(), any())).thenReturn(true);
      mockedStatic.when(() -> CityTaxUtil.shouldCalculateCityTax(anyBoolean(), any())).thenReturn(false);

      List<DistributionHotel> result = service.performDistributionHotelsPostProcess(
          payload, List.of(dbResult));

      // HOTEL2 should be added with empty rates
      DistributionHotel hotel2 = result.stream()
          .filter(h -> h.getRates().isEmpty() && h.getArrivalDateToday())
          .findFirst().orElse(null);
      assertThat(hotel2).isNotNull();
      assertThat(hotel2.getRates()).isEmpty();

      // HOTEL1 should be present and available, amount = dbAmountWithCityTax = 110
      DistributionHotel hotel1 = result.stream()
          .filter(h -> "HOTEL1".equals(h.getHotelCode()))
          .findFirst().orElse(null);
      assertThat(hotel1).isNotNull();
      assertThat(hotel1.getAvailable()).isTrue();
      var rates = hotel1.getRates();
      assertThat(rates).hasSize(1);
      var rooms = rates.getFirst().getRooms();
      assertThat(rooms).hasSize(1);
      var room = rooms.getFirst();
      assertThat(room.getRoomType()).isEqualTo("ROOM1");
      var availableCosts = room.getAvailableCosts();
      assertThat(availableCosts).hasSize(1);
      var cost = availableCosts.getFirst();
      assertThat(cost.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(110));
    }
  }

  @Test
  void testPerformDistributionHotelsPostProcessWithRestriction_addsHotelNotInDb_andCalculatesCityTax() {
    // Setup payload with two hotels, only one in DB
    DistributionPayload payload = mock(DistributionPayload.class);
    when(payload.getHotelCodes()).thenReturn(Arrays.asList("HOTEL1", "HOTEL2"));
    when(payload.getArrival()).thenReturn(LocalDate.now().toString());
    when(payload.getDeparture()).thenReturn(LocalDate.now().plusDays(1).toString());
    when(payload.getRoomQty()).thenReturn(new int[]{1});
    when(payload.getRoomTypes()).thenReturn(new String[][]{{"ROOM1"}, {"ROOM1"}});

    // Setup city tax info
    HotelsCityTaxInfo hotelsCityTaxInfo = HotelsCityTaxInfo.builder()
        .hotelsCityTaxes(Map.of("HOTEL1", HotelCityTax.builder().build())).build();
    when(payload.getHotelsCityTaxInfo()).thenReturn(hotelsCityTaxInfo);

    // Setup DB result for HOTEL1 only
    DistributionHotelAvailResultWithRestrictionSet dbResult =
        mock(DistributionHotelAvailResultWithRestrictionSet.class);
    when(dbResult.getHotelCode()).thenReturn("HOTEL1");
    when(dbResult.getAmount()).thenReturn(BigDecimal.valueOf(100));
    when(dbResult.getAmountWithCityTax()).thenReturn(BigDecimal.valueOf(0));
    when(dbResult.getQuantity()).thenReturn(1);
    when(dbResult.getAvailableDate()).thenReturn(LocalDate.now());
    when(dbResult.getRateClassification()).thenReturn("RATE1");
    when(dbResult.getRoomType()).thenReturn("ROOM1");
    when(dbResult.getCurrency()).thenReturn("EUR");

    // City tax feature enabled
    when(cityTaxFeatureUtil.isFeatureEnabled()).thenReturn(true);
    when(cityTaxFeatureUtil.isFallbackEnabled()).thenReturn(true);

    // City tax should apply and calculate (shouldCalculateCityTax=true → getAmountWithCityTax=120)
    try (var mockedStatic = mockStatic(CityTaxUtil.class)) {
      mockedStatic.when(() -> CityTaxUtil.shouldApplyCityTax(anyBoolean(), anyString(), any(), any(), any())).thenReturn(true);
      mockedStatic.when(() -> CityTaxUtil.shouldCalculateCityTax(anyBoolean(), any())).thenReturn(true);
      mockedStatic.when(() -> CityTaxUtil.getAmountWithCityTax(any(), any(), any(), anyInt(), anyInt())).thenReturn(BigDecimal.valueOf(120));

      List<DistributionHotel> result = service.performDistributionHotelsPostProcess(
          payload, List.of(dbResult));

      // HOTEL1 should be present and available
      DistributionHotel hotel1 = result.stream()
          .filter(h -> "HOTEL1".equals(h.getHotelCode()))
          .findFirst().orElse(null);
      assertThat(hotel1).isNotNull();
      assertThat(hotel1.getAvailable()).isTrue();
      var rates = hotel1.getRates();
      assertThat(rates).hasSize(1);
      var rooms = rates.getFirst().getRooms();
      assertThat(rooms).hasSize(1);
      var room = rooms.getFirst();
      assertThat(room.getRoomType()).isEqualTo("ROOM1");
      var availableCosts = room.getAvailableCosts();
      assertThat(availableCosts).hasSize(1);
      var cost = availableCosts.getFirst();
      assertThat(cost.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(120));
    }
  }

  @Test
  void testDebugLoggingPaths() {
    Logger logger = (Logger) LoggerFactory.getLogger(DistributionHotelsPostProcessorSvc.class);
    Level originalLevel = logger.getLevel();
    logger.setLevel(Level.DEBUG);

    try {
      DistributionPayload payload = mock(DistributionPayload.class);
      when(payload.getHotelCodes()).thenReturn(Arrays.asList("HOTEL1", "HOTEL2"));
      when(payload.getArrival()).thenReturn(LocalDate.now().toString());
      when(payload.getDeparture()).thenReturn(LocalDate.now().plusDays(1).toString());
      when(payload.getRoomQty()).thenReturn(new int[]{1});
      when(payload.getRoomTypes()).thenReturn(new String[][]{{"ROOM1"}});

      HotelsCityTaxInfo hotelsCityTaxInfo = mock(HotelsCityTaxInfo.class);
      when(payload.getHotelsCityTaxInfo()).thenReturn(hotelsCityTaxInfo);

      DistributionHotelAvailResultWithRestrictionSet dbResult =
          mock(DistributionHotelAvailResultWithRestrictionSet.class);
      when(dbResult.getHotelCode()).thenReturn("HOTEL1");
      when(dbResult.getRateClassification()).thenReturn("RATE1");
      when(dbResult.getRoomType()).thenReturn("ROOM1");
      when(dbResult.getQuantity()).thenReturn(1);
      when(dbResult.getAvailableDate()).thenReturn(LocalDate.now());
      when(dbResult.getCurrency()).thenReturn("EUR");
      when(dbResult.getAmount()).thenReturn(BigDecimal.valueOf(100));
      when(dbResult.getAmountWithCityTax()).thenReturn(BigDecimal.valueOf(110));

      when(cityTaxFeatureUtil.isFeatureEnabled()).thenReturn(false);

      List<DistributionHotel> result = service.performDistributionHotelsPostProcess(
          payload, List.of(dbResult));

      assertThat(result).isNotEmpty();
    } finally {
      logger.setLevel(originalLevel);
    }
  }
}

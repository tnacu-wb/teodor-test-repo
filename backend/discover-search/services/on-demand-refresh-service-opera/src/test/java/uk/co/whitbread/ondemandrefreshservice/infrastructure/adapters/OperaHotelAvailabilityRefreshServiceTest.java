package uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.*;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RateCategory;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.FeatureFlag;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventoryResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RateRestrictionResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.DailyRates;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.ondemandrefreshservice.utils.MockDataReader.generateMockRsp;

@ExtendWith(MockitoExtension.class)
class OperaHotelAvailabilityRefreshServiceTest {

    @Mock
    private HotelInventoryOutPort hotelInventoryOutPort;
    @Mock
    private DailyRatesOutPort dailyRatesOutPort;
    @Mock
    private RateRestrictionOutPort rateRestrictionOutPort;
    @Mock
    private HotelAvailabilitiesBatchOutPort dbBatchService;
    @Mock
    private ContentEntityServiceOutPort contentOutPort;
    @Mock
    private OcdAdapterOutPort ocdAdapterOutPort;
    @Mock
    private UnleashWrapper<FeatureFlag> unleashWrapper;

    @InjectMocks
    private OperaHotelAvailabilityRefreshService operaHotelAvailabilityRefreshSvc;

    @Captor
    private ArgumentCaptor<List<HotelEntity>> hotelEntitiesCaptor;

    private static final Set<String> hotelCodes = new HashSet<>(List.of("TKINPT"));
    private static final String HOTEL_CODE = "TKINPT";
    private static final LocalDate startDate = LocalDate.parse("2022-11-01");
    private static final LocalDate endDate = LocalDate.parse("2022-11-02"); // Reduced range for simplicity

    @BeforeEach
    void setUp() throws IOException {
        // Mock common dependencies
        final HotelInventoryResponse hotelInventoryRsp = generateMockRsp("/mock_data/inventory.json",
                HotelInventoryResponse.class);
        when(hotelInventoryOutPort.getOperaHotelInventory(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(hotelInventoryRsp.getHotelInventories().stream().findFirst().orElseThrow());

        final DailyRates flexDailyRates = generateMockRsp("/mock_data/flexrate_daily_rates.json", DailyRates.class);
        final Map<String, DailyRates> dailyRates = new HashMap<>();
        dailyRates.put(RateCategory.FLEXRATE.name(), flexDailyRates);
        when(dailyRatesOutPort.getOperaDailyRates(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(dailyRates);

        final RateRestrictionResponse rateRestrictionRsp = generateMockRsp(
                "/mock_data/los_restriction_empty_rsp.json", RateRestrictionResponse.class);
        when(rateRestrictionOutPort.getRateRestrictions(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(rateRestrictionRsp);

        // Mock Unleash feature flags
        FeatureFlag featureFlag = mock(FeatureFlag.class);
        lenient().when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
        lenient().when(unleashWrapper.isEnabled(featureFlag.getCityTaxUk())).thenReturn(false);
        lenient().when(unleashWrapper.isEnabled(featureFlag.getCityTaxUkFallback())).thenReturn(false);
    }

    @Test
    void refreshHotelAvailabilities_WithoutCityTax_Success() {
        // Arrange
        lenient().when(contentOutPort.getHotelsWithCityTax(anyString(), anyString()))
                .thenReturn(Mono.just(Collections.emptyList()));

        // Act
        operaHotelAvailabilityRefreshSvc.refreshHotelAvailabilities(hotelCodes, startDate, endDate);

        // Assert
        verify(dbBatchService, timeout(1000)).persistHotelEntities(hotelEntitiesCaptor.capture());
        verify(ocdAdapterOutPort, never()).getAmountAfterTax(anyString(), anyString(), anyString(),
                any(Integer.class), anyString(), anyString());

        List<HotelEntity> capturedHotelEntities = hotelEntitiesCaptor.getValue();
        assertNotNull(capturedHotelEntities);
        assertEquals(2, capturedHotelEntities.size());

        capturedHotelEntities.forEach(hotelEntity ->
                hotelEntity.getRates().forEach(ratePlanEntity ->
                        assertNull(ratePlanEntity.getAmountWithCityTax())
                )
        );
    }
}

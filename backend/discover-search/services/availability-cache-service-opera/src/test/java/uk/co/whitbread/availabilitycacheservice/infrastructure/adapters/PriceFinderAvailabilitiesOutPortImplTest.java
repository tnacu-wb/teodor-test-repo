package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.HotelRoomRateInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.GqtHotelAvailabilitiesJpaRepository;

@ExtendWith(MockitoExtension.class)
class PriceFinderAvailabilitiesOutPortImplTest {

  @InjectMocks
  private PriceFinderAvailabilitiesOutPortImpl priceFinderAvailabilitiesOutPort;

  @Mock
  private GqtHotelAvailabilitiesJpaRepository hotelAvailabilitiesJpaRepository;


  @Test
  void getMinRateHotelAvailabilities_Successful() {
    when(hotelAvailabilitiesJpaRepository.findAvailabilitiesForGqtOpera(any(), any(), any())).thenReturn(
        buildHotelAvailabilitiesResultSet());
    List<HotelRoomRateInfo> result =
        priceFinderAvailabilitiesOutPort.getMinRateHotelAvailabilities(List.of("LONKIN", "LONEUS"),
            LocalDate.of(2025, 3, 6), LocalDate.of(2025, 3, 7));

    assertEquals(2, result.size());
  }

  private List<HotelAvailabilitiesResultSet> buildHotelAvailabilitiesResultSet() {

    return List.of(
        HotelAvailabilitiesResultSet.builder()
            .hotelCode("LONEUS")
            .availableDate(LocalDate.of(2025, 3, 6))
            .rateCode("FLEXRATE")
            .amount(BigDecimal.valueOf(50))
            .currency("G")
            .quantity(3)
            .roomType("DOUBLE")
            .build(),
        HotelAvailabilitiesResultSet.builder()
            .hotelCode("LONKIN")
            .availableDate(LocalDate.of(2025, 3, 6))
            .rateCode("FLEXRATE")
            .amount(BigDecimal.valueOf(60))
            .currency("G")
            .quantity(3)
            .roomType("DOUBLE")
            .build(),
        HotelAvailabilitiesResultSet.builder()
            .hotelCode("LONKIN")
            .availableDate(LocalDate.of(2025, 3, 6))
            .rateCode("FLEXRATE")
            .amount(BigDecimal.valueOf(50))
            .currency("G")
            .quantity(0)
            .roomType("SINGLE")
            .build(),
        HotelAvailabilitiesResultSet.builder()
            .hotelCode("HEAPTI")
            .availableDate(LocalDate.of(2025, 3, 7))
            .amount(BigDecimal.valueOf(50))
            .currency("G")
            .quantity(0)
            .roomType("SINGLE")
            .build()
    );
  }


}

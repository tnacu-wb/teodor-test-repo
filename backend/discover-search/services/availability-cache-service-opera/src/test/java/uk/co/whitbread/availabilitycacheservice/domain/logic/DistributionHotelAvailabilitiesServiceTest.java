package uk.co.whitbread.availabilitycacheservice.domain.logic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.DistributionPersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.ContentClientLookUpService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;

@ExtendWith(MockitoExtension.class)
class DistributionHotelAvailabilitiesServiceTest {

  @Mock
  private DistributionPersistenceAdapter distributionPersistenceAdapter;

  @Mock
  private ContentClientLookUpService contentClientLookUpService;

  @InjectMocks
  private DistributionHotelAvailabilitiesService service;

  @Test
  void testGetHotelAvailabilities_setsCityTaxInfoAndReturnsHotels() {
    DistributionPayload payload = mock(DistributionPayload.class);
    String country = "GB";
    String language = "en";
    List<String> hotelCodes = List.of("HOTEL1", "HOTEL2");
    HotelsCityTaxInfo cityTaxInfo = mock(HotelsCityTaxInfo.class);
    List<DistributionHotel> expectedHotels = List.of(mock(DistributionHotel.class));

    when(payload.getCountry()).thenReturn(country);
    when(payload.getLanguage()).thenReturn(language);
    when(payload.getHotelCodes()).thenReturn(hotelCodes);
    when(contentClientLookUpService.getHotelsCityTaxInfo(country, language, hotelCodes)).thenReturn(cityTaxInfo);
    when(distributionPersistenceAdapter.getHotelAvailabilitiesForDistribution(payload)).thenReturn(expectedHotels);

    List<DistributionHotel> result = service.getHotelAvailabilities(payload);

    verify(payload).setHotelsCityTaxInfo(cityTaxInfo);
    assertThat(result).isEqualTo(expectedHotels);
  }
}

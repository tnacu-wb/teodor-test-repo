package uk.co.whitbread.availabilitycacheservice.domain.logic;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.DistributionHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.DistributionPersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.ContentClientLookUpService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributionHotelAvailabilitiesService implements DistributionHotelAvailabilitiesPort {

  private final DistributionPersistenceAdapter distributionPersistenceAdapter;
  private final ContentClientLookUpService contentClientLookUpService;

  public List<DistributionHotel> getHotelAvailabilities(
      final DistributionPayload distributionPayload) {

    String country = distributionPayload.getCountry();
    String language = distributionPayload.getLanguage();
    List<String> hotelCodes = distributionPayload.getHotelCodes();
    distributionPayload.setHotelsCityTaxInfo(contentClientLookUpService.getHotelsCityTaxInfo(
        country, language, hotelCodes));

    log.debug("Process getHotelAvailabilities for Distribution with payload - {}", distributionPayload);
    final List<DistributionHotel> distributionHotelList = distributionPersistenceAdapter
        .getHotelAvailabilitiesForDistribution(distributionPayload);
    log.debug("distributionHotelList: {}", distributionHotelList);

    return distributionHotelList;
  }
}

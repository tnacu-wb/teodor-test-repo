package uk.co.whitbread.availabilitycacheservice.domain.logic;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtSearchPayload;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.GqtHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.GqtHotelAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.ContentClientLookUpService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;

@Slf4j
@AllArgsConstructor
public class GqtHotelAvailabilitiesService implements GqtHotelAvailabilitiesPort {

  private GqtHotelAvailabilitiesPersistencePort gqtHotelAvailabilitiesPersistencePort;
  private final ContentClientLookUpService contentClientLookUpService;
  private final CityTaxFeatureUtil cityTaxFeatureUtil;

  @Override
  public List<GqtOperaHotelAvailabilities> getGqtHotelAvailabilities(final GqtSearchPayload gqtSearchPayload) {
    log.info("Processing getGqtHotelAvailabilities for criteria - hotelCodes={}, arrival={}, "
            + "departure={}, country={}, language={}",
        gqtSearchPayload.getHotelCodes() == null ? "" :
            gqtSearchPayload.getHotelCodes().stream()
                .map(SanitizingUtils::sanitize)
                .toList(),
        sanitize(gqtSearchPayload.getArrival()),
        sanitize(gqtSearchPayload.getDeparture()),
        sanitize(gqtSearchPayload.getCountry()),
        sanitize(gqtSearchPayload.getLanguage()));

    String country = gqtSearchPayload.getCountry();
    String language = gqtSearchPayload.getLanguage();
    List<String> hotelCodes = gqtSearchPayload.getHotelCodes();

    if (cityTaxFeatureUtil.isFeatureEnabled()) {
      gqtSearchPayload.setHotelsCityTaxInfo(
          contentClientLookUpService.getHotelsCityTaxInfo(country, language, hotelCodes));
    }

    final List<GqtOperaHotelAvailabilities> gqtOperaHotelAvailabilitiesList =
        gqtHotelAvailabilitiesPersistencePort.getGqtHotelAvailabilitiesForOpera(gqtSearchPayload);
    log.info("gqtOperaHotelAvailabilitiesList: {}", gqtOperaHotelAvailabilitiesList);

    return gqtOperaHotelAvailabilitiesList;
  }
}

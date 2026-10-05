package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtSearchPayload;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.GqtHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.gqt.GqtHotelAvailabilitiesMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.GqtSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.gqt.GqtHotelAvailabilitiesDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.gqt.GqtOperaHotelAvailabilitiesDto;

@Slf4j
@AllArgsConstructor
@RestController
public class GqtHotelAvailabilitiesController {

  private final GqtHotelAvailabilitiesPort gqtHotelAvailabilitiesService;

  private final GqtHotelAvailabilitiesMapper gqtHotelAvailabilitiesMapper;

  @GetMapping(value = "/gqt/search/hotels/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GqtHotelAvailabilitiesDto> getGqtHotelAvailabilities(
      @Valid GqtSearchCriteria gqtSearchCriteria) {

    log.info("Requesting gqrHotelAvailabilities with gqtAvailabilitiesSearchCriteria - hotelCodes={}, arrival={}, "
        + "departure={}, country={}, language={}",
        gqtSearchCriteria.getHotelCodes() == null ? "" :
            gqtSearchCriteria.getHotelCodes().stream()
              .map(SanitizingUtils::sanitize)
              .toList(),
        sanitize(gqtSearchCriteria.getArrival()),
        sanitize(gqtSearchCriteria.getDeparture()),
        sanitize(gqtSearchCriteria.getCountry()),
        sanitize(gqtSearchCriteria.getLanguage()));

    var gqtSearchPayload = buildGqtSearchPayload(gqtSearchCriteria);

    final List<GqtOperaHotelAvailabilities> hotels =
        gqtHotelAvailabilitiesService.getGqtHotelAvailabilities(gqtSearchPayload);

    GqtHotelAvailabilitiesDto gqtHotelAvailabilitiesDto;
    if (CollectionUtils.isEmpty(hotels)) {
      log.info(
          "No availabilities found in DB and returning Empty GQT HotelAvailabilities with status - 200");
      gqtHotelAvailabilitiesDto = new GqtHotelAvailabilitiesDto(Collections.emptyList());
      log.debug("gqtHotelAvailabilitiesDto: {}", gqtHotelAvailabilitiesDto);
      return new ResponseEntity<>(gqtHotelAvailabilitiesDto, HttpStatus.OK);
    }

    final List<GqtOperaHotelAvailabilitiesDto> gqtOperaHotelAvailabilitiesDtoList =
        gqtHotelAvailabilitiesMapper.toGqtOperaHotelAvailabilitiesDtoList(hotels);
    log.debug("gqtOperaHotelAvailabilitiesDtoList - {} and hotels - {}", gqtOperaHotelAvailabilitiesDtoList, hotels);

    gqtHotelAvailabilitiesDto = new GqtHotelAvailabilitiesDto(gqtOperaHotelAvailabilitiesDtoList);
    log.debug("gqtHotelAvailabilitiesDto - {}", gqtHotelAvailabilitiesDto);
    ResponseEntity<GqtHotelAvailabilitiesDto> responseEntity = new ResponseEntity<>(gqtHotelAvailabilitiesDto,
        HttpStatus.OK);
    log.debug("responseEntity body - {} and response entity status code - {}", responseEntity.getBody(),
        responseEntity.getStatusCode());
    return new ResponseEntity<>(gqtHotelAvailabilitiesDto, HttpStatus.OK);
  }

  private GqtSearchPayload buildGqtSearchPayload(GqtSearchCriteria gqtSearchCriteria) {
    return GqtSearchPayload.builder()
        .hotelCodes(gqtSearchCriteria.getHotelCodes())
        .arrival(gqtSearchCriteria.getArrival())
        .departure(gqtSearchCriteria.getDeparture())
        .country(gqtSearchCriteria.getCountry())
        .language(gqtSearchCriteria.getLanguage())
        .build();
  }
}

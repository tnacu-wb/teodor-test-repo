package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.DataValidationPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.HotelAvailabilitiesException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.DataValidationMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.DataValidationSearchRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.HotelAvailResultSetDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.HotelAvailResultSetDtoList;

@Slf4j
@AllArgsConstructor
@RestController
@ConditionalOnProperty(value = "data.validation.enabled", havingValue = "true")
public class DataValidatorController {

  private final DataValidationPort dataValidationSvc;
  private final DataValidationMapper mapper;

  @GetMapping(value = "/search/hotels/data", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HotelAvailResultSetDtoList> dataValidator(@Valid DataValidationSearchRequest searchCriteria) {
    try {
      log.info("Requesting hotel availabilities for data validation with input:{}",
          sanitize(searchCriteria));
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesFrmDB =
          dataValidationSvc.getHotelAvailabilities(searchCriteria);
      final List<HotelAvailResultSetDto> hotelAvailabilitiesDto =
          mapper.toHotelAvailResultSetDtoList(hotelAvailabilitiesFrmDB);
      final HotelAvailResultSetDtoList hotelAvailabilitiesDtoLst =
          buildDistributionHotelAvailabilitiesDto(hotelAvailabilitiesDto);
      log.debug("Hotel Availabilities Result Frm DB: {}", hotelAvailabilitiesDtoLst);
      return new ResponseEntity<>(hotelAvailabilitiesDtoLst, HttpStatus.OK);
    } catch (Exception ex) {
      log.error("Error occurred while searching hotel availabilities {}", ex.getMessage());
      throw new HotelAvailabilitiesException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private HotelAvailResultSetDtoList buildDistributionHotelAvailabilitiesDto(
      final List<HotelAvailResultSetDto> hotelAvailabilitiesDto) {
    final HotelAvailResultSetDtoList hotelAvailabilitiesDtoLst =
        new HotelAvailResultSetDtoList(hotelAvailabilitiesDto.size(), hotelAvailabilitiesDto);
    log.debug("Hotel availabilities result set : {}", hotelAvailabilitiesDtoLst);
    return hotelAvailabilitiesDtoLst;
  }

}

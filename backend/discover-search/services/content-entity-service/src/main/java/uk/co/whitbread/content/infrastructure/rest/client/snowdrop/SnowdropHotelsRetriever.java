package uk.co.whitbread.content.infrastructure.rest.client.snowdrop;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.content.domain.model.dlp.in.DlpInformationRequest;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.DlpInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.HotelDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.AemClient;
import uk.co.whitbread.content.infrastructure.rest.client.content.mapper.LabelsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.CategoryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.adapter.SnowdropClient;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.model.in.HotelLocationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.model.out.HotelLocationResponseDto;

@RequiredArgsConstructor
@Component
@Slf4j
public class SnowdropHotelsRetriever {

  public static final String DLP_RADIUS_UNITS = "dlp.config.map.radius.units";
  public static final String DLP_RADIUS = "dlp.config.map.radius";
  public static final String EN_RADIUS = "30";
  public static final String EN_RADIUS_UNIT = "mi";
  public static final String DE_RADIUS = "50";
  public static final String DE_RADIUS_UNIT = "km";
  public static final String LANGUAGE_EN = "en";
  private final SnowdropClient snowdropClient;
  private final AemClient aemClient;
  private final LabelsRequestMapper labelsRequestMapper;

  public List<HotelDto> mapHotelsFromSnowdrop(DlpInformationRequest dlpInformationRequest,
                                                 DlpInformationDto dlpInformationDto) {
    return Optional.ofNullable(retrieveHotelsFromSnowdrop(dlpInformationRequest, dlpInformationDto))
            .orElse(Collections.emptyList())
            .stream()
            .map(response -> HotelDto.builder()
                    .code(response.getCode())
                    .build())
            .toList();
  }

  public List<HotelLocationResponseDto> retrieveHotelsFromSnowdrop(
          DlpInformationRequest dlpInformationRequest, DlpInformationDto dlpInformationDto) {
    log.debug("Entered retrieveHotelsFromSnowdrop with country={}, language={}, dlpPath={}",
            dlpInformationRequest.getCountry(), dlpInformationRequest.getLanguage(),
            dlpInformationRequest.getDlpPath());
    var map = dlpInformationDto.getMap();
    if (map == null || map.getLatitude() == null || map.getLongitude() == null) {
      return Collections.emptyList();
    }
    boolean isEnglish = LANGUAGE_EN.equalsIgnoreCase(dlpInformationRequest.getLanguage());
    Map<String, String> labels = aemClient.getLabels(
            labelsRequestMapper.toDtoModel(dlpInformationRequest.getCountry(),
                    dlpInformationRequest.getLanguage(), CategoryEnumDto.MAIN));

    String radiusUnits = labels.getOrDefault(
            DLP_RADIUS_UNITS, isEnglish ? EN_RADIUS_UNIT : DE_RADIUS_UNIT);

    String radius = StringUtils.isNotEmpty(map.getRadius()) ? map.getRadius()
            : labels.getOrDefault(DLP_RADIUS, isEnglish ? EN_RADIUS : DE_RADIUS);

    return snowdropClient.getHotelsLocationByLatLong(new HotelLocationRequestDto(
                map.getLatitude(), map.getLongitude(), radius, radiusUnits));
  }
}

package uk.co.whitbread.reservation.infrastructure.rest.client.content;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.HotelPaymentInformation;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.reservation.domain.model.out.BusinessNotesResponse;
import uk.co.whitbread.reservation.domain.model.out.HotelInfoResponse;
import uk.co.whitbread.reservation.domain.model.searchrules.out.SearchRules;
import uk.co.whitbread.reservation.domain.ports.secondary.CacheOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.HotelInformationMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.HotelPaymentInformationMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.IndexHeaderResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.NotesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.SearchRulesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.service.ContentClient;

@Slf4j
@RequiredArgsConstructor
public class ContentOutPortImpl implements ContentOutPort {

  private final ContentClient contentClient;
  private static final String CONTROL_CHARACTER_REGEX = "[\\\\p{Cntrl}\\\\r\\\\n]";
  private static final String LANGUAGE_CODE_REGEX = "^[a-zA-Z]{2,5}$";
  private final IndexHeaderResponseMapper indexHeaderResponseMapper;
  private final NotesResponseMapper notesResponseMapper;
  private final HotelPaymentInformationMapper hotelPaymentInformationMapper;
  private final HotelInformationMapper hotelInformationMapper;
  private final SearchRulesResponseMapper searchRulesResponseMapper;
  private final CacheOutPort cacheOutPort;

  public IndexHeaderData getIndexHeaderData(String country, String language) {
    log.debug("Entered getIndexHeader with country={}, language={}",
        sanitize(country, CONTROL_CHARACTER_REGEX),
        sanitize(language, LANGUAGE_CODE_REGEX));
    final var indexData = contentClient.getIndexHeaderData(country, language);
    return indexHeaderResponseMapper.toModel(indexData);
  }

  @Override
  public BusinessNotesResponse getBusinessNotes(String language) {
    log.debug("Entered getBusinessNotes with language={}", sanitize(language, LANGUAGE_CODE_REGEX));
    var clientResponse = contentClient.getBusinessNotes(language);
    return notesResponseMapper.toModel(clientResponse);
  }

  @Override
  public HotelPaymentInformation getHotelPaymentInformation(String hotelId,
      String language, String country) {
    log.debug("Entered getHotelPaymentInformation with language={}, country={}",
        sanitize(language, CONTROL_CHARACTER_REGEX),
        sanitize(country, CONTROL_CHARACTER_REGEX));
    var hotelPaymentInformationDto = contentClient.getHotelPaymentInformation(hotelId, language,
        country);
    return hotelPaymentInformationMapper.toModel(hotelPaymentInformationDto);
  }

  @Override
  public HotelInfoResponse getHotelInformation(String hotelId, String country, String language) {
    log.debug("Entered getHotelInformation with hotelId={}",
        sanitize(hotelId, CONTROL_CHARACTER_REGEX));
    var hotelInformationDto = cacheOutPort.getHotelInformationFromCache(hotelId, country, language);
    if (hotelInformationDto == null) {
      hotelInformationDto = contentClient.getHotelInformation(hotelId, country, language);
    }
    return hotelInformationMapper.toModel(hotelInformationDto);
  }

  public SearchRules getSearchRules(String channel, Optional<String> brand) {
    log.debug("Entered getSearchRules with channel={}, brand={}",
        sanitize(channel, CONTROL_CHARACTER_REGEX),
        sanitize(brand.orElse(null), CONTROL_CHARACTER_REGEX));
    final var searchRules = contentClient.getSearchRules(channel, brand);
    return searchRulesResponseMapper.toModel(searchRules);
  }

  private String sanitize(final String input, final String regex) {
    return  Optional.ofNullable(input).map(c -> c.replaceAll(regex, "")).orElse("");
  }
}

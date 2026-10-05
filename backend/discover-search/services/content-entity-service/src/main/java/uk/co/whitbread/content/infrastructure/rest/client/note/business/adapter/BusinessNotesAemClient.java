package uk.co.whitbread.content.infrastructure.rest.client.note.business.adapter;

import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_BUSINESS_NOTE_EXCEPTION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.model.in.BusinessNotesResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.model.out.BusinessNotesRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out.BusinessNoteDto;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out.NoteDto;

@Slf4j
@RequiredArgsConstructor
@Component
public class BusinessNotesAemClient {

  public static final String BUSINESS_NOTES = "businessNotes";
  public static final String ALLOW = "allow";
  public static final String HEADER = "header";
  public static final String FOOTER = "footer";
  public static final String CARD_TYPES = "cardTypes";
  public static final String PACKAGES = "packages";
  public static final String ALLOWANCES = "allowances";
  private final WebClient aemWebClient;
  private final AemProperties aemProperties;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "BusinessNotesCache")
  public BusinessNotesResponseAemDto getBusinessNotes(
      BusinessNotesRequestAemDto businessNotesRequestAemDto) {
    log.debug("Get business notes from AEM. Language: {}", businessNotesRequestAemDto.getLang());

    return aemWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(aemProperties.getBusinessNotesEndpoint())
            .build(businessNotesRequestAemDto.getLang()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_BUSINESS_NOTE_EXCEPTION,
                  String.format("Get business notes from AEM. Language: %s",
                      businessNotesRequestAemDto.getLang()),
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
        })
        .map(dict -> map(dict, businessNotesRequestAemDto.getLang()))
        .doOnError(
            exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public BusinessNotesResponseAemDto map(Map<String, String> dictionary, String language) {
    Map<String, BusinessNoteDto> businessNotes = new HashMap<>();
    List<NoteDto> headers = new ArrayList<>();
    List<NoteDto> footers = new ArrayList<>();
    List<NoteDto> packages = new ArrayList<>();
    List<NoteDto> cardTypes = new ArrayList<>();
    List<NoteDto> allowances = new ArrayList<>();

    dictionary.forEach((k, v) -> {
      String[] key = k.split("\\.");
      String type = key[0];
      String id = key[1];

      NoteDto noteDto = new NoteDto(id, language, v);

      switch (type) {
        case BUSINESS_NOTES -> {
          BusinessNoteDto businessNote = businessNotes.getOrDefault(id, new BusinessNoteDto(id, language, "", ""));
          if (ALLOW.equalsIgnoreCase(key[2])) {
            businessNote.setAllow(v);
          } else {
            businessNote.setDeny(v);
          }
          businessNotes.putIfAbsent(id, businessNote);
        }
        case HEADER -> headers.add(noteDto);
        case FOOTER -> footers.add(noteDto);
        case PACKAGES -> packages.add(noteDto);
        case CARD_TYPES -> cardTypes.add(noteDto);
        case ALLOWANCES -> allowances.add(noteDto);
        default -> log.warn("Invalid type '{}' in AEM key '{}'", type, k);
      }
    });

    BusinessNotesResponseAemDto businessNotesResponseAemDto = new BusinessNotesResponseAemDto();
    businessNotesResponseAemDto.setBusinessNotes(businessNotes.values().stream().toList());
    businessNotesResponseAemDto.setHeaders(headers);
    businessNotesResponseAemDto.setFooters(footers);
    businessNotesResponseAemDto.setPackages(packages);
    businessNotesResponseAemDto.setCardTypes(cardTypes);
    businessNotesResponseAemDto.setAllowances(allowances);

    return businessNotesResponseAemDto;
  }
}

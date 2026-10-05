package uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata;

import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_PAGE_DATA_EXCEPTION;

import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.domain.model.inn.business.pagedata.in.PageDataRequest;
import uk.co.whitbread.content.domain.ports.secondary.PageDataOutPort;
import uk.co.whitbread.content.infrastructure.rest.client.content.exceptions.ContentException;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.aem.PageDataAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.mapper.PageDataRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.model.PageDataRequestDto;

@RequiredArgsConstructor
@Slf4j
public class PageDataOutPortImpl implements PageDataOutPort {

  private final PageDataRequestMapper pageDataRequestMapper;
  private final PageDataAemClient pageDataAemClient;

  @Override
  public Map<String, Map<String, String>> getPageData(PageDataRequest pageDataRequest) {
    log.debug("Entered getPageData with country={}, language={}, dictionaries={}",
        pageDataRequest.getCountry(), pageDataRequest.getLanguage(),
        pageDataRequest.getDictionaries().size());

    PageDataRequestDto pageDataRequestDto = pageDataRequestMapper.toDtoModel(pageDataRequest);
    if (pageDataRequestDto.getDictionaries() != null) {
      Collections.sort(pageDataRequestDto.getDictionaries());
    }
    final Map<String, Map<String, String>> pageData = pageDataAemClient.getPageData(pageDataRequestDto);

    if (pageData != null && !pageData.isEmpty()) {
      return pageData;
    }

    var message = String.format(
        "No data for this getPageData request for country=%s, language=%s,"
            + " dictionaries=%s", pageDataRequest.getCountry(),
        pageDataRequest.getLanguage(), pageDataRequest.getDictionaries().size());
    var exception = new ContentException(AEM_PAGE_DATA_EXCEPTION, message);
    ExceptionLogger.log(log, exception);
    throw exception;
  }
}

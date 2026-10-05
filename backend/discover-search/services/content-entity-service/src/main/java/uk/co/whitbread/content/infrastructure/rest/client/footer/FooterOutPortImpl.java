package uk.co.whitbread.content.infrastructure.rest.client.footer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import uk.co.whitbread.content.domain.model.footer.in.FooterRequest;
import uk.co.whitbread.content.domain.model.footer.out.FooterResponse;
import uk.co.whitbread.content.domain.ports.secondary.FooterOutPort;
import uk.co.whitbread.content.infrastructure.rest.client.footer.adapter.FooterAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.footer.mapper.FooterRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.footer.mapper.FooterResponseMapper;

@Slf4j
@RequiredArgsConstructor
public class FooterOutPortImpl implements FooterOutPort {

  private final FooterRequestMapper footerRequestMapper;
  private final FooterAemClient aemClient;
  private final FooterResponseMapper footerResponseMapper;

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "FooterCache")
  public FooterResponse getFooterInformation(FooterRequest footerRequest) {
    log.debug("Entered getFooterInformation with country={}, language={}, brand={}",
        footerRequest.getCountry(), footerRequest.getLanguage(), footerRequest.getSite());

    var request = footerRequestMapper.toDto(footerRequest);
    var footerResponseAemDto = aemClient.getFooterInformation(request);

    return footerResponseMapper.toModel(footerResponseAemDto);
  }
}

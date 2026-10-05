package uk.co.whitbread.content.domain.logic;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.content.domain.model.inn.business.pagedata.in.PageDataRequest;
import uk.co.whitbread.content.domain.ports.primary.PageDataInPort;
import uk.co.whitbread.content.domain.ports.secondary.PageDataOutPort;

@Slf4j
@RequiredArgsConstructor
public class PageDataInPortImpl implements PageDataInPort {
  private final PageDataOutPort pageDataOutPort;

  @Override
  public Map<String, Map<String, String>> getPageData(PageDataRequest pageDataRequest) {
    return pageDataOutPort.getPageData(pageDataRequest);
  }
}

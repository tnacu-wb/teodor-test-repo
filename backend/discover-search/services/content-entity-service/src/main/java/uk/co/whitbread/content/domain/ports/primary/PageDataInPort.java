package uk.co.whitbread.content.domain.ports.primary;

import java.util.Map;
import uk.co.whitbread.content.domain.model.inn.business.pagedata.in.PageDataRequest;

public interface PageDataInPort {
  Map<String, Map<String, String>> getPageData(PageDataRequest pageDataRequest);

}

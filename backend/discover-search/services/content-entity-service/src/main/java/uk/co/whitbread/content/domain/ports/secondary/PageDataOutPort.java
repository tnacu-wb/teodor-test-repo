package uk.co.whitbread.content.domain.ports.secondary;

import java.util.Map;
import uk.co.whitbread.content.domain.model.inn.business.pagedata.in.PageDataRequest;

public interface PageDataOutPort {

  Map<String,  Map<String, String>> getPageData(PageDataRequest pageDataRequest);
}

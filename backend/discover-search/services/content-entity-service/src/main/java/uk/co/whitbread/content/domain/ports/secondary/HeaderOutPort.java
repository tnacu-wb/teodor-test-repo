package uk.co.whitbread.content.domain.ports.secondary;

import uk.co.whitbread.content.domain.model.inn.business.header.in.HeaderRequest;
import uk.co.whitbread.content.domain.model.inn.business.header.in.LayoutRequest;
import uk.co.whitbread.content.domain.model.inn.business.header.out.HeaderResponse;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Layout;

public interface HeaderOutPort {

  HeaderResponse getHeaderInformation(HeaderRequest headerRequest);

  Layout getLayoutInformation(LayoutRequest layoutRequest);

}

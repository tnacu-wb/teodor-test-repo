package uk.co.whitbread.content.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.content.domain.model.inn.business.header.in.HeaderRequest;
import uk.co.whitbread.content.domain.model.inn.business.header.in.LayoutRequest;
import uk.co.whitbread.content.domain.model.inn.business.header.out.HeaderResponse;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Layout;
import uk.co.whitbread.content.domain.ports.primary.HeaderInPort;
import uk.co.whitbread.content.domain.ports.secondary.HeaderOutPort;

@Slf4j
@RequiredArgsConstructor
public class HeaderInPortImpl implements HeaderInPort {

  private final HeaderOutPort headerOutPort;

  @Override
  public HeaderResponse getHeaderInformation(HeaderRequest headerRequest) {
    return headerOutPort.getHeaderInformation(headerRequest);
  }

  @Override
  public Layout getLayoutInformation(LayoutRequest layoutRequest) {
    return headerOutPort.getLayoutInformation(layoutRequest);
  }
}

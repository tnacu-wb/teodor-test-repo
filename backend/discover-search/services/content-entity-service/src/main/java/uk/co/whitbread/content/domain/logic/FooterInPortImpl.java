package uk.co.whitbread.content.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.content.domain.model.footer.in.FooterRequest;
import uk.co.whitbread.content.domain.model.footer.out.FooterResponse;
import uk.co.whitbread.content.domain.ports.primary.FooterInPort;
import uk.co.whitbread.content.domain.ports.secondary.FooterOutPort;

@Slf4j
@RequiredArgsConstructor
public class FooterInPortImpl implements FooterInPort {

  private final FooterOutPort footerOutPort;

  @Override
  public FooterResponse getFooterInformation(FooterRequest footerRequest) {
    return footerOutPort.getFooterInformation(footerRequest);
  }

}

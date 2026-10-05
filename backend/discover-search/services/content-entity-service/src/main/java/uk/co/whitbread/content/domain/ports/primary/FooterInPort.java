package uk.co.whitbread.content.domain.ports.primary;

import uk.co.whitbread.content.domain.model.footer.in.FooterRequest;
import uk.co.whitbread.content.domain.model.footer.out.FooterResponse;

public interface FooterInPort {

  FooterResponse getFooterInformation(FooterRequest footerRequest);
}

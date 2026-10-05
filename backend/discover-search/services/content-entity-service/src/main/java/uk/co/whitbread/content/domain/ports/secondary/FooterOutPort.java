package uk.co.whitbread.content.domain.ports.secondary;

import uk.co.whitbread.content.domain.model.footer.in.FooterRequest;
import uk.co.whitbread.content.domain.model.footer.out.FooterResponse;

public interface FooterOutPort {

  FooterResponse getFooterInformation(FooterRequest footerRequest);
}

package uk.co.whitbread.content.domain.ports.primary;

import uk.co.whitbread.content.domain.model.seo.in.SeoRequest;
import uk.co.whitbread.content.domain.model.seo.out.SeoResponse;

public interface SeoInPort {

  SeoResponse getSeoInformation(SeoRequest seoRequest);
}

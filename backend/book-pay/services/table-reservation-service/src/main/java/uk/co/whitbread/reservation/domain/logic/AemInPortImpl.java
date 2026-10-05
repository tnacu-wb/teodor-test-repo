package uk.co.whitbread.reservation.domain.logic;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.whitbread.reservation.domain.model.out.aem.AemCookieContentResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemFooterResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemHeaderResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.BookPage;
import uk.co.whitbread.reservation.domain.model.out.aem.LocationsResponse;
import uk.co.whitbread.reservation.domain.model.out.getlabel.LabelDataListResponse;
import uk.co.whitbread.reservation.domain.ports.primary.AemInPort;
import uk.co.whitbread.reservation.domain.ports.secondary.AemOutPort;

@Service
@Slf4j
@Data
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AemInPortImpl implements AemInPort {

  private final AemOutPort aemOutPortImpl;

  @Override
  public AemHeaderResponse getHeaders(String restaurant) {
    return aemOutPortImpl.getHeaders(restaurant);
  }

  @Override
  public AemFooterResponse getFooters(String restaurant) {
    return aemOutPortImpl.getFooters(restaurant);
  }

  @Override
  public BookPage getBookPageContent(String restaurant, String location, String subLocation) {
    return aemOutPortImpl.getBookPageContent(restaurant, location, subLocation);
  }

  @Override
  public AemCookieContentResponse getCookieContent() {
    return aemOutPortImpl.getCookieContent();
  }

  @Override
  public LocationsResponse locations(String restaurant, String location, String subLocation) {
    return aemOutPortImpl.locations(restaurant, location, subLocation);
  }

  @Override
  public LabelDataListResponse getLabel() {
    return aemOutPortImpl.getLabel();
  }

}

package uk.co.whitbread.reservation.domain.ports.primary;

import uk.co.whitbread.reservation.domain.model.out.aem.AemCookieContentResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemFooterResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemHeaderResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.BookPage;
import uk.co.whitbread.reservation.domain.model.out.aem.LocationsResponse;
import uk.co.whitbread.reservation.domain.model.out.getlabel.LabelDataListResponse;

public interface AemInPort {

  AemHeaderResponse getHeaders(String restaurant);

  AemFooterResponse getFooters(String restaurant);

  BookPage getBookPageContent(String restaurant, String location, String subLocation);

  AemCookieContentResponse getCookieContent();

  LocationsResponse locations(String restaurant, String location, String subLocation);

  LabelDataListResponse getLabel();

}

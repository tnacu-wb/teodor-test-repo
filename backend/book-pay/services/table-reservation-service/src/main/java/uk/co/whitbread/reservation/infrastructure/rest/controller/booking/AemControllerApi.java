package uk.co.whitbread.reservation.infrastructure.rest.controller.booking;

import jakarta.validation.constraints.Pattern;
import java.util.List;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemCookieContentResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemFooterResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemHeaderResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.BookPageDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.LabelDataDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.ZonalUuidResponseDto;

public interface AemControllerApi {

  AemHeaderResponseDto getHeaders(String restaurant);

  AemFooterResponseDto getFooters(String restaurant);

  BookPageDto getBookPageContent(String restaurant, String location, String subLocation);

  AemCookieContentResponseDto getCookieContentResponseDto();

  List<ZonalUuidResponseDto> locations(
      @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Invalid restaurant: only letters, numbers, and dashes allowed.")
      String restaurant,
      String location,
      String subLocation);

  List<LabelDataDto> getLabel();

}

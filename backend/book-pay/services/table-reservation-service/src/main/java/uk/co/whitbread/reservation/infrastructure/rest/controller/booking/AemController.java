package uk.co.whitbread.reservation.infrastructure.rest.controller.booking;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.reservation.domain.model.out.aem.AemCookieContentResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemFooterResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemHeaderResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.BookPage;
import uk.co.whitbread.reservation.domain.ports.primary.AemInPort;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.BookPageResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.CookieContentResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.FooterResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.HeaderResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.LabelDataResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.ZonalUuidResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemCookieContentResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemFooterResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemHeaderResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.BookPageDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.LabelDataDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.ZonalUuidResponseDto;

@RequestMapping(value = "event/v1")
@RestController
@Validated
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AemController implements AemControllerApi {

  private final AemInPort aemInPort;
  private final HeaderResponseMapper headerResponseMapper;
  private final FooterResponseMapper footerResponseMapper;
  private final BookPageResponseMapper bookPageResponseMapper;
  private final CookieContentResponseMapper cookieContentResponseMapper;
  private final ZonalUuidResponseMapper zonalUuidResponseMapper;

  private final LabelDataResponseMapper labelDataResponseMapper;

  @GetMapping("/headers")
  @Cacheable(value = "headersCache")
  public AemHeaderResponseDto getHeaders(@RequestParam String restaurant) {
    AemHeaderResponse aemHeaderResponse = aemInPort.getHeaders(restaurant);
    log.info("aemHeaderResponse ::::{}", aemHeaderResponse);
    AemHeaderResponseDto aemHeaderResponseDto = headerResponseMapper.toDto(aemHeaderResponse);
    log.info("aemHeaderResponseDto :::::{}", aemHeaderResponseDto);
    return aemHeaderResponseDto;
  }

  @GetMapping("/footers")
  @Cacheable(value = "footersCache")
  public AemFooterResponseDto getFooters(@RequestParam String restaurant) {
    AemFooterResponse aemFooterResponse = aemInPort.getFooters(restaurant);
    log.info("aemFooterResponse:::{}", aemFooterResponse);
    return footerResponseMapper.toDto(aemFooterResponse);
  }

  @GetMapping("/book-page")
  @Cacheable(value = "bookPageCache", cacheManager = "cacheManager30Min")
  public BookPageDto getBookPageContent(@RequestParam String restaurant,
      @RequestParam String location,
      @RequestParam String subLocation) {
    BookPage bookPage = aemInPort.getBookPageContent(restaurant, location, subLocation);
    log.info("bookPage:::{}", bookPage);
    return bookPageResponseMapper.toDto(bookPage);

  }

  @GetMapping("/cookie-consent")
  @Cacheable(value = "cookieContentCache")
  public AemCookieContentResponseDto getCookieContentResponseDto() {
    AemCookieContentResponse aemCookieContentResponse = aemInPort.getCookieContent();
    return cookieContentResponseMapper.toDto(aemCookieContentResponse);
  }

  @GetMapping("/locations")
  public List<ZonalUuidResponseDto> locations(@RequestParam String restaurant,
      @RequestParam String location,
      @RequestParam String subLocation) {
    return zonalUuidResponseMapper.toDto(
        aemInPort.locations(restaurant, location, subLocation).getLocations());
  }

  @GetMapping("/label-data")
  public List<LabelDataDto> getLabel() {
    return labelDataResponseMapper.toDto(aemInPort.getLabel().getLabels());
  }


}

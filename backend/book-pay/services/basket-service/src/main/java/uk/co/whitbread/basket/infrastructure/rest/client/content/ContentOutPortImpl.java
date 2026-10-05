package uk.co.whitbread.basket.infrastructure.rest.client.content;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNotesResponse;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.basket.infrastructure.rest.client.content.mapper.ContentResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.content.service.ContentClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentOutPortImpl implements ContentOutPort {

  private final ContentClient contentClient;
  private final ContentResponseMapper contentResponseMapper;

  @Override
  public HotelPaymentInformation getHotelPaymentDetails(String hotelCode, String country,
                                                        String language) {
    log.debug("Entered getHotelPaymentDetails with hotelCode={}, country={} and language={}",
        hotelCode, country, language);
    var clientResponse =
        contentClient.getHotelPaymentDetails(hotelCode, country, language);
    return contentResponseMapper.toModel(clientResponse);
  }

  @Override
  public BusinessNotesResponse getBusinessNotes(String language) {
    log.debug("Entered getBusinessNotes with language={}",
        language);
    var clientResponse =
        contentClient.getBusinessNotes(language);
    return contentResponseMapper.toModel(clientResponse);
  }


}

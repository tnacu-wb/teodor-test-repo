package uk.co.whitbread.wallet.infrastructure.rest.client.content;

import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.PAGE_EXTENSION;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.PREMIER_INN_DE_LINK;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.PREMIER_INN_LINK;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.generated.models.content.HotelInformationDto;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.wallet.ErrorCode;
import uk.co.whitbread.wallet.domain.exception.HotelInfoNotFoundException;
import uk.co.whitbread.wallet.domain.model.out.HotelInfo;
import uk.co.whitbread.wallet.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.wallet.infrastructure.rest.client.content.mapper.HotelInfoMapper;
import uk.co.whitbread.wallet.infrastructure.rest.client.content.service.ContentClient;

@RequiredArgsConstructor
@Slf4j
public class ContentOutPortImpl implements ContentOutPort {

  private final ContentClient contentClient;
  private final HotelInfoMapper hotelInfoMapper;

  @Override
  public HotelInfo getHotelInformation(String country, String language, String hotelId) {
    HotelInformationDto hotelInformation = Optional.ofNullable(
        contentClient.getHotelInformation(country, language,
            hotelId)).orElseThrow(() -> {
              HotelInfoNotFoundException hotelInfoNotFoundException = new HotelInfoNotFoundException(
                  ErrorCode.DIGITAL_HOTEL_INFO_NOT_FOUND_EXCEPTION,
                  String.format("Hotel Information not found for hotel %s", hotelId));
              ExceptionLogger.log(log, hotelInfoNotFoundException);
              return hotelInfoNotFoundException;
            });
    HotelInfo hotelInfoModel = hotelInfoMapper.toHotelInfoModel(hotelInformation);
    String baseLink = language.equalsIgnoreCase("en") ? PREMIER_INN_LINK : PREMIER_INN_DE_LINK;
    hotelInfoModel.setLinks(baseLink + hotelInfoModel.getLinks() + PAGE_EXTENSION);
    return hotelInfoModel;
  }
}

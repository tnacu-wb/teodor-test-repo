package uk.co.whitbread.wallet.infrastructure.rest.client.content.mapper;

import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.PI_BRAND;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.PI_BRAND_DE;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.PI_BRAND_NAME;

import java.util.List;
import org.jsoup.Jsoup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.basket.generated.models.content.AddressDto;
import uk.co.whitbread.basket.generated.models.content.HotelInformationDto;
import uk.co.whitbread.wallet.domain.model.out.HotelInfo;

@Mapper(componentModel = "spring")
public abstract class HotelInfoMapper {

  @Mapping(source = "name", target = "hotelName")
  @Mapping(source = "brand", target = "brand", qualifiedByName = "toBrandModel")
  @Mapping(source = "coordinates.latitude", target = "latitude")
  @Mapping(source = "coordinates.longitude", target = "longitude")
  @Mapping(source = "address", target = "address", qualifiedByName = "toAddressModel")
  @Mapping(source = "contactDetails.hotelNationalPhone", target = "phone")
  @Mapping(source = "parkingDescription", target = "parking", qualifiedByName = "toParkingDescriptionModel")
  @Mapping(source = "links.detailsPage", target = "links")
  @Mapping(source = "address", target = "country", qualifiedByName = "toCountryModel")
  public abstract HotelInfo toHotelInfoModel(HotelInformationDto hotelInformationDto);

  @Named("toAddressModel")
  protected String toAddressModel(AddressDto addressDto) {
    return addressDto.getAddressLine1() + ", " + addressDto.getAddressLine2() + ", "
        + addressDto.getPostalCode();
  }

  @Named("toParkingDescriptionModel")
  protected String toParkingDescriptionModel(String parkingDescription) {
    return Jsoup.parse(parkingDescription).text();
  }

  @Named("toBrandModel")
  protected String toBrandModel(String brand) {
    return List.of(PI_BRAND, PI_BRAND_DE).contains(brand) ? PI_BRAND_NAME : brand;
  }

  @Named("toCountryModel")
  protected String toCountryModel(AddressDto addressDto) {
    return addressDto.getCountry();
  }
}

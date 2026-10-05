package uk.co.whitbread.hotel.info.model.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HotelInfoRequest {

    private Country country = Country.GB;
    private Language language = Language.EN;
    private HotelInfoFormat format = HotelInfoFormat.LONG;
}
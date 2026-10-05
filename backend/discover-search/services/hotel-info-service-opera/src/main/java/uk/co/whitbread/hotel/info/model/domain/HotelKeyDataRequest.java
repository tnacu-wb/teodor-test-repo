package uk.co.whitbread.hotel.info.model.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HotelKeyDataRequest {

    private Language language = Language.EN;
}
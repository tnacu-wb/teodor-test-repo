package uk.co.whitbread.hotel.info.model.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MultiHotelKeyDataResponse {
    private List<HotelKeyData> keyData;
}

package uk.co.whitbread.hotel.info.model.domain;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MultiHotelInfoResponse {

    private int total;

    @JsonRawValue
    private List<String> hotels;
}

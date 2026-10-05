package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates;

import lombok.*;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatePlanEntityDto {
    private HotelEntity hotelEntity;
    private String roomType;
    private String rateCode;
    private String currencyCode;
    private boolean isAvailable;
    private int minLos;
    private int maxLos;
    private RatePlanScheduleDetail ratePlanScheduleDetail;
}

package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DailyRatesInput {

    private String hotelId;
    private String ratePlanCode;
    private String startDate;
    private String endDate;
    private long limit;

}

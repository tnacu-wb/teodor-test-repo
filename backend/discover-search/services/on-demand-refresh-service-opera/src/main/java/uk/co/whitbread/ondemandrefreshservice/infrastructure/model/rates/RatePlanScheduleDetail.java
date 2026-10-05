package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatePlanScheduleDetail {

    private RateAmounts rateAmounts;
    private RateAmountBoundaries rateAmountBoundaries;
    private Classifications classifications;
    private RateSchedulePackages rateSchedulePackages;
    private int tierID;
    private String start;
    private String end;
    private List<String> roomTypeList;
    private boolean sunday;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;

}

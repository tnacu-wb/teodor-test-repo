package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PrimaryDetails {

    private Description description;
    private String startSellDate;
    private String endSellDate;
    private boolean privilegedRate;
    private boolean privilegedRateRestriction;
    private String lockStatus;
    private int sellSequence;


}

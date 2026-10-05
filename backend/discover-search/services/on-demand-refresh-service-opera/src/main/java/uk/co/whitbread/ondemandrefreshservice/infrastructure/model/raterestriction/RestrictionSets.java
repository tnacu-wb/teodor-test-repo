package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RestrictionSets {

    private RestrictionControl restrictionControl;
    private RestrictionStatus restrictionStatus;
    private ActualTimeSpan actualTimeSpan;
    private boolean onRequest;
    private String start;
    private String end;
    private boolean sunday;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;

}

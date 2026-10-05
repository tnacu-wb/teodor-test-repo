package uk.co.whitbread.ondemandrefreshservice.domain.model;

import java.time.LocalDateTime;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SchedulerRequest {
    private Set<String> hotelIds;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean reschedule;
    private LocalDateTime jobTriggerTime;
}

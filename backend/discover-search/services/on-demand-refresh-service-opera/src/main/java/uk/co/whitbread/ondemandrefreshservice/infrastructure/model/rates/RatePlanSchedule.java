package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RatePlanSchedule {
    @EqualsAndHashCode.Include
    private RatePlanScheduleId ratePlanScheduleId;
    private RatePlanScheduleDetail ratePlanScheduleDetail;
}

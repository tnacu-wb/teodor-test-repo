package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class RestrictionControl {
    private boolean house;
    private String roomType;
    private String ratePlanCode;
    private String ratePlanCategory;
}

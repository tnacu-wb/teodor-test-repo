package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingPreference {

    @JsonProperty("FoodPreference")
    private long foodPreference;

    @JsonProperty("PreselectWifi")
    private boolean preselectWifi;

    @JsonProperty("Reason")
    private String reason;

    @JsonProperty("RoomRequirements")
    private RoomRequirements roomRequirements;
}

package uk.co.whitbread.contentservice.roomtypes.model.aem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AEMCookiePoliciesResponse {
    @JsonProperty(value = ":items")
    private AEMCookiePoliciesItems items;
}

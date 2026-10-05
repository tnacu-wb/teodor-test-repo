package uk.co.whitbread.contentservice.roomtypes.model.aem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class RateRoot {
    @JsonProperty(value = ":items")
    private Map<String, RateContentFragment> items;
}

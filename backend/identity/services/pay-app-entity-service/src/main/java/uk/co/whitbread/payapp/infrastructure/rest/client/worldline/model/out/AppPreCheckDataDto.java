package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AppPreCheckDataDto(Boolean isTetheredUser, @JsonProperty("applicationGUID") String applicationGuid) {
}

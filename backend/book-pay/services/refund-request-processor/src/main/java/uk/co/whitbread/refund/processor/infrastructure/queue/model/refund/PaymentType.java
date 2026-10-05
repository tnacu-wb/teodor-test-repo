package uk.co.whitbread.refund.processor.infrastructure.queue.model.refund;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentType {
    @JsonProperty("CARD")
    CARD,
    @JsonProperty("PIBA")
    PIBA,
    @JsonProperty("PIBA_EU")
    PIBA_EU
}

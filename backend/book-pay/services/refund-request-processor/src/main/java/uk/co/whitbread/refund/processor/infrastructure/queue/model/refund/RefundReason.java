package uk.co.whitbread.refund.processor.infrastructure.queue.model.refund;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RefundReason {
    @JsonProperty("CANCEL")
    CANCEL,
    @JsonProperty("AMEND")
    AMEND,
    @JsonProperty("ROLLBACK")
    ROLLBACK
}

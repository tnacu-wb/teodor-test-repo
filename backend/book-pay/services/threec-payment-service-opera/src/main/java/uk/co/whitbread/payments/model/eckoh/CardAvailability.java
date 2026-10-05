package uk.co.whitbread.payments.model.eckoh;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CardAvailability {
    CARD_PRESENT("cp"),
    CARD_NOT_PRESENT("cnp");

    final String eckohMapping;

    CardAvailability(String eckohMapping) {
        this.eckohMapping = eckohMapping;
    }

    @JsonValue
    public String getEckohMapping() {
        return eckohMapping;
    }
}

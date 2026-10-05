package uk.co.whitbread.company.model;

public enum AnswerType {
    F("F"),
    U("U");

    private final String value;

    AnswerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

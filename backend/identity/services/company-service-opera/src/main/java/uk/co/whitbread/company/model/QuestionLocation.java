package uk.co.whitbread.company.model;

public enum QuestionLocation {
    B("B"),
    R("R");

    private String value;

    QuestionLocation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

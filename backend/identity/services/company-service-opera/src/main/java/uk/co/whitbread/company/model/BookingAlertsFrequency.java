package uk.co.whitbread.company.model;

public enum BookingAlertsFrequency {
    N("N"),
    A("A"),
    D("D"),
    W("W"),
    M("M");

    private final String value;

    BookingAlertsFrequency(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

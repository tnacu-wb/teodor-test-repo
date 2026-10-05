package uk.co.whitbread.payments.model;

public enum BookingType {
    PAY_NOW("paynow"),
    PAY_ON_ARRIVAL("payonarrival");

    final String eckohMapping;

    BookingType(String eckohMapping) {
        this.eckohMapping = eckohMapping;
    }

    public String getEckohMapping() {
        return eckohMapping;
    }
}

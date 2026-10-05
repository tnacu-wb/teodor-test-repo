package uk.co.whitbread.hotel.info.model.domain;

public enum HotelInfoFormat {
    SHORT("summary"),
    LONG("complete");

    private String prefix;

    HotelInfoFormat(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}

package uk.co.whitbread.hotel.account.model;

public enum CountryCode {


    GB, DE;

    public String asLowerCase() {
        return this.name().toLowerCase();
    }

}

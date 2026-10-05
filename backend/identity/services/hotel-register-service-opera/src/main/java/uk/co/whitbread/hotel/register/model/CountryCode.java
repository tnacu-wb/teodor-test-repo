package uk.co.whitbread.hotel.register.model;

public enum CountryCode {


    GB,DE;

    public String asLowerCase(){
        return this.name().toLowerCase();
    }

}

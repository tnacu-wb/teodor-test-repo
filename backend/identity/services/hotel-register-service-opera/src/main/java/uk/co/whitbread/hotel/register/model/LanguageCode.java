package uk.co.whitbread.hotel.register.model;

public enum LanguageCode {

    EN,DE;

    public String asLowerCase(){
        return this.name().toLowerCase();
    }
}

package uk.co.whitbread.hotel.account.model;

public enum LanguageCode {

    EN, DE;

    public String asLowerCase() {
        return this.name().toLowerCase();
    }
}

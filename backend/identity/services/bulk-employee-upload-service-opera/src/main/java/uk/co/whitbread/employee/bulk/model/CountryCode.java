package uk.co.whitbread.employee.bulk.model;

public enum CountryCode {


    GB,DE;

    public String asLowerCase(){
        return this.name().toLowerCase();
    }

    public static String getCountryCode(String countryCode) {
        for (CountryCode code : CountryCode.values()) {
            if (code.name().equalsIgnoreCase(countryCode)) {
                return code.asLowerCase();
            }
        }
        return GB.asLowerCase();
    }
}

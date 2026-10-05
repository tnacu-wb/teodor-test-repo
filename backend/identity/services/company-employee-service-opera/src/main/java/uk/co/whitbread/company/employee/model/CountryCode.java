package uk.co.whitbread.company.employee.model;

public enum CountryCode {


    GB,DE;

    public String asLowerCase(){
        return this.name().toLowerCase();
    }

}

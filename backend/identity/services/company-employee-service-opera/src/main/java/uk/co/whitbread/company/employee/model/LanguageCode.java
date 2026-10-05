package uk.co.whitbread.company.employee.model;

public enum LanguageCode {

    EN,DE;

    public String asLowerCase(){
        return this.name().toLowerCase();
    }
}

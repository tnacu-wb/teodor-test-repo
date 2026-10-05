package uk.co.whitbread.employee.bulk.model;

public enum LanguageCode {

    EN,DE;

    public String asLowerCase(){
        return this.name().toLowerCase();
    }

    public static String getLanguageCode(String languageCode) {
        for (LanguageCode code : LanguageCode.values()) {
            if (code.name().equalsIgnoreCase(languageCode)) {
                return code.asLowerCase();
            }
        }
        return EN.asLowerCase();
    }
}

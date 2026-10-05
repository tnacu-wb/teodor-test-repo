package uk.co.whitbread.employee.bulk.model;

public enum SpreadsheetAccessLevel {

    GUEST("Guest"),
    SELF_BOOKER("Self Booker"),
    BOOKER("Booker"),
    TRAVEL_MANAGER("Travel Manager");

    private String accessLevel;

    SpreadsheetAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getAccessLevel() {
        return this.accessLevel;
    }
}

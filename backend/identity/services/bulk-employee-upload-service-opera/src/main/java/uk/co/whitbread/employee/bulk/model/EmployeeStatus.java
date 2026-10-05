package uk.co.whitbread.employee.bulk.model;

/**
 * The different statuses of company employees
 */
public enum EmployeeStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    SUSPENDED("SUSPENDED"),
    DEACTIVATED("DEACTIVATED"),
    PURGED("PURGED");

    private String value;

    EmployeeStatus(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EmployeeStatus valueOfIgnoreCase(String name) {
        if (name == null) {
            return null;
        }
        return valueOf(name.toUpperCase());
    }
}

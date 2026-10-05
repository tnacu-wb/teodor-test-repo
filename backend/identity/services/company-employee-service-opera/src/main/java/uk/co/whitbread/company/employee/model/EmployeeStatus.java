package uk.co.whitbread.company.employee.model;

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

}

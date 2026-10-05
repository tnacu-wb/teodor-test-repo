package uk.co.whitbread.employee.bulk.model;

import lombok.Data;

@Data
public class EmployeeListRequest {

    private boolean fullListMarker;
    private Integer recordsPerPage;
    private Integer pageRequired;
}


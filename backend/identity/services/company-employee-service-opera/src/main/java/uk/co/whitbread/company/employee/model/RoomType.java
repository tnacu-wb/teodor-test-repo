package uk.co.whitbread.company.employee.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum RoomType {
    FAM("Family"),
    DB("Double"),
    TWIN("Twin"),
    DIS("Disabled"),
    SB("Single");
    private final String description;

    RoomType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

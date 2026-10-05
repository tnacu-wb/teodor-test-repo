package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import uk.co.whitbread.hotel.account.validation.NotNullIfAnotherFieldHasValue;

@NotNullIfAnotherFieldHasValue.List({
        @NotNullIfAnotherFieldHasValue(
                dependFieldName = "employeeId",
                fieldName = "business",
                fieldValue = "true"),
        @NotNullIfAnotherFieldHasValue(
                dependFieldName = "companyId",
                fieldName = "business",
                fieldValue = "true")
})
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BBStaysRequest {

    private boolean business;
    private String companyId;
    private String employeeId;
    private BookingStatus typeOfBooking;
    private SortOrder sortOrder;
    private StaysFilterType filterType;
    private String filterValue;
    private boolean includeCheckInBookings;
    private String continuationToken;
}

package uk.co.whitbread.hotel.register.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Passport {

    @NotEmpty
    private String number;
    private String countryOfIssue;
}

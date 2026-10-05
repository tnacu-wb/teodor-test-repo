package uk.co.whitbread.hotel.register.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.register.validation.PostcodeConstraint;
import uk.co.whitbread.shared.commons.validation.CompanyName;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PostcodeConstraint
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {

    @NotEmpty
    private String line1;

    private String line2;

    private String line3;

    private String line4;

    private String line5;

    private String postCode;

    @NotEmpty
    private String countryCode;

    @NotNull
    private AddressType type;

    @CompanyName
    private String companyName;
}

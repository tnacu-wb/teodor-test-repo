package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.shared.commons.validation.CompanyName;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    private String line1;

    private String line2;

    private String line3;

    private String line4;

    private String line5;

    private String postCode;

    @Pattern(regexp = "^[A-Z]{1,3}$", message = "Invalid country code")
    private String countryCode;

    private String  countryCodeISO;

    private AddressType type;

    @CompanyName
    private String companyName;

}

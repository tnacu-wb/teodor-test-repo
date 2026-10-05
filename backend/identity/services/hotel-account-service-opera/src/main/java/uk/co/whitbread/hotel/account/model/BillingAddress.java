package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingAddress implements Serializable {

    private String line1;

    private String line2;

    private String line3;

    private String line4;

    private String line5;

    private String postCode;

    private String countryCode;

    private String  countryCodeISO;

    private AddressType type;

    private String companyName;

}

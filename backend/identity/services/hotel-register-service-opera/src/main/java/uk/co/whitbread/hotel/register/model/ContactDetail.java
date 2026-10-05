package uk.co.whitbread.hotel.register.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uk.co.whitbread.hotel.register.validation.Phone;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDetail {

    @NotEmpty
    private String title;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotNull
    @Email
    private String email;

    @Phone
    private String telephone;

    @Phone
    private String mobile;

    @Valid
    @NotNull
    private Address address;

    private String nationality;

    @Valid
    private Passport passport;

    private String carRegistration;
    private String dialingCode;

}
package uk.co.whitbread.hotel.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import uk.co.whitbread.hotel.payment.validation.TelephoneOrMobilePresent;

@Data
@TelephoneOrMobilePresent
@ToString(exclude = {"title", "firstName", "lastName", "address", "emailAddress", "telephoneNumber", "mobileNumber", "companyName"})
public class Booker {


    @NotEmpty
    @Schema(required = true, example = "Mr")
    private String title;
    @NotEmpty
    @Schema(required = true, example = "John")
    private String firstName;
    @NotEmpty
    @Schema(required = true, example = "Smith")
    private String lastName;
    @NotNull
    @Valid
    @Schema(required = true)
    private Address address;
    @NotEmpty
    @Schema(example = "johnsmith@whitbread.com")
    private String emailAddress;
    @Schema(example = "01582424200")
    private String telephoneNumber;
    @Schema(example = "07468473148")
    private String mobileNumber;
    @Schema(example = "STRING")
    private String guestHistoryNumber;
    @Schema(example = "false")
    private Boolean acceptFutureMailing;
    @Schema(example = "STRING")
    private String companyName;
    @Schema(example = "STRING")
    private String companyId;
    @Schema(example = "STRING")
    private String employeeId;


}

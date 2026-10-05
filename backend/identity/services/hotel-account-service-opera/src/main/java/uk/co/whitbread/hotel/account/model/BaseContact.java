package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.account.validation.Phone;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseContact implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String firstName;

    private String lastName;

    @Email
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Phone
    private String telephone;

    @Phone
    private String mobile;

    @Size(max = 3)
    private String nationality;
    private Passport passport;
    private String carRegistration;

}

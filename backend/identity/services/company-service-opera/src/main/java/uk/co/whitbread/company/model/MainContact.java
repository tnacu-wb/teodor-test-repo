package uk.co.whitbread.company.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainContact {
    @NotEmpty
    private String id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String emailAddress;
    private String mobileNumber;
    private String phoneNumber;
    private Boolean textConfirmation;
    private String position;

}

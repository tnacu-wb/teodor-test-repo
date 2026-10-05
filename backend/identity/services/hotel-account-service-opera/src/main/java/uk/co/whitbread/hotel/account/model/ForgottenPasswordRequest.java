package uk.co.whitbread.hotel.account.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import uk.co.whitbread.hotel.account.validation.UrlWhitelist;

@Data
public class ForgottenPasswordRequest {

    @NotBlank
    private String username;

    @URL
    @UrlWhitelist
    private String url;
}
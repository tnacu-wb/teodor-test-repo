package uk.co.whitbread.hotel.account.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.account.config.WhitelistProperties;



import java.net.URI;
import java.util.Collections;
import java.util.Optional;


public class UrlWhitelistValidator implements ConstraintValidator<UrlWhitelist, String> {


    @Autowired
    WhitelistProperties whitelistProperties;

    @Override
    public void initialize(UrlWhitelist urlWhitelist) {
        //No initialisation required

    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext constraintValidatorContext) {


        // Only validate if present
        if (!Optional.ofNullable(url).isPresent()) {
            return true;
        }

        try {

            String domain = (new URI(url)).getHost();
            return Optional.ofNullable(whitelistProperties.getRegex())
                    .orElse(Collections.emptyList())
                    .stream()
                    .anyMatch(domain::matches);

        } catch (Exception e) {
            return false;
        }

    }
}

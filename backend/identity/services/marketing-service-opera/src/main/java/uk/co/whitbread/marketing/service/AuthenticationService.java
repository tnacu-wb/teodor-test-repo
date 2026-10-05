package uk.co.whitbread.marketing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.bart.enums.BartBookingChannelCode;
import uk.co.whitbread.bart.enums.BartHotelBrandCode;
import uk.co.whitbread.marketing.client.hotelaccount.CustomerAccount;
import uk.co.whitbread.marketing.client.hotelaccount.HotelAccountClient;
import uk.co.whitbread.marketing.exception.ValidationException;
import uk.co.whitbread.marketing.model.newsletter.ContactType;
import uk.co.whitbread.shared.auth.service.TokenService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    public static final String INVALID_TOKEN = "Invalid token.";
    private final TokenService authTokenService;
    private final HotelAccountClient hotelAccountClient;

    public void validate(String jwt, ContactType contactType, String contactValue, BartHotelBrandCode hotelBrand,
                         BartBookingChannelCode bookingChannel, boolean business, String origin) {
        final String emailExtracted = authTokenService.retrieveEmailAndVerifyToken(jwt)
                .orElseThrow(() -> {
                    log.error("Couldn't find email in JWT: {}", jwt);
                    return new ValidationException(INVALID_TOKEN);
                });

        if (ContactType.email.equals(contactType)) {
            validateEmail(emailExtracted, contactValue);
        } else {
            validatePhone(contactValue, emailExtracted, jwt, hotelBrand, bookingChannel, business, origin);
        }
    }

    public String extractAndValidateEmailFromJwt(String jwt) {
        return authTokenService.retrieveEmailAndVerifyToken(jwt)
            .orElseThrow(() -> {
                log.error("Token Verification Failed. Couldn't find email in JWT");
                return new ValidationException(INVALID_TOKEN);
            });
    }

    protected void validateEmail(String emailFromJwt, String emailFromRequest) {
        if (!emailFromJwt.toLowerCase().equals(emailFromRequest.toLowerCase())) {
            log.error("Email from JWT and email from request don't match");
            throw new ValidationException(INVALID_TOKEN);
        }
    }

    protected void validatePhone(String phone, String emailExtracted, String jwt, BartHotelBrandCode hotelBrand,
                                 BartBookingChannelCode bookingChannel, boolean business, String origin) {
        final String sessionId = authTokenService.retrieveAndVerifyToken(jwt)
                .orElseThrow(() -> {
                    log.error("Couldn't find email in JWT: {}", jwt);
                    return new ValidationException(INVALID_TOKEN);
                });
        final CustomerAccount customer = hotelAccountClient.getCustomer(emailExtracted, hotelBrand, sessionId,
                jwt, bookingChannel, business, origin);

        if (!phone.equals(customer.getContactDetail().getMobile()) &&
                !phone.equals(customer.getContactDetail().getTelephone())) {
            log.error("Phone provided doesn't match to any of the phones registered.");
            throw new ValidationException(INVALID_TOKEN);
        }

    }
}

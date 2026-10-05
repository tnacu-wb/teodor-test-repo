package uk.co.whitbread.marketing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.bart.enums.BartBookingChannelCode;
import uk.co.whitbread.bart.enums.BartHotelBrandCode;
import uk.co.whitbread.marketing.client.hotelaccount.HotelAccountClient;
import uk.co.whitbread.marketing.client.customerhub.model.ContactDetail;
import uk.co.whitbread.marketing.client.hotelaccount.CustomerAccount;
import uk.co.whitbread.marketing.exception.ValidationException;
import uk.co.whitbread.marketing.model.newsletter.ContactType;

import java.util.Optional;
import uk.co.whitbread.shared.auth.service.TokenService;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private static final String VALID_TOKEN = "Bearer token123";
    private static final String INVALID_TOKEN = "token123";
    private static final String EMAIL = "test@mail.com";
    private static final String PHONE = "+447777777777";
    private static final String SESSION_ID = "sessionId";
    private static final String ORIGIN = "origin";

    @Mock
    private TokenService authTokenService;

    @Mock
    private HotelAccountClient hotelAccountClient;

    @InjectMocks
    private AuthenticationService target;

    @Test
    void validateEmail() {
        Mockito.when(authTokenService.retrieveEmailAndVerifyToken(VALID_TOKEN))
                .thenReturn(Optional.of(EMAIL));

        target.validate(VALID_TOKEN, ContactType.email, EMAIL, BartHotelBrandCode.PI, BartBookingChannelCode.WEB, false, ORIGIN);

        Mockito.verify(authTokenService).retrieveEmailAndVerifyToken(VALID_TOKEN);
        Mockito.verify(authTokenService, Mockito.never()).retrieveAndVerifyToken(VALID_TOKEN);

    }
    @Test
    void validateEmailLowercase() {
        Mockito.when(authTokenService.retrieveEmailAndVerifyToken(VALID_TOKEN))
                .thenReturn(Optional.of(EMAIL.toUpperCase()));

        target.validate(VALID_TOKEN, ContactType.email, EMAIL, BartHotelBrandCode.PI, BartBookingChannelCode.WEB, false, ORIGIN);

        Mockito.verify(authTokenService).retrieveEmailAndVerifyToken(VALID_TOKEN);
        Mockito.verify(authTokenService, Mockito.never()).retrieveAndVerifyToken(VALID_TOKEN);

    }

    @Test
    void validatePhone() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setContactDetail(new ContactDetail());
        customerAccount.getContactDetail().setMobile("+447777777777");
        customerAccount.getContactDetail().setTelephone("+447777777766");
        customerAccount.getContactDetail().setEmail(EMAIL);

        Mockito.when(authTokenService.retrieveEmailAndVerifyToken(VALID_TOKEN))
                .thenReturn(Optional.of(EMAIL));
        Mockito.when(authTokenService.retrieveAndVerifyToken(VALID_TOKEN))
                .thenReturn(Optional.of(SESSION_ID));

        Mockito.when(hotelAccountClient.getCustomer(EMAIL, BartHotelBrandCode.PI, SESSION_ID, VALID_TOKEN, BartBookingChannelCode.WEB,
                false, ORIGIN))
                .thenReturn(customerAccount);

        target.validate(VALID_TOKEN, ContactType.phone, PHONE, BartHotelBrandCode.PI, BartBookingChannelCode.WEB, false, ORIGIN);

        Mockito.verify(authTokenService).retrieveEmailAndVerifyToken(VALID_TOKEN);
        Mockito.verify(authTokenService).retrieveAndVerifyToken(VALID_TOKEN);
        Mockito.verify(hotelAccountClient).getCustomer(EMAIL, BartHotelBrandCode.PI, SESSION_ID, VALID_TOKEN, BartBookingChannelCode.WEB,
                false, ORIGIN);
    }

    @Test
    void validatePhoneWithOnlyLandLine() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setContactDetail(new ContactDetail());
        customerAccount.getContactDetail().setTelephone("+447777777777");
        customerAccount.getContactDetail().setEmail(EMAIL);

        Mockito.when(authTokenService.retrieveEmailAndVerifyToken(VALID_TOKEN))
                .thenReturn(Optional.of(EMAIL));
        Mockito.when(authTokenService.retrieveAndVerifyToken(VALID_TOKEN))
                .thenReturn(Optional.of(SESSION_ID));

        Mockito.when(hotelAccountClient.getCustomer(EMAIL, BartHotelBrandCode.PI, SESSION_ID, VALID_TOKEN, BartBookingChannelCode.WEB,
                false, ORIGIN))
                .thenReturn(customerAccount);

        target.validate(VALID_TOKEN, ContactType.phone, PHONE, BartHotelBrandCode.PI, BartBookingChannelCode.WEB, false, ORIGIN);

        Mockito.verify(authTokenService).retrieveEmailAndVerifyToken(VALID_TOKEN);
        Mockito.verify(authTokenService).retrieveAndVerifyToken(VALID_TOKEN);
        Mockito.verify(hotelAccountClient).getCustomer(EMAIL, BartHotelBrandCode.PI, SESSION_ID, VALID_TOKEN, BartBookingChannelCode.WEB,
                false, ORIGIN);
    }

    @Test
    void validatePhoneWithOnlyMobile() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setContactDetail(new ContactDetail());
        customerAccount.getContactDetail().setMobile("+447777777777");
        customerAccount.getContactDetail().setEmail(EMAIL);

        Mockito.when(authTokenService.retrieveEmailAndVerifyToken(VALID_TOKEN))
                .thenReturn(Optional.of(EMAIL));
        Mockito.when(authTokenService.retrieveAndVerifyToken(VALID_TOKEN))
                .thenReturn(Optional.of(SESSION_ID));

        Mockito.when(hotelAccountClient.getCustomer(EMAIL, BartHotelBrandCode.PI, SESSION_ID, VALID_TOKEN, BartBookingChannelCode.WEB,
                false, ORIGIN))
                .thenReturn(customerAccount);

        target.validate(VALID_TOKEN, ContactType.phone, PHONE, BartHotelBrandCode.PI, BartBookingChannelCode.WEB, false, ORIGIN);

        Mockito.verify(authTokenService).retrieveEmailAndVerifyToken(VALID_TOKEN);
        Mockito.verify(authTokenService).retrieveAndVerifyToken(VALID_TOKEN);
        Mockito.verify(hotelAccountClient).getCustomer(EMAIL, BartHotelBrandCode.PI, SESSION_ID, VALID_TOKEN, BartBookingChannelCode.WEB,
                false, ORIGIN);
    }

    @Test
    void validateShouldReturnValidationErrorWhenPhoneDoesntMatch() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setContactDetail(new ContactDetail());
        customerAccount.getContactDetail().setMobile("+443377777777");
        customerAccount.getContactDetail().setEmail(EMAIL);

        Mockito.when(authTokenService.retrieveEmailAndVerifyToken(VALID_TOKEN))
                .thenReturn(Optional.of(EMAIL));
        Mockito.when(authTokenService.retrieveAndVerifyToken(VALID_TOKEN))
                .thenReturn(Optional.of(SESSION_ID));

        Mockito.when(hotelAccountClient.getCustomer(EMAIL, BartHotelBrandCode.PI, SESSION_ID, VALID_TOKEN, BartBookingChannelCode.WEB,
                false, ORIGIN))
                .thenReturn(customerAccount);

        assertThrows(ValidationException.class,
            () -> target.validate(VALID_TOKEN, ContactType.phone, PHONE, BartHotelBrandCode.PI, BartBookingChannelCode.WEB, false, ORIGIN));

    }

    @Test
    void validateEmailShouldReturnValidationErrorWhenEmailDoesntMatch() {
        Mockito.when(authTokenService.retrieveEmailAndVerifyToken(VALID_TOKEN))
                .thenReturn(Optional.of("other@email.com"));

        assertThrows(ValidationException.class,
            () -> target.validate(VALID_TOKEN, ContactType.email, EMAIL, BartHotelBrandCode.PI, BartBookingChannelCode.WEB, false, ORIGIN));
    }

    @Test
    void extractAndValidateEmailFromJwt_shouldReturnEmail_whenJwtIsValid() {
        Mockito.when(authTokenService.retrieveEmailAndVerifyToken(VALID_TOKEN))
            .thenReturn(Optional.of(EMAIL));

        String result = target.extractAndValidateEmailFromJwt(VALID_TOKEN);

        assertEquals(EMAIL, result);
        Mockito.verify(authTokenService).retrieveEmailAndVerifyToken(VALID_TOKEN);
    }

    @Test
    void extractAndValidateEmailFromJwt_shouldThrowException_whenJwtIsInvalid() {
        Mockito.when(authTokenService.retrieveEmailAndVerifyToken(INVALID_TOKEN))
            .thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> target.extractAndValidateEmailFromJwt(INVALID_TOKEN));
        Mockito.verify(authTokenService).retrieveEmailAndVerifyToken(INVALID_TOKEN);
    }

}
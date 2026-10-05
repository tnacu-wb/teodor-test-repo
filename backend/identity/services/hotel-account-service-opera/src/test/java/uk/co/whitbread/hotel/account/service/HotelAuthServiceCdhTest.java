package uk.co.whitbread.hotel.account.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.account.model.LanguageCode.EN;
import static uk.co.whitbread.hotel.account.service.auth0.Auth0Service.RESET_PASSWORD_TOKEN_EXPIRY_FIELD;
import static uk.co.whitbread.hotel.account.service.auth0.Auth0Service.RESET_PASSWORD_TOKEN_FIELD;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.hotel.account.exceptions.AccountNotFoundException;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordRequest;
import uk.co.whitbread.hotel.account.model.ValidateResetKeyRequest;
import uk.co.whitbread.hotel.account.model.ValidateResetKeyResponse;
import uk.co.whitbread.hotel.account.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.account.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.hotel.account.properties.EmailProperties;
import uk.co.whitbread.hotel.account.service.auth0.Auth0BusinessService;
import uk.co.whitbread.hotel.account.service.auth0.Auth0LeisureService;
import uk.co.whitbread.hotel.account.service.auth0.Auth0Service;
import uk.co.whitbread.hotel.account.utils.ResetPasswordDataGenerator;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.azureemail.model.PasswordReset;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.CustomerDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.ContactDetail;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountsResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HotelAuthServiceCdhTest {

    private static final String RESET_PASSWORD_URL_PI = "http://resetUrlPi";
    private static final String RESET_PASSWORD_URL_INNB = "http://resetUrlInnb";
    private static final String RESET_PASSWORD_URL_INNB_DE = "http://resetUrlInnbDe";
    private static final String EMAIL_ADDRESS = "a@b.c";
    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";
    private static final Auth0User AUTH_USER = Auth0User.builder()
        .email(EMAIL_ADDRESS)
        .id("user_id")
        .build();
    private static final GetCustomerAccountResponse CDH_CUSTOMER = GetCustomerAccountResponse.builder()
        .contactDetail(
            ContactDetail.builder().email(EMAIL_ADDRESS).firstName(FIRST_NAME).lastName(LAST_NAME)
                .build()).build();

    private static final GetEmployeesResponse CDH_EMPLOYEES = GetEmployeesResponse.builder()
        .results(List.of(GetEmployeeResponse
            .builder()
            .emailAddress(EMAIL_ADDRESS)
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .build())).
        build();

    @Mock
    private Auth0LeisureService mockAuth0LeisureService;
    @Mock
    private Auth0BusinessService mockAuth0BusinessService;
    @Mock
    private CustomerDataService customerDataService;
    @Mock
    private EmployeeDataService employeeDataService;
    @Mock
    private AzureEmailService azureEmailService;
    @Mock
    private UnleashWrapper<FeatureFlag> unleashWrapper;
    @Mock
    private FeatureFlag featureFlag;

    private HotelAuthServiceCdh sut;

    @BeforeEach
    void setUp() {
        final Auth0Properties auth0Properties = new Auth0Properties();
        final EmailProperties emailProperties = new EmailProperties();
        emailProperties.setPiResetPasswordUrl(RESET_PASSWORD_URL_PI);
        emailProperties.setInnBusinessResetPasswordUrl(RESET_PASSWORD_URL_INNB);
        emailProperties.setInnBusinessResetPasswordUrlDe(RESET_PASSWORD_URL_INNB_DE);
        final ResetPasswordDataGenerator resetPasswordDataGenerator = new ResetPasswordDataGenerator(
            emailProperties);

        sut = new HotelAuthServiceCdh(mockAuth0LeisureService, mockAuth0BusinessService,
            customerDataService, employeeDataService, azureEmailService, resetPasswordDataGenerator,
            auth0Properties, unleashWrapper);

        when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
        when(unleashWrapper.isEnabled(any())).thenReturn(false);
    }

    @Test
    void forgetPassword_shouldSendPiEmail() {
        final ForgottenPasswordRequest request = new ForgottenPasswordRequest();
        request.setUsername(EMAIL_ADDRESS);

        doReturn(Optional.of(AUTH_USER)).when(mockAuth0LeisureService).getAuth0User(EMAIL_ADDRESS);
        doReturn(Optional.of(
            GetCustomerAccountsResponse.builder().results(List.of(CDH_CUSTOMER)).build())).when(
                customerDataService)
            .getCustomerAccountList(eq(EMAIL_ADDRESS));

        final String language = EN.asLowerCase();
        sut.forgottenPasswordToken(request, language);

        final ArgumentCaptor<PasswordReset> passwordResetCaptor = ArgumentCaptor.forClass(
            PasswordReset.class);

        verify(azureEmailService, timeout(2000)).sendResetPasswordEmail(passwordResetCaptor.capture());

        final PasswordReset actual = passwordResetCaptor.getValue();

        assertThat(actual.getEmail(), is(EMAIL_ADDRESS));
        assertThat(actual.getFirstName(), is(FIRST_NAME));
        assertThat(actual.getLastName(), is(LAST_NAME));
        assertThat(actual.getResetPasswordUrl(), startsWith(RESET_PASSWORD_URL_PI));
        assertThat(actual.getLanguage(), is(language));
    }

    @Test
    void forgetPassword_withCdhFeatureFlagEnabled_shouldSendPiEmail() {
        final ForgottenPasswordRequest request = new ForgottenPasswordRequest();
        request.setUsername(EMAIL_ADDRESS);

        when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
        when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);

        doReturn(Optional.of(AUTH_USER)).when(mockAuth0LeisureService).getAuth0User(EMAIL_ADDRESS);
        doReturn(Optional.of(
            GetCustomerAccountsResponse.builder().results(List.of(CDH_CUSTOMER)).build())).when(
                customerDataService)
            .getCustomerAccountListV3(EMAIL_ADDRESS);

        final String language = EN.asLowerCase();
        sut.forgottenPasswordToken(request, language);

        final ArgumentCaptor<PasswordReset> passwordResetCaptor = ArgumentCaptor.forClass(
            PasswordReset.class);

        verify(azureEmailService, timeout(2000)).sendResetPasswordEmail(passwordResetCaptor.capture());
        verify(customerDataService).getCustomerAccountListV3(EMAIL_ADDRESS);

        final PasswordReset actual = passwordResetCaptor.getValue();

        assertThat(actual.getEmail(), is(EMAIL_ADDRESS));
        assertThat(actual.getFirstName(), is(FIRST_NAME));
        assertThat(actual.getLastName(), is(LAST_NAME));
        assertThat(actual.getResetPasswordUrl(), startsWith(RESET_PASSWORD_URL_PI));
        assertThat(actual.getLanguage(), is(language));
    }

    @Test
    void resetPassword_success() {
        testResetPassword_success(false);
    }

    @Test
    void resetPasswordBusiness_success() {
        testResetPassword_success(true);
    }

    @Test
    void resetPassword_accountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> testResetPassword_accountNotFound(false));
    }

    @Test
    void resetPasswordBusiness_accountNotFound() {
        assertThrows(AccountNotFoundException.class, () -> testResetPassword_accountNotFound(true));
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    void forgetPassword_shouldSendInnBusinessEmail(String language, String expectedUrlPrefix) {
        final ForgottenPasswordRequest request = new ForgottenPasswordRequest();
        request.setUsername(EMAIL_ADDRESS);

        when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
        when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);
        doReturn(Optional.of(AUTH_USER)).when(mockAuth0BusinessService).getAuth0User(EMAIL_ADDRESS);
        doReturn(Optional.of(CDH_EMPLOYEES)).when(employeeDataService)
            .getEmployees(any(GetEmployeesQueryParams.class), eq(EMAIL_ADDRESS));

        sut.forgottenPasswordTokenBusiness(request, language);

        final ArgumentCaptor<PasswordReset> passwordResetCaptor = ArgumentCaptor.forClass(
            PasswordReset.class);

        verify(azureEmailService, timeout(2000)).sendBBResetPasswordEmail(passwordResetCaptor.capture());

        final PasswordReset actual = passwordResetCaptor.getValue();

        assertThat(actual.getEmail(), is(EMAIL_ADDRESS));
        assertThat(actual.getFirstName(), is(FIRST_NAME));
        assertThat(actual.getLastName(), is(LAST_NAME));
        assertThat(actual.getResetPasswordUrl(), startsWith(expectedUrlPrefix));
        assertThat(actual.getLanguage(), is(language));
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    void forgetPassword_withCdhFeatureFlagEnabled_shouldSendInnBusinessEmail(String language, String expectedUrlPrefix) {

        final ForgottenPasswordRequest request = new ForgottenPasswordRequest();
        request.setUsername(EMAIL_ADDRESS);

        when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
        when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);

        doReturn(Optional.of(AUTH_USER)).when(mockAuth0BusinessService).getAuth0User(EMAIL_ADDRESS);
        doReturn(Optional.of(CDH_EMPLOYEES)).when(employeeDataService)
            .getEmployeesV2(any(GetEmployeesQueryParams.class), eq(EMAIL_ADDRESS));

        sut.forgottenPasswordTokenBusiness(request, language);

        final ArgumentCaptor<PasswordReset> passwordResetCaptor = ArgumentCaptor.forClass(
            PasswordReset.class);

        verify(azureEmailService, timeout(2000)).sendBBResetPasswordEmail(passwordResetCaptor.capture());

        final PasswordReset actual = passwordResetCaptor.getValue();

        assertThat(actual.getEmail(), is(EMAIL_ADDRESS));
        assertThat(actual.getFirstName(), is(FIRST_NAME));
        assertThat(actual.getLastName(), is(LAST_NAME));
        assertThat(actual.getResetPasswordUrl(), startsWith(expectedUrlPrefix));
        assertThat(actual.getLanguage(), is(language));
    }

    @Test
    void validateResetKey_shouldReturnSuccessForValidKey() {
        // Given
        String validResetKey = "validResetKey";
        Auth0User mockUser = Auth0User.builder()
            .email("test@example.com")
            .appMetadata(Map.of("reset_password_token", validResetKey,
                "reset_password_token_expiry", Instant.now().getEpochSecond() + 3600))
            .build();
        doReturn(List.of(mockUser)).when(mockAuth0BusinessService).findUserByResetToken(validResetKey);

        ValidateResetKeyRequest request = new ValidateResetKeyRequest(validResetKey);

        // When
        ValidateResetKeyResponse response = sut.validateResetKey(request);

        // Then
        assertThat(response.valid(), is(true));
        assertThat(response.emailAddress(), is("test@example.com"));
        verify(mockAuth0BusinessService).findUserByResetToken(validResetKey);
    }

    @Test
    void validateResetKey_shouldReturnFailureForInvalidKey_tokenExpired() {
        // Given
        String invalidResetKey = "invalidResetKey";
        Auth0User mockUser = Auth0User.builder()
            .email("test@example.com")
            .appMetadata(Map.of("reset_password_token", invalidResetKey,
                "reset_password_token_expiry", Instant.now().getEpochSecond() - 3600))
            .build();
        doReturn(List.of(mockUser)).when(mockAuth0BusinessService).findUserByResetToken(invalidResetKey);

        ValidateResetKeyRequest request = new ValidateResetKeyRequest(invalidResetKey);

        // When
        ValidateResetKeyResponse response = sut.validateResetKey(request);

        // Then
        assertThat(response.valid(), is(false));
        assertThat(response.emailAddress(), is((String) null));
        verify(mockAuth0BusinessService).findUserByResetToken(invalidResetKey);
    }

    @Test
    void validateResetKey_shouldReturnFailureWhenNoUserFound() {
        // Given
        String resetKey = "nonExistentKey";
        doReturn(List.of()).when(mockAuth0BusinessService).findUserByResetToken(resetKey);

        ValidateResetKeyRequest request = new ValidateResetKeyRequest(resetKey);

        // When
        ValidateResetKeyResponse response = sut.validateResetKey(request);

        // Then
        assertThat(response.valid(), is(false));
        assertThat(response.emailAddress(), is((String) null));
        verify(mockAuth0BusinessService).findUserByResetToken(resetKey);
    }

    private static Stream<Arguments> provideTestArguments() {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of("en", RESET_PASSWORD_URL_INNB),
            org.junit.jupiter.params.provider.Arguments.of("de", RESET_PASSWORD_URL_INNB_DE)
        );
    }

    private void testResetPassword_success(boolean business) {
        final Auth0Service auth0Service = business ? mockAuth0BusinessService : mockAuth0LeisureService;
        final String token = "abcdxyz";
        final long now = Instant.now().getEpochSecond();
        final long tokenExpiryTime = now + 3600;

        final Map<String, Object> userAppMetadata = Map.of(RESET_PASSWORD_TOKEN_FIELD, token,
            RESET_PASSWORD_TOKEN_EXPIRY_FIELD, tokenExpiryTime);
        AUTH_USER.setAppMetadata(userAppMetadata);
        doReturn(Optional.of(AUTH_USER)).when(auth0Service).getAuth0User(EMAIL_ADDRESS);
        final ArgumentCaptor<Auth0User> userArgumentCaptor = ArgumentCaptor.forClass(Auth0User.class);

        if (business) {
            sut.resetPasswordBusiness(EMAIL_ADDRESS, "new_pass", token);
        } else {
            sut.resetPassword(EMAIL_ADDRESS, "new_pass", token);
        }

        verify(auth0Service).updateUserInAuth0(eq(AUTH_USER.getId()), userArgumentCaptor.capture());

        final Auth0User updatedUser = userArgumentCaptor.getValue();
        final Map<String, Object> updatedAppMetadata = updatedUser.getAppMetadata();

        assertThat(updatedAppMetadata.containsKey(RESET_PASSWORD_TOKEN_FIELD), is(true));
        assertThat(updatedAppMetadata.containsKey(RESET_PASSWORD_TOKEN_EXPIRY_FIELD), is(true));
        assertThat(updatedAppMetadata.get(RESET_PASSWORD_TOKEN_FIELD), nullValue());
        assertThat(updatedAppMetadata.get(RESET_PASSWORD_TOKEN_EXPIRY_FIELD), nullValue());
    }

    private void testResetPassword_accountNotFound(boolean business) {
        final Auth0Service auth0Service = business ? mockAuth0BusinessService : mockAuth0LeisureService;
        doReturn(Optional.empty()).when(auth0Service).getAuth0User(EMAIL_ADDRESS);
        if (business) {
            sut.resetPasswordBusiness(EMAIL_ADDRESS, "new_pass", "token");
        } else {
            sut.resetPassword(EMAIL_ADDRESS, "new_pass", "token");
        }
    }
}

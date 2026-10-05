package uk.co.whitbread.hotel.account.service;

import static uk.co.whitbread.hotel.account.service.auth0.Auth0Service.RESET_PASSWORD_TOKEN_EXPIRY_FIELD;
import static uk.co.whitbread.hotel.account.service.auth0.Auth0Service.RESET_PASSWORD_TOKEN_FIELD;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.account.exceptions.AccountNotFoundException;
import uk.co.whitbread.hotel.account.exceptions.InvalidTokenException;
import uk.co.whitbread.hotel.account.model.ChangePasswordResponse;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordRequest;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordResponse;
import uk.co.whitbread.hotel.account.model.ValidateResetKeyRequest;
import uk.co.whitbread.hotel.account.model.ValidateResetKeyResponse;
import uk.co.whitbread.hotel.account.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.account.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.hotel.account.service.auth0.Auth0Service;
import uk.co.whitbread.hotel.account.utils.ResetPasswordDataGenerator;
import uk.co.whitbread.hotel.account.utils.ResetPasswordDataGenerator.ResetPasswordData;
import uk.co.whitbread.hotel.account.utils.Utils;
import uk.co.whitbread.hotel.account.validation.ResetPasswordTokenValidator;
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

@Slf4j
@AllArgsConstructor
@Service
public class HotelAuthServiceCdh {

    @Qualifier("auth0LeisureService")
    private final Auth0Service auth0LeisureService;
    @Qualifier("auth0BusinessService")
    private final Auth0Service auth0BusinessService;
    private final CustomerDataService customerDataService;
    private final EmployeeDataService employeeDataService;
    private final AzureEmailService azureEmailService;
    private final ResetPasswordDataGenerator resetPasswordDataGenerator;
    private final Auth0Properties auth0Properties;
    private final UnleashWrapper<FeatureFlag> unleashWrapper;

    private final String FORGOTTEN_PW_MESSAGE_AUTH0 = "Received a forgotten password request who wasn't found in Auth0";
    private final String FORGOTTEN_PW_MESSAGE_CDH = "Received a forgotten password request who wasn't found in CDH";

    public ForgottenPasswordResponse forgottenPasswordToken(ForgottenPasswordRequest request,
        String language) {

        log.debug("Called HotelAuthServiceCdh.forgottenPasswordToken");

        final String emailAddress = request.getUsername();
        final Optional<Auth0User> userOptional = auth0LeisureService.getAuth0User(emailAddress);
        userOptional.ifPresentOrElse(user -> {
            final Optional<GetCustomerAccountsResponse> customerAccountsResponse =
                unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                    ? customerDataService.getCustomerAccountListV3(emailAddress)
                    : customerDataService.getCustomerAccountList(emailAddress);
            customerAccountsResponse.ifPresentOrElse(response -> {
                final List<GetCustomerAccountResponse> results = response.getResults();
                if (Objects.isNull(results) || results.isEmpty()) {
                    log.warn(FORGOTTEN_PW_MESSAGE_CDH);
                } else {
                    final ResetPasswordData resetPasswordData = resetPasswordDataGenerator
                        .generateResetPasswordData(request.getUrl(), language, false);
                    final String resetToken = resetPasswordData.getResetToken();
                    saveResetTokenInAuth0(user, resetToken, false);
                    sendAsyncPiResetPasswordEmail(results.get(0).getContactDetail(),
                        request.getUsername(), resetPasswordData.getResetUrl(), language);
                }
            }, () -> log.warn(FORGOTTEN_PW_MESSAGE_CDH));
        }, () -> log.warn(FORGOTTEN_PW_MESSAGE_AUTH0));

        // DNRQ-3790 - always return true to avoid leaking information on registered accounts
        return new ForgottenPasswordResponse(true);
    }

    public ForgottenPasswordResponse forgottenPasswordTokenBusiness(
        ForgottenPasswordRequest request, String language) {

        log.debug("Called HotelAuthServiceCdh.forgottenPasswordTokenBusiness");

        final String emailAddress = request.getUsername();
        final Optional<Auth0User> userOptional = auth0BusinessService.getAuth0User(emailAddress);
        userOptional.ifPresentOrElse(user -> {
            final GetEmployeesQueryParams queryParameters = GetEmployeesQueryParams.builder()
                .emailAddress(emailAddress).build();
            final Optional<GetEmployeesResponse> optionalGetEmployeesResponse =
                unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                    ? employeeDataService.getEmployeesV2(queryParameters, emailAddress)
                    : employeeDataService.getEmployees(queryParameters, emailAddress);
            optionalGetEmployeesResponse.ifPresentOrElse(employeesResponse -> {
                    final List<GetEmployeeResponse> employeesResponseList = employeesResponse.getResults();
                    if (Objects.isNull(employeesResponseList) || employeesResponseList.isEmpty()) {
                        log.warn(FORGOTTEN_PW_MESSAGE_CDH);
                    } else {
                        processForgotPassword(request, language, user, employeesResponseList.get(0));
                    }
                }, () -> log.warn(FORGOTTEN_PW_MESSAGE_CDH));
        }, () -> log.warn(FORGOTTEN_PW_MESSAGE_AUTH0));

        // DNRQ-3790 - always return true to avoid leaking information on registered accounts
        return new ForgottenPasswordResponse(true);
    }

    public ChangePasswordResponse resetPassword(String email, String newPassword,
        String passwordToken) {

        return resetPassword(email, newPassword, passwordToken, auth0LeisureService);
    }

    public ChangePasswordResponse resetPasswordBusiness(String email, String newPassword,
        String passwordToken) {

        return resetPassword(email, newPassword, passwordToken, auth0BusinessService);
    }

    public ValidateResetKeyResponse validateResetKey(ValidateResetKeyRequest request) {
        List<Auth0User> users = auth0BusinessService.findUserByResetToken(request.resetKey());
        if (users.isEmpty()) {
            log.warn("No user found for provided reset key {} in the validation flow.",
                Utils.sanitizeInputString(request.resetKey()));
            return new ValidateResetKeyResponse(false, null);
        }
        Auth0User user = users.get(0);
        try {
            validateResetKey(request.resetKey(), user);
        } catch (InvalidTokenException e) {
            log.warn("Invalid password reset key {} for user {}", Utils.sanitizeInputString(request.resetKey()),
                user.getEmail(), e);
            return new ValidateResetKeyResponse(false, null);
        }
        return new ValidateResetKeyResponse(true, user.getEmail());
    }

    private void processForgotPassword(ForgottenPasswordRequest request, String language, Auth0User user,
                                       GetEmployeeResponse employee) {
        final ResetPasswordData resetPasswordData = resetPasswordDataGenerator
            .generateResetPasswordData(request.getUrl(), language, true);
        final String resetToken = resetPasswordData.getResetToken();
        saveResetTokenInAuth0(user, resetToken, true);
        sendAsyncBbResetPasswordEmail(employee, request.getUsername(),
            resetPasswordData.getResetUrl(), language);
    }

    private void sendAsyncPiResetPasswordEmail(ContactDetail contactDetail, String emailAddress,
        String resetUrl, String language) {

        CompletableFuture.runAsync(() -> {
            try {
                final PasswordReset passwordReset = PasswordReset.builder()
                    .email(emailAddress)
                    .firstName(contactDetail.getFirstName())
                    .lastName(contactDetail.getLastName())
                    .resetPasswordUrl(resetUrl)
                    .language(language)
                    .build();
                azureEmailService.sendResetPasswordEmail(passwordReset);
                log.info("MyPI forgotten password email sent success");
            } catch (Exception e) {
                log.error("An error occurred while trying to send a MyPI reset password email", e);
            }
        });
    }

    private void sendAsyncBbResetPasswordEmail(GetEmployeeResponse employee,
        String emailAddress, String resetUrl, String language) {

        CompletableFuture.runAsync(() -> {
            try {
                final PasswordReset passwordReset = PasswordReset.builder()
                    .email(emailAddress)
                    .firstName(employee.getFirstName())
                    .lastName(employee.getLastName())
                    .resetPasswordUrl(resetUrl)
                    .language(language)
                    .build();
                azureEmailService.sendBBResetPasswordEmail(passwordReset);
                log.info("BB forgotten password email sent success");
            } catch (Exception e) {
                log.error("An error occurred while trying to send a BB reset password email", e);
            }
        });
    }

    private void saveResetTokenInAuth0(Auth0User user, String resetToken, boolean business) {
        final Auth0User updatedUser = Auth0User.builder()
            .appMetadata(Map.of(RESET_PASSWORD_TOKEN_FIELD, resetToken, RESET_PASSWORD_TOKEN_EXPIRY_FIELD,
                Instant.now().getEpochSecond() + auth0Properties.getResetPasswordTokenTtlSec()))
            .build();
        if (business) {
            auth0BusinessService.updateUserInAuth0(user.getId(), updatedUser);
        } else {
            auth0LeisureService.updateUserInAuth0(user.getId(), updatedUser);
        }
    }

    private ChangePasswordResponse resetPassword(String email, String newPassword,
        String passwordToken, Auth0Service auth0Service) {

        final Optional<Auth0User> auth0User = auth0Service.getAuth0User(email);

        final Auth0User user = auth0User.orElseThrow(() -> new AccountNotFoundException(
            "Customer account not found in Auth0"));

        validateResetKey(passwordToken, user);

        final Auth0User updatedUser = buildUpdatedUser(newPassword);
        auth0Service.updateUserInAuth0(user.getId(), updatedUser);
        return new ChangePasswordResponse(true);
    }

    private void validateResetKey(String resetKey, Auth0User user) {
        final long now = Instant.now().getEpochSecond();
        final Map<String, Object> userAppMetadata = user.getAppMetadata();
        final ResetPasswordTokenValidator tokenValidator = new ResetPasswordTokenValidator(userAppMetadata, now);
        tokenValidator.validateToken(resetKey);
    }

    private Auth0User buildUpdatedUser(String newPassword) {
        final Map<String, Object> updatedAppMetadata = new HashMap<>();
        updatedAppMetadata.put(RESET_PASSWORD_TOKEN_FIELD, null);
        updatedAppMetadata.put(RESET_PASSWORD_TOKEN_EXPIRY_FIELD, null);
        return Auth0User.builder()
            .password(newPassword)
            .appMetadata(updatedAppMetadata)
            .build();
    }

}

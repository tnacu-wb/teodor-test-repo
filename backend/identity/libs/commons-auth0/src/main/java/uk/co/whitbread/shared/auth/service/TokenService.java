package uk.co.whitbread.shared.auth.service;

import static java.util.Optional.of;

import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.shared.auth.constants.ProfileParam;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.jwt.ProviderTokenVerifier;
import uk.co.whitbread.shared.auth.jwt.TokenExtractor;
import uk.co.whitbread.shared.auth.model.CCUIDetails;
import uk.co.whitbread.shared.auth.model.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.model.EmployeeDetails;

@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final List<ProviderTokenVerifier> providerTokenVerifiers;
    private final TokenExtractor tokenExtractor;


    /**
     * Take the Authorization header and extract the JWT token. Verify against at least one TokenVerifier
     * @return email and bookingFlow
     */
    public CCUIDetails retrieveAndVerifyCCUIToken(String authorization) {
        Optional<Map<ProfileParam, String>> profileParamOptionalMap =
                retrieveAndVerifyToken(authorization, List.of(ProfileParam.CCUI_EMAIL, ProfileParam.BOOKING_FLOW));
        String email = profileParamOptionalMap.map(param -> param.get(ProfileParam.CCUI_EMAIL)).orElse(null);
        String bookingFlow = profileParamOptionalMap.map(param -> param.get(ProfileParam.BOOKING_FLOW)).orElse(null);
        return CCUIDetails.builder().email(email).bookingFlow(bookingFlow).build();
    }

    /**
     * Take the Authorization header and extract the JWT token. Verify against at least one TokenVerifier
     * @return sessionId
     */
    public Optional<String> retrieveAndVerifyToken(String authorization) {
        return retrieveAndVerifyToken(authorization, List.of(ProfileParam.SESSION_ID))
                .map(value -> value.get(ProfileParam.SESSION_ID));
    }

    /**
     * Take the Authorization header and extract the JWT token. Verify against at least one TokenVerifier
     * @return email
     */
    public Optional<String> retrieveEmailAndVerifyToken(String authorization) {
        return retrieveAndVerifyToken(authorization, List.of(ProfileParam.EMAIL))
                .map(value -> value.get(ProfileParam.EMAIL));
    }
    
    /**
     * Take the Authorization header and extract the JWT token. Verify against at least one TokenVerifier
     * @return companyId and employeeId
     */
    public EmployeeDetails retrieveEmployeeDetailsAndVerifyToken(String authorization) {
        Optional<Map<ProfileParam, String>> profileParamOptionalMap =
                retrieveAndVerifyToken(authorization, List.of(ProfileParam.COMPANY_ID, ProfileParam.EMPLOYEE_ID));
        String companyId = profileParamOptionalMap.map(cmpyId -> cmpyId.get(ProfileParam.COMPANY_ID)).orElse(null);
        String employeeId = profileParamOptionalMap.map(empId -> empId.get(ProfileParam.EMPLOYEE_ID)).orElse(null);
        return new EmployeeDetails(companyId, employeeId);
    }

    /**
     * Take the Authorization header and extract the JWT token. Verify against at least one TokenVerifier
     * @return customerAccountId
     */
    public Optional<String> retrieveCustomerAccountIdAndVerifyToken(String authorization) {
        return retrieveAndVerifyToken(authorization, List.of(ProfileParam.CUSTOMER_ACCOUNT_ID))
            .map(value -> value.get(ProfileParam.CUSTOMER_ACCOUNT_ID));
    }

    /**
     * Take the Authorization header and extract the JWT token. Verify against at least one TokenVerifier
     * @return iat
     */
    public Optional<String> retrieveIssueAtAndVerifyToken(String authorization) {
        return retrieveAndVerifyToken(authorization, List.of(ProfileParam.ISSUED_AT))
            .map(value -> value.get(ProfileParam.ISSUED_AT));
    }

    /**
     * Take the Authorization header and extract the JWT token. Verify against at least one
     * TokenVerifier
     *
     * @return companyAccountId and employeeAccountId
     */
    public CdhEmployeeDetails retrieveCdhEmployeeDetailsAndVerifyToken(String authorization) {
        Optional<Map<ProfileParam, String>> profileParamOptionalMap =
            retrieveAndVerifyToken(authorization,
                List.of(ProfileParam.COMPANY_ACCOUNT_ID, ProfileParam.EMPLOYEE_ACCOUNT_ID, ProfileParam.USER_EMAIL, ProfileParam.ACCESS_LEVEL));
        String companyAccountId = profileParamOptionalMap.map(
            id -> id.get(ProfileParam.COMPANY_ACCOUNT_ID)).orElse(null);
        String employeeAccountId = profileParamOptionalMap.map(
            id -> id.get(ProfileParam.EMPLOYEE_ACCOUNT_ID)).orElse(null);
        final String userEmail = profileParamOptionalMap.map(
            id -> id.get(ProfileParam.USER_EMAIL)).orElse(null);
        final String accessLevel = profileParamOptionalMap.map(
            id -> id.get(ProfileParam.ACCESS_LEVEL)).orElse(null);
        return CdhEmployeeDetails.builder().companyAccountId(companyAccountId)
            .employeeAccountId(employeeAccountId).userEmail(userEmail).accessLevel(accessLevel).build();
    }
    
    private Optional<Map<ProfileParam,String>> retrieveAndVerifyToken(String authorization, List<ProfileParam> profileParams) {
        Optional<String> tokenOpt = tokenExtractor.extractToken(authorization);

        if (tokenOpt.isPresent()) {
            Optional<DecodedJWT> optDecodedJWT = getDecodedJWT(tokenOpt.get());
            if (optDecodedJWT.isPresent()) {
                return of(profileParams.stream()
                        .map(profileParam -> this.retrieveToken(optDecodedJWT.get(), profileParam))
                        .filter(Optional::isPresent).map(Optional::get).collect(Collectors.
                                toMap(Map.Entry::getKey, Map.Entry::getValue)));
            } else {
                throw new TokenVerificationException("Provided token was invalid or expired");
            }

        } else {
            return Optional.empty();
        }
    }

    private Optional<DecodedJWT> getDecodedJWT(String token) {
        List<CompletableFuture<Optional<DecodedJWT>>> cf = new ArrayList<>();
        providerTokenVerifiers.forEach(providerTokenVerifier -> {
            CompletableFuture<Optional<DecodedJWT>> stringCompletableFuture = CompletableFuture.supplyAsync(
                () -> providerTokenVerifier.verifyAndDecodeToken(
                    token));
            cf.add(stringCompletableFuture);
        });

        return cf.stream()
            .map(
                (Function<CompletableFuture<Optional<DecodedJWT>>, Optional<DecodedJWT>>) optionalCompletableFuture -> {
                    try {
                        return optionalCompletableFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        log.info(String.format("Token not verified due to exception %s",
                            e.getMessage()));
                        return Optional.empty();
                    }
                })
            .filter(Optional::isPresent)
            .findFirst()
            .orElse(Optional.empty());
    }
    
    private Optional<Map.Entry<ProfileParam, String>> retrieveToken(DecodedJWT decodedJWT, ProfileParam profileParam) {
        return tokenExtractor.retrieve(decodedJWT, profileParam).map(value -> Map.entry(profileParam, value));
    }

}

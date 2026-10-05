package uk.co.whitbread.shared.auth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.shared.auth.account.CCUIDetails;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.jwt.ProviderTokenVerifier;
import uk.co.whitbread.shared.auth.jwt.TokenExtractor;
import uk.co.whitbread.shared.auth.jwt.VerifiedToken;

/**
 * Validates JWT tokens against configured tenant providers and extracts claims.
 */
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final List<ProviderTokenVerifier> providerTokenVerifiers;
    private final TokenExtractor tokenExtractor;

    public CCUIDetails retrieveAndVerifyCCUIToken(String authorization) {
        Optional<VerifiedToken> verified = extractAndVerify(authorization);
        if (verified.isEmpty()) {
            return CCUIDetails.builder().build();
        }
        var claims = verified.get().claims();
        return CCUIDetails.builder()
            .email(tokenExtractor.retrieveCCUIEmail(claims).orElse(null))
            .bookingFlow(tokenExtractor.retrieveBookingFlow(claims).orElse(null))
            .build();
    }

    public Optional<String> retrieveAndVerifyToken(String authorization) {
        return extractAndVerify(authorization)
            .flatMap(vt -> tokenExtractor.retrieveSessionId(vt.claims()));
    }

    public Optional<String> retrieveEmailAndVerifyToken(String authorization) {
        return extractAndVerify(authorization)
            .flatMap(vt -> tokenExtractor.retrieveEmail(vt.claims()));
    }

    public EmployeeDetails retrieveEmployeeDetailsAndVerifyToken(String authorization) {
        Optional<VerifiedToken> verified = extractAndVerify(authorization);
        if (verified.isEmpty()) {
            return new EmployeeDetails(null, null);
        }
        var claims = verified.get().claims();
        return new EmployeeDetails(
            tokenExtractor.retrieveCompanyId(claims).orElse(null),
            tokenExtractor.retrieveEmployeeId(claims).orElse(null)
        );
    }

    public Optional<String> retrieveCustomerAccountIdAndVerifyToken(String authorization) {
        return extractAndVerify(authorization)
            .flatMap(vt -> tokenExtractor.retrieveCustomerAccountId(vt.claims(), vt.namespace()));
    }

    public CdhEmployeeDetails retrieveCdhEmployeeDetailsAndVerifyToken(String authorization) {
        Optional<VerifiedToken> verified = extractAndVerify(authorization);
        if (verified.isEmpty()) {
            return CdhEmployeeDetails.builder().build();
        }
        var claims = verified.get().claims();
        var namespace = verified.get().namespace();
        return CdhEmployeeDetails.builder()
            .companyAccountId(tokenExtractor.retrieveCompanyAccountId(claims, namespace).orElse(null))
            .employeeAccountId(tokenExtractor.retrieveEmployeeAccountId(claims, namespace).orElse(null))
            .userEmail(tokenExtractor.retrieveUserEmail(claims, namespace).orElse(null))
            .accessLevel(tokenExtractor.retrieveAccessLevel(claims).orElse(null))
            .build();
    }

    private Optional<VerifiedToken> extractAndVerify(String authorization) {
        Optional<String> tokenOpt = tokenExtractor.extractToken(authorization);
        if (tokenOpt.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(
            getVerifiedToken(tokenOpt.get())
                .orElseThrow(() -> new TokenVerificationException("Provided token was invalid or expired"))
        );
    }

    private Optional<VerifiedToken> getVerifiedToken(String token) {
        List<CompletableFuture<Optional<VerifiedToken>>> futures = new ArrayList<>();
        providerTokenVerifiers.forEach(verifier ->
            futures.add(CompletableFuture.supplyAsync(() -> verifier.verifyAndDecodeToken(token)))
        );

        return futures.stream()
            .map(future -> {
                try {
                    return future.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("Token verification interrupted: {}", e.getMessage());
                    return Optional.<VerifiedToken>empty();
                } catch (ExecutionException e) {
                    log.info("Token not verified due to exception: {}", e.getMessage());
                    return Optional.<VerifiedToken>empty();
                }
            })
            .filter(Optional::isPresent)
            .findFirst()
            .orElse(Optional.empty());
    }
}

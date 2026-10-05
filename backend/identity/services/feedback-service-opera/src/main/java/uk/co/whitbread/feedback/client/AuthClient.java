package uk.co.whitbread.feedback.client;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.feedback.properties.AuthProperties;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@Component
public class AuthClient {

    private final AuthProperties authProperties;

    public String getAuthorisationCode() {

        AuthenticationContext authenticationContext;
        AuthenticationResult authenticationResult;
        ExecutorService executorService = null;

        try {
            executorService = Executors.newFixedThreadPool(1);

            authenticationContext = new AuthenticationContext(
                    authProperties.getAuthorityUrl(),
                    false,
                    executorService);

            Future<AuthenticationResult> async = authenticationContext.acquireToken(
                    authProperties.getCrmResourceUrl(),
                    new ClientCredential(authProperties.getClientId(), authProperties.getClientSecret()),
                    null);

            authenticationResult = async.get();

        } catch (MalformedURLException | InterruptedException | ExecutionException e) {

            throw new RuntimeException(e);

        } finally {

            if (executorService != null) {
                executorService.shutdown();
            }
        }

        return authenticationResult.getAccessToken();
    }

}

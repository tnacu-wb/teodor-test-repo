package uk.co.whitbread.shared.auth.mgmt;

import com.auth0.client.HttpOptions;
import com.auth0.client.ProxyOptions;
import com.auth0.utils.Asserts;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Class that provides a custom implementation of the Management API methods defined in https://auth0.com/docs/api/management/v2
 * Used to enhance the jobs errors details API, undefined currently in the com.auth0 package
 * To begin create an instance of CustomManagementAPI(String, String) using the tenant domain and API token.
 * This class is not entirely thread-safe: A new immutable OkHttpClient instance is being created with each instantiation, not sharing the thread pool with any prior existing client instance.
 */
public class CustomManagementAPI {
    private final HttpUrl baseUrl;
    private String apiToken;
    private final OkHttpClient client;

    /**
     * Create an instance with the given tenant's domain and API token.
     * In addition, accepts an HttpOptions that will be used to configure the networking client.
     *
     * @param domain   – the tenant's domain.
     * @param apiToken – the token to authenticate the calls with.
     * @param options  – configuration options for this client instance.
     */
    public CustomManagementAPI(String domain, String apiToken, HttpOptions options) {
        Asserts.assertNotNull(domain, "domain");
        Asserts.assertNotNull(apiToken, "api token");

        this.baseUrl = createBaseUrl(domain);
        if (baseUrl == null) {
            throw new IllegalArgumentException("The domain had an invalid format and couldn't be parsed as an URL.");
        }
        this.apiToken = apiToken;
        client = buildNetworkingClient(options);
    }

    /**
     * Create an instance with the given tenant's domain and API token.
     * See the Management API section in the readme or visit https://auth0.com/docs/api/management/v2/tokens
     * to learn how to obtain a token.
     *
     * @param domain   the tenant's domain.
     * @param apiToken the token to authenticate the calls with.
     */
    public CustomManagementAPI(String domain, String apiToken) {
        this(domain, apiToken, new HttpOptions());
    }

    private OkHttpClient buildNetworkingClient(HttpOptions options) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        final ProxyOptions proxyOptions = options.getProxyOptions();

        if (proxyOptions != null) {
            //Set proxy
            clientBuilder.proxy(proxyOptions.getProxy());
            //Set authentication, if present
            final String proxyAuth = proxyOptions.getBasicAuthentication();
            if (proxyAuth != null) {
                clientBuilder.proxyAuthenticator(new Authenticator() {

                    private static final String PROXY_AUTHORIZATION_HEADER = "Proxy-Authorization";

                    @Override
                    public okhttp3.Request authenticate(Route route, Response response) throws IOException {
                        if (response.request().header(PROXY_AUTHORIZATION_HEADER) != null) {
                            return null;
                        }
                        return response.request().newBuilder()
                                .header(PROXY_AUTHORIZATION_HEADER, proxyAuth)
                                .build();
                    }
                });
            }
        }

        return clientBuilder
                .connectTimeout(options.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(options.getReadTimeout(), TimeUnit.SECONDS)
                .build();
    }

    public void setApiToken(String apiToken) {
        Asserts.assertNotNull(apiToken, "api token");
        this.apiToken = apiToken;
    }

    private HttpUrl createBaseUrl(String domain) {
        String url = domain;
        if (!domain.startsWith("https://") && !domain.startsWith("http://")) {
            url = "https://" + domain;
        }
        return HttpUrl.parse(url);
    }

    /**
     * Getter for the JobErrors entity.
     *
     * @return the JobErrors entity.
     */
    public JobErrorsEntity jobErrors() {
        return new JobErrorsEntity(client, baseUrl, apiToken);
    }

}

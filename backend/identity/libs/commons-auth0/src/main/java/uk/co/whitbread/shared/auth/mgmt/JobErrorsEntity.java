package uk.co.whitbread.shared.auth.mgmt;

import com.auth0.exception.Auth0Exception;
import com.auth0.net.Request;
import com.auth0.net.Response;
import com.auth0.utils.Asserts;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletionException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import uk.co.whitbread.shared.auth.mgmt.json.JobErrors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class JobErrorsEntity {

    protected final OkHttpClient client;
    protected final HttpUrl baseUrl;
    protected final String apiToken;

    public JobErrorsEntity(OkHttpClient client, HttpUrl baseUrl, String apiToken) {
        this.client = client;
        this.baseUrl = baseUrl;
        this.apiToken = apiToken;
    }

    /**
     * Request a Job execution Errors details.
     * See https://auth0.com/docs/api/management/v2#!/Jobs/get_errors
     *
     * @param jobId the id of the job to retrieve.
     * @return a Request to execute.
     */
    public Request<JobErrors[]> get(String jobId) {
        Asserts.assertNotNull(jobId, "job id");

        String url = baseUrl
                .newBuilder()
                .addPathSegments("api/v2/jobs")
                .addPathSegment(jobId)
                .addPathSegment("errors")
                .build()
                .toString();

        return new OkHttpJobErrorsRequest(client, url, apiToken);
    }

    /**
     * A simple Request implementation using OkHttp directly, replacing the removed
     * com.auth0.net.CustomRequest from Auth0 SDK v2.
     */
        private record OkHttpJobErrorsRequest(OkHttpClient client, String url,
                                              String apiToken) implements Request<JobErrors[]> {

        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        @Override
            public Response<JobErrors[]> execute() throws Auth0Exception {
                okhttp3.Request request = new Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + apiToken)
                    .get()
                    .build();

                try (okhttp3.Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new Auth0Exception(
                            "Request to " + url + " failed with status code: " + response.code());
                    }
                    String body = response.body() != null ? response.body().string() : "";
                    JobErrors[] parsed = OBJECT_MAPPER.readValue(body,
                        new TypeReference<>() {
                        });
                    int statusCode = response.code();
                    Map<String, String> headers = new HashMap<>();
                    for (String name : response.headers().names()) {
                        headers.put(name, response.header(name));
                    }
                    return new ResponseWrapper<>(headers, parsed, statusCode);
                } catch (IOException e) {
                    throw new Auth0Exception("Failed to execute request", e);
                }
            }

            @Override
            public CompletableFuture<Response<JobErrors[]>> executeAsync() {
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        return execute();
                    } catch (Auth0Exception e) {
                        throw new CompletionException(e);
                    }
                });
            }

            @Override
            public Request<JobErrors[]> addHeader(String name, String value) {
                return this;
            }

            @Override
            public Request<JobErrors[]> addParameter(String name, Object value) {
                return this;
            }

            @Override
            public Request<JobErrors[]> setBody(Object body) {
                return this;
            }
        }

    /**
     * Simple Response implementation since com.auth0.net.ResponseImpl is package-private.
     */
        private record ResponseWrapper<T>(Map<String, String> headers, T body,
                                          int statusCode) implements Response<T> {

        @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

            @Override
            public T getBody() {
                return body;
            }

            @Override
            public int getStatusCode() {
                return statusCode;
            }
        }
}

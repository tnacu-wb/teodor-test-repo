package uk.co.whitbread.hotelcountries.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotelcountries.exception.AEMServiceException;
import uk.co.whitbread.hotelcountries.model.aem.AemCountries;
import uk.co.whitbread.hotelcountries.properties.AemProperties;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class AemClient {

    private final AemProperties properties;
    private final Jaxb2Marshaller marshaller;
    private final HttpClientBuilder clientBuilder;

    private static final String UNEXPECTED_STATUS_CODE = "Connection error when retrieving country information from {}. Status code {}.";
    private static final String AEM_CONNECTION_ERROR_MESSAGE = "Connection error when retrieving country information from AEM.";

    private AemCountries backupAemCountries;

    public AemCountries getAemCountries(String country, String language) {
        if (properties.getAuthentication().isEnabled()) {
            setupAuthentication(country, language);
        }

        try (CloseableHttpClient client = clientBuilder.build()) {
            String url = properties.getParameterisedUrl(country, language);
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
            httpGet.setConfig(requestConfig);
            ClassicHttpResponse response = client.execute(httpGet);

            int statusCode = response.getCode();
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = response.getEntity().getContent();
                backupAemCountries = (AemCountries) marshaller.unmarshal(new StreamSource(inputStream));
                return backupAemCountries;
            }
            if (backupAemCountries != null) {
                log.warn(UNEXPECTED_STATUS_CODE + " Using cached list.", url, statusCode);
                return backupAemCountries;
            }
            log.error(UNEXPECTED_STATUS_CODE + " Backup list hasn't been created yet.", url, statusCode);
            throw new AEMServiceException("Return code while retrieving country XML file from AEM was " + statusCode);
        } catch (IOException e) {
            if (backupAemCountries != null) {
                log.warn(AEM_CONNECTION_ERROR_MESSAGE + " Using cached list.");
                return backupAemCountries;
            }
            log.error(AEM_CONNECTION_ERROR_MESSAGE + " Backup list hasn't been created yet.");
            throw new AEMServiceException("Error trying to connect to AEM", e);
        } catch (XmlMappingException e) {
            throw new AEMServiceException("Exception while unmarshalling country information from AEM", e);
        }
    }

    private void setupAuthentication(String country, String language) {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(properties.getHost(country, language), properties.getPort()),
                new UsernamePasswordCredentials(properties.getAuthentication().getUsername(), properties.getAuthentication().getPassword().toCharArray()));
        clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
    }
}

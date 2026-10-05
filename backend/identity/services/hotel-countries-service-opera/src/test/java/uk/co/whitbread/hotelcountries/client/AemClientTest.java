package uk.co.whitbread.hotelcountries.client;

import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.oxm.MarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import uk.co.whitbread.hotelcountries.exception.AEMServiceException;
import uk.co.whitbread.hotelcountries.properties.AemProperties;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AemClientTest {

    private static final String COUNTRY = "GB";
    private static final String LANGUAGE = "EN";
    private static final String URL = "URL";

    @Mock
    private Jaxb2Marshaller jaxb2Marshaller;

    @Mock
    private HttpClientBuilder httpClientBuilder;

    @Mock
    private AemProperties aemProperties;

    @Mock
    private AemProperties.Authentication authentication;

    @Mock
    private CloseableHttpClient closeableHttpClient;

    @Mock
    private CloseableHttpResponse httpResponse;

    @Mock
    private HttpEntity httpEntity;

    @Mock
    private InputStream inputStream;

    @InjectMocks
    private AemClient aemClient;

    @BeforeEach
    public void setUp() throws IOException {
        when(aemProperties.getAuthentication()).thenReturn(authentication);
        when(httpClientBuilder.build()).thenReturn(closeableHttpClient);

        when(aemProperties.getParameterisedUrl(COUNTRY, LANGUAGE)).thenReturn(URL);

        when(httpResponse.getCode()).thenReturn(HttpStatus.SC_OK);
        when(httpEntity.getContent()).thenReturn(inputStream);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(closeableHttpClient.execute(any())).thenReturn(httpResponse);
    }

    @Test
    public void getAemCountriesSuccess_authDisabled() throws IOException {

        aemClient.getAemCountries(COUNTRY, LANGUAGE);

        ArgumentCaptor<StreamSource> sourceArgumentCaptor = ArgumentCaptor.forClass(StreamSource.class);
        verify(jaxb2Marshaller).unmarshal(sourceArgumentCaptor.capture());
        assertThat(sourceArgumentCaptor.getValue().getInputStream(), is(httpResponse.getEntity().getContent()));
    }

    @Test
    public void getAemCountriesSuccess_authEnabled() throws IOException {

        when(authentication.isEnabled()).thenReturn(true);
        when(authentication.getUsername()).thenReturn("USERNAME");
        when(authentication.getPassword()).thenReturn("PASSWORD");

        aemClient.getAemCountries(COUNTRY, LANGUAGE);

        verify(aemProperties).getHost(COUNTRY, LANGUAGE);
        verify(aemProperties).getPort();
        verify(authentication).getUsername();
        verify(authentication).getPassword();

        ArgumentCaptor<StreamSource> sourceArgumentCaptor = ArgumentCaptor.forClass(StreamSource.class);
        verify(jaxb2Marshaller).unmarshal(sourceArgumentCaptor.capture());
        assertThat(sourceArgumentCaptor.getValue().getInputStream(), is(httpResponse.getEntity().getContent()));
    }

    @Test
    public void getAemCountriesError_authEnabled_usernameIsNull() {

        when(authentication.isEnabled()).thenReturn(true);
        when(authentication.getPassword()).thenReturn("PASSWORD");

        assertThatThrownBy(() -> aemClient.getAemCountries(COUNTRY, LANGUAGE)).
                isInstanceOf(NullPointerException.class).
                hasFieldOrPropertyWithValue("message", "User name");
    }

    @Test
    public void getAemCountriesError_httpStatus400() {

        when(httpResponse.getCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);

        assertThatThrownBy(() -> aemClient.getAemCountries(COUNTRY, LANGUAGE)).
                isInstanceOf(AEMServiceException.class).
                hasFieldOrPropertyWithValue("errorCode", "003").
                hasFieldOrPropertyWithValue("message", "Return code while retrieving country XML file from AEM was 400");
    }

    @Test
    public void getAemCountriesError_withIOException() throws IOException {
        when(closeableHttpClient.execute(any(HttpUriRequest.class))).thenThrow(IOException.class);
        assertThatThrownBy(() -> aemClient.getAemCountries(COUNTRY, LANGUAGE)).
                isInstanceOf(AEMServiceException.class).
                hasFieldOrPropertyWithValue("errorCode", "003").
                hasFieldOrPropertyWithValue("message", "Error trying to connect to AEM");
    }

    @Test
    public void getAemCountriesError_withXmlMappingException() {
        when(httpClientBuilder.build()).thenThrow(MarshallingFailureException.class);
        try {
            aemClient.getAemCountries(COUNTRY, LANGUAGE);
            fail();
        } catch (AEMServiceException e) {
            assertEquals(e.getErrorCode(), "003");
            assertEquals(e.getMessage(), "Exception while unmarshalling country information from AEM");
        }
    }

}
package uk.co.whitbread.shared.auth.jwt;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;

import java.security.PublicKey;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PublicKeyProviderTest {

    private static final String TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImFfdmFsaWRfa2V5X2lkIn0.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.xU7Z2opj5tcBKXlKhtLxnN4UdXLFe47i1KTaLe_tIACCO3CSrow89Sfn0pjCvIRHNNjzWlxxpmfT68_eB-DCy8NXXd6d-OJhng5VMWGtJtAqHaXuAvgA8qMGFGCrSqULHlvITRhNYWhKXQOPs_XfcZ1spv1sz8EQgaUhIf2vvV0";
    @Mock
    private JwkProvider jwkProvider;

    @Mock
    private Jwk jwk;

    @Mock
    private PublicKey publicKey;

    @InjectMocks
    private PublicKeyProvider underTest;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        when(jwkProvider.get(anyString())).thenReturn(jwk);
        when(jwk.getPublicKey()).thenReturn(publicKey);
    }

    @Test
    public void retrievePublicKey() {
        // Given a valid token, when we retrieve a public key
        PublicKey actualPublicKey = underTest.retrievePublicKey(TOKEN);

        // The public key matches the one retrieved from the provider
        Assert.assertThat(actualPublicKey, CoreMatchers.is(publicKey));
    }

    @Test
    public void shouldThrowServiceExceptionWhenRetrievingPublicKey() throws JwkException {

        when(jwkProvider.get(anyString())).thenThrow(new JwkException(""));

        exception.expect(TokenVerificationException.class);
        exception.expectMessage("Error while retrieving Public Key in JWK.");

        underTest.retrievePublicKey(TOKEN);
    }
}
package uk.co.whitbread.hotel.register.utils.register;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.auth.model.Auth0User;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class Auth0ManagementTransformerTest {

    private static final String EMAIL = "email@example.com";
    private static final String PASSWORD = "password1";
    private static final String GUEST_HISTORY_NUMBER = "GHN_test";
    private static final String GUEST_HISTORY_NUMBER_LABEL = "guest_history_number";

    @InjectMocks
    private Auth0ManagementTransformer target;

    @Test
    public void transform() {
        Auth0User user = target.transform(EMAIL, PASSWORD, Map.of(GUEST_HISTORY_NUMBER_LABEL, GUEST_HISTORY_NUMBER));
        assertThat(user.getName()).isEqualTo(EMAIL);
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getAppMetadata().get(GUEST_HISTORY_NUMBER_LABEL)).isEqualTo(GUEST_HISTORY_NUMBER);
        assertThat(user.getEmail()).isEqualTo(EMAIL);
    }
}
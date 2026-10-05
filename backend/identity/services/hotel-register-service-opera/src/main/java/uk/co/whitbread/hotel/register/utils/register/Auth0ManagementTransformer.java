package uk.co.whitbread.hotel.register.utils.register;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.auth.model.Auth0User;

@RequiredArgsConstructor
@Component
public class Auth0ManagementTransformer {

    public static final String GHN_LABEL = "guest_history_number";
    public static final String EMPLOYEE_STATUS_LABEL = "employee_status";

    public Auth0User transform(String email, String password, Map<String, Object> appMetadata) {
        Auth0User user = new Auth0User();
        user.setName(email);
        user.setEmail(email);
        user.setEmailVerified(true);
        user.setPassword(password);
        user.setAppMetadata(appMetadata);
        return user;
    }
}

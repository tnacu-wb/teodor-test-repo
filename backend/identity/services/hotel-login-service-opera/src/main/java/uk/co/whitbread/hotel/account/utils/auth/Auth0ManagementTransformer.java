package uk.co.whitbread.hotel.account.utils.auth;

import com.auth0.json.mgmt.users.User;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Auth0ManagementTransformer {

    public static final String GHN_LABEL = "guest_history_number";
    public static final String EMPLOYEE_STATUS_LABEL = "employee_status";

    public User transform(String email, String password, Map<String, Object> appMetadata) {
        User user = new User();
        user.setName(email);
        user.setEmail(email);
        user.setEmailVerified(true);
        user.setPassword(password.toCharArray());
        user.setAppMetadata(appMetadata);
        return user;
    }
}
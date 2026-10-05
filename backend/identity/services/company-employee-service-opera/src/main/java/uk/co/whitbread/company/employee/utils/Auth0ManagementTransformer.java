package uk.co.whitbread.company.employee.utils;

import uk.co.whitbread.shared.auth.model.Auth0User;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.auth.properties.ManagementProperties;

@RequiredArgsConstructor
@Component
public class Auth0ManagementTransformer {

    private final ManagementProperties authProperties;

    public static final String GHN_LABEL = "guest_history_number";
    public static final String EMPLOYEE_STATUS_LABEL = "employee_status";
    public static final String ACTIVATION_KEY_LABEL = "activation_key";
    public static final String COMPANY_ACCOUNT_ID_LABEL = "company_account_id";
    public static final String EMPLOYEE_ACCOUNT_ID_LABEL = "employee_account_id";

    public Auth0User transform(String email, String password, Map<String, Object> appMetadata) {
        Auth0User user = new Auth0User();
        user.setName(email);
        user.setEmail(email);
        user.setConnection(authProperties.getConnection());
        user.setEmailVerified(true);
        user.setPassword(password);
        user.setAppMetadata(appMetadata);
        return user;
    }
}

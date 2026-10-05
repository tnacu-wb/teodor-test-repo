package uk.co.whitbread.hotel.register.utils.register;

import lombok.experimental.UtilityClass;
import uk.co.whitbread.common.validators.password.PasswordByConfigValidator;
import uk.co.whitbread.hotel.register.exceptions.PasswordPolicyException;

@UtilityClass
public class PasswordPolicyUtil {

  private static final String AUTH_0_POLICY_CONFIG_KEY = "registerAuth0";
  private static final String POLICY_CONFIG_KEY = "register";

  public static void enforcePasswordPolicy(PasswordByConfigValidator passwordValidator,
      String password, boolean involveAuth0) {
    String passwordPolicyConfigKey =
        involveAuth0 ? AUTH_0_POLICY_CONFIG_KEY : POLICY_CONFIG_KEY;
    passwordValidator.isValid(password, passwordPolicyConfigKey,
        PasswordPolicyUtil::triggerPasswordPolicyError);
  }

  public static void triggerPasswordPolicyError(String errorMessage) {
    throw new PasswordPolicyException(errorMessage);
  }

}
package uk.co.whitbread.hotel.account.utils;

import static uk.co.whitbread.hotel.account.model.LanguageCode.DE;

import java.security.SecureRandom;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.account.properties.EmailProperties;

@Component
@RequiredArgsConstructor
public class ResetPasswordDataGenerator {

  static final String RESET_URL_KEY_PARAM = "?key=";
  static final String RESET_URL_TOKEN_PARAM = "?token=";
  static final int BB_TOKEN_LENGTH = 10;
  static final int PI_TOKEN_LENGTH = 8;

  private final EmailProperties emailProperties;

  @RequiredArgsConstructor
  @Getter
  public static class ResetPasswordData {
    private final String resetToken;
    private final String resetUrl;
  }

  public ResetPasswordData generateResetPasswordData(String requestUrl, String language,
      boolean business) {

    final String resetToken = generatePasswordResetToken(business);
    final String resetUrl = getResetPasswordUrl(requestUrl, resetToken, language, business);

    return new ResetPasswordData(resetToken, resetUrl);
  }

  private String generatePasswordResetToken(boolean business) {
    final int tokenLength = business ? BB_TOKEN_LENGTH : PI_TOKEN_LENGTH;
    return RandomStringUtils
            .random(tokenLength, 0, 0, true, true, null, new SecureRandom());
  }

  private String getResetPasswordUrl(String requestUrl, String resetToken, String language,
                                     boolean business) {
    if (StringUtils.isBlank(requestUrl)) {
      return getResetPasswordUrlPrefix(language, business) + resetToken;
    }
    final StringBuilder urlBuilder = new StringBuilder(requestUrl);
    if (business) {
      if (!requestUrl.endsWith(RESET_URL_KEY_PARAM)) {
        urlBuilder.append(RESET_URL_KEY_PARAM);
      }
    } else if (!requestUrl.endsWith(RESET_URL_TOKEN_PARAM)) {
        urlBuilder.append(RESET_URL_TOKEN_PARAM);
    }
    return urlBuilder.append(resetToken).toString();
  }

  private String getResetPasswordUrlPrefix(String language, boolean business) {
    if (business) {
      if (DE.name().equalsIgnoreCase(language)) {
        return emailProperties.getInnBusinessResetPasswordUrlDe();
      }
      return emailProperties.getInnBusinessResetPasswordUrl();
    }
    return emailProperties.getPiResetPasswordUrl();
  }
}

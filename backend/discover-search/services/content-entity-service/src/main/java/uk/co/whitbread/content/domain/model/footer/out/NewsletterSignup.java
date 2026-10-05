package uk.co.whitbread.content.domain.model.footer.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterSignup {

  private String newsletterText;
  private String newsletterButtonLabel;
  private String firstNameLabel;
  private String firstNameMaxLengthError;
  private String firstNameValidError;
  private String firstNameMinLengthError;
  private String lastNameLabel;
  private String lastNameValidError;
  private String lastNameMaxLengthError;
  private String lastNameMinLengthError;
  private String emailLabel;
  private String emailValidError;
  private String emailMaxLengthError;
  private String emailMinLengthError;
  private String countrySelectLabel;
  private String countrySelectEmptyFieldError;
  private String serverError;
  private String signupTitle;
  private String doubleOptInTitle;
  private String doubleOptInText;
  private String confirmationText;
  private String bookStayButtonText;
  private String bookStayButtonUrl;
  private String introViewTitle;
  private String privacyPolicyText;
  private String introViewText;
  private String signUpButtonText;
}

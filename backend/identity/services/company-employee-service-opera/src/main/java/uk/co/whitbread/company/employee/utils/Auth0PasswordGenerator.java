package uk.co.whitbread.company.employee.utils;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordData;
import org.passay.PasswordGenerator;
import org.passay.PasswordValidator;
import org.passay.RepeatCharactersRule;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Auth0PasswordGenerator {

  private static final int NUMBER_OF_CHARACTERS = 1;
  private static final int INVALID_NUMBER_OF_REPEATED_CHARACTERS_IN_A_ROW = 3;
  private static final int PASSWORD_LENGTH = 10;

  public String generateAuth0CompliantPassword() {
    PasswordGenerator passwordGenerator = new PasswordGenerator();
    String generatedPassword;
    do {
      generatedPassword = passwordGenerator.generatePassword(PASSWORD_LENGTH, getPasswordRules());
    } while (!isValidPassword(generatedPassword));

    return generatedPassword;
  }

  private List<CharacterRule> getPasswordRules() {
    CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase);
    lowerCaseRule.setNumberOfCharacters(NUMBER_OF_CHARACTERS);

    CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase);
    upperCaseRule.setNumberOfCharacters(NUMBER_OF_CHARACTERS);

    CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit);
    digitRule.setNumberOfCharacters(NUMBER_OF_CHARACTERS);

    return List.of(lowerCaseRule, upperCaseRule, digitRule);
  }

  private boolean isValidPassword(String password) {
    RepeatCharactersRule repeatCharactersRule = new RepeatCharactersRule(
        INVALID_NUMBER_OF_REPEATED_CHARACTERS_IN_A_ROW);
    PasswordValidator validator = new PasswordValidator(repeatCharactersRule);
    return validator.validate(new PasswordData(password)).isValid();
  }
}

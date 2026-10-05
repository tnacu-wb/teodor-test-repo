package uk.co.whitbread.company.employee.utils;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class Auth0PasswordGeneratorTest {

  private Auth0PasswordGenerator auth0PasswordGenerator;

  @BeforeEach
  public void setUp() {
    auth0PasswordGenerator = new Auth0PasswordGenerator();
  }

  @Test
  public void generateAuth0CompliantPassword_Success() {
    String generatedPassword = auth0PasswordGenerator.generateAuth0CompliantPassword();

    assertNotNull(generatedPassword);
  }
}

package uk.co.whitbread.hotel.account.utils.token;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SecureTokenGeneratorTest {

    SecureTokenGenerator secureTokenGenerator;

    @BeforeEach
    public void setUp() throws Exception {
        secureTokenGenerator = new SecureTokenGenerator();
    }

    @Test
    public void shouldGenerateRandomTokensWithCorrectLength() {
        //given
        int length = 5;

        //when
        String token = secureTokenGenerator.generateToken(length);

        //then
        assertThat(token.length(), is(length));
    }

    @Test
    public void tokensShouldBeDifferent() {
        //given
        int length = 5;

        //when
        String token = secureTokenGenerator.generateToken(length);
        String token2 = secureTokenGenerator.generateToken(length);

        //then
        assertThat(token.equals(token2), is(false));
    }

    @Test
    public void gracefullyReturnsEmptyStringIfLengthIsNotValid() {
        //given
        int length = 0;

        //when
        String token = secureTokenGenerator.generateToken(length);

        //then
        assertThat(token.equals(""), is(true));
    }

    @Test
    public void alwaysReturnsAMaximumOf100Digits() {
        //given
        int length = 99999999;

        //when
        String token = secureTokenGenerator.generateToken(length);

        //then
        assertThat(token.length(), is(100));
    }
}

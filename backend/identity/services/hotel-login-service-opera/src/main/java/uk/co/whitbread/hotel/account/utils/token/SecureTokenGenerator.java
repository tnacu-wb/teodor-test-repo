package uk.co.whitbread.hotel.account.utils.token;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.stream.IntStream;

@Component
public class SecureTokenGenerator {

    private static final String SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final SecureRandom random = new SecureRandom();

    public String generateToken(Integer length) {
        if (length < 1) {
            return "";
        }

        //max length to avoid malicious requests from slowing down the server
        if(length > 100) {
            length = 100;
        }

        char[] token = new char[length];

        IntStream.range(0, length)
                .parallel()
                .forEach(i -> token[i] = SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));

        return new String(token);
    }

}

package uk.co.whitbread.hotel.captcha.service;

import de.triology.recaptchav2java.ReCaptcha;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

    @Value("${captcha.secret}")
    private String captchaSecret;

    public Boolean isValid(String token) {
        return new ReCaptcha(captchaSecret).isValid(token);
    }

}

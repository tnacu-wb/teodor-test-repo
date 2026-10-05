package uk.co.whitbread.common.validators.password;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.passay.AllowedRegexRule;
import org.passay.IllegalCharacterRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.String.join;
import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.StringUtils.isEmpty;
import static uk.co.whitbread.common.validators.password.Password.DEFAULT_ERROR_MESSAGE;

/**
 * Can be used either with the @Password annotation on a field or directly by calling
 * {@link #isValid(String, String, Consumer)}
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordByConfigValidator implements ConstraintValidator<Password, String> {

    private final PasswordProperties passwordProperties;

    @Getter(PROTECTED)
    @Setter(PROTECTED)
    private String configKey;

    @Override
    public void initialize(Password passwordAnnotation) {
        setConfigKey(passwordAnnotation.value());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        Consumer<String> errorMessageConsumer = errorMessage -> {
            setErrorMessageInContext(errorMessage, context);
        };

        String configKey = getConfigKey();

        return isValid(value, configKey, errorMessageConsumer);
    }

    public boolean isValid(String value, String configKey, Consumer<String> errorMessageConsumer) {


        Optional<PasswordConfig> passwordConfigOptional =
                getPasswordConfig(configKey);

        if(!passwordConfigOptional.isPresent()) {
            setDefaultErrorMessage(errorMessageConsumer);
            return false;
        }

        PasswordConfig passwordConfig = passwordConfigOptional.get();

        if(!passwordConfig.isEnabled()) {
            return true;
        }

        if(value == null) {
            if(passwordConfig.isAllowNull()) {
                return true;
            }
            setErrorMessage(passwordConfig, errorMessageConsumer);
            return false;
        }

        Optional<PasswordValidator> passwordValidatorOptional =
                getPasswordValidator(passwordConfig, configKey);

        if(!passwordValidatorOptional.isPresent()) {
            setErrorMessage(passwordConfig, errorMessageConsumer);
            return false;
        }

        PasswordValidator passwordValidator = passwordValidatorOptional.get();
        RuleResult ruleResult = passwordValidator.validate(new PasswordData(value));
        if(ruleResult.isValid()) {
            return true;
        }

        setErrorMessage(ruleResult, passwordValidator, passwordConfig, errorMessageConsumer);

        return false;
    }

    protected void setErrorMessage(RuleResult ruleResult, PasswordValidator passwordValidator, PasswordConfig passwordConfig, Consumer<String> errorMessageConsumer) {

        String errorMessage = generateErrorMessage(ruleResult, passwordValidator, passwordConfig);
        errorMessageConsumer.accept(errorMessage);
    }

    protected void setErrorMessage(PasswordConfig passwordConfig, Consumer<String> errorMessageConsumer) {

        String errorMessage = generateErrorMessage(null, null, passwordConfig);
        errorMessageConsumer.accept(errorMessage);
    }

    protected void setDefaultErrorMessage(Consumer<String> errorMessageConsumer) {
        errorMessageConsumer.accept(DEFAULT_ERROR_MESSAGE);
    }

    protected String generateErrorMessage(RuleResult ruleResult, PasswordValidator passwordValidator, PasswordConfig passwordConfig) {

        String staticErrorMessage = passwordConfig.getStaticErrorMessage();
        if(!isEmpty(staticErrorMessage)) {
            return staticErrorMessage;
        }

        if(ruleResult != null && passwordValidator!= null) {
            List<String> errorMessages = passwordValidator.getMessages(ruleResult);
            String ruleResultErrorMessage = join(", ", errorMessages);
            if (!isEmpty(ruleResultErrorMessage)) {
                return ruleResultErrorMessage;
            }
        }

        return DEFAULT_ERROR_MESSAGE;
    }

    protected void setErrorMessageInContext(String errorMessage, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
    }

    protected Optional<PasswordConfig> getPasswordConfig(String key) {

        Map<String, PasswordConfig> configs = passwordProperties.getConfigs();
        if(CollectionUtils.isEmpty(configs)) {
            log.error("No password configuration found for key {}", key);
            return Optional.empty();
        }

        PasswordConfig passwordConfig = configs.get(key);
        if (passwordConfig == null) {
            log.error("No password configuration found for key {}", key);
            return Optional.empty();
        }

        return Optional.of(passwordConfig);
    }

    protected Optional<PasswordValidator> getPasswordValidator(PasswordConfig passwordConfig, String key) {

        List<Rule> passwordRules = createPasswordRuleList();
        addLengthRule(passwordConfig, passwordRules);
        addIllegalCharsRule(passwordConfig, passwordRules);
        addRegExpRules(passwordConfig, passwordRules);

        if (passwordRules.isEmpty()) {
            log.error("Empty password config for key {}", key);
            return Optional.empty();
        }


        PasswordValidator result = new PasswordValidator(passwordRules);
        return Optional.of(result);
    }

    protected List<Rule> createPasswordRuleList() {
        return new ArrayList<>();
    }

    protected void addLengthRule(PasswordConfig passwordConfig, List<Rule> passwordRules) {

        Integer minLength = passwordConfig.getMinLength();
        Integer maxLength = passwordConfig.getMaxLength();
        boolean setMinLength = (minLength != null);
        boolean setMaxLength = (maxLength != null);
        boolean addLengthConstraint = (setMinLength || setMaxLength);

        if (!addLengthConstraint) {
            return;
        }

        int min = setMinLength ? minLength : 0;
        int max = setMaxLength ? maxLength : MAX_VALUE;
        Rule lengthRule = new LengthRule(min, max);
        passwordRules.add(lengthRule);
    }

    protected void addIllegalCharsRule(PasswordConfig passwordConfig, List<Rule> passwordRules) {

        String illegalChars = passwordConfig.getIllegalChars();
        if (isEmpty(illegalChars)) {
            return;
        }

        char[] illegalCharsArray = illegalChars.toCharArray();
        Rule illegalCharsRule = new IllegalCharacterRule(illegalCharsArray);
        passwordRules.add(illegalCharsRule);
    }

    protected void addRegExpRules(PasswordConfig passwordConfig, List<Rule> passwordRules) {

        List<String> regExps = passwordConfig.getRegExps();
        if(CollectionUtils.isEmpty(regExps)) {
            return;
        }

        regExps.forEach(regExp -> addRegExpRule(regExp, passwordRules));
    }

    protected void addRegExpRule(String regExp, List<Rule> passwordRules) {

        if(isEmpty(regExp)) {
            return;
        }

        AllowedRegexRule regExpRule = new AllowedRegexRule(regExp);
        passwordRules.add(regExpRule);
    }
}
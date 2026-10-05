package uk.co.whitbread.common.validators.password;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.passay.AllowedRegexRule;
import org.passay.IllegalCharacterRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;

import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.common.validators.password.Password.DEFAULT_ERROR_MESSAGE;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class PasswordByConfigValidatorTest {

    public static final String CONFIG_KEY = "config_key_test";
    public static final String PASSWORD = "password_test";
    public static final String ERROR_MESSAGE_1 = "error_message_1";
    public static final String ERROR_MESSAGE_2 = "error_message_2";
    public static final String ILLEGAL_CHARS = "!~$";
    public static final String REGEXP_1 = "regexp1";
    public static final String REGEXP_2 = "regexp2";
    public static final String STATIC_ERROR_MESSAGE = "static-error-message-test";
    @Mock private Password passwordAnnotationMock;

    @Mock private PasswordProperties passwordPropertiesMock;

    @Mock private ConstraintValidatorContext constraintValidatorContextMock;
    @Mock private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilderMock;

    @InjectMocks
    @Spy private PasswordByConfigValidator testObj;
    @Mock private PasswordValidator passwordValidatorMock;
    @Mock private RuleResult ruleResultMock;
    @Mock private Rule ruleMock;
    @Mock private PasswordConfig passwordConfigMock;
    @Mock private List<Rule> passwordRulesMock;
    @Mock private Consumer<String> errorMessageConsumerMock;


    @BeforeEach
    public void setUp() throws Exception {

        when(passwordAnnotationMock.value()).thenReturn(CONFIG_KEY);
        when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilderMock);
        doReturn(CONFIG_KEY).when(testObj).getConfigKey();
        doReturn(passwordRulesMock).when(testObj).createPasswordRuleList();
        when(passwordConfigMock.isEnabled()).thenReturn(TRUE);
    }

    @Test
    public void shouldInitialize() {

        testObj.initialize(passwordAnnotationMock);
        verify(testObj).setConfigKey(CONFIG_KEY);
    }

    @Test
    public void shouldFailWhenNoPasswordValidatorFound() {

        doReturn(Optional.of(passwordConfigMock)).when(testObj).getPasswordConfig(CONFIG_KEY);
        doReturn(Optional.empty()).when(testObj).getPasswordValidator(passwordConfigMock, CONFIG_KEY);
        boolean result = testObj.isValid(PASSWORD, constraintValidatorContextMock);
        assertFalse(result);
        verify(testObj).setErrorMessageInContext(DEFAULT_ERROR_MESSAGE, constraintValidatorContextMock);
    }

    @Test
    public void shouldSuccessWhenPasswordValid() {

        doReturn(Optional.of(passwordConfigMock)).when(testObj).getPasswordConfig(CONFIG_KEY);
        doReturn(Optional.of(passwordValidatorMock)).when(testObj).getPasswordValidator(passwordConfigMock, CONFIG_KEY);
        when(passwordValidatorMock.validate(any(PasswordData.class))).thenReturn(ruleResultMock);
        when(ruleResultMock.isValid()).thenReturn(true);
        boolean result = testObj.isValid(PASSWORD, constraintValidatorContextMock);
        assertTrue(result);
    }

    @Test
    public void shouldSuccessWhenDisabled() {

        doReturn(Optional.of(passwordConfigMock)).when(testObj).getPasswordConfig(CONFIG_KEY);
        when(passwordConfigMock.isEnabled()).thenReturn(FALSE);
        boolean result = testObj.isValid(PASSWORD, constraintValidatorContextMock);
        assertTrue(result);
    }

    @Test
    public void shouldSuccessWhenAllowNull() {

        doReturn(Optional.of(passwordConfigMock)).when(testObj).getPasswordConfig(CONFIG_KEY);
        when(passwordConfigMock.isAllowNull()).thenReturn(TRUE);
        boolean result = testObj.isValid(null, constraintValidatorContextMock);
        assertTrue(result);
    }

    @Test
    public void shouldFailWhenNotAllowNull() {

        doReturn(Optional.of(passwordConfigMock)).when(testObj).getPasswordConfig(CONFIG_KEY);
        boolean result = testObj.isValid(null, constraintValidatorContextMock);
        assertFalse(result);
    }

    @Test
    public void shouldFailWhithErrorMessage() {

        doReturn(ERROR_MESSAGE_1).when(testObj).generateErrorMessage(ruleResultMock, passwordValidatorMock, passwordConfigMock);
        doReturn(Optional.of(passwordConfigMock)).when(testObj).getPasswordConfig(CONFIG_KEY);
        doReturn(Optional.of(passwordValidatorMock)).when(testObj).getPasswordValidator(passwordConfigMock, CONFIG_KEY);
        when(passwordValidatorMock.validate(any(PasswordData.class))).thenReturn(ruleResultMock);
        when(ruleResultMock.isValid()).thenReturn(false);

        doNothing().when(testObj).setErrorMessageInContext(anyString(), any(ConstraintValidatorContext.class));
        boolean result = testObj.isValid(PASSWORD, constraintValidatorContextMock);
        assertFalse(result);

        verify(testObj).setErrorMessageInContext(ERROR_MESSAGE_1, constraintValidatorContextMock);
    }

    @Test
    public void shouldGenerateErrorMessageFromStaticErrorMessage() {

        when(passwordConfigMock.getStaticErrorMessage()).thenReturn(STATIC_ERROR_MESSAGE);
        String result = testObj.generateErrorMessage(ruleResultMock, passwordValidatorMock, passwordConfigMock);
        assertEquals(STATIC_ERROR_MESSAGE, result);
    }

    @Test
    public void shouldGenerateErrorMessageFromRuleSet() {

        when(passwordValidatorMock.getMessages(ruleResultMock)).thenReturn(asList(ERROR_MESSAGE_1, ERROR_MESSAGE_2));
        String result = testObj.generateErrorMessage(ruleResultMock, passwordValidatorMock, passwordConfigMock);
        assertEquals(ERROR_MESSAGE_1+", "+ERROR_MESSAGE_2, result);
    }

    @Test
    public void shouldGenerateErrorMessageUsingDefault() {

        when(passwordValidatorMock.getMessages(ruleResultMock)).thenReturn(emptyList());
        String result = testObj.generateErrorMessage(ruleResultMock, passwordValidatorMock, passwordConfigMock);
        assertEquals(DEFAULT_ERROR_MESSAGE, result);
    }

    @Test
    public void shouldGenerateErrorMessageWhenNoRuleResult() {

        String result = testObj.generateErrorMessage(null, null, passwordConfigMock);
        assertEquals(DEFAULT_ERROR_MESSAGE, result);
    }

    @Test
    public void shouldSetErrorMessageInContext() {
        testObj.setErrorMessageInContext(ERROR_MESSAGE_1, constraintValidatorContextMock);
        verify(constraintValidatorContextMock).disableDefaultConstraintViolation();
        verify(constraintValidatorContextMock).buildConstraintViolationWithTemplate(ERROR_MESSAGE_1);
        verify(constraintViolationBuilderMock).addConstraintViolation();
    }

    @Test
    public void shouldSetErrorMessage() {

        doReturn(ERROR_MESSAGE_1).when(testObj).generateErrorMessage(ruleResultMock, passwordValidatorMock, passwordConfigMock);
        testObj.setErrorMessage(ruleResultMock, passwordValidatorMock, passwordConfigMock, errorMessageConsumerMock);
        verify(errorMessageConsumerMock).accept(ERROR_MESSAGE_1);
    }

    @Test
    public void shouldSetErrorMessageShort() {

        doReturn(ERROR_MESSAGE_1).when(testObj).generateErrorMessage(null, null, passwordConfigMock);
        testObj.setErrorMessage(passwordConfigMock, errorMessageConsumerMock);
        verify(errorMessageConsumerMock).accept(ERROR_MESSAGE_1);
    }

    @Test
    public void shouldSetDefaultErrorMessage() {

        testObj.setDefaultErrorMessage(errorMessageConsumerMock);
        verify(errorMessageConsumerMock).accept(DEFAULT_ERROR_MESSAGE);
    }

    @Test
    public void shouldNotGetPasswordConfigWhenNoConfig() {

        Optional<PasswordConfig> result = testObj.getPasswordConfig(CONFIG_KEY);
        assertFalse(result.isPresent());
    }

    @Test
    public void shouldNotGetPasswordConfigWhenEmptyConfig() {

        when(passwordPropertiesMock.getConfigs()).thenReturn(emptyMap());
        Optional<PasswordConfig> result = testObj.getPasswordConfig(CONFIG_KEY);
        assertFalse(result.isPresent());
    }

    @Test
    public void shouldNotGetPasswordConfigWhenKeyNotInConfig() {

        when(passwordPropertiesMock.getConfigs()).thenReturn(singletonMap("otherKey", passwordConfigMock));
        Optional<PasswordConfig> result = testObj.getPasswordConfig(CONFIG_KEY);
        assertFalse(result.isPresent());
    }

    @Test
    public void shouldGetPasswordConfig() {

        when(passwordPropertiesMock.getConfigs()).thenReturn(singletonMap(CONFIG_KEY, passwordConfigMock));
        Optional<PasswordConfig> result = testObj.getPasswordConfig(CONFIG_KEY);
        assertTrue(result.isPresent());
    }

    @Test
    public void shouldNotGetPasswordValidatorWhenNoRuleAdded() {

        doNothing().when(testObj).addLengthRule(passwordConfigMock, passwordRulesMock);
        doNothing().when(testObj).addIllegalCharsRule(passwordConfigMock, passwordRulesMock);
        doNothing().when(testObj).addRegExpRules(passwordConfigMock, passwordRulesMock);
        when(passwordRulesMock.isEmpty()).thenReturn(true);

        Optional<PasswordValidator> result = testObj.getPasswordValidator(passwordConfigMock, CONFIG_KEY);

        verify(testObj).addLengthRule(passwordConfigMock, passwordRulesMock);
        verify(testObj).addIllegalCharsRule(passwordConfigMock, passwordRulesMock);
        verify(testObj).addRegExpRules(passwordConfigMock, passwordRulesMock);

        assertEquals(Optional.empty(), result);

    }

    @Test
    public void shouldGetPasswordValidatorWhenRulesAdded() {

        doNothing().when(testObj).addLengthRule(passwordConfigMock, passwordRulesMock);
        doNothing().when(testObj).addIllegalCharsRule(passwordConfigMock, passwordRulesMock);
        doNothing().when(testObj).addRegExpRules(passwordConfigMock, passwordRulesMock);
        when(passwordRulesMock.isEmpty()).thenReturn(false);

        Optional<PasswordValidator> result = testObj.getPasswordValidator(passwordConfigMock, CONFIG_KEY);

        verify(testObj).addLengthRule(passwordConfigMock, passwordRulesMock);
        verify(testObj).addIllegalCharsRule(passwordConfigMock, passwordRulesMock);
        verify(testObj).addRegExpRules(passwordConfigMock, passwordRulesMock);

        assertTrue(result.isPresent());

    }

    @Test
    public void shouldNotAddLengthRule() {

        when(passwordConfigMock.getMinLength()).thenReturn(null);
        when(passwordConfigMock.getMaxLength()).thenReturn(null);
        testObj.addLengthRule(passwordConfigMock, passwordRulesMock);
        verify(passwordRulesMock, never()).add(any(Rule.class));
    }

    @Test
    public void shouldAddLengthRuleWhenOnlyMin() {

        ArgumentCaptor<Rule> ruleArgumentCaptor = ArgumentCaptor.forClass(Rule.class);

        when(passwordConfigMock.getMinLength()).thenReturn(5);
        when(passwordConfigMock.getMaxLength()).thenReturn(null);
        testObj.addLengthRule(passwordConfigMock, passwordRulesMock);
        verify(passwordRulesMock).add(ruleArgumentCaptor.capture());
        Rule rule = ruleArgumentCaptor.getValue();
        assertNotNull(rule);
        assertTrue(rule instanceof LengthRule);
        LengthRule lengthRule = (LengthRule)rule;
        assertEquals(5, lengthRule.getMinimumLength());
        assertEquals(Integer.MAX_VALUE, lengthRule.getMaximumLength());
    }

    @Test
    public void shouldAddLengthRuleWhenOnlyMax() {

        ArgumentCaptor<Rule> ruleArgumentCaptor = ArgumentCaptor.forClass(Rule.class);

        when(passwordConfigMock.getMinLength()).thenReturn(null);
        when(passwordConfigMock.getMaxLength()).thenReturn(10);
        testObj.addLengthRule(passwordConfigMock, passwordRulesMock);
        verify(passwordRulesMock).add(ruleArgumentCaptor.capture());
        Rule rule = ruleArgumentCaptor.getValue();
        assertNotNull(rule);
        assertTrue(rule instanceof LengthRule);
        LengthRule lengthRule = (LengthRule)rule;
        assertEquals(0, lengthRule.getMinimumLength());
        assertEquals(10, lengthRule.getMaximumLength());
    }

    @Test
    public void shouldAddLengthRuleWhenBothMinAndMax() {

        ArgumentCaptor<Rule> ruleArgumentCaptor = ArgumentCaptor.forClass(Rule.class);

        when(passwordConfigMock.getMinLength()).thenReturn(5);
        when(passwordConfigMock.getMaxLength()).thenReturn(10);
        testObj.addLengthRule(passwordConfigMock, passwordRulesMock);
        verify(passwordRulesMock).add(ruleArgumentCaptor.capture());
        Rule rule = ruleArgumentCaptor.getValue();
        assertNotNull(rule);
        assertTrue(rule instanceof LengthRule);
        LengthRule lengthRule = (LengthRule)rule;
        assertEquals(5, lengthRule.getMinimumLength());
        assertEquals(10, lengthRule.getMaximumLength());
    }

    @Test
    public void shouldNotAddIllegalCharRuleWhenNoneConfigured() {

        testObj.addIllegalCharsRule(passwordConfigMock, passwordRulesMock);
        verify(passwordRulesMock, never()).add(any(Rule.class));
    }

    @Test
    public void shouldAddIllegalCharRule() {

        ArgumentCaptor<Rule> ruleArgumentCaptor = ArgumentCaptor.forClass(Rule.class);

        when(passwordConfigMock.getIllegalChars()).thenReturn(ILLEGAL_CHARS);
        testObj.addIllegalCharsRule(passwordConfigMock, passwordRulesMock);
        verify(passwordRulesMock).add(ruleArgumentCaptor.capture());
        Rule rule = ruleArgumentCaptor.getValue();
        assertNotNull(rule);
        assertTrue(rule instanceof IllegalCharacterRule);
        IllegalCharacterRule illegalCharacterRule = (IllegalCharacterRule)rule;
        assertArrayEquals(new char[]{'!','~','$'}, illegalCharacterRule.getIllegalCharacters());
    }

    @Test
    public void shouldNotAddRegexpRulesWhenNoneConfigured() {

        testObj.addRegExpRules(passwordConfigMock, passwordRulesMock);
        verify(passwordRulesMock, never()).add(any(Rule.class));
    }

    @Test
    public void shouldAddRegexpRules() {

        ArgumentCaptor<Rule> ruleArgumentCaptor = ArgumentCaptor.forClass(Rule.class);

        when(passwordConfigMock.getRegExps()).thenReturn(asList(REGEXP_1, REGEXP_2));
        testObj.addRegExpRules(passwordConfigMock, passwordRulesMock);
        verify(passwordRulesMock, times(2)).add(ruleArgumentCaptor.capture());
        List<Rule> rules = ruleArgumentCaptor.getAllValues();
        assertNotNull(rules);
        assertEquals(2, rules.size());
        Rule rule1 = rules.get(0);
        Rule rule2 = rules.get(1);
        assertTrue(rule1 instanceof AllowedRegexRule);
        assertTrue(rule2 instanceof AllowedRegexRule);
        AllowedRegexRule allowedRegexRule1 = (AllowedRegexRule)rule1;
        AllowedRegexRule allowedRegexRule2 = (AllowedRegexRule)rule2;
        assertTrue(allowedRegexRule1.getPattern().matcher(REGEXP_1).matches());
        assertTrue(allowedRegexRule2.getPattern().matcher(REGEXP_2).matches());
    }

}
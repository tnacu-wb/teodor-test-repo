package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.NewCardMobileInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.NewCardWebInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.in.ConditionalValidationValidator;
import uk.co.whitbread.payment.orchestrator.domain.ports.primary.PaymentOrchestrationInPort;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.PaymentSecurityProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.PaymentGlobalExceptionHandler;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.PaymentExceptionHandler;

/**
 * Unit tests for the PaymentController unified init endpoint (POST /api/payments/init).
 *
 * <p>Tests polymorphic request deserialization, class-level @ConditionalValidation for
 * returnUrl security (HTTPS enforcement, host allowlist), Jackson discriminator handling,
 * HTTP 201 Created responses, and framework-free error code mapping.
 *
 * <p>Validates: Requirements 4.2, 4.3, 13.5, 13.6, 3.6, 3.7, 18.6
 */
@ExtendWith(MockitoExtension.class)
class PaymentControllerInitTest {

  private static final String INIT_URL = "/api/payments/init";
  private static final String BASKET_ID = "AQN-147756bb-bb71-4842-959a-2efe87e378ed";
  private static final String VALID_RETURN_URL = "https://www.premierinn.com/payments/3ds-return";
  private static final String TRANSACTION_ID = "190410112056083383";
  private static final List<String> ALLOWED_HOSTS =
      List.of("premierinn.com", "www.premierinn.com", "premierinn.digital");

  private MockMvc mockMvc;

  @Mock
  private PaymentOrchestrationInPort paymentOrchestrationInPort;

  @BeforeEach
  void setUp() {
    var securityProperties = new PaymentSecurityProperties();
    securityProperties.setAllowedReturnUrlHosts(ALLOWED_HOSTS);
    ReturnUrlValidator returnUrlValidator = new ReturnUrlValidator(securityProperties);

    // Create a validator factory with Spring DI support for the ConditionalValidationValidator
    LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();
    validatorFactory.setConstraintValidatorFactory(
        new SpringConstraintValidatorFactory(returnUrlValidator));
    validatorFactory.afterPropertiesSet();

    PaymentController controller = new PaymentController(paymentOrchestrationInPort);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new PaymentExceptionHandler(), new PaymentGlobalExceptionHandler())
        .setValidator(validatorFactory)
        .build();
  }

  @Nested
  class NewCardWebInitialization {

    @Test
    void validRequest_returns201WithPaymentInitResponse() throws Exception {
      when(paymentOrchestrationInPort.initPayment(any(PaymentInitCommand.class)))
          .thenReturn(new PaymentInitResult(true, TRANSACTION_ID, null, null));

      String request = """
          {
            "paymentMethod": "NEW_CARD_WEB",
            "basketId": "%s",
            "returnUrl": "%s",
            "country": "gb",
            "language": "en",
            "userType": "LEISURE",
            "clientChannel": "PI"
          }""".formatted(BASKET_ID, VALID_RETURN_URL);

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.paymentMethod").value("NEW_CARD_WEB"))
          .andExpect(jsonPath("$.transactionId").value(TRANSACTION_ID))
          .andExpect(jsonPath("$.methodConfig").doesNotExist());

      ArgumentCaptor<PaymentInitCommand> commandCaptor =
          ArgumentCaptor.forClass(PaymentInitCommand.class);
      verify(paymentOrchestrationInPort).initPayment(commandCaptor.capture());

      PaymentInitCommand captured = commandCaptor.getValue();
      assertThat(captured).isInstanceOf(NewCardWebInitCommand.class);
      NewCardWebInitCommand webCommand = (NewCardWebInitCommand) captured;
      assertThat(webCommand.basketId()).isEqualTo(BASKET_ID);
      assertThat(webCommand.returnUrl()).isEqualTo(VALID_RETURN_URL);
      assertThat(webCommand.country()).isEqualTo("gb");
      assertThat(webCommand.language()).isEqualTo("en");
      assertThat(webCommand.userType()).isEqualTo("LEISURE");
      assertThat(webCommand.clientChannel()).isEqualTo("PI");
    }

    @Test
    void validRequestWithPremierinnDigitalHost_returns201() throws Exception {
      when(paymentOrchestrationInPort.initPayment(any(PaymentInitCommand.class)))
          .thenReturn(new PaymentInitResult(true, TRANSACTION_ID, null, null));

      String request = """
          {
            "paymentMethod": "NEW_CARD_WEB",
            "basketId": "%s",
            "returnUrl": "https://premierinn.digital/payments/callback",
            "country": "gb",
            "language": "en",
            "userType": "LEISURE",
            "clientChannel": "PI"
          }""".formatted(BASKET_ID);

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.paymentMethod").value("NEW_CARD_WEB"))
          .andExpect(jsonPath("$.transactionId").value(TRANSACTION_ID));
    }
  }

  @Nested
  class NewCardMobileInitialization {

    @Test
    void validRequest_returns201WithPaymentInitResponse() throws Exception {
      when(paymentOrchestrationInPort.initPayment(any(PaymentInitCommand.class)))
          .thenReturn(new PaymentInitResult(true, TRANSACTION_ID, null, null));

      String request = """
          {
            "paymentMethod": "NEW_CARD_MOBILE",
            "basketId": "%s",
            "country": "gb",
            "language": "en",
            "userType": "LEISURE",
            "clientChannel": "APPS_IOS"
          }""".formatted(BASKET_ID);

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.paymentMethod").value("NEW_CARD_MOBILE"))
          .andExpect(jsonPath("$.transactionId").value(TRANSACTION_ID))
          .andExpect(jsonPath("$.methodConfig").doesNotExist());

      ArgumentCaptor<PaymentInitCommand> commandCaptor =
          ArgumentCaptor.forClass(PaymentInitCommand.class);
      verify(paymentOrchestrationInPort).initPayment(commandCaptor.capture());

      PaymentInitCommand captured = commandCaptor.getValue();
      assertThat(captured).isInstanceOf(NewCardMobileInitCommand.class);
      NewCardMobileInitCommand mobileCommand = (NewCardMobileInitCommand) captured;
      assertThat(mobileCommand.basketId()).isEqualTo(BASKET_ID);
      assertThat(mobileCommand.country()).isEqualTo("gb");
      assertThat(mobileCommand.language()).isEqualTo("en");
      assertThat(mobileCommand.userType()).isEqualTo("LEISURE");
      assertThat(mobileCommand.clientChannel()).isEqualTo("APPS_IOS");
    }
  }

  @Nested
  class ReturnUrlSecurityValidation {

    @Test
    void httpReturnUrl_returns400ValidationError() throws Exception {
      String request = """
          {
            "paymentMethod": "NEW_CARD_WEB",
            "basketId": "%s",
            "returnUrl": "http://www.premierinn.com/payments/3ds-return",
            "country": "gb",
            "language": "en",
            "userType": "LEISURE",
            "clientChannel": "PI"
          }""".formatted(BASKET_ID);

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

      verifyNoInteractions(paymentOrchestrationInPort);
    }

    @Test
    void hostNotInAllowlist_returns400ValidationError() throws Exception {
      String request = """
          {
            "paymentMethod": "NEW_CARD_WEB",
            "basketId": "%s",
            "returnUrl": "https://evil.com/phishing",
            "country": "gb",
            "language": "en",
            "userType": "LEISURE",
            "clientChannel": "PI"
          }""".formatted(BASKET_ID);

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

      verifyNoInteractions(paymentOrchestrationInPort);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "ftp://premierinn.com/file",
        "not a url at all",
        "premierinn.com/callback"
    })
    void invalidUrlSchemes_return400ValidationError(String invalidUrl) throws Exception {
      String request = """
          {
            "paymentMethod": "NEW_CARD_WEB",
            "basketId": "%s",
            "returnUrl": "%s",
            "country": "gb",
            "language": "en",
            "userType": "LEISURE",
            "clientChannel": "PI"
          }""".formatted(BASKET_ID, invalidUrl);

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

      verifyNoInteractions(paymentOrchestrationInPort);
    }

    @Test
    void missingReturnUrlForWeb_returns400ValidationError() throws Exception {
      String request = """
          {
            "paymentMethod": "NEW_CARD_WEB",
            "basketId": "%s",
            "country": "gb",
            "language": "en",
            "userType": "LEISURE",
            "clientChannel": "PI"
          }""".formatted(BASKET_ID);

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

      verifyNoInteractions(paymentOrchestrationInPort);
    }

    @Test
    void blankReturnUrlForWeb_returns400ValidationError() throws Exception {
      String request = """
          {
            "paymentMethod": "NEW_CARD_WEB",
            "basketId": "%s",
            "returnUrl": "   ",
            "country": "gb",
            "language": "en",
            "userType": "LEISURE",
            "clientChannel": "PI"
          }""".formatted(BASKET_ID);

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

      verifyNoInteractions(paymentOrchestrationInPort);
    }
  }

  @Nested
  class JacksonPolymorphicDeserialization {

    @Test
    void missingPaymentMethodDiscriminator_returns400() throws Exception {
      String request = """
          {
            "basketId": "%s",
            "returnUrl": "%s",
            "country": "gb",
            "language": "en",
            "userType": "LEISURE",
            "clientChannel": "PI"
          }""".formatted(BASKET_ID, VALID_RETURN_URL);

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

      verifyNoInteractions(paymentOrchestrationInPort);
    }

    @Test
    void unknownPaymentMethodType_returns400() throws Exception {
      String request = """
          {
            "paymentMethod": "UNKNOWN_TYPE",
            "basketId": "%s",
            "country": "gb",
            "language": "en",
            "userType": "LEISURE",
            "clientChannel": "PI"
          }""".formatted(BASKET_ID);

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

      verifyNoInteractions(paymentOrchestrationInPort);
    }

    @Test
    void emptyRequestBody_returns400() throws Exception {
      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

      verifyNoInteractions(paymentOrchestrationInPort);
    }

    @Test
    void malformedJson_returns400() throws Exception {
      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content("{not-valid-json"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

      verifyNoInteractions(paymentOrchestrationInPort);
    }
  }

  @Nested
  class FieldValidation {

    @Test
    void missingBasketId_returns400() throws Exception {
      String request = """
          {
            "paymentMethod": "NEW_CARD_MOBILE",
            "country": "gb",
            "language": "en",
            "userType": "LEISURE",
            "clientChannel": "APPS_IOS"
          }""";

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

      verifyNoInteractions(paymentOrchestrationInPort);
    }

    @Test
    void blankCountry_returns400() throws Exception {
      String request = """
          {
            "paymentMethod": "NEW_CARD_MOBILE",
            "basketId": "%s",
            "country": "   ",
            "language": "en",
            "userType": "LEISURE",
            "clientChannel": "APPS_IOS"
          }""".formatted(BASKET_ID);

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));

      verifyNoInteractions(paymentOrchestrationInPort);
    }
  }

  @Nested
  class ErrorCodeMapping {

    @Test
    void basketNotFound_returnsNotFound() throws Exception {
      when(paymentOrchestrationInPort.initPayment(any(PaymentInitCommand.class)))
          .thenReturn(new PaymentInitResult(
              false, null, PaymentErrorCode.BASKET_NOT_FOUND, "No basket found"));

      String request = validWebRequest();

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.error.code").value("BASKET_NOT_FOUND"))
          .andExpect(jsonPath("$.error.message").value("No basket found"));
    }

    @Test
    void paymentMethodNotAvailable_returnsUnprocessableEntity() throws Exception {
      when(paymentOrchestrationInPort.initPayment(any(PaymentInitCommand.class)))
          .thenReturn(new PaymentInitResult(
              false, null, PaymentErrorCode.PAYMENT_METHOD_NOT_AVAILABLE,
              "Card payment not available"));

      String request = validWebRequest();

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isUnprocessableContent())
          .andExpect(jsonPath("$.error.code").value("PAYMENT_METHOD_NOT_AVAILABLE"));
    }

    @Test
    void gatewayError_returnsBadGateway() throws Exception {
      when(paymentOrchestrationInPort.initPayment(any(PaymentInitCommand.class)))
          .thenReturn(new PaymentInitResult(
              false, null, PaymentErrorCode.GATEWAY_ERROR, "Gateway timeout"));

      String request = validWebRequest();

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isBadGateway())
          .andExpect(jsonPath("$.error.code").value("GATEWAY_ERROR"));
    }

    @Test
    void authorizationInProgress_returnsConflict() throws Exception {
      when(paymentOrchestrationInPort.initPayment(any(PaymentInitCommand.class)))
          .thenReturn(new PaymentInitResult(
              false, null, PaymentErrorCode.AUTHORIZATION_IN_PROGRESS,
              "Authorization in progress"));

      String request = validWebRequest();

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.error.code").value("AUTHORIZATION_IN_PROGRESS"));
    }

    @Test
    void expired_returnsGone() throws Exception {
      when(paymentOrchestrationInPort.initPayment(any(PaymentInitCommand.class)))
          .thenReturn(new PaymentInitResult(
              false, null, PaymentErrorCode.EXPIRED, "Payment session expired"));

      String request = validWebRequest();

      mockMvc.perform(post(INIT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isGone())
          .andExpect(jsonPath("$.error.code").value("EXPIRED"));
    }
  }

  @Nested
  class DeletedEndpoints {

    @Test
    void secureFieldsEndpoint_returns404() throws Exception {
      String request = """
          {
            "basketId": "%s",
            "returnUrl": "%s",
            "country": "gb",
            "language": "en"
          }""".formatted(BASKET_ID, VALID_RETURN_URL);

      mockMvc.perform(post("/api/payments/secure-fields")
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isNotFound());

      verifyNoInteractions(paymentOrchestrationInPort);
    }

    @Test
    void mobileSdkEndpoint_returns404() throws Exception {
      String request = """
          {
            "basketId": "%s",
            "country": "gb",
            "language": "en"
          }""".formatted(BASKET_ID);

      mockMvc.perform(post("/api/payments/mobile-sdk")
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isNotFound());

      verifyNoInteractions(paymentOrchestrationInPort);
    }
  }

  @Nested
  class AuthorizeEndpoint {

    @Test
    void validAuthorizeRequest_returns200WithResult() throws Exception {
      AuthorizeResult successResult = new AuthorizeResult(true, null, null);
      when(paymentOrchestrationInPort.authorizePayment(BASKET_ID)).thenReturn(successResult);

      String request = """
          {"basketId": "%s"}""".formatted(BASKET_ID);

      mockMvc.perform(post("/api/payments/authorize")
              .contentType(MediaType.APPLICATION_JSON)
              .content(request))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.errorCode").doesNotExist())
          .andExpect(jsonPath("$.errorMessage").doesNotExist());
    }
  }

  private String validWebRequest() {
    return """
        {
          "paymentMethod": "NEW_CARD_WEB",
          "basketId": "%s",
          "returnUrl": "%s",
          "country": "gb",
          "language": "en",
          "userType": "LEISURE",
          "clientChannel": "PI"
        }""".formatted(BASKET_ID, VALID_RETURN_URL);
  }

  /**
   * Custom constraint validator factory that injects the {@link ReturnUrlValidator}
   * into the {@link ConditionalValidationValidator} without needing a full Spring context.
   */
  private static class SpringConstraintValidatorFactory
      implements jakarta.validation.ConstraintValidatorFactory {

    private final ReturnUrlValidator returnUrlValidator;

    SpringConstraintValidatorFactory(ReturnUrlValidator returnUrlValidator) {
      this.returnUrlValidator = returnUrlValidator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends jakarta.validation.ConstraintValidator<?, ?>> T getInstance(
        Class<T> key) {
      if (key == ConditionalValidationValidator.class) {
        ConditionalValidationValidator validator = new ConditionalValidationValidator();
        // Inject the ReturnUrlValidator using reflection since @Autowired won't work
        // without Spring context
        try {
          var field = ConditionalValidationValidator.class
              .getDeclaredField("returnUrlValidator");
          field.setAccessible(true);
          field.set(validator, returnUrlValidator);
        } catch (NoSuchFieldException | IllegalAccessException e) {
          throw new RuntimeException("Failed to inject ReturnUrlValidator", e);
        }
        return (T) validator;
      }
      // Default: instantiate with no-arg constructor
      try {
        return key.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        throw new RuntimeException("Cannot instantiate " + key, e);
      }
    }

    @Override
    public void releaseInstance(jakarta.validation.ConstraintValidator<?, ?> instance) {
      // No-op for test
    }
  }
}

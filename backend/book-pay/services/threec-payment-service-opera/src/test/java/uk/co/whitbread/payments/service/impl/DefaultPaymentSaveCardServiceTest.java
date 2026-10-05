package uk.co.whitbread.payments.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.client.ThreeCPaymentClient;
import uk.co.whitbread.payments.config.RevisedSolutionConfig;
import uk.co.whitbread.payments.properties.ThreeCProperties;
import uk.co.whitbread.payments.repository.PaymentRepository;
import uk.co.whitbread.payments.service.ProviderAccountFactory;
import uk.co.whitbread.payments.service.TemplateService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DefaultPaymentSaveCardServiceTest {

  @InjectMocks
  protected DefaultPaymentService defaultPaymentService;
  @Mock
  protected ProviderAccountFactory providerAccountFactory;
  @Mock
  protected PaymentRepository paymentRepository;
  @Mock
  protected ThreeCPaymentClient threeCPaymentClient;
  @Mock
  protected TemplateService templateService;
  @Mock
  protected ThreeCProperties threeCProperties;
  @Mock
  protected RevisedSolutionConfig revisedSolutionConfig;

  @BeforeEach
  void setUp() {
    defaultPaymentService.noTransactionCode = List.of("711", "752");
  }

  @ParameterizedTest
  @MethodSource("uk.co.whitbread.payments.service.impl.DefaultPaymentSaveCardServiceTestData#saveCard")
  void saveCard(DefaultPaymentSaveCardServiceTestData.SaveCardHandlerTestData testData) {
    //Arrange
    var request = testData.saveCardRequest();
    Optional.ofNullable(testData.mocks())
        .ifPresent(mocks -> mocks.accept(this));
    //Act & Assert
    if (testData.isException()) {
      assertThrows(testData.exceptionType(), () -> defaultPaymentService.saveCard(request).block());
    } else {
      var response = defaultPaymentService.saveCard(request).block();
      Optional.ofNullable(testData.assertions())
          .ifPresent(assertions -> assertions.accept(response));
    }
  }

  @ParameterizedTest
  @MethodSource("uk.co.whitbread.payments.service.impl.DefaultPaymentAuthorizeScaServiceTestData#authorizeSca")
  void authorizeSca(DefaultPaymentAuthorizeScaServiceTestData.AuthorizeScaHandlerTestData testData) {
    //Arrange
    var request = testData.authorizeScaRequest();
    Optional.ofNullable(testData.mocks())
            .ifPresent(mocks -> mocks.accept(this));
    //Act & Assert
    if (testData.isException()) {
      assertThrows(testData.exceptionType(), () -> defaultPaymentService.authorizeSca(request).block());
    } else {
      var response = defaultPaymentService.authorizeSca(request).block();
      Optional.ofNullable(testData.assertions())
              .ifPresent(assertions -> assertions.accept(response));
    }
  }
}
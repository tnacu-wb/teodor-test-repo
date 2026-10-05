package uk.co.whitbread.payments.integration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.payments.Application;
import uk.co.whitbread.payments.mapper.SaveCardMapper;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.feature.FeatureFlag;
import uk.co.whitbread.payments.model.feature.UnleashWrapper;
import uk.co.whitbread.payments.repository.PaymentRepository;
import uk.co.whitbread.payments.service.EMerchantService;
import uk.co.whitbread.payments.util.PaymentIdGenerator;

import java.io.IOException;
import java.time.Duration;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationBaseIT {

    @Getter
    protected static MockWebServer mockWebServer;

    @Getter
    protected ObjectMapper objectMapper;

    @Getter
    protected WebTestClient webTestClient;

    @Getter
    private static final int PORT = 8080;

    protected PaymentRepository paymentRepository;
    protected PaymentRepository paymentRepositoryMock;
    @Autowired
    protected EMerchantService eMerchantService;
    @Autowired
    protected PaymentIdGenerator paymentIdGenerator;
    @Autowired
    protected DynamoDbAsyncTable<PaymentsSchema> paymentsTable;
    @Autowired
    protected SaveCardMapper saveCardMapper;
    @Autowired
    protected UnleashWrapper<FeatureFlag> unleashWrapper;

    @AfterEach
    void afterEach() {
        mockWebServer.close();
    }

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(PORT);

        var jsonMapper = JsonMapper.builder()
                .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .findAndAddModules()
                .build();

        webTestClient = WebTestClient.bindToServer()
                .baseUrl(String.format("http://localhost:%s", mockWebServer.getPort()))
                .responseTimeout(Duration.ofSeconds(10))
                .codecs(configurer -> {
                    configurer.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(jsonMapper));
                    configurer.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(jsonMapper));
                })
                .build();
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        paymentRepository = new PaymentRepository(eMerchantService, paymentIdGenerator, saveCardMapper, paymentsTable, unleashWrapper);
        paymentRepositoryMock = Mockito.spy(paymentRepository);
    }

}
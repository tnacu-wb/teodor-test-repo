package uk.co.whitbread.hotel.account.utils.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.hotel.account.mapper.AddressMapper;
import uk.co.whitbread.hotel.account.mapper.CardNumberMapper;
import uk.co.whitbread.hotel.account.mapper.CustomerMapper;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.hotel.account.model.CustomerRequest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;

@ExtendWith(MockitoExtension.class)
class CdhCustomerTransformerTest {

    private final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);
    private final CardNumberMapper cardNumberMapper = Mappers.getMapper(CardNumberMapper.class);
    private final AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private CdhCustomerTransformer cdhCustomerTransformer;

    @BeforeEach
    void setUp() {
        cdhCustomerTransformer = spy(new CdhCustomerTransformer(customerMapper));
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.findAndRegisterModules();
        ReflectionTestUtils.setField(customerMapper, "cardNumberMapper", cardNumberMapper);
        ReflectionTestUtils.setField(customerMapper, "addressMapper", addressMapper);
    }

    @Test
    void transformCustomerToCdhCustomerAccountRequest() throws IOException {
        CustomerRequest updateCustomerRequest = objectMapper.readValue(
                new File("src/test/resources/mapping/updateCustomer/customerRequestFullUpdate.json"),
                CustomerRequest.class);
        Customer customer = objectMapper.readValue(
                new File("src/test/resources/mapping/getCustomer/CustomerPI.json"), Customer.class);
        CustomerAccountRequest customerAccountRequest = objectMapper.readValue(
                new File("src/test/resources/mapping/cdh/CdhCustomerAccount.json"),
                CustomerAccountRequest.class);

        CustomerAccountRequest result = cdhCustomerTransformer.transformToCustomerAccountRequest(updateCustomerRequest,
                customer);

        assertThat(result).isEqualTo(customerAccountRequest);
    }
}

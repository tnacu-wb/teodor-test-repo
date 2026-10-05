package uk.co.whitbread.hotel.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.account.client.HotelAccountCacheProvider;
import uk.co.whitbread.hotel.account.model.ContactDetail;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.hotel.account.model.PaymentPreference;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class CustomerCacheControllerTestIT {

    private static final String MY_USER_ID = "any.user@email.com";
    private static final String NATIONALITY = "GB";

    @LocalServerPort
    int serverPort;

    @MockitoBean
    private HotelAccountCacheProvider hotelAccountCacheProvider;

  @BeforeEach
    void setUp() {
        RestAssured.port = serverPort;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void getCustomer_ok() {
        when(hotelAccountCacheProvider.getCustomer(any())).thenReturn(buildCustomer());

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                log().everything().
                get("/customers/internal/{customer-id}", MY_USER_ID).
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK);
    }

    private Customer buildCustomer() {
        Customer customer = new Customer();
        customer.setPaymentPreference(new PaymentPreference());

        ContactDetail contactDetail = random(ContactDetail.class);

        contactDetail.setTelephone("07701234567");
        contactDetail.setEmail("user@test.com");
        contactDetail.setMobile("07701234567");
        contactDetail.setNationality(NATIONALITY);

        customer.setContactDetail(contactDetail);

        return customer;
    }

}

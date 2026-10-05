package uk.co.whitbread.hotel.register.service;

import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.register.client.HotelReservationEntityClient;
import uk.co.whitbread.hotel.register.exceptions.CdhServiceException;
import uk.co.whitbread.hotel.register.mapper.CustomerMapper;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.model.CustomerResponse;
import uk.co.whitbread.hotel.register.model.ReservationRequest;
import uk.co.whitbread.hotel.register.properties.CdhProperties;
import uk.co.whitbread.hotel.register.service.auth0.Auth0LeisureService;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.cdh.CustomerDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountResponse;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class CdhPiRegisterServiceTest {

    private static final String DEFAULT_LANGUAGE = "en";
    private static final String CUSTOMER_ACCOUNT_ID = "350d6489-e9f8-4448-98d6-257e850d0165";
    private static final String GUEST_HISTORY_NUMBER = "G123456";
    private static final LocalDate GUEST_HISTORY_CREATED = LocalDate.now();
    private static final String GENERIC_ERROR_MESSAGE = "An error occurred when attempting to register a new customer.";

    @InjectMocks
    private CdhPiRegisterService service;
    @Mock
    private Auth0LeisureService auth0LeisureService;
    @Mock
    private CustomerDataService cdhCustomerService;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private EmailService emailService;
    @Mock
    private CdhProperties cdhProperties;
    @Mock
    private HotelReservationEntityClient hotelReservationEntityClient;

    @Test
    void createPiCustomerInCdhAsync_success() {
        // Given
        var newCustomer = random(Customer.class);
        var customerAccountRequest = new CustomerAccountRequest();

        when(customerMapper.toCdhRequest(newCustomer, GUEST_HISTORY_NUMBER, GUEST_HISTORY_CREATED))
                .thenReturn(customerAccountRequest);

        // When
        service.createPiCustomerInCdhAsync(newCustomer, GUEST_HISTORY_NUMBER, GUEST_HISTORY_CREATED);

        // Then
        verify(cdhCustomerService).createCustomerAccount(customerAccountRequest);
    }

    @Test
    void piRegisterInCdh_success() throws Auth0ApiException {
        // Given
        var newCustomer = random(Customer.class);
        var customerAccountRequest = new CustomerAccountRequest();
        var customerAccountResponse = CustomerAccountResponse.builder()
                .customerAccountId(CUSTOMER_ACCOUNT_ID)
                .build();
        var customerResponse = new CustomerResponse(true, null, CUSTOMER_ACCOUNT_ID, false,
                false);

        when(customerMapper.toCdhRequest(newCustomer)).thenReturn(customerAccountRequest);
        when(cdhCustomerService.createCustomerAccount(customerAccountRequest)).thenReturn(
                customerAccountResponse);
        when(customerMapper.toCustomerResponse(newCustomer.getContactDetail().getEmail())).thenReturn(customerResponse);

        // When
        var response = service.piRegisterInCdh(newCustomer, DEFAULT_LANGUAGE);

        // Then
        assertEquals(customerResponse, response);
        verify(auth0LeisureService).saveUserInAuth0(newCustomer.getContactDetail().getEmail(),
                newCustomer.getPassword(), null);
        verify(emailService).sendAsyncPiRegisterEmail(newCustomer, DEFAULT_LANGUAGE);
    }

    @Test
    void piRegisterInCdh_throwsCdhServiceException() {
        // Given
        var newCustomer = random(Customer.class);
        var customerAccountRequest = new CustomerAccountRequest();

        when(customerMapper.toCdhRequest(newCustomer)).thenReturn(customerAccountRequest);
        when(cdhCustomerService.createCustomerAccount(customerAccountRequest)).thenThrow(
                new CDHException());

        assertThrows(CdhServiceException.class, () -> service.piRegisterInCdh(newCustomer, DEFAULT_LANGUAGE), GENERIC_ERROR_MESSAGE);

        // Then
        verify(emailService, times(0)).sendAsyncPiRegisterEmail(newCustomer, DEFAULT_LANGUAGE);
    }

    @Test
    void piRegisterInCdh_throwsAuth0Exception() throws Auth0ApiException {
        // Given
        var newCustomer = random(Customer.class);
        var email = newCustomer.getContactDetail().getEmail();

        doThrow(new Auth0ApiException(GENERIC_ERROR_MESSAGE, null)).when(auth0LeisureService)
                .saveUserInAuth0(email, newCustomer.getPassword(), null);

        assertThrows(AuthServiceException.class, () -> service.piRegisterInCdh(newCustomer, DEFAULT_LANGUAGE));

        // Then
        verify(cdhCustomerService, times(0)).createCustomerAccount(any(CustomerAccountRequest.class));
        verify(emailService, times(0)).sendAsyncPiRegisterEmail(newCustomer, DEFAULT_LANGUAGE);
    }
    
    @Nested
    class UpdateOperaReservationTests {

        private CustomerAccountResponse customerAccountResponse;
        private Customer newCustomer;

        @BeforeEach
        public void setUp() {
            customerAccountResponse = new CustomerAccountResponse();
            newCustomer = random(Customer.class);
        }

        @Test
        void updateOperaReservation_success() {
            // Arrange
            customerAccountResponse.setCustomerAccountId("CUST_20302bb9-a1d1-4bb7-af56-f3ee97070276");
            newCustomer.setBasketReference("AKU-b94ba40e-b46e-4417-85d0-526c9e1b698e");

            // Act
            service.updateOperaReservation(customerAccountResponse, newCustomer);

            // Assert
            verify(hotelReservationEntityClient).linkLeisureCustomer(any(ReservationRequest.class));
        }

        @Test
        void updateOperaReservation_noCustomerAccountId() {
            // Arrange
            customerAccountResponse.setCustomerAccountId(null);
            newCustomer.setBasketReference("AKU-b94ba40e-b46e-4417-85d0-526c9e1b698e");

            // Act
            service.updateOperaReservation(customerAccountResponse, newCustomer);

            // Assert
            verify(hotelReservationEntityClient, times(0)).linkLeisureCustomer(any(ReservationRequest.class));
        }

        @Test
        void updateOperaReservation_noBasketReference() {
            // Arrange
            customerAccountResponse.setCustomerAccountId("CUST_20302bb9-a1d1-4bb7-af56-f3ee97070276");
            newCustomer.setBasketReference(null);

            // Act
            service.updateOperaReservation(customerAccountResponse, newCustomer);

            // Assert
            verify(hotelReservationEntityClient, times(0)).linkLeisureCustomer(any(ReservationRequest.class));
        }

        @Test
        void updateOperaReservation_throwsCdhServiceException() {
            // Arrange
            customerAccountResponse.setCustomerAccountId("CUST_20302bb9-a1d1-4bb7-af56-f3ee97070276");
            newCustomer.setBasketReference("AKU-b94ba40e-b46e-4417-85d0-526c9e1b698e");
            doThrow(new CdhServiceException("Failed to link the leisure customer due to an unknown error"))
                    .when(hotelReservationEntityClient).linkLeisureCustomer(any(ReservationRequest.class));

            // Act & Assert
            assertThrows(CdhServiceException.class, () -> service.updateOperaReservation(customerAccountResponse, newCustomer));
        }
    }
}

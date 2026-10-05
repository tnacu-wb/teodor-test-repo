package uk.co.whitbread.hotel.register.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import uk.co.whitbread.hotel.register.mapper.AccountMapper;
import uk.co.whitbread.hotel.register.model.*;
import uk.co.whitbread.hotel.register.properties.CdhProperties;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HotelRegisterServiceTest {

    private static final String DEFAULT_LANGUAGE = "en";
    private static final String EMAIL = "test@mail.com";

    @InjectMocks
    private HotelRegisterService service;

    @Mock
    private CdhProperties cdhProperties;

    @Mock
    private CdhPiRegisterService cdhPiRegisterService;

    @Mock
    private CdhBbRegisterService cdhBbRegisterService;

    @Mock
    private MarketingService marketingService;

    @Mock
    private AccountMapper accountMapper;

    @Test
    public void createCustomer_piDataFetchEnabled_success() {
        // Given
        Customer newCustomer = random(Customer.class);
        CustomerResponse customerResponse = new CustomerResponse(true, null, "12345", false, false);

        when(cdhProperties.isEnablePiDataFetch()).thenReturn(true);
        when(cdhPiRegisterService.piRegisterInCdh(newCustomer, DEFAULT_LANGUAGE)).thenReturn(
            customerResponse);

        // When
        CustomerResponse response = service.createCustomer(newCustomer, DEFAULT_LANGUAGE, false);

        // Then
        assertEquals(customerResponse, response);
    }

    @Test
    public void createCustomer_bbDataFetchEnabled_success() {
        // Given
        var newCustomer = random(Customer.class);
        var customerResponse = new CustomerResponse(true, null, EMAIL, false, false);

        when(cdhProperties.isEnableBbDataFetch()).thenReturn(true);
        when(cdhBbRegisterService.bbRegisterInCdh(newCustomer, DEFAULT_LANGUAGE)).thenReturn(
            customerResponse);

        // When
        var response = service.createCustomer(newCustomer, DEFAULT_LANGUAGE, true);

        // Then
        assertEquals(customerResponse, response);
    }

    @Test
    void registerInnBStepOne_shouldCallMarketingService_whenNotExistingEmployee() {
        // Given
        var request = random(InnBRegistrationStepOneRequest.class);
        var innBResponse = InnBRegistrationStepOneResponse.builder()
            .existingCompany(false)
            .existingEmployee(false)
            .existingCompanyType(null)
            .build();

        when(cdhBbRegisterService.registerInnBStepOneInCdh(request)).thenReturn(innBResponse);

        // When
        var response = service.registerInnBStepOne(request);

        // Then
        assertEquals(innBResponse, response);
        verify(cdhBbRegisterService).registerInnBStepOneInCdh(request);
        verify(marketingService).updateMarketingOptIn(request.getUpdatePreferencesRequest(),
            request.getEmail());
    }

    @Test
    void registerInnBStepOne_shouldNotCallMarketingService_whenExistingEmployee() {
        // Given
        var request = random(InnBRegistrationStepOneRequest.class);
        var innBResponse = InnBRegistrationStepOneResponse.builder()
            .existingCompany(false)
            .existingEmployee(true)
            .existingCompanyType(null)
            .build();

        when(cdhBbRegisterService.registerInnBStepOneInCdh(request)).thenReturn(innBResponse);

        // When
        var response = service.registerInnBStepOne(request);

        // Then
        assertEquals(innBResponse, response);
        verify(cdhBbRegisterService).registerInnBStepOneInCdh(request);
        verifyNoInteractions(marketingService);
    }

    @Test
    void registerInnBStepTwo_shouldCallMarketingService() {
        // Given
        var request = random(InnBRegistrationStepTwoRequest.class);
        var expectedResponse = random(InnBRegistrationStepTwoResponse.class);

        when(cdhBbRegisterService.registerInnBStepTwoInCdh(request)).thenReturn(expectedResponse);

        // When
        var response = service.registerInnBStepTwo(request);

        // Then
        assertEquals(expectedResponse, response);
        verify(cdhBbRegisterService).registerInnBStepTwoInCdh(request);
        verify(marketingService).updateMarketingOptIn(request.getUpdatePreferencesRequest(), response.getEmail());
    }

    @Test
    void registerAccount_success() {
        // Given
        var request = random(AppsCustomer.class);
        var customer = random(Customer.class);
        var expectedResponse = random(CustomerResponse.class);
        when(accountMapper.toAccountRequest(request)).thenReturn(customer);
        when(cdhPiRegisterService.piRegisterInCdh(customer, DEFAULT_LANGUAGE)).thenReturn(
            expectedResponse);

        // When
        var response = service.registerAccount(request, DEFAULT_LANGUAGE);

        // Then
        assertEquals(expectedResponse, response);
        verify(accountMapper).toAccountRequest(request);
        verify(cdhPiRegisterService).piRegisterInCdh(customer, DEFAULT_LANGUAGE);
    }

}

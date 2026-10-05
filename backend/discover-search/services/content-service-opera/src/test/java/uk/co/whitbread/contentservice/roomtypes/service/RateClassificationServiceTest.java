package uk.co.whitbread.contentservice.roomtypes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.contentservice.roomtypes.model.BrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.LanguageCode;
import uk.co.whitbread.contentservice.roomtypes.model.RateClassification;
import uk.co.whitbread.contentservice.roomtypes.model.RateClassificationResponse;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRateClassifications;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RateDescription;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RateLongDescription;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RateName;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RateNotes;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RateOrder;
import uk.co.whitbread.contentservice.roomtypes.service.client.AEMRateClassificationsService;
import uk.co.whitbread.contentservice.roomtypes.util.AEMResponseConverter;

@ExtendWith(MockitoExtension.class)
public class RateClassificationServiceTest {

    @InjectMocks
    private RateClassificationService serviceUnderTest;

    @Mock
    private AEMRateClassificationsService aemRateClassificationsService;

    @Mock
    private AEMResponseConverter aemResponseConverter;


    @Test
    public void shouldGetAllRateClassificationsWithEmptyRatesFromAem() {

        String hotelCode = "LONCUT";

        List<AEMRateClassifications> aemRateClassifications = new ArrayList<>();
        when(aemRateClassificationsService.getRateClassifications(LanguageCode.en.name(), BrandCode.pi.name(), hotelCode))
                .thenReturn(aemRateClassifications);

        List<RateClassification> rateClassifications = new ArrayList<>();
        when(aemResponseConverter.convertAEMResponseRates(aemRateClassifications))
                .thenReturn(rateClassifications);

        RateClassificationResponse expectedResponse = new RateClassificationResponse();
        expectedResponse.setRateClassifications(rateClassifications);

        final RateClassificationResponse rateClassificationResponse = serviceUnderTest.getAllRateClassifications(LanguageCode.en.name(), BrandCode.pi.name(), hotelCode);

        assertThat(rateClassificationResponse.getRateClassifications().size()).isZero();
        assertEquals(expectedResponse, rateClassificationResponse );
    }

    @Test
    public void shouldGetAllRateClassificationsWithNoEmptyRatesFromAem() {

        String hotelCode = "LONCUT";

        List<AEMRateClassifications> aemRateClassifications = new ArrayList<>();
        aemRateClassifications.add(createAemRateClassification());
        when(aemRateClassificationsService.getRateClassifications(LanguageCode.en.name(), BrandCode.pi.name(), hotelCode))
                .thenReturn(aemRateClassifications);

        List<RateClassification> rateClassifications = new ArrayList<>();
        rateClassifications.add(createRateClassification());
        when(aemResponseConverter.convertAEMResponseRates(aemRateClassifications))
                .thenReturn(rateClassifications);

        RateClassificationResponse expectedResponse = new RateClassificationResponse();
        expectedResponse.setRateClassifications(rateClassifications);

        final RateClassificationResponse rateClassificationResponse = serviceUnderTest.getAllRateClassifications(LanguageCode.en.name(), BrandCode.pi.name(), hotelCode);

        assertThat(rateClassificationResponse.getRateClassifications().size()).isNotZero();
        assertEquals(expectedResponse.getRateClassifications().get(0), rateClassificationResponse.getRateClassifications().get(0) );
    }

    private AEMRateClassifications createAemRateClassification() {
        return AEMRateClassifications.builder()
                .rateClassification(uk.co.whitbread.contentservice.roomtypes.model.aem.RateClassification.builder().value("R").build())
                .rateOrder(RateOrder.builder().value("Non-Flex").build())
                .rateName(RateName.builder().value("A").build())
                .rateDescription(RateDescription.builder().value("Jetzt bezahlen. Keine Änderungen möglich").build())
                .rateLongDescription(RateLongDescription.builder().value("").build())
                .rateNotes(RateNotes.builder().value("5").build())
                .build();
    }

    private RateClassification createRateClassification() {
         return RateClassification.builder()
                 .rateClassification( "R" )
                 .rateOrder("Non-Flex")
                 .rateName("A")
                 .rateDescription("Jetzt bezahlen. Keine Änderungen möglich")
                 .rateLongDescription("")
                 .rateNotes("5")
                 .build();
    }
}

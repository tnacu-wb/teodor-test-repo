package uk.co.whitbread.payments.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.model.Booking;
import uk.co.whitbread.payments.model.BusinessSite;
import uk.co.whitbread.payments.model.ChannelType;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RevisedSolutionConfigTest {

    @InjectMocks
    private RevisedSolutionConfig revisedSolutionConfig;

    @Test
    void verifyReturnsFalseIfFeaturedisabled() {

        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .sessionId("test_sessionId")
                .booking(Booking.builder()
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .build();

        revisedSolutionConfig.setFeatureEnabled(false);

        var result = revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema);
        assertEquals(false,result);
    }

    @Test
    void verifyReturnsFalseIfMobileFeatureDisabled() {

        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .sessionId("test_sessionId")
                .booking(Booking.builder()
                        .channel(ChannelType.APPS_ANDROID.name())
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .build();
        revisedSolutionConfig.setFeatureEnabled(true);
        revisedSolutionConfig.setMobileAppEnabled(false);
        List<String> hotelList = Stream.of("LKEBAR", "HAMHOF", "Orange").collect(Collectors.toList());
        revisedSolutionConfig.setHotelList(hotelList);

        var result = revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema);
        assertEquals(false,result);
    }

    @Test
    void verifyReturnsTrueIfAndroidAppsEnabled() {

        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .sessionId("test_sessionId")
                .booking(Booking.builder()
                        .channel(ChannelType.APPS_ANDROID.name())
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .build();
        revisedSolutionConfig.setFeatureEnabled(true);
        revisedSolutionConfig.setAndroidAppEnabled(true);
        List<String> hotelList = Stream.of("LKEBAR", "HAMHOF", "Orange").collect(Collectors.toList());
        revisedSolutionConfig.setHotelList(hotelList);

        var result = revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema);
        assertEquals(true,result);
    }

    @Test
    void verifyReturnsFalseIfIosAppsEnabled() {

        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .sessionId("test_sessionId")
                .booking(Booking.builder()
                        .channel(ChannelType.APPS_IOS.name())
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .build();
        revisedSolutionConfig.setFeatureEnabled(true);
        revisedSolutionConfig.setIosAppEnabled(false);
        List<String> hotelList = Stream.of("LKEBAR", "HAMHOF", "Orange").collect(Collectors.toList());
        revisedSolutionConfig.setHotelList(hotelList);

        var result = revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema);
        assertEquals(false,result);
    }

    @Test
    void verifyReturnsFalseIfChannelIsCcc() {

        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .sessionId("test_sessionId")
                .booking(Booking.builder()
                        .channel(ChannelType.CCC.name())
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .build();

        var result = revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema);
        assertEquals(false,result);
    }

    @Test
    void verifyReturnsTrueIfChannelIsPIAndFeatureEnabled() {

        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .sessionId("test_sessionId")
                .booking(Booking.builder()
                        .channel(ChannelType.PI.name())
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .build();
        revisedSolutionConfig.setFeatureEnabled(true);
        revisedSolutionConfig.setMobileAppEnabled(false);
        List<String> hotelList = Stream.of("LKEBAR", "HAMHOF", "Orange").collect(Collectors.toList());
        revisedSolutionConfig.setHotelList(hotelList);

        var result = revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema);
        assertEquals(true,result);
    }

    @Test
    void verifyReturnsFalseIfHotelIsIncludedInList() {

        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .sessionId("test_sessionId")
                .booking(Booking.builder()
                        .channel(ChannelType.PI.name())
                        .businessSite(BusinessSite.builder()
                                .identifier("LKEBAR")
                                .build())
                        .build())
                .build();
        revisedSolutionConfig.setFeatureEnabled(true);
        revisedSolutionConfig.setMobileAppEnabled(false);
        List<String> hotelList = Stream.of("LKEBAR", "HAMHOF", "Orange").collect(Collectors.toList());
        revisedSolutionConfig.setHotelList(hotelList);

        var result = revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema);
        assertEquals(false,result);
    }
}

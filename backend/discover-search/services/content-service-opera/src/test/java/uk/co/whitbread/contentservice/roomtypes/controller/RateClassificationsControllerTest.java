package uk.co.whitbread.contentservice.roomtypes.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.contentservice.roomtypes.model.*;
import uk.co.whitbread.contentservice.roomtypes.service.RateClassificationService;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RateClassificationsControllerTest {

    @InjectMocks
    private RateClassificationsController controller;

    @Mock
    private RateClassificationService rateClassificationService;

    @Test
    public void getRateClassificationSuccessResponse() {

        RateClassification rateClassification_Q = RateClassification.builder()
                .rateClassification("Q")
                .rateOrder("0")
                .rateName("Smei-Flex")
                .rateDescription("Pay now. Change arrival date. Cancel up to 3 days before arrival.")
                .rateLongDescription("In general, if you repay the price of what you bought within the delay period you won’t pay any interest. That’s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won’t charge you any interest if you clear your balance before your delay period is up – even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.")
                .rateNotes("Lorem ipsum dolor sit amet")
                .build();

        RateClassification rateClassification_F = RateClassification.builder()
                .rateClassification("F")
                .rateOrder("1")
                .rateName("Advance")
                .rateDescription("Pay now. Change arrival date. Cancel up to 28 days before arrival.")
                .rateLongDescription("In general, if you repay the price of what you bought within the delay period you won’t pay any interest. That’s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won’t charge you any interest if you clear your balance before your delay period is up – even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.")
                .rateNotes("Lorem ipsum dolor sit amet")
                .build();

        RateClassificationResponse rateClassificationResponse = new RateClassificationResponse();
        List<RateClassification> rateClassificationList = new ArrayList<>();
        rateClassificationList.add(rateClassification_Q);
        rateClassificationList.add(rateClassification_F);
        rateClassificationResponse.setRateClassifications(rateClassificationList);
        when(rateClassificationService.getAllRateClassifications("en", "pi", ""))
        .thenReturn(rateClassificationResponse);


        final RateClassificationResponse rateClassifications = controller.getAllRateClassifications("", LanguageCode.en, BrandCode.pi);
        assertThat(rateClassifications.getRateClassifications().get(0).getRateClassification().equalsIgnoreCase("Q")).isTrue();
        assertThat(rateClassifications.getRateClassifications().get(0).getRateClassification().equalsIgnoreCase("F"));
    }

    @Test
    public void getRateClassificationsForSpecificCodeSuccessResponse() {

        RateClassification rateClassification_Q = RateClassification.builder()
                .rateClassification("Q")
                .rateOrder("0")
                .rateName("Smei-Flex")
                .rateDescription("Pay now. Change arrival date. Cancel up to 3 days before arrival.")
                .rateLongDescription("In general, if you repay the price of what you bought within the delay period you won’t pay any interest. That’s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won’t charge you any interest if you clear your balance before your delay period is up – even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.")
                .rateNotes("Lorem ipsum dolor sit amet")
                .build();

        RateClassificationResponse rateClassificationResponse = new RateClassificationResponse();
        List<RateClassification> rateClassificationList = new ArrayList<>();
        rateClassificationList.add(rateClassification_Q);
        rateClassificationResponse.setRateClassifications(rateClassificationList);
        when(rateClassificationService.getRateClassificationsForRate("en", "pi", "", "Q"))
                .thenReturn(rateClassificationResponse);

        final RateClassificationResponse rateClassifications = controller.getRateClassificationForRate("", LanguageCode.en, BrandCode.pi, "Q");
        assertThat(rateClassifications.getRateClassifications().get(0).getRateClassification().equalsIgnoreCase("Q")).isTrue();
        assertThat(rateClassifications.getRateClassifications().get(0).getRateOrder().equalsIgnoreCase("0")).isTrue();
        assertThat(rateClassifications.getRateClassifications().get(0).getRateDescription().equalsIgnoreCase("Pay now. Change arrival date. Cancel up to 3 days before arrival.")).isTrue();
        assertThat(rateClassifications.getRateClassifications().get(0).getRateLongDescription().equalsIgnoreCase("In general, if you repay the price of what you bought within the delay period you won’t pay any interest. That’s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won’t charge you any interest if you clear your balance before your delay period is up – even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.")).isTrue();
        assertThat(rateClassifications.getRateClassifications().get(0).getRateNotes().equalsIgnoreCase("Lorem ipsum dolor sit amet")).isTrue();
    }

    @Test
    public void getNotAvailableRateClassification() {

        RateClassificationResponse rateClassificationResponse = new RateClassificationResponse();
        List<RateClassification> rateClassificationList = new ArrayList<>();
        rateClassificationResponse.setRateClassifications(rateClassificationList);
        when(rateClassificationService.getRateClassificationsForRate("en", "pi", "", "ZZZ"))
                .thenReturn(rateClassificationResponse);

        final RateClassificationResponse rateClassifications = controller.getRateClassificationForRate("", LanguageCode.en, BrandCode.pi, "ZZZ");
        assertThat(rateClassifications.getRateClassifications().isEmpty()).isTrue();

    }
}

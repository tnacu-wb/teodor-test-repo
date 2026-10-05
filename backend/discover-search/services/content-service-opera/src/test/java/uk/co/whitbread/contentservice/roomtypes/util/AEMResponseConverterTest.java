package uk.co.whitbread.contentservice.roomtypes.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.contentservice.roomtypes.model.BookingNotifications;
import uk.co.whitbread.contentservice.roomtypes.model.BrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.CookiePolicies;
import uk.co.whitbread.contentservice.roomtypes.model.Facility;
import uk.co.whitbread.contentservice.roomtypes.model.RateClassification;
import uk.co.whitbread.contentservice.roomtypes.model.RoomType;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMBookingNotifications;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMCookiePolicies;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRateClassifications;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRoomType;
import uk.co.whitbread.contentservice.roomtypes.model.aem.BookingInfoMessage;
import uk.co.whitbread.contentservice.roomtypes.model.aem.Facilities;
import uk.co.whitbread.contentservice.roomtypes.model.aem.GridImage;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RateDescription;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RateLongDescription;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RateName;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RateNotes;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RateOrder;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomCategory;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomDescription;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomImage;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomInfo;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomInfoLabel;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomLabel;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RoomTypeCode;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.*;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.manageview.cookiegroup.CookieName;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.manageview.cookiegroup.Description;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.manageview.cookiegroup.IsAlwaysActive;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.manageview.cookiegroup.Title;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.manageview.cookiegroup.ToggleLabel;

@ExtendWith(MockitoExtension.class)
public class AEMResponseConverterTest {

    @InjectMocks
    private AEMResponseConverter util;

    private AEMRoomType aemRoomType_SB;

    private AEMRoomType aemRoomType_PB;

    private RoomType roomType_SB;

    private RoomType roomType_PB;

    private AEMRateClassifications aemRateClassification_Q;

    private AEMRateClassifications aemRateClassification_F;

    private RateClassification rateClassification_Q;

    private RateClassification rateClassification_F;


    @BeforeEach
    public void setup() {
        aemRoomType_SB = AEMRoomType.builder()
                .roomTypeCode(RoomTypeCode.builder().value("SB").build())
                .roomCategory(RoomCategory.builder().value("Standard").build())
                .roomLabel(RoomLabel.builder().value("Standard Room").build())
                .roomDescription(RoomDescription.builder().value("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.").build())
                .roomInfoLabel(RoomInfoLabel.builder().value("Room Information").build())
                .roomInfo(RoomInfo.builder().value("This hotel is being refurbished, we're sorry for any inconvenience.").build())
                .roomImage(RoomImage.builder().value(null).build())
                .gridImage(GridImage.builder().value(null).build())
                .facilities(Facilities.builder().value(Collections.emptyList()).build())
                .build();

        aemRoomType_PB = AEMRoomType.builder()
                .roomTypeCode(RoomTypeCode.builder().value("PB").build())
                .roomCategory(RoomCategory.builder().value("Business").build())
                .roomLabel(RoomLabel.builder().value("Business Room").build())
                .roomDescription(RoomDescription.builder().value("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.").build())
                .roomInfoLabel(RoomInfoLabel.builder().value("Room Information").build())
                .roomInfo(RoomInfo.builder().value(null).build())
                .roomImage(RoomImage.builder().value(null).build())
                .gridImage(GridImage.builder().value(null).build())
                .facilities(Facilities.builder().value(Collections.emptyList()).build())
                .build();

        roomType_SB = RoomType.builder()
                .roomTypeCode("SB")
                .roomCategory("Standard")
                .roomLabel("Standard Room")
                .roomDescription("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.")
                .roomInfoLabel("Room Information")
                .roomInfo("This hotel is being refurbished, we're sorry for any inconvenience.")
                .roomImage(null)
                .gridImage(null)
                .facilities(null)
                .build();

        roomType_PB = RoomType.builder()
                .roomTypeCode("PB")
                .roomCategory("Business")
                .roomLabel("Business Room")
                .roomDescription("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.")
                .roomInfoLabel("Room Information")
                .roomInfo(null)
                .roomImage(null)
                .gridImage(null)
                .facilities(null)
                .build();


        aemRateClassification_F = AEMRateClassifications.builder()
                .rateClassification(uk.co.whitbread.contentservice.roomtypes.model.aem.RateClassification.builder().value("F").build())
                .rateOrder(RateOrder.builder().value("1").build())
                .rateName(RateName.builder().value("Advance").build())
                .rateDescription(RateDescription.builder().value("Pay now. Change arrival date. Cancel up to 28 days before arrival.").build())
                .rateLongDescription(RateLongDescription.builder().value("In general, if you repay the price of what you bought within the delay period you won’t pay any interest. That’s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won’t charge you any interest if you clear your balance before your delay period is up – even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.").build())
                .rateNotes(RateNotes.builder().value("Lorem ipsum dolor sit amet...").build())
                .build();

        aemRateClassification_Q = AEMRateClassifications.builder()
                .rateClassification(uk.co.whitbread.contentservice.roomtypes.model.aem.RateClassification.builder().value("Q").build())
                .rateOrder(RateOrder.builder().value("0").build())
                .rateName(RateName.builder().value("Smei-Flex").build())
                .rateDescription(RateDescription.builder().value("Pay now. Change arrival date. Cancel up to 3 days before arrival.").build())
                .rateLongDescription(RateLongDescription.builder().value("In general, if you repay the price of what you bought within the delay period you won’t pay any interest. That’s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won’t charge you any interest if you clear your balance before your delay period is up – even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.").build())
                .rateNotes(RateNotes.builder().value("Lorem ipsum dolor sit amet").build())
                .build();

        rateClassification_Q = RateClassification.builder()
                .rateClassification("Q")
                .rateOrder("0")
                .rateName("Smei-Flex")
                .rateDescription("Pay now. Change arrival date. Cancel up to 3 days before arrival.")
                .rateLongDescription("In general, if you repay the price of what you bought within the delay period you won’t pay any interest. That’s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won’t charge you any interest if you clear your balance before your delay period is up – even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.")
                .rateNotes("Lorem ipsum dolor sit amet")
                .build();

        rateClassification_F = RateClassification.builder()
                .rateClassification("F")
                .rateOrder("1")
                .rateName("Advance")
                .rateDescription("Pay now. Change arrival date. Cancel up to 28 days before arrival.")
                .rateLongDescription("In general, if you repay the price of what you bought within the delay period you won’t pay any interest. That’s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won’t charge you any interest if you clear your balance before your delay period is up – even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.")
                .rateNotes("Lorem ipsum dolor sit amet...")
                .build();
    }

    @Test
    public void convertAEMResponseSuccess() {

        List<AEMRoomType> aemRoomTypeList = new ArrayList<>();
        aemRoomTypeList.add(aemRoomType_SB);
        aemRoomTypeList.add(aemRoomType_PB);

        final List<RoomType> roomTypes = util.convertAEMResponse(aemRoomTypeList);
        assertThat(roomTypes.size()).isEqualTo(2);
        assertThat(roomTypes.get(0).getRoomTypeCode()).isEqualTo("SB");
        assertThat(roomTypes.get(0).getRoomCategory()).isEqualTo("Standard");
        assertThat(roomTypes.get(0).getRoomLabel()).isEqualTo("Standard Room");
        assertThat(roomTypes.get(0).getRoomDescription()).isEqualTo("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.");
        assertThat(roomTypes.get(0).getRoomInfoLabel()).isEqualTo("Room Information");
        assertThat(roomTypes.get(0).getRoomInfo()).isEqualTo("This hotel is being refurbished, we're sorry for any inconvenience.");
        assertThat(roomTypes.get(0).getGridImage()).isNull();
        assertThat(roomTypes.get(0).getRoomImage()).isNull();
        assertThat(roomTypes.get(0).getFacilities()).isEmpty();
        assertThat(roomTypes.get(1).getRoomTypeCode()).isEqualTo("PB");
        assertThat(roomTypes.get(1).getRoomCategory()).isEqualTo("Business");
        assertThat(roomTypes.get(1).getRoomLabel()).isEqualTo("Business Room");
        assertThat(roomTypes.get(1).getRoomDescription()).isEqualTo("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.");
        assertThat(roomTypes.get(1).getRoomInfoLabel()).isEqualTo("Room Information");
        assertThat(roomTypes.get(1).getRoomInfo()).isNull();
        assertThat(roomTypes.get(1).getGridImage()).isNull();
        assertThat(roomTypes.get(1).getRoomImage()).isNull();
        assertThat(roomTypes.get(1).getFacilities()).isEmpty();
    }

    @Test
    public void getRoomTypesForCodeSuccess() {

        List<AEMRoomType> aemRoomTypeList = new ArrayList<>();
        aemRoomTypeList.add(aemRoomType_SB);
        aemRoomTypeList.add(aemRoomType_PB);

        final List<RoomType> roomTypes = util.getRoomTypesForCode(aemRoomTypeList, "sb");
        assertThat(roomTypes.size()).isEqualTo(1);
        assertThat(roomTypes.get(0).getRoomTypeCode()).isEqualTo("SB");
        assertThat(roomTypes.get(0).getRoomCategory()).isEqualTo("Standard");
        assertThat(roomTypes.get(0).getRoomLabel()).isEqualTo("Standard Room");
        assertThat(roomTypes.get(0).getRoomDescription()).isEqualTo("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.");
        assertThat(roomTypes.get(0).getRoomInfoLabel()).isEqualTo("Room Information");
        assertThat(roomTypes.get(0).getRoomInfo()).isEqualTo("This hotel is being refurbished, we're sorry for any inconvenience.");
        assertThat(roomTypes.get(0).getGridImage()).isNull();
        assertThat(roomTypes.get(0).getRoomImage()).isNull();
        assertThat(roomTypes.get(0).getFacilities()).isEmpty();
    }

    @Test
    public void splitFacilitiesSuccess() {

        List<String> facilitiesList = new ArrayList<>();
        facilitiesList.add("Comfort:A kingsize Hypnos bed with a cosy duvet and choice of pillows");
        facilitiesList.add("Convenience:Tea & coffee making facilities and a power shower");

        final List<Facility> facility = util.splitFacilities(facilitiesList);
        assertThat(facility.get(0).getFacilityTitle().equalsIgnoreCase("Comfort")).isTrue();
        assertThat(facility.get(1).getFacilityTitle().equalsIgnoreCase("Convenience")).isTrue();
        assertThat(facility.get(0).getFacilityDescription().equalsIgnoreCase("A kingsize Hypnos bed with a cosy duvet and choice of pillows")).isTrue();
        assertThat(facility.get(1).getFacilityDescription().equalsIgnoreCase("Tea & coffee making facilities and a power shower")).isTrue();

    }


    @Test
    public void convertAEMRatesResponseSuccess() {
        List<AEMRateClassifications> aemRateClassificationsList = new ArrayList<>();
        aemRateClassificationsList.add(aemRateClassification_F);
        aemRateClassificationsList.add(aemRateClassification_Q);

        //check that the order is correct and even though added first F is the second object
        final List<RateClassification> rateClassifications = util.convertAEMResponseRates(aemRateClassificationsList);
        assertThat(rateClassifications.size()).isEqualTo(2);
        assertThat(rateClassifications.get(0).getRateClassification()).isEqualTo("Q");
        assertThat(rateClassifications.get(0).getRateOrder()).isEqualTo("0");
        assertThat(rateClassifications.get(0).getRateName()).isEqualTo("Smei-Flex");
        assertThat(rateClassifications.get(0).getRateDescription()).isEqualTo("Pay now. Change arrival date. Cancel up to 3 days before arrival.");
        assertThat(rateClassifications.get(0).getRateLongDescription()).isEqualTo("In general, if you repay the price of what you bought within the delay period you won’t pay any interest. That’s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won’t charge you any interest if you clear your balance before your delay period is up – even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.");
        assertThat(rateClassifications.get(0).getRateNotes()).isEqualTo("Lorem ipsum dolor sit amet");
        assertThat(rateClassifications.get(1).getRateClassification()).isEqualTo("F");
        assertThat(rateClassifications.get(1).getRateOrder()).isEqualTo("1");
        assertThat(rateClassifications.get(1).getRateName()).isEqualTo("Advance");
        assertThat(rateClassifications.get(1).getRateDescription()).isEqualTo("Pay now. Change arrival date. Cancel up to 28 days before arrival.");
        assertThat(rateClassifications.get(1).getRateLongDescription()).isEqualTo("In general, if you repay the price of what you bought within the delay period you won’t pay any interest. That’s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won’t charge you any interest if you clear your balance before your delay period is up – even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.");
        assertThat(rateClassifications.get(1).getRateNotes()).isEqualTo("Lorem ipsum dolor sit amet...");
    }

    @Test
    public void getRatesClassificationsForRateSuccess() {

        List<AEMRateClassifications> aemRateClassificationsList = new ArrayList<>();
        aemRateClassificationsList.add(aemRateClassification_F);
        aemRateClassificationsList.add(aemRateClassification_Q);

        final List<RateClassification> rateClassifications = util.getRateClassificationsForRate(aemRateClassificationsList, "Q");
        assertThat(rateClassifications.size()).isEqualTo(1);
        assertThat(rateClassifications.get(0).getRateClassification()).isEqualTo("Q");
        assertThat(rateClassifications.get(0).getRateOrder()).isEqualTo("0");
        assertThat(rateClassifications.get(0).getRateName()).isEqualTo("Smei-Flex");
        assertThat(rateClassifications.get(0).getRateDescription()).isEqualTo("Pay now. Change arrival date. Cancel up to 3 days before arrival.");
        assertThat(rateClassifications.get(0).getRateLongDescription()).isEqualTo("In general, if you repay the price of what you bought within the delay period you won’t pay any interest. That’s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won’t charge you any interest if you clear your balance before your delay period is up – even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.");
        assertThat(rateClassifications.get(0).getRateNotes()).isEqualTo("Lorem ipsum dolor sit amet");
    }

    @Test
    public void convertAEMCookiePoliciesSuccess() {
        AEMCookiePolicies aemCookiePolicies1 = AEMCookiePolicies.builder()
                .version(new Version("1"))
                .brand(new Brand(BrandCode.pi.name()))
                .cookieOptInExpiryDays(new CookieOptInExpiryDays(365))
                .cookieOptOutExpiryDays(new CookieOptOutExpiryDays(365))
                .introViewTitle(new IntroViewTitle("Cookie Intro Title"))
                .introViewDescription(new IntroViewDescription("<p>Intro description</p>"))
                .introViewManageButtonText(new IntroViewManageButtonText("Manage Cookies"))
                .introViewAcceptAllButtonText(new IntroViewAcceptAllButtonText("Accept all Cookies"))
                .introViewNecessaryOnlyButtonText(new IntroViewNecessaryOnlyButtonText("Necessary Only"))
                .manageViewTitle(new ManageViewTitle("Manage Title"))
                .manageViewDescription(new ManageViewDescription("<p>Manage description</p>"))
                .manageViewSaveSettingsButtonText(new ManageViewSaveSettingsButtonText("Manage Saving Button"))
                .manageViewAlwaysActiveText(new ManageViewAlwaysActiveText("Manage View Active Test")).build();
        AEMCookiePolicies aemCookiePolicies2 = AEMCookiePolicies.builder()
                .cookieName(new CookieName("dtm.adobe.functional"))
                .title(new Title("This is a new title for functional cookie"))
                .description(new Description("This is a new description for functional cookie"))
                .isAlwaysActive(new IsAlwaysActive("true"))
                .toggleLabel(new ToggleLabel("Toggle Label for functional")).build();
        AEMCookiePolicies aemCookiePolicies3 = AEMCookiePolicies.builder()
                .cookieName(new CookieName("dtm.adobe.analytics"))
                .title(new Title("This is a new title for analytics"))
                .description(new Description("This is a new description for analytics"))
                .isAlwaysActive(new IsAlwaysActive("false"))
                .toggleLabel(new ToggleLabel("Toggle Label for analytics")).build();

        List<AEMCookiePolicies> aemCookiePolicies = Arrays.asList(aemCookiePolicies1, aemCookiePolicies2, aemCookiePolicies3);
        CookiePolicies underTest = util.convertAEMCookiePolicies(aemCookiePolicies);

        assertThat(underTest.getVersion()).isEqualTo("1");
        assertThat(underTest.getBrand()).isEqualTo(BrandCode.pi.name());

        assertThat(underTest.getIntroView().getTitle()).isEqualTo("Cookie Intro Title");
        assertThat(underTest.getIntroView().getDescription()).isEqualTo("<p>Intro description</p>");
        assertThat(underTest.getIntroView().getManageButtonText()).isEqualTo("Manage Cookies");
        assertThat(underTest.getIntroView().getAcceptAllButtonText()).isEqualTo("Accept all Cookies");

        assertThat(underTest.getManageView().getTitle()).isEqualTo("Manage Title");
        assertThat(underTest.getManageView().getDescription()).isEqualTo("<p>Manage description</p>");
        assertThat(underTest.getManageView().getSaveSettingsButtonText()).isEqualTo("Manage Saving Button");
        assertThat(underTest.getManageView().getAlwaysActiveText()).isEqualTo("Manage View Active Test");

        assertThat(underTest.getManageView().getCookieGroup().get(0).getCookieName()).isEqualTo("dtm.adobe.functional");
        assertThat(underTest.getManageView().getCookieGroup().get(0).getTitle()).isEqualTo("This is a new title for functional cookie");
        assertThat(underTest.getManageView().getCookieGroup().get(0).getDescription()).isEqualTo("This is a new description for functional cookie");
        assertThat(underTest.getManageView().getCookieGroup().get(0).getIsAlwaysActive()).isEqualTo(true);
        assertThat(underTest.getManageView().getCookieGroup().get(0).getToggleLabel()).isEqualTo("Toggle Label for functional");

        assertThat(underTest.getManageView().getCookieGroup().get(1).getCookieName()).isEqualTo("dtm.adobe.analytics");
        assertThat(underTest.getManageView().getCookieGroup().get(1).getTitle()).isEqualTo("This is a new title for analytics");
        assertThat(underTest.getManageView().getCookieGroup().get(1).getIsAlwaysActive()).isEqualTo(false);
        assertThat(underTest.getManageView().getCookieGroup().get(1).getDescription()).isEqualTo("This is a new description for analytics");
        assertThat(underTest.getManageView().getCookieGroup().get(1).getToggleLabel()).isEqualTo("Toggle Label for analytics");
    }

    @Test
    public void convertAEMBookingNotificationsResponseSuccess() {
        List<AEMBookingNotifications> aemBookingNotifications = new ArrayList<>();
        aemBookingNotifications.add(createAEMBookingNotifications("message 1"));
        aemBookingNotifications.add(createAEMBookingNotifications("message 2"));

        List<BookingNotifications> underTest = util.convertAEMBookingNotificationsResponse(aemBookingNotifications);

        assertThat(underTest.size()).isEqualTo(2);
        assertThat(underTest.get(0).getBookingInfoMessage()).isEqualTo("message 1");
        assertThat(underTest.get(1).getBookingInfoMessage()).isEqualTo("message 2");

    }

    private AEMBookingNotifications createAEMBookingNotifications(String message) {
        return AEMBookingNotifications.builder()
                .bookingInfoMessage(BookingInfoMessage.builder().value(message).build())
                .build();
    }

}

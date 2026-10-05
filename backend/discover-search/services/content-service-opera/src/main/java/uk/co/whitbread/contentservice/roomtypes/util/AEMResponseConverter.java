package uk.co.whitbread.contentservice.roomtypes.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.contentservice.roomtypes.model.BookingNotifications;
import uk.co.whitbread.contentservice.roomtypes.model.CookiePolicies;
import uk.co.whitbread.contentservice.roomtypes.model.Facility;
import uk.co.whitbread.contentservice.roomtypes.model.RateClassification;
import uk.co.whitbread.contentservice.roomtypes.model.RoomType;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMBookingNotifications;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMCookiePolicies;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRateClassifications;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRoomType;
import uk.co.whitbread.contentservice.roomtypes.model.aem.RateLongDescription;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.CookieDurationConfig;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.IntroView;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.ManageView;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.manageview.CookieGroup;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Component
@Slf4j
public class AEMResponseConverter {

    public List<RoomType> convertAEMResponse(List<AEMRoomType> aemRoomTypes) {

        return aemRoomTypes.stream().map(aemRoomType ->
                RoomType.builder()
                        .roomTypeCode(aemRoomType.getRoomTypeCode().getValue())
                        .roomCategory(aemRoomType.getRoomCategory().getValue())
                        .roomLabel(aemRoomType.getRoomLabel().getValue())
                        .roomDescription(aemRoomType.getRoomDescription().getValue())
                        .roomInfoLabel(aemRoomType.getRoomInfoLabel().getValue())
                        .roomInfo(aemRoomType.getRoomInfo().getValue())
                        .gridImage(aemRoomType.getGridImage().getValue())
                        .roomImage(aemRoomType.getGridImage().getValue())
                        .facilities(aemRoomType.getFacilities().getValue() != null ? splitFacilities(aemRoomType.getFacilities().getValue()) : Collections.emptyList())
                        .build()).collect(toList());

    }

    public List<RateClassification> convertAEMResponseRates(List<AEMRateClassifications> aemRateClassifications) {
        return aemRateClassifications.stream().map(aemRateClassification ->
                RateClassification.builder()
                        .rateClassification(aemRateClassification.getRateClassification().getValue())
                        .rateOrder(aemRateClassification.getRateOrder().getValue())
                        .rateName(aemRateClassification.getRateName().getValue())
                        .rateDescription(aemRateClassification.getRateDescription().getValue())
                        .rateLongDescription(
                            Optional.ofNullable(aemRateClassification.getRateLongDescription()).map(
                                RateLongDescription::getValue).orElse(EMPTY))
                        .rateNotes(aemRateClassification.getRateNotes().getValue())
                        .build())
                        .sorted(Comparator.comparing(RateClassification::getRateOrder)).collect(toList());
    }

    public CookiePolicies convertAEMCookiePolicies(List<AEMCookiePolicies> aemCookiePolicies) {
        IntroView introView = IntroView.builder()
                .title(aemCookiePolicies.get(0).getIntroViewTitle().getValue())
                .description(aemCookiePolicies.get(0).getIntroViewDescription().getValue())
                .acceptAllButtonText(aemCookiePolicies.get(0).getIntroViewAcceptAllButtonText().getValue())
                .necessaryOnlyButtonText(aemCookiePolicies.get(0).getIntroViewNecessaryOnlyButtonText().getValue())
                .manageButtonText(aemCookiePolicies.get(0).getIntroViewManageButtonText().getValue())
                .build();
        List<CookieGroup> cookieGroups = aemCookiePolicies.stream().skip(1).map(aemCookiePolicy -> CookieGroup.builder()
                .cookieName(aemCookiePolicy.getCookieName().getValue())
                .title(aemCookiePolicy.getTitle().getValue())
                .description(aemCookiePolicy.getDescription().getValue())
                .isAlwaysActive(aemCookiePolicy.getIsAlwaysActive().getValue().equals("true"))
                .toggleLabel(aemCookiePolicy.getToggleLabel().getValue())
                .build())
                .collect(Collectors.toList());
        ManageView manageView = ManageView.builder()
                .title(aemCookiePolicies.get(0).getManageViewTitle().getValue())
                .description(aemCookiePolicies.get(0).getManageViewDescription().getValue())
                .saveSettingsButtonText(aemCookiePolicies.get(0).getManageViewSaveSettingsButtonText().getValue())
                .alwaysActiveText(aemCookiePolicies.get(0).getManageViewAlwaysActiveText().getValue())
                .cookieGroup(cookieGroups)
                .build();
        CookieDurationConfig duration = CookieDurationConfig.builder()
                .cookieOptOutExpiryDays(aemCookiePolicies.get(0).getCookieOptOutExpiryDays().getValue())
                .cookieOptInExpiryDays(aemCookiePolicies.get(0).getCookieOptInExpiryDays().getValue())
                .build();
        return CookiePolicies.builder()
                .version(aemCookiePolicies.get(0).getVersion().getValue())
                .brand(aemCookiePolicies.get(0).getBrand().getValue())
                .config(duration)
                .introView(introView)
                .manageView(manageView)
                .build();
    }

    public List<Facility> splitFacilities(List<String> facilities) {

        return facilities.stream().map(facility ->
                Facility.builder()
                        .facilityTitle(facility.split(":")[0])
                        .facilityDescription(facility.split(":")[1])
                        .build()).collect(toList());
    }

    public List<RoomType> getRoomTypesForCode(List<AEMRoomType> aemRoomTypes, String roomTypeCode) {

        List<AEMRoomType> roomTypesForCode =  aemRoomTypes.stream()
                .filter(aemRoomType -> aemRoomType.getRoomTypeCode().getValue().equalsIgnoreCase(roomTypeCode))
                .collect(toList());
        return convertAEMResponse(roomTypesForCode);

    }

    public  List<RateClassification> getRateClassificationsForRate(List<AEMRateClassifications> aemRateClassifications, String rateClassification) {

        List <AEMRateClassifications> rateClassificationsForRate = aemRateClassifications.stream()
                .filter(aemRateClassification -> aemRateClassification.getRateClassification().getValue().equalsIgnoreCase(rateClassification))
                .collect(toList());
        return convertAEMResponseRates(rateClassificationsForRate);
    }

    public List<BookingNotifications> convertAEMBookingNotificationsResponse(List<AEMBookingNotifications> aemBookingNotifications) {
        return aemBookingNotifications
                .stream().map(bookingNotifications -> BookingNotifications.builder()
                        .bookingInfoMessage(bookingNotifications.getBookingInfoMessage().getValue())
                        .build()).collect(toList());
    }
}

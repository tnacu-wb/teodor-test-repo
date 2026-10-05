package com.whitbread.premierinn.hoteldetails;

import static com.whitbread.premierinn.common.utils.StringUtils.EMPTY_STRING;

import androidx.annotation.NonNull;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.availability.Coordinates;
import com.whitbread.premierinn.api.response.availability.Facility;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.data.graphql.mapper.GraphQLHotelInfoMappersKt;
import com.whitbread.premierinn.data.hotel.mapper.HotelMappersKt;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelFacilityDomain;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain;
import com.whitbread.premierinn.domain.hotel.entity.HotelBookingAvailabilityState;
import com.whitbread.premierinn.hoteldetails.uimodel.HeadingUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.MapUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.RestaurantBarUiModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;

public class UiModelItemFactory {

    @NonNull
    public static Function<Pair<HotelDetailsInput, Pair<String, String>>, UiModelListItem> createHeadingUiModelFromInput(
            int order,
            float distance
    ) {
        return pair -> HeadingUiModel.builder()
                .order(order)
                .distance(distance)
                .limitedAvailability(false)
                .address(StringUtils.EMPTY_STRING)
                .extraMessage(StringUtils.EMPTY_STRING)
                .isPremierPlus(false)
                .facilities(Collections.emptyList())
                .build();
    }

    @NonNull
    public static Function3<HotelInformationDomain, Boolean, Pair<String, String>, UiModelListItem>
    createHeadingUiModel(int order, float distance) {
        return (hotelInfo, limitedAvailability, pair) -> {
            HeadingUiModel.Builder builder = HeadingUiModel.builder()
                    .order(order)
                    .distance(distance)
                    .extraMessage(hotelInfo.getMessagingFlag().getText())
//                    .tripAdvisor(hotelInfo.tripAdvisor())
//                    .tripAdvisorRating(hotelInfo.getRatingState())
                    .limitedAvailability(limitedAvailability)
                    .address(GraphQLHotelInfoMappersKt.toCommaSeparatedAddress(hotelInfo.getAddress()));

//            if (hotelInfo.isMessagingAvailable()
//                    && hotelInfo.hotelBrand() != Hotel.Brand.HUB
//                    && hotelInfo.hotelBrand() != Hotel.Brand.ZIP) {
//                builder.messagingFlag(hotelInfo.messagingFlag());
//            }

            if (hotelInfo.getHotelFacilities() != null) {
                List<Facility> facilities = displayableFacilities(hotelInfo.getHotelFacilities());
                Facility isPremierPlus = CollectionsKt.firstOrNull(
                        facilities,
                        facility -> facility.code() == Facility.Codes.PREMIER_PLUS_ROOM);

                builder
                        .isPremierPlus(isPremierPlus != null)
                        .facilities(CollectionsKt.take(facilities, 4));
            }

            return builder.build();
        };
    }

    private static List<Facility> displayableFacilities(List<HotelFacilityDomain> listOfHotelFacilities) {
        List<Facility> displayableFacilities = new ArrayList<>();
        for (HotelFacilityDomain facility : listOfHotelFacilities) {
            if (facility != null && facility.isVisible()) {
                displayableFacilities.add(MappersKt.toFacility(facility));
            }
        }
        return displayableFacilities;
    }

    public static Function3<List<String>, HotelBookingAvailabilityState, HotelInformationDomain, UiModelListItem>
    createRestaurantHeaderOrGalleryUiModel(int order) {
        return (strings, hotelAvailabilityState, hotelInfo) -> {
            boolean hasRestaurant = hotelAvailabilityState.getGetHotelAvailability() != null;
            boolean hasUpsellItems = hotelAvailabilityState.getGetHotelAvailability().getPackages() != null;
            int headerTitleStringRes = !hasRestaurant && !hasUpsellItems
                    ? R.string.restaurant_info_breakfast_tab_title
                    : R.string.restaurant_info_title;

            RestaurantBarUiModel.Builder builder = RestaurantBarUiModel.builder()
                    .type(RestaurantBarUiModel.Type.RESTAURANT)
                    .order(order)
                    .headingStringRes(headerTitleStringRes)
                    .imageUrls(strings);

            if (hotelInfo != null && hotelInfo.getRestaurantInfo() != null && hotelInfo.getRestaurantInfo().getHasDisclaimer()) {
                builder.disclaimer(hotelInfo.getRestaurantInfo().getMenus().get(0).getDisclaimer());
            } else {
                builder.disclaimer(EMPTY_STRING);
            }

            return builder.build();
        };
    }

    public static Function<Pair<HotelDetailsInput, HotelInformationDomain>, UiModelListItem> createMapUiModel(
            @NonNull HotelDetailsMessageProvider provider, int order) {

        return pair -> {
            HotelDetailsInput input = pair.getFirst();
            HotelInformationDomain hotelInfo = pair.getSecond();

            if (input.searchResultsInput() == null || input.distanceFromSearchedLocation() == 0f) {
                return MapUiModel.builder().order(order)
                        .distance(0f)
                        .hotelAddress(GraphQLHotelInfoMappersKt.toCommaSeparatedAddress(hotelInfo.getAddress()))
                        .hotelLocationGQ(hotelInfo.getCoordinates())
                        .shortDirections(provider.shortDescription(hotelInfo.getHotelDescription()))
                        .hotelBrand(HotelMappersKt.mapToBrand(hotelInfo.getBrand()))
                        .build();

            } else {
                return MapUiModel.builder().order(order)
                        .hotelAddress(GraphQLHotelInfoMappersKt.toCommaSeparatedAddress(hotelInfo.getAddress()))
                        .searchedLocation(Coordinates.create(input.searchResultsInput().latitude(),
                                input.searchResultsInput().longitude()))
                        .hotelLocationGQ(hotelInfo.getCoordinates())
                        .shortDirections(provider.shortDescription(hotelInfo.getHotelDescription()))
                        .searchedLocationTerm(input.searchResultsInput().placeName())
                        .distance(input.distanceFromSearchedLocation())
                        .hotelBrand(HotelMappersKt.mapToBrand(hotelInfo.getBrand()))
                        .build();
            }
        };
    }
}
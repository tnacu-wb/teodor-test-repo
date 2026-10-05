package com.whitbread.premierinn.hoteldetails;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.StringResourceProvider;
import com.whitbread.premierinn.common.utils.HtmlUtils;
import com.whitbread.premierinn.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

public class HotelDetailsMessageProvider {

    private final Context context;
    private final StringResourceProvider commonStringsProvider;

    @Inject
    public HotelDetailsMessageProvider(@NonNull Context context, @NonNull StringResourceProvider provider) {
        this.context = context;
        this.commonStringsProvider = provider;
    }

    public String hotelDescriptionUnavailable() {
        return context.getString(R.string.hotel_details_hotel_description_unavailable);
    }

    public String hotelDirectionUnavailable() {
        return context.getString(R.string.hotel_details_hotel_directions_unavailable);
    }

    public List<Content> parkingInfoContents(@NonNull String parkingDescription) {
        List<Content> contents = new ArrayList<>();

        if (!parkingDescription.isEmpty()) {
                contents.add(Content.builder().contentType(Content.ContentType.BODY)
                        .text(parkingDescriptionFromHtml(parkingDescription)).build());
        } else {
                contents.add(Content.builder().contentType(Content.ContentType.INLINE)
                        .text(context.getString(R.string.parking_info_heading_start)).build());
                contents.add(Content.builder().contentType(Content.ContentType.INLINE_BOLD)
                        .text(context.getString(R.string.parking_info_no_parking_legend)).build());
                contents.add(Content.builder().contentType(Content.ContentType.INLINE)
                        .text(context.getString(R.string.parking_info_heading_end)).build());
        }

        return contents;
    }

    public String hotelDirections(@Nullable String hotelDirections) {
        return hotelDirections != null
                ? HtmlUtils.parseTags(hotelDirections).toString()
                : hotelDirectionUnavailable();
    }

    public String hotelDescription(@Nullable String hotelDescription) {
        return hotelDescription != null
                ? HtmlUtils.parseTags(hotelDescription).toString()
                : hotelDescriptionUnavailable();
    }

    public String parkingDescriptionFromHtml(@Nullable String parkingDescription) {
        if (parkingDescription == null) {
            return StringUtils.EMPTY_STRING;
        } else {
            return HtmlUtils.parseTags(parkingDescription).toString();
        }
    }

    public String shortDescription(@Nullable String description) {
        return description != null ? HtmlUtils.parseTags(description).toString() : "";
    }

    public Single<List<Content>> breakfastTabContent() {
        return Observable.fromArray(
                Content.builder().contentType(Content.ContentType.HEADER).text(context.getResources()
                        .getString(R.string.restaurant_info_full_breakfast_heading)).build(),
                Content.builder().contentType(Content.ContentType.BODY).text(context.getResources()
                        .getString(R.string.restaurant_info_full_breakfast_body)).build(),
                Content.builder().contentType(Content.ContentType.HEADER).text(context.getResources()
                        .getString(R.string.restaurant_info_continental_breakfast_heading)).build(),
                Content.builder().contentType(Content.ContentType.BODY).text(context.getResources()
                        .getString(R.string.restaurant_info_continental_breakfast_body)).build()
        ).toList();
    }

    public Single<List<Content>> dinnerTabContent() {
        return Observable.fromArray(
                Content.builder().contentType(Content.ContentType.HEADER).text(context.getResources()
                        .getString(R.string.restaurant_info_dinner_heading)).build(),
                Content.builder().contentType(Content.ContentType.BODY).text(context.getResources()
                        .getString(R.string.restaurant_info_dinner_body)).build(),
                Content.builder().contentType(Content.ContentType.HEADER).text(context.getResources()
                        .getString(R.string.restaurant_info_meal_deal_heading)).build(),
                Content.builder().contentType(Content.ContentType.BODY).text(context.getResources()
                        .getString(R.string.restaurant_info_meal_deal_body)).build()
        ).toList();
    }

    public Single<List<Content>> kidsTabContent() {
        return Observable.fromArray(
                Content.builder().contentType(Content.ContentType.HEADER).text(context.getResources()
                        .getString(R.string.restaurant_info_kids_heading)).build(),
                Content.builder().contentType(Content.ContentType.BODY).text(context.getResources()
                        .getString(R.string.restaurant_info_kids_body)).build()
        ).toList();
    }

    public StringResourceProvider getCommonStringsProvider() {
        return this.commonStringsProvider;
    }

    public List<Content> noBreakFastMessage() {
        Content part1 = Content.builder()
                .contentType(Content.ContentType.INLINE_BOLD)
                .text(context.getString(R.string.hotel_details_restaurant_breakfast_not_available_part1))
                .build();
        Content part2 = Content.builder()
                .contentType(Content.ContentType.INLINE)
                .text(context.getString(R.string.hotel_details_restaurant_breakfast_not_available_part2))
                .build();
        return Arrays.asList(part1, part2);
    }

    public String getFoodOptionsTitle() {
        return context.getString(R.string.hotel_details_restaurant_food_title);
    }

    public String accessibilityCallDescription(boolean isHotelNumber, String emailAddress) {
        return context.getString(isHotelNumber
                ? R.string.hotel_details_accessibility_call_hotel
                : R.string.hotel_details_accessibility_call_customer_service,
                emailAddress);
    }

    public String accessibilityCallButtonLabel(boolean isHotelNumber) {
        return context.getString(isHotelNumber ? R.string.call_hotel : R.string.call_us);
    }

    public String accessibilityEmailAddress() {
        return context.getString(R.string.hotel_details_accessibility_email_address);
    }
}

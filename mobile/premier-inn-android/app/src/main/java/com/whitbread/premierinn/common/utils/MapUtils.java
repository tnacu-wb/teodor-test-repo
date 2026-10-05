package com.whitbread.premierinn.common.utils;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.domain.hotel.entity.Hotel;
import com.whitbread.premierinn.searchresults.HotelListItem;
import com.whitbread.premierinn.searchresults.SearchResultsUiMappersKt;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import static com.whitbread.premierinn.common.utils.ResourceUtils.getBitmap;
import static com.whitbread.premierinn.common.utils.ResourceUtils.getBitmapWithText;
import static com.whitbread.premierinn.common.utils.ResourceUtils.getHotelPinBitmap;

public final class MapUtils {

    private MapUtils() {
        throw new AssertionError("no instances allowed");
    }

    public static MarkerOptions createHotelMarkerOptions(@NonNull Context context, @NonNull LatLng location, Hotel.Brand hotelBrand) {
        int pinDrawable;
        switch (hotelBrand) {
            case HUB: pinDrawable = R.drawable.ic_map_pin_hub; break;
            case ZIP: pinDrawable = R.drawable.ic_map_pin_zip; break;
            default: pinDrawable = R.drawable.ic_map_pins; break;
        }
        return new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmap(context, pinDrawable)));
    }

    public static MarkerOptions createPinMarkerOptionsWithText(@NonNull Context context, @NonNull LatLng location, @NonNull String text) {
        int drawableRes = R.drawable.ic_map_search_pin_small;
        if (text.length() > 7) {
            drawableRes = R.drawable.ic_map_search_pin_large;
            if (text.length() > 14) {
                text = text.substring(0, 13) + "...";
            }
        }
        return new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapWithText(context, drawableRes, text)));
    }

    public static MarkerOptions createHotelPinMarkerOptionsWithText(@NonNull Context context,
                                                                    @NonNull HotelListItem item) {
        return new MarkerOptions()
                .position(new LatLng(item.getLocation().getLatitude(), item.getLocation().getLongitude()))
                .icon(createHotelBitmapDescriptorWithText(context, SearchResultsUiMappersKt.toMapDrawableRes(item),
                        SearchResultsUiMappersKt.toMapPinText(item)));
    }

    public static BitmapDescriptor createHotelBitmapDescriptorWithText(@NonNull Context context, @DrawableRes int drawableRes,
                                                                       @NonNull String text) {
        return BitmapDescriptorFactory.fromBitmap(getHotelPinBitmap(context, drawableRes, text));
    }


    public static CameraUpdate createCameraUpdateFromMarkers(float distance, Marker... markers) {
        if (markers.length == 1) {
            return CameraUpdateFactory.newLatLngZoom(markers[0].getPosition(), 13f);
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < markers.length; i++) {
            builder.include(markers[i].getPosition());
        }

        LatLngBounds bounds = builder.build();
        return CameraUpdateFactory.newLatLngBounds(bounds, 0);
    }
}
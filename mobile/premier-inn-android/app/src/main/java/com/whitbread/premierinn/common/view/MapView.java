package com.whitbread.premierinn.common.view;

import static com.whitbread.premierinn.common.utils.AppExtensions.dpToPx;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.availability.Coordinates;
import com.whitbread.premierinn.domain.hotel.entity.Hotel;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;


public class MapView extends FrameLayout {

    public static final int MAP_PADDING_DP = 100;
    public static final int MAP_DEFAULT_PIXEL_WIDTH_PIXELS = 1440;
    public static final int MAP_DEFAULT_PIXEL_HEIGHT_PIXELS = 2560;
    private static final String MAP_CURRENT_POSITION_KEY = "map_current_position_key";
    private static final float MAP_ZOOM_DEFAULT = 15;
    private static final int RESET_BUTTON_LAYOUT_WIDTH_DP = 70;
    private static final int RESET_BUTTON_LAYOUT_HEIGHT_DP = 30;
    private static final int RESET_BUTTON_LAYOUT_MARGIN_LEFT_RIGHT_DP = 20;

    private final BehaviorRelay<GoogleMap> mapRelay = BehaviorRelay.create();
    private final PublishRelay<Pair<Integer, Integer>> scrollRelay = PublishRelay.create();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private SupportMapFragment mapFragment;

    private int mapPaddingPx;
    private boolean scrollGesturesEnabled;
    private boolean zoomControlsEnabled;
    private boolean zoomGesturesEnabled;
    private boolean rotateGesturesEnabled;
    private boolean buttonResetVisible;
    private boolean resetButtonOnTop;
    private CameraPosition savedCameraPosition;

    @ColorInt
    private int textColor;
    private float horizontalDensityRatio;
    private float verticalDensityRatio;
    private HotelMarkers hotelMarkers;
    private SearchMarkers searchMarkers;

    public MapView(Context context) {
        super(context);
        init(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public void setMapInfo(@NonNull Coordinates hotelCoordinate, @Nullable Coordinates pointOfInterest,
                           @Nullable String pointOfInterestName, Hotel.Brand hotelBrand) {
        compositeDisposable.add(
                mapRelay.subscribe(googleMap -> {
                    googleMap.setIndoorEnabled(false);
                    LatLng hotelLocation = new LatLng(hotelCoordinate.latitude(), hotelCoordinate.longitude());
                    LatLng searchLocation = null;
                    if (pointOfInterest != null) {
                        searchLocation = new LatLng(pointOfInterest.latitude(), pointOfInterest.longitude());
                    }
                    hotelMarkers = new HotelMarkers(getContext(), googleMap, hotelLocation, hotelBrand);
                    if (pointOfInterest != null && pointOfInterestName != null) {
                        searchMarkers = new SearchMarkers(getContext(), googleMap, searchLocation, pointOfInterestName, textColor);
                    }
                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        horizontalDensityRatio = MAP_DEFAULT_PIXEL_WIDTH_PIXELS / (float) displayMetrics.widthPixels;
                        verticalDensityRatio = MAP_DEFAULT_PIXEL_HEIGHT_PIXELS / (float) displayMetrics.heightPixels;
                    } else {
                        horizontalDensityRatio = MAP_DEFAULT_PIXEL_WIDTH_PIXELS / (float) displayMetrics.heightPixels;
                        verticalDensityRatio = MAP_DEFAULT_PIXEL_HEIGHT_PIXELS / (float) displayMetrics.widthPixels;
                    }
                    LatLng finalSearchLocation = searchLocation;
                    googleMap.setOnMarkerClickListener(marker -> {
                                hotelMarkers.animateOnClick((String) marker.getTag(), googleMap, hotelLocation);
                                if (searchMarkers != null) {
                                    searchMarkers.animateOnClick((String) marker.getTag(), googleMap, finalSearchLocation);
                                }
                                return true;
                            }
                    );
                    googleMap.setOnCameraMoveListener(() -> changeMarkers(googleMap, hotelLocation, finalSearchLocation));

                    if (savedCameraPosition == null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hotelLocation, MAP_ZOOM_DEFAULT));
                        if (finalSearchLocation != null) {
                            LatLngBounds latLngStartPosition = new LatLngBounds.Builder()
                                    .include(hotelLocation)
                                    .include(finalSearchLocation).build();
                            googleMap.setOnMapLoadedCallback(() ->
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngStartPosition, mapPaddingPx)));
                        }
                    } else {
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(savedCameraPosition));
                        changeMarkers(googleMap, hotelLocation, searchLocation);
                    }
                    if (buttonResetVisible) {
                        addResetButton(googleMap, hotelLocation, searchLocation);
                    }

                    compositeDisposable.add(scrollRelay
                            .subscribe(pair -> googleMap.animateCamera(CameraUpdateFactory.scrollBy(pair.first, pair.second))));
                }));
    }

    public Relay<Pair<Integer, Integer>> getScrollPointsRelay() {
        return scrollRelay;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        mapPaddingPx = dpToPx(getContext(), MAP_PADDING_DP);
        if (attrs != null) {
            TypedArray attributesArray = getContext().obtainStyledAttributes(attrs, R.styleable.MapView);
            scrollGesturesEnabled = attributesArray.getBoolean(R.styleable.MapView_mv_scrollGesturesEnabled, true);
            zoomControlsEnabled = attributesArray.getBoolean(R.styleable.MapView_mv_zoomControlsEnabled, true);
            zoomGesturesEnabled = attributesArray.getBoolean(R.styleable.MapView_mv_zoomGesturesEnabled, true);
            rotateGesturesEnabled = attributesArray.getBoolean(R.styleable.MapView_mv_rotateGesturesEnabled, true);
            buttonResetVisible = attributesArray.getBoolean(R.styleable.MapView_mv_buttonResetEnabled, false);
            resetButtonOnTop = attributesArray.getBoolean(R.styleable.MapView_mv_resetButtonOnTop, false);
            attributesArray.recycle();
        }
        TypedArray array = context.getTheme().obtainStyledAttributes(R.style.AppTheme, new int[]{
                android.R.attr.textColor
        });
        textColor = array.getColor(0, Color.BLACK);
        array.recycle();

        GoogleMapOptions googleMapOptions = new GoogleMapOptions()
                .mapToolbarEnabled(false)
                .rotateGesturesEnabled(rotateGesturesEnabled)
                .zoomControlsEnabled(zoomControlsEnabled)
                .scrollGesturesEnabled(scrollGesturesEnabled)
                .zoomGesturesEnabled(zoomGesturesEnabled);

        mapFragment = SupportMapFragment.newInstance(googleMapOptions);

        if (!isInEditMode()) {
            FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(getId(), mapFragment);
            fragmentTransaction.commit();

            mapFragment.getMapAsync(mapRelay::accept);
        }
    }

    private void changeMarkers(@NonNull GoogleMap googleMap, @NonNull LatLng hotelLocation, @Nullable LatLng searchLocation) {
        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        hotelMarkers.changeMarkerAtBoundary(googleMap, hotelLocation, bounds, getWidth(), getHeight(), verticalDensityRatio,
                horizontalDensityRatio);
        if (searchMarkers != null && searchLocation != null) {
            searchMarkers.changeMarkerAtBoundary(googleMap, searchLocation, bounds, getWidth(), getHeight(), verticalDensityRatio,
                    horizontalDensityRatio);
        }
    }

    private void addResetButton(@NonNull GoogleMap googleMap, @NonNull LatLng hotelLocation, @Nullable LatLng pointOfInterest) {
        LayoutParams layoutParams = new LayoutParams(dpToPx(getContext(), RESET_BUTTON_LAYOUT_WIDTH_DP),
                dpToPx(getContext(), RESET_BUTTON_LAYOUT_HEIGHT_DP));
        layoutParams.setMargins(0, dpToPx(getContext(), RESET_BUTTON_LAYOUT_MARGIN_LEFT_RIGHT_DP),
                dpToPx(getContext(), RESET_BUTTON_LAYOUT_MARGIN_LEFT_RIGHT_DP),
                dpToPx(getContext(), RESET_BUTTON_LAYOUT_MARGIN_LEFT_RIGHT_DP));
        layoutParams.setMarginEnd(dpToPx(getContext(), RESET_BUTTON_LAYOUT_MARGIN_LEFT_RIGHT_DP));
        if (resetButtonOnTop) {
            layoutParams.gravity = Gravity.END | Gravity.TOP;
        } else {
            layoutParams.gravity = Gravity.END | Gravity.BOTTOM;
        }

        TextView textView = new TextView(getContext());
        textView.setId(R.id.mv_reset_button);
        textView.setLayoutParams(layoutParams);
        textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_reset_map_background));
        textView.setGravity(Gravity.CENTER);
        textView.setText(getResources().getString(R.string.map_full_screen_reset_button));
        textView.setTextAppearance(getContext(), R.style.Header4);
        textView.setTextColor(Color.WHITE);
        textView.setOnClickListener(v -> {
            if (pointOfInterest != null) {
                LatLngBounds latLngStartPosition = new LatLngBounds.Builder().include(hotelLocation).include(pointOfInterest).build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngStartPosition, mapPaddingPx));
            } else {
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        CameraPosition.builder()
                                .target(hotelLocation)
                                .zoom(MAP_ZOOM_DEFAULT)
                                .build()
                ));
            }
        });
        addView(textView);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // View Group
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !(zoomControlsEnabled || zoomGesturesEnabled || scrollGesturesEnabled || rotateGesturesEnabled || buttonResetVisible)
                || super.onInterceptTouchEvent(ev);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        compositeDisposable.add(mapRelay
                .subscribe(googleMap -> bundle.putParcelable(MAP_CURRENT_POSITION_KEY, googleMap.getCameraPosition())));
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(null);
        if (state instanceof Bundle) {
            savedCameraPosition = ((Bundle) state).getParcelable(MAP_CURRENT_POSITION_KEY);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        compositeDisposable.add(mapRelay.subscribe(this::clearMap));
        if (compositeDisposable.size() > 0) {
            compositeDisposable.clear();
        }
        super.onDetachedFromWindow();
    }

    private void clearMap(@Nullable GoogleMap googleMap) {
        if (googleMap != null) {
            googleMap.setOnMarkerClickListener(null);
            googleMap.setOnCameraMoveListener(null);
            googleMap.clear();
        }
        if (hotelMarkers != null) {
            hotelMarkers.clear();
            hotelMarkers = null;
        }
        if (searchMarkers != null) {
            searchMarkers.clear();
            searchMarkers = null;
        }
    }

    /**
     * Some common values for SearchMarkers and HotelMarkers
     */
    private abstract static class Markers {
        protected static final float Z_INDEX_FULL_FOREGROUND = 1.0f;
        protected static final float Z_INDEX_MID_FOREGROUND = 0.7f;
        protected static final float Z_INDEX_MID_BACKGROUND = 0.3f;
        protected static final float Z_INDEX_FULL_BACKGROUND = 0.0f;
        protected static final float ANCHOR_X_MIDDLE = 0.5f;
        protected static final float ANCHOR_Y_MIDDLE = 0.5f;
        private float pointerWidth;
        private float pointerHeight;

        protected abstract void clear();

        public float getPointerWidth() {
            return pointerWidth;
        }

        void setPointerWidth(float pointerWidth) {
            this.pointerWidth = pointerWidth;
        }

        public float getPointerHeight() {
            return pointerHeight;
        }

        void setPointerHeight(float pointerHeight) {
            this.pointerHeight = pointerHeight;
        }

        public void animateOnClick(String tag, GoogleMap googleMap, LatLng latLngLocation) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, googleMap.getCameraPosition().zoom));
        }
    }

    /**
     * This class encapsulates the various search location markers that can appear on the map:
     * - the search marker for locations on-screen (MAIN)
     * - the search marker that points to a search location above the TOP of the screen (POINTER_UP)
     * - the search marker that points to a search location to the RIGHT of the screen (POINTER_RIGHT)
     * - the search marker that points to a search location below the BOTTOM of the screen (POINTER_DOWN)
     * - the search marker that  points to a search location to the LEFT of the screen (POINTER_LEFT)
     */
    private static class SearchMarkers extends Markers {

        private static final float SMALL_VERTICAL_TEXT_OFFSET = 0.25f;
        private static final float MEDIUM_VERTICAL_TEXT_OFFSET = 0.5f;

        private static final String MAP_SEARCH_MARKER_TAG = "search_marker_tag";
        @ColorInt final int textColor;
        private final Context context;
        private final Map<Name, Marker> markers;

        SearchMarkers(@NonNull Context context, @NonNull GoogleMap googleMap, @NonNull LatLng searchLocation,
                      @NonNull String searchToWrite, @ColorInt int textColor) {
            this.context = context.getApplicationContext();
            this.textColor = textColor;
            markers = new HashMap<>();

            Marker mainMarker = googleMap.addMarker(new MarkerOptions()
                    .position(searchLocation)
                    .zIndex(Z_INDEX_FULL_BACKGROUND)
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            writeTextOnBitmap(
                                    R.drawable.ic_map_search_pin_small,
                                    searchToWrite, SMALL_VERTICAL_TEXT_OFFSET))));
            markers.put(Name.MAIN, mainMarker);

            Bitmap pointerUpWithText = writeTextOnBitmap(R.drawable.ic_map_search_pointer_up, searchToWrite, MEDIUM_VERTICAL_TEXT_OFFSET);
            float density = Resources.getSystem().getDisplayMetrics().density;
            setPointerWidth(pointerUpWithText.getWidth() / density);
            setPointerHeight(pointerUpWithText.getHeight() / density);
            Marker searchPointerUp = googleMap.addMarker(new MarkerOptions()
                    .position(searchLocation).icon(BitmapDescriptorFactory.fromBitmap(pointerUpWithText))
                    .anchor(ANCHOR_X_MIDDLE, ANCHOR_Y_MIDDLE)
                    .zIndex(Z_INDEX_MID_BACKGROUND)
                    .visible(false));
            markers.put(Name.POINTER_UP, searchPointerUp);

            Marker searchPointerRight = googleMap.addMarker(new MarkerOptions()
                    .position(searchLocation)
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            writeTextOnBitmap(R.drawable.ic_map_search_pointer_right, searchToWrite, MEDIUM_VERTICAL_TEXT_OFFSET)))
                    .anchor(ANCHOR_X_MIDDLE, ANCHOR_Y_MIDDLE)
                    .zIndex(Z_INDEX_MID_BACKGROUND)
                    .visible(false));
            markers.put(Name.POINTER_RIGHT, searchPointerRight);

            Marker searchPointerDown = googleMap.addMarker(new MarkerOptions()
                    .position(searchLocation)
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            writeTextOnBitmap(R.drawable.ic_map_search_pointer_down, searchToWrite, MEDIUM_VERTICAL_TEXT_OFFSET)))
                    .anchor(ANCHOR_X_MIDDLE, ANCHOR_Y_MIDDLE)
                    .zIndex(Z_INDEX_MID_BACKGROUND)
                    .visible(false));
            markers.put(Name.POINTER_DOWN, searchPointerDown);

            Marker searchPointerLeft = googleMap.addMarker(new MarkerOptions()
                    .position(searchLocation)
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            writeTextOnBitmap(R.drawable.ic_map_search_pointer_left, searchToWrite, MEDIUM_VERTICAL_TEXT_OFFSET)))
                    .anchor(ANCHOR_X_MIDDLE, ANCHOR_Y_MIDDLE)
                    .zIndex(Z_INDEX_MID_BACKGROUND)
                    .visible(false));
            markers.put(SearchMarkers.Name.POINTER_LEFT, searchPointerLeft);
            // Tags used as reference for click listener
            for (Marker marker : markers.values()) {
                marker.setTag(MAP_SEARCH_MARKER_TAG);
            }
        }

        @Override
        public void animateOnClick(String tag, GoogleMap googleMap, LatLng latLngLocation) {
            if (tag.equals(MAP_SEARCH_MARKER_TAG)) {
                super.animateOnClick(tag, googleMap, latLngLocation);
                setVisibleOnly(Name.MAIN);
            }
        }

        /**
         * Change from the standard search pin to a box containing text and an arrow when the search location leaves the screen
         */
        public void changeMarkerAtBoundary(@NonNull GoogleMap googleMap, @NonNull LatLng searchLocation, @NonNull LatLngBounds bounds,
                                           int mapViewWidth, int mapViewHeight, float verticalDensityRatio, float horizontalDensityRatio) {
            double vertical = Math.abs(bounds.northeast.latitude - bounds.southwest.latitude);
            double horizontal = Math.abs(bounds.northeast.longitude - bounds.southwest.longitude);
            double markerMarginVertical = (vertical / (double) mapViewHeight)
                    * (getPointerHeight() * 2 / verticalDensityRatio);
            double markerMarginHorizontal = (horizontal / (double) mapViewWidth)
                    * (getPointerWidth() * 2 / horizontalDensityRatio);
            LatLng northeastMarkerBound = new LatLng(bounds.northeast.latitude - markerMarginVertical,
                    bounds.northeast.longitude - markerMarginHorizontal);
            LatLng southwestMarkerBound = new LatLng(bounds.southwest.latitude + markerMarginVertical,
                    bounds.southwest.longitude + markerMarginHorizontal);

            LatLng searchPosition = markers.get(Name.MAIN).getPosition();
            boolean searchMarkerInside = googleMap.getProjection().getVisibleRegion().latLngBounds.contains(searchPosition);
            if (searchMarkerInside) {
                setVisibleOnly(SearchMarkers.Name.MAIN);
            } else {
                LatLng offscreenMarkerLatLong = new LatLng(searchLocation.latitude, searchLocation.longitude);
                // Hits the top
                if (searchPosition.latitude > googleMap.getProjection().getVisibleRegion().latLngBounds.northeast.latitude) {
                    offscreenMarkerLatLong = new LatLng(northeastMarkerBound.latitude, offscreenMarkerLatLong.longitude);
                    setVisibleOnlyAtPosition(SearchMarkers.Name.POINTER_UP, offscreenMarkerLatLong);
                }
                // Hits the right
                if (searchPosition.longitude
                        > googleMap.getProjection().getVisibleRegion().latLngBounds.northeast.longitude) {
                    offscreenMarkerLatLong = new LatLng(offscreenMarkerLatLong.latitude, northeastMarkerBound.longitude);
                    setVisibleOnlyAtPosition(SearchMarkers.Name.POINTER_RIGHT, offscreenMarkerLatLong);
                }
                // Hits the bottom
                if (searchPosition.latitude < googleMap.getProjection().getVisibleRegion().latLngBounds.southwest.latitude) {
                    offscreenMarkerLatLong = new LatLng(southwestMarkerBound.latitude, offscreenMarkerLatLong.longitude);
                    setVisibleOnlyAtPosition(SearchMarkers.Name.POINTER_DOWN, offscreenMarkerLatLong);
                }
                // Hits the left
                if (searchPosition.longitude
                        < googleMap.getProjection().getVisibleRegion().latLngBounds.southwest.longitude) {
                    offscreenMarkerLatLong = new LatLng(offscreenMarkerLatLong.latitude, southwestMarkerBound.longitude);
                    setVisibleOnlyAtPosition(SearchMarkers.Name.POINTER_LEFT, offscreenMarkerLatLong);
                }
            }
        }

        @Override
        public void clear() {
            if (markers != null) {
                markers.clear();
            }
        }

        private Bitmap writeTextOnBitmap(int res, @NonNull String text, float verticalOffset) {
            BitmapTextUtil bitmapTextUtil = new BitmapTextUtil(context, res);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(textColor);
            paint.setTypeface(ResourcesCompat.getFont(context, R.font.proxima_nova_semibold));
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.map_indicator_text_size));

            Rect textRect = new Rect();
            paint.getTextBounds(text, 0, text.length(), textRect);

            if (textRect.width() >= bitmapTextUtil.getMaxWidth() && res == R.drawable.ic_map_search_pin_small) {
                bitmapTextUtil = new BitmapTextUtil(context, R.drawable.ic_map_search_pin_large);
                paint.getTextBounds(text, 0, text.length(), textRect);
            }

            String displayedText = text;
            while (textRect.width() >= bitmapTextUtil.getMaxWidth()) {
                text = text.substring(0, text.length() - 1);
                displayedText = text + "...";
                paint.getTextBounds(displayedText, 0, displayedText.length(), textRect);
            }

            int xPos = (bitmapTextUtil.getCanvas().getWidth() / 2) - 2;
            int yPos = (int) ((bitmapTextUtil.getCanvas().getHeight() * verticalOffset) - ((paint.descent() + paint.ascent()) / 2));
            bitmapTextUtil.getCanvas().drawText(displayedText, xPos, yPos, paint);

            return bitmapTextUtil.getBitmap();
        }

        private void setVisibleOnly(@NonNull Name markerName) {
            setVisibleAtPosition(markerName, null);
        }

        private void setVisibleOnlyAtPosition(@NonNull Name markerName, @NonNull LatLng position) {
            setVisibleAtPosition(markerName, position);
        }

        private void setVisibleAtPosition(@NonNull Name markerName, @Nullable LatLng position) {
            for (Map.Entry<Name, Marker> marker : markers.entrySet()) {
                if (markerName.equals(marker.getKey())) {
                    marker.getValue().setVisible(true);
                    if (position != null) {
                        marker.getValue().setPosition(position);
                    }
                } else {
                    marker.getValue().setVisible(false);
                }
            }
        }

        public enum Name {
            MAIN,
            POINTER_UP,
            POINTER_RIGHT,
            POINTER_DOWN,
            POINTER_LEFT
        }
    }

    private static class BitmapTextUtil {
        private final Bitmap bitmap;
        private final Canvas canvas;
        private final int maxWidth;

        BitmapTextUtil(@NonNull Context context, int resId) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), resId).copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(bitmap);
            maxWidth = canvas.getWidth() * 94 / 110 - 4;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public Canvas getCanvas() {
            return canvas;
        }

        public int getMaxWidth() {
            return maxWidth;
        }
    }

    /**
     * This class encapsulates the hotel location markers that can appear on the map:
     * - the hotel marker for locations on-screen (MAIN)
     * - the hotel 'head', that is overlaid on the pointer when the hotel is offscreen (HEAD)
     * - a rotatable pointer that sits under the HEAD and points in the direction of the offscreen hotel location (POINTER)
     */
    private static class HotelMarkers extends Markers {
        private static final String MAP_HOTEL_MARKER_TAG = "hotel_marker_tag";

        private final Map<HotelMarkers.Name, Marker> markers;

        HotelMarkers(@NonNull Context context, @NonNull GoogleMap googleMap, @NonNull LatLng hotelLocation, Hotel.Brand hotelBrand) {
            markers = new HashMap<>();

            googleMap.clear();

            int pinDrawable;
            switch (hotelBrand) {
                case HUB: pinDrawable = R.drawable.ic_map_pin_hub; break;
                case ZIP: pinDrawable = R.drawable.ic_map_pin_zip; break;
                default: pinDrawable = R.drawable.ic_map_pins; break;
            }
            Marker mainMarker = googleMap.addMarker(new MarkerOptions()
                    .position(hotelLocation)
                    .zIndex(Z_INDEX_FULL_FOREGROUND)
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmap(context, pinDrawable))));
            markers.put(Name.MAIN, mainMarker);

            int pinHeadBgDrawable;
            switch (hotelBrand) {
                case HUB: pinHeadBgDrawable = R.drawable.ic_map_hotel_hub_pointer; break;
                case ZIP: pinHeadBgDrawable = R.drawable.ic_map_hotel_zip_pointer; break;
                default: pinHeadBgDrawable = R.drawable.ic_map_hotel_pointer; break;
            }
            Bitmap pointerBitmap = getBitmap(context, pinHeadBgDrawable);
            float density = Resources.getSystem().getDisplayMetrics().density;
            setPointerWidth(pointerBitmap.getWidth() / density);
            setPointerHeight(pointerBitmap.getHeight() / density);
            Marker hotelPointer = googleMap.addMarker(new MarkerOptions()
                    .position(hotelLocation)
                    .icon(BitmapDescriptorFactory.fromBitmap(pointerBitmap))
                    .anchor(ANCHOR_X_MIDDLE, ANCHOR_Y_MIDDLE)
                    .zIndex(Z_INDEX_MID_FOREGROUND)
                    .visible(false));
            markers.put(Name.POINTER, hotelPointer);

            int pinHeadDrawable;
            switch (hotelBrand) {
                case HUB: pinHeadDrawable = R.drawable.ic_map_hotel_hub_head; break;
                case ZIP: pinHeadDrawable = R.drawable.ic_map_hotel_zip_head; break;
                default: pinHeadDrawable = R.drawable.ic_map_pins; break;
            }

            Marker hotelHead = googleMap.addMarker(new MarkerOptions()
                    .position(hotelLocation)
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmap(context, pinHeadDrawable)))
                    .anchor(ANCHOR_X_MIDDLE, ANCHOR_Y_MIDDLE)
                    .zIndex(Z_INDEX_FULL_FOREGROUND)
                    .visible(false));
            markers.put(Name.HEAD, hotelHead);

            // Tags used as reference for click listener
            for (Marker marker : markers.values()) {
                marker.setTag(MAP_HOTEL_MARKER_TAG);
            }
        }

        private static Bitmap getBitmap(@NonNull Context context, int drawableId) {
            Bitmap returnedBitmap = null;
            Drawable drawable = ContextCompat.getDrawable(context, drawableId);
            if (drawable instanceof BitmapDrawable) {
                returnedBitmap = ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof VectorDrawable) {
                returnedBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(returnedBitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }
            if (returnedBitmap != null) {
                return returnedBitmap;
            } else {
                throw new IllegalArgumentException("unsupported drawable type");
            }
        }

        @Override
        protected void clear() {
            if (markers != null) {
                markers.clear();
            }
        }

        @Override
        public void animateOnClick(String tag, GoogleMap googleMap, LatLng latLngLocation) {
            if (tag.equals(MAP_HOTEL_MARKER_TAG)) {
                super.animateOnClick(tag, googleMap, latLngLocation);
                setOnlyMainVisible();
            }
        }

        /**
         * Change from the standard hotel pin to the PI logo on a rotating pointer when the hotel location leaves the screen
         */
        public void changeMarkerAtBoundary(@NonNull GoogleMap googleMap, @NonNull LatLng hotelLocation, @NonNull LatLngBounds bounds,
                                           int mapViewWidth, int mapViewHeight, float verticalDensityRatio, float horizontalDensityRatio) {
            double vertical = Math.abs(bounds.northeast.latitude - bounds.southwest.latitude);
            double horizontal = Math.abs(bounds.northeast.longitude - bounds.southwest.longitude);
            double markerMarginVertical = (vertical / (double) mapViewHeight)
                    * (getPointerHeight() * 2 / verticalDensityRatio);
            double markerMarginHorizontal = (horizontal / (double) mapViewWidth)
                    * (getPointerWidth() * 2 / horizontalDensityRatio);
            LatLng northeastMarkerBound = new LatLng(bounds.northeast.latitude - markerMarginVertical,
                    bounds.northeast.longitude - markerMarginHorizontal);
            LatLng southwestMarkerBound = new LatLng(bounds.southwest.latitude + markerMarginVertical,
                    bounds.southwest.longitude + markerMarginHorizontal);

            LatLng hotelPosition = markers.get(HotelMarkers.Name.MAIN).getPosition();
            boolean hotelMarkerInside = googleMap.getProjection().getVisibleRegion().latLngBounds.contains(hotelPosition);
            if (hotelMarkerInside) {
                setOnlyMainVisible();
            } else {
                LatLng offscreenMarkerLatLong = new LatLng(hotelLocation.latitude, hotelLocation.longitude);
                // Hits the top
                if (hotelPosition.latitude > googleMap.getProjection().getVisibleRegion().latLngBounds.northeast.latitude) {
                    offscreenMarkerLatLong = new LatLng(northeastMarkerBound.latitude, offscreenMarkerLatLong.longitude);
                }
                // Hits the right
                if (hotelPosition.longitude
                        > googleMap.getProjection().getVisibleRegion().latLngBounds.northeast.longitude) {
                    offscreenMarkerLatLong = new LatLng(offscreenMarkerLatLong.latitude, northeastMarkerBound.longitude);
                }
                // Hits the bottom
                if (hotelPosition.latitude < googleMap.getProjection().getVisibleRegion().latLngBounds.southwest.latitude) {
                    offscreenMarkerLatLong = new LatLng(southwestMarkerBound.latitude, offscreenMarkerLatLong.longitude);

                }
                // Hits the left
                if (hotelPosition.longitude
                        < googleMap.getProjection().getVisibleRegion().latLngBounds.southwest.longitude) {
                    offscreenMarkerLatLong = new LatLng(offscreenMarkerLatLong.latitude, southwestMarkerBound.longitude);
                }

                double deltaLatitude = hotelLocation.latitude - offscreenMarkerLatLong.latitude;
                double deltaLongitude = hotelLocation.longitude - offscreenMarkerLatLong.longitude;
                float rotation = (float) Math.toDegrees(Math.atan2(deltaLongitude, deltaLatitude));
                setOnlyOffScreenPointerVisible(offscreenMarkerLatLong, rotation);
            }
        }

        private void setOnlyOffScreenPointerVisible(@NonNull LatLng offscreenMarkerLatLong, float rotation) {
            markers.get(Name.MAIN).setVisible(false);

            Marker offScreenHotelPointer = markers.get(Name.POINTER);
            offScreenHotelPointer.setPosition(offscreenMarkerLatLong);
            offScreenHotelPointer.setRotation(rotation);
            offScreenHotelPointer.setVisible(true);

            Marker offScreenHotelHead = markers.get(Name.HEAD);
            offScreenHotelHead.setPosition(offscreenMarkerLatLong);
            offScreenHotelHead.setVisible(true);
        }

        private void setOnlyMainVisible() {
            for (Map.Entry<HotelMarkers.Name, Marker> marker : markers.entrySet()) {
                if (Name.MAIN.equals(marker.getKey())) {
                    marker.getValue().setVisible(true);
                } else {
                    marker.getValue().setVisible(false);
                }
            }
        }

        enum Name {
            MAIN,
            HEAD,
            POINTER
        }
    }
}

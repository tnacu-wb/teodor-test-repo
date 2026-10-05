package com.whitbread.premierinn.hoteldetails.viewholder;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.common.utils.MapUtils;
import com.whitbread.premierinn.databinding.ViewHotelDetailsMapComponentBinding;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.event.MapViewClickEvent;
import com.whitbread.premierinn.hoteldetails.uimodel.MapUiModel;

class MapViewHolder extends BaseRecyclerViewHolder<MapUiModel> implements OnMapReadyCallback {
    private final ViewHotelDetailsMapComponentBinding binding;

    private final Context context;
    private GoogleMap googleMap;

    MapViewHolder(ViewHotelDetailsMapComponentBinding binding, PublishRelay<Object> relay) {
        super(binding.getRoot());

        this.binding = binding;
        context = binding.getRoot().getContext();
        binding.hotelDetailsMap.onCreate(null);
        binding.hotelDetailsMap.getMapAsync(this);

        MapViewClickEvent mapClickEvent = new MapViewClickEvent(getAdapterPosition());

        RxView.clicks(binding.mapViewClick).subscribe(o -> relay.accept(mapClickEvent));
        RxView.clicks(binding.openInMapButton).subscribe(o -> relay.accept(mapClickEvent));
    }

    @Override
    public void bind(MapUiModel item) {
        binding.hotelDetailsMap.setTag(item);
        binding.hotelAddress.setText(item.hotelAddress());

        if (item.searchedLocation() != null) {
            setUpSearchedAndHotelLocationForMap(item);
        } else {
            setUpHotelLocationForMap(item);
        }
    }

    private void setUpSearchedAndHotelLocationForMap(MapUiModel item) {
        LatLng searchedLatLng, hotelLatLng;
        if (googleMap == null) {
            return;
        }
        if (item.hotelLocation() != null) {
            searchedLatLng = new LatLng(item.searchedLocation().latitude(), item.searchedLocation().longitude());
            hotelLatLng = new LatLng(item.hotelLocation().latitude(), item.hotelLocation().longitude());
        } else {
            searchedLatLng = new LatLng(item.searchedLocation().latitude(), item.searchedLocation().longitude());
            hotelLatLng = new LatLng(item.hotelLocationGQ().getLatitude(), item.hotelLocationGQ().getLongitude());
        }

        Marker searchLocationMarker = googleMap.addMarker(MapUtils.createPinMarkerOptionsWithText(context, searchedLatLng,
                item.searchedLocationTerm()));
        Marker hotelMarker = googleMap.addMarker(MapUtils.createHotelMarkerOptions(context, hotelLatLng, item.hotelBrand()));

        addToMap(item.distance(), searchLocationMarker, hotelMarker);
    }

    private void setUpHotelLocationForMap(MapUiModel item) {
        LatLng hotelLatLng;
        if (googleMap == null) {
            return;
        }
        if (item.hotelLocation() != null) {
            hotelLatLng = new LatLng(item.hotelLocation().latitude(), item.hotelLocation().longitude());
        } else {
            hotelLatLng = new LatLng(item.hotelLocationGQ().getLatitude(), item.hotelLocationGQ().getLongitude());
        }

        Marker hotelMarker = googleMap.addMarker(MapUtils.createHotelMarkerOptions(context, hotelLatLng, item.hotelBrand()));

        addToMap(item.distance(), hotelMarker);
    }

    private void addToMap(float distance, Marker... markers) {
        googleMap.moveCamera(MapUtils.createCameraUpdateFromMarkers(distance, markers));
        googleMap.moveCamera(CameraUpdateFactory.zoomOut());
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        MapsInitializer.initialize(context.getApplicationContext());
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.setIndoorEnabled(false);
        MapUiModel item = (MapUiModel) binding.hotelDetailsMap.getTag();
        if (item == null) {
            return;
        }
        if (item.searchedLocation() != null) {
            setUpSearchedAndHotelLocationForMap(item);
        } else {
            setUpHotelLocationForMap(item);
        }
    }
}
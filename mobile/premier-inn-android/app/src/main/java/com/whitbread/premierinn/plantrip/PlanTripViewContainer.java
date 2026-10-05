package com.whitbread.premierinn.plantrip;


import android.Manifest;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.jakewharton.rxbinding3.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.availability.Coordinates;
import com.whitbread.premierinn.common.ToastUtil;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.utils.BottomSheetSimpleCallback;
import com.whitbread.premierinn.common.utils.HtmlUtils;
import com.whitbread.premierinn.common.utils.IntentUtils;
import com.whitbread.premierinn.databinding.ActivityPlanTripBinding;
import com.whitbread.premierinn.domain.hotel.entity.Hotel;

import io.reactivex.Observable;
import kotlin.Unit;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

public class PlanTripViewContainer implements PlanTripPresenter.View {

    private static final double CENTER_FACTOR = 1.8;
    private int bottomSheetState = -1;
    private final RxPermissions rxPermissions;
    private ActivityPlanTripBinding binding;
    private BaseActivity activity;

    public PlanTripViewContainer(@NonNull BaseActivity activity, ActivityPlanTripBinding binding) {
        this.activity = activity;
        this.binding = binding;
        rxPermissions = new RxPermissions(activity);

        activity.setToolbar(activity.getString(R.string.plan_your_trip), true);

        binding.bottomSheet.tswPlanYourTripMilesDistance.setFactory(() -> createTextViewInstance(activity));

        Animation animationIn = createTextSwitcherAnimation(activity, R.anim.slide_in_up);
        Animation animationOut = createTextSwitcherAnimation(activity, R.anim.slide_out_up);

        binding.bottomSheet.tswPlanYourTripMilesDistance.setInAnimation(animationIn);
        binding.bottomSheet.tswPlanYourTripMilesDistance.setOutAnimation(animationOut);

        initBottomSheet();
    }

    private void initBottomSheet() {
        BottomSheetBehavior.from(binding.bottomSheet.bsPlanYourTrip).setBottomSheetCallback(new BottomSheetSimpleCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                int center = (int) (binding.mvPlanTrip.getHeight() / CENTER_FACTOR);
                if (bottomSheetState != newState && newState == STATE_EXPANDED) {
                    binding.mvPlanTrip.getScrollPointsRelay().accept(new Pair<>(0, center - view.getTop()));
                    bottomSheetState = newState;
                } else if (newState == STATE_COLLAPSED) {
                    View buttonView = binding.mvPlanTrip.findViewById(R.id.mv_reset_button);
                    if (buttonView != null) {
                        buttonView.performClick();
                    }
                    bottomSheetState = newState;
                }
            }
        });

        setBottomSheetMaxHeightToScreenHeightPercent(0.8);
    }

    private void setBottomSheetMaxHeightToScreenHeightPercent(double heightPercentage) {
        binding.bottomSheet.bsPlanYourTrip.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.bottomSheet.bsPlanYourTrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                DisplayMetrics screenDisplayMetrics = binding.bottomSheet.bsPlanYourTrip.getResources().getDisplayMetrics();
                binding.bottomSheet.bsPlanYourTrip.getLayoutParams().height = (int) (screenDisplayMetrics.heightPixels * heightPercentage);
                binding.bottomSheet.bsPlanYourTrip.requestLayout();
            }
        });
    }

    private View createTextViewInstance(Context context) {
        TextView textView = new TextView(context);
        textView.setTextColor(ContextCompat.getColor(context, R.color.premier_inn_purple));
        textView.setTypeface(ResourcesCompat.getFont(activity.getApplicationContext(), R.font.proxima_nova_semibold));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
                .getDimension(R.dimen.default_text_size));

        return textView;
    }

    private Animation createTextSwitcherAnimation(@NonNull Context context, @AnimRes int anim) {
        Animation animation = AnimationUtils.loadAnimation(context, anim);
        animation.setDuration(400);
        animation.setStartOffset(600);
        return animation;
    }

    @Override
    public void setHotelCoordinates(Coordinates hotelCoordinates, Hotel.Brand hotelBrand) {
        binding.mvPlanTrip.setMapInfo(hotelCoordinates, null, null, hotelBrand);
    }

    @Override
    public Observable<Unit> onDirectionClicked() {
        return RxView.clicks(binding.bottomSheet.cabPlanYourTripGetDirections);
    }

    @Override
    public Observable<Boolean> onLocationPermissionRequested() {
        return rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION).filter(granted -> granted);
    }

    @Override
    public void setAddress(@NonNull String line1, @NonNull String line2) {
        binding.bottomSheet.tvPlanYourTripAddressLine1.setText(line1);
        binding.bottomSheet.tvPlanYourTripAddressLine2.setText(line2);
    }

    @Override
    public void setTelephone(@NonNull String telephone) {
        this.binding.bottomSheet.tvPlanYourTripTelephone.setText(String.format(activity.getString(R.string.tel), telephone));
        this.binding.bottomSheet.tvPlanYourTripTelephone.setVisibility(View.VISIBLE);
    }

    @Override
    public void setDirectionsInfo(@NonNull String direction) {
        binding.bottomSheet.tvPlanYourTripDirections.setText(direction);
    }

    @Override
    public void setParkingInfo(@NonNull String parking) {
        binding.bottomSheet.tvPlanYourTripHotelParking.setText(HtmlUtils.parseTags(parking).toString());
    }

    @Override
    public void startUriActivity(@NonNull String uri) {
        activity.startActivity(IntentUtils.createWebLinkIntent(uri));
    }

    @Override
    public void setDistanceFromUser(float distanceInMiles) {
        binding.bottomSheet.tvPlanYourTripDirections.setText(String.format(activity
                .getString(R.string.plan_your_trip_hotel_distance_from_location), distanceInMiles));
    }

    @Override
    public void setHotelName(String name) {
        binding.bottomSheet.tvPlanYourTripDirections.setText(name);
    }

    @Override
    public void finishWithMessage() {
        new ToastUtil(activity).showLong(activity.getString(R.string.search_results_toast_search_problem));
        activity.finish();
    }
}

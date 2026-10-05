package com.whitbread.premierinn.bookingpreferences;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxrelay2.Relay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.criteria.roomselector.Room;
import com.whitbread.premierinn.databinding.ActivityBookingPreferencesBinding;
import com.whitbread.premierinn.domain.common.RoomCriteria;
import com.whitbread.premierinn.login.LoginActivity;
import com.whitbread.premierinn.login.Screen;
import com.whitbread.premierinn.login.ScreenType;
import com.whitbread.premierinn.mealpreferences.MealPreferencesActivity;
import com.whitbread.premierinn.roompreferences.RoomPreferencesActivity;

import io.reactivex.Observable;
import kotlin.Unit;

public class BookingPreferencesViewContainer extends ViewContainer implements BookingPreferencesPresenter.View {

    private final ActivityBookingPreferencesBinding binding;
    private final Relay<Object> bookingPrefChangeRelay;

    private Context context;

    public BookingPreferencesViewContainer(@NonNull BaseActivity bookingPreferencesActivity,
                                           @NonNull Relay<Object> bookingPrefChangeRelay,
                                           ActivityBookingPreferencesBinding binding) {
        super(bookingPreferencesActivity);
        this.context = bookingPreferencesActivity;
        this.bookingPrefChangeRelay = bookingPrefChangeRelay;
        this.binding = binding;
        getActivity().setToolbar(getActivity().getString(R.string.booking_preferences_title), true);
    }

    @Override
    public void showLoading(boolean show) {
        if (show) {
            binding.bookingPrefsProgress.setVisibility(View.VISIBLE);
            binding.bookingPrefsContainer.setVisibility(View.GONE);
        } else {
            binding.bookingPrefsProgress.setVisibility(View.GONE);
            binding.bookingPrefsContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showMealPreference(@NonNull String mealPreferenceDescription) {
        binding.bookingPrefsMealPreference.setText(mealPreferenceDescription);
        binding.bookingPrefsMealPreferenceChange.setText(R.string.change);
    }

    @Override
    public void showNoMealPreference() {
        binding.bookingPrefsMealPreference.setText(R.string.booking_preference_none);
        binding.bookingPrefsMealPreferenceChange.setText(R.string.booking_preference_add);
    }

    @Override
    public void showRoomRequirements(@NonNull RoomCriteria roomRequirements) {
        String adults = context.getResources()
                .getQuantityString(R.plurals.number_of_adults_capitalised,
                        roomRequirements.getNumberOfAdults(),
                        roomRequirements.getNumberOfAdults());
        String children = context.getResources()
                .getQuantityString(R.plurals.number_of_children_capitalised,
                        roomRequirements.getNumberOfChildren(),
                        roomRequirements.getNumberOfChildren());
        String roomChoice = "";
        if (roomRequirements.getRoomType() != null) {
            roomChoice = Room.getRoomTypeLabel(context, Room.typeLookUp(roomRequirements.getRoomType().getCode()));
        }

        if (roomRequirements.getIncludeCot()) {
            roomChoice = getActivity().getString(R.string.summary_breakdown_room_description_with_cot, roomChoice);
        }

        binding.bookingPrefsAdults.setText(adults);
        binding.bookingPrefsChildren.setText(children);
        binding.bookingPrefsRoom.setText(roomChoice);

        binding.bookingPrefsNoneRoomRequirements.setVisibility(View.GONE);
        binding.bookingPrefsRoomRequirements.setVisibility(View.VISIBLE);
        binding.bookingPrefsRoomChange.setText(R.string.change);
    }

    @Override
    public void showNoRoomRequirements() {
        binding.bookingPrefsNoneRoomRequirements.setVisibility(View.VISIBLE);
        binding.bookingPrefsRoomRequirements.setVisibility(View.GONE);
    }

    @Override
    public Observable<Unit> onChangeRoomRequirementsClick() {
        return RxView.clicks(binding.bookingPrefsRoomChange);
    }

    @Override
    public Observable<Unit> onChangeMealPrefsClick() {
        return RxView.clicks(binding.bookingPrefsMealPreferenceChange);
    }

    @Override
    public Observable<Object> onBookingPrefChanged() {
        return bookingPrefChangeRelay;
    }

    @Override
    public void showError() {
        Toast.makeText(context, getActivity().getString(R.string.error_get_preferences), Toast.LENGTH_LONG).show();
    }

    @Override
    public void startMealPreferencesActivity() {
        getActivity().startActivityForResult(MealPreferencesActivity.createIntent(getActivity()),
                MealPreferencesActivity.MEAL_PREFERENCES_RESULT_KEY);
    }

    @Override
    public void startRoomPreferencesActivity() {
        getActivity().startActivityForResult(RoomPreferencesActivity.createIntent(getActivity()),
                RoomPreferencesActivity.ROOM_PREFERENCES_RESULT_KEY);
    }

    @Override
    public void showForceLoginMessage() {
        Toast.makeText(getActivity(), R.string.force_login_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startLogInActivity() {
        getActivity().startActivity(LoginActivity.createIntent(getActivity(),
                new Screen(ScreenType.ACCOUNT_LOGIN.name(), AnalyticsConstants.ScreenState.AUTHENTICATION_ERROR_LOG_IN)));
    }
}
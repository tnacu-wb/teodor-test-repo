package com.whitbread.premierinn.mybookings;

import static com.whitbread.premierinn.ciol.fragments.RoomKeyBottomSheetFragmentKt.ROOM_KEY;
import static com.whitbread.premierinn.data.common.Constants.EMPTY_STRING;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.bookingdetails.BookingDetailsActivity;
import com.whitbread.premierinn.ciol.entity.PreStayUiModel;
import com.whitbread.premierinn.ciol.fragments.RoomKeyBottomSheetFragment;
import com.whitbread.premierinn.ciol.uimodel.InfoBottomSheetData;
import com.whitbread.premierinn.ciol.uimodel.RoomKeyInstructionsModel;
import com.whitbread.premierinn.ciol.utils.CiolViewsExtensionsKt;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.bottomnavigation.BottomNavigationActivity;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.common.utils.IntentUtils;
import com.whitbread.premierinn.common.view.ListItem;
import com.whitbread.premierinn.databinding.ActivityMyBookingsBinding;
import com.whitbread.premierinn.findbooking.FindBookingActivity;
import com.whitbread.premierinn.login.LoginActivity;
import com.whitbread.premierinn.login.Screen;
import com.whitbread.premierinn.login.ScreenType;
import com.whitbread.premierinn.plantrip.PlanTripActivity;
import com.whitbread.premierinn.qrkiosk.QRCodeActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import kotlin.Unit;

public class MyBookingsViewContainer extends ViewContainer implements MyBookingsPresenter.View {
    private final ActivityMyBookingsBinding binding;

    private final PublishRelay<OnClickItemAction> itemsClickRelay = PublishRelay.create();
    private final BookingListAdapter bookingListAdapter = new BookingListAdapter(null, itemsClickRelay);
    private final Relay<Object> bookingCanceledRelay;
    private final Relay<Object> importedBookingRelay;
    private final Relay<Object> loginRelay;

    public MyBookingsViewContainer(@NonNull BaseActivity activity, @NonNull Relay<Object> bookingCanceledRelay,
                                   @NonNull Relay<Object> importedBookingRelay,
                                   @NonNull Relay<Object> loginRelay,
                                   ActivityMyBookingsBinding binding) {
        super(activity);
        this.binding = binding;
        this.bookingCanceledRelay = bookingCanceledRelay;
        this.importedBookingRelay = importedBookingRelay;
        this.loginRelay = loginRelay;
        activity.setToolbar(activity.getString(R.string.my_bookings_title), false);
        binding.myBookingsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.myBookingsList.setAdapter(bookingListAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_divider_transparent));
        binding.myBookingsList.addItemDecoration(dividerItemDecoration);
        binding.myBookingsProgress.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white),
                        PorterDuff.Mode.SRC_IN);
        binding.myBookingsInfoMessageBoxView.setVisibility(View.GONE);
    }

    @Override
    public Observable<OnClickItemAction> onBookingListClicks() {
        return bookingListAdapter.listItemClicks();
    }

    @Override
    public void startPlanTripActivity(@NonNull String hotelCode) {
        PlanTripActivity.start(getActivity(), hotelCode);

    }

    @Override
    public void startBookingDetailsActivity(@NonNull String bookingReference, String uuidBasketReference, String token) {
        getActivity().startActivityForResult(BookingDetailsActivity.createIntent(getActivity(),
                        bookingReference, uuidBasketReference, null, null, null,
                        token, EMPTY_STRING, EMPTY_STRING, false, EMPTY_STRING, EMPTY_STRING),
                MyBookingsActivity.BOOKING_DETAILS_REQUEST_CODE);
    }

    @Override
    public void startQRCodeActivity(@NonNull String bookingReference) {
        getActivity().startActivity(QRCodeActivity.createIntent(getActivity(), bookingReference));
    }

    @Override
    public void showNoBookingsScreenWithLoginPrompt() {
        binding.myBookingsEmptyStateLoggedOut.getRoot().setVisibility(View.VISIBLE);

        binding.myBookingsEmptyStateLoggedIn.getRoot().setVisibility(View.GONE);
        binding.myBookingsList.setVisibility(View.GONE);
    }

    @Override
    public void showNoBookingsScreen() {
        binding.myBookingsEmptyStateLoggedIn.getRoot().setVisibility(View.VISIBLE);

        binding.myBookingsEmptyStateLoggedOut.getRoot().setVisibility(View.GONE);
        binding.myBookingsList.setVisibility(View.GONE);
    }

    @Override
    public Observable<Unit> onLogInClick() {
        return RxView.clicks(binding.myBookingsEmptyStateLoggedOut.myBookingsLoginButton);
    }

    @Override
    public Observable<Unit> onSearchClick() {
        return RxView.clicks(binding.myBookingsEmptyStateLoggedIn.myBookingsSearchHotelButton);
    }

    @Override
    public void startLogInActivity() {
        getActivity().startActivityForResult(LoginActivity.createIntent(getActivity(),
                new Screen(ScreenType.ACCOUNT_LOGIN.name(), AnalyticsConstants.ScreenState.BOOKING_LOG_IN)),
                LoginActivity.ACTIVITY_RESULT_REQUEST_CODE);
    }

    @Override
    public Observable<Object> onLoginSuccessful() {
        return loginRelay;
    }

    @Override
    public void showLoadingInToolbar(boolean show) {
        binding.myBookingsProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void showLoading(boolean show) {
        binding.myBookingProgressBarContainer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        binding.myBookingProgressBarContainer.setClickable(show);
    }

    @Override
    public void showSearchScreen() {
        BottomNavigationActivity<ActivityMyBookingsBinding> activity = (BottomNavigationActivity<ActivityMyBookingsBinding>) getActivity();
        activity.startLandingActivity();
    }

    @Override
    public Observable<Object> onBookingCancelled() {
        return bookingCanceledRelay;
    }

    @Override
    public Observable<Unit> onFindBookingClick() {
        return RxView.clicks(getActivity().findViewById(R.id.my_bookings_find_booking_toolbar_button))
                .mergeWith(RxView.clicks(getActivity().findViewById(R.id.my_bookings_find_booking_button)));
    }

    @Override
    public void startFindBookingActivity() {
        getActivity().startActivityForResult(
                FindBookingActivity.createIntent(getActivity(), null), FindBookingActivity.ACTIVITY_RESULT_REQUEST_CODE);
    }

    @Override
    public Observable<Object> onImportedBooking() {
        return importedBookingRelay;
    }

    @Override
    public void showSuccessImportedBookingMessage() {
        Toast.makeText(getActivity(), getActivity().getString(R.string.my_bookings_success_import), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showImportBookingError() {
        Toast.makeText(getActivity(), getActivity().getString(R.string.my_bookings_error_get_booking), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showBookings(@NotNull List<? extends ListItem> bookingUiModels) {
        bookingListAdapter.submitList((ArrayList) bookingUiModels);

        binding.myBookingsList.setVisibility(View.VISIBLE);

        binding.myBookingsEmptyStateLoggedOut.getRoot().setVisibility(View.GONE);
        binding.myBookingsEmptyStateLoggedIn.getRoot().setVisibility(View.GONE);
    }

    @Override
    public void showLoadingError() {
        Toast.makeText(getActivity(), getActivity().getString(R.string.my_bookings_loading_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showUpdateError() {
        Toast.makeText(getActivity(), getActivity().getString(R.string.my_bookings_update_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showCheckInInfoBottomSheet(PreStayUiModel preStayModel) {

        InfoBottomSheetData infoBottomSheetData = InfoBottomSheetData.Companion.startCheckInData(getActivity().getResources());
        CiolViewsExtensionsKt.showCheckInInformationBottomSheet(getActivity(), infoBottomSheetData, () -> {
            IntentUtils.startCiolActivity(getActivity(), preStayModel);
            return Unit.INSTANCE;
        });
    }

    public void showGetRoomKeyInstructionsBottomSheet(@NonNull RoomKeyInstructionsModel model) {
        RoomKeyBottomSheetFragment roomKeyBottomSheet = new RoomKeyBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ROOM_KEY, model);
        roomKeyBottomSheet.setArguments(bundle);
        roomKeyBottomSheet.show(getActivity().getSupportFragmentManager(), roomKeyBottomSheet.getTag());
    }

    @Override
    public void showStartCheckInOnlineError() {
        Toast.makeText(getActivity(), getActivity().getString(R.string.start_ciol_generic_error), Toast.LENGTH_LONG).show();
    }
}

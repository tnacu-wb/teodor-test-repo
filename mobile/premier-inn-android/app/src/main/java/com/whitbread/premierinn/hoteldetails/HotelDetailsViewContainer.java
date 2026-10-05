package com.whitbread.premierinn.hoteldetails;

import static com.whitbread.premierinn.hoteldetails.HotelDetailsActivity.REQUEST_CODE_CALENDAR_SCREEN;
import static com.whitbread.premierinn.hoteldetails.HotelDetailsActivity.REQUEST_CODE_ROOMS_SCREEN;
import static com.whitbread.premierinn.hoteldetails.HotelDetailsActivity.REQUEST_CODE_SUMMARY_SCREEN;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.additionalinformation.AdditionalInformationActivity;
import com.whitbread.premierinn.alternativeroomselection.AlternativeRoomSelectionActivity;
import com.whitbread.premierinn.bathroomselection.BathroomSelectionActivity;
import com.whitbread.premierinn.calendar.maincalendar.HomeCalendarActivity;
import com.whitbread.premierinn.common.BookingFlowInput;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.common.utils.IntentUtils;
import com.whitbread.premierinn.databinding.ActivityHotelDetailsBinding;
import com.whitbread.premierinn.domain.common.RoomCriteria;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.InfoItem;
import com.whitbread.premierinn.guestdetails.GuestDetailsActivity;
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeInput;
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetFragment;
import com.whitbread.premierinn.hoteldetails.event.AccesibilityClickEvent;
import com.whitbread.premierinn.hoteldetails.event.AccessibilityRoomTypeInfoClickEvent;
import com.whitbread.premierinn.hoteldetails.event.CheckAvailabilityClickEvent;
import com.whitbread.premierinn.hoteldetails.event.CoronavirusDismissClickEvent;
import com.whitbread.premierinn.hoteldetails.event.DiscountCodeClickEvent;
import com.whitbread.premierinn.hoteldetails.event.EditDatesClickEvent;
import com.whitbread.premierinn.hoteldetails.event.EditGuestClickEvent;
import com.whitbread.premierinn.hoteldetails.event.FacilitiesClickEvent;
import com.whitbread.premierinn.hoteldetails.event.FindOutMoreClickEvent;
import com.whitbread.premierinn.hoteldetails.event.HotelsNearbyClickEvent;
import com.whitbread.premierinn.hoteldetails.event.ImportantInfoClickEvent;
import com.whitbread.premierinn.hoteldetails.event.MainGalleryItemClickEvent;
import com.whitbread.premierinn.hoteldetails.event.MakePhoneCallEvent;
import com.whitbread.premierinn.hoteldetails.event.MapViewClickEvent;
import com.whitbread.premierinn.hoteldetails.event.OpenWebLinkEvent;
import com.whitbread.premierinn.hoteldetails.event.ParkingClickEvent;
import com.whitbread.premierinn.hoteldetails.event.RateClickEvent;
import com.whitbread.premierinn.hoteldetails.event.ReadMoreClickEvent;
import com.whitbread.premierinn.hoteldetails.event.RoomTypesClickEvent;
import com.whitbread.premierinn.hoteldetails.event.SelectRateClickEvent;
import com.whitbread.premierinn.hoteldetails.event.SendEmailEvent;
import com.whitbread.premierinn.hoteldetails.event.StartSummaryOrRoomSelectionEvent;
import com.whitbread.premierinn.hoteldetails.facilities.HotelFacilitiesBottomSheetFragment;
import com.whitbread.premierinn.hoteldetails.facilities.HotelFacilitiesModel;
import com.whitbread.premierinn.hoteldetails.hotelfulldescription.AboutThisHotelBottomSheetFragment;
import com.whitbread.premierinn.hoteldetails.hotelfulldescription.HotelFullDescription;
import com.whitbread.premierinn.hoteldetails.hotelmapfullscreen.HotelMapFullScreenActivity;
import com.whitbread.premierinn.hoteldetails.hotelmapfullscreen.MapStartInfo;
import com.whitbread.premierinn.hoteldetails.roomvariantdetails.RoomVariantDetailsBottomSheetFragment;
import com.whitbread.premierinn.hoteldetails.uimodel.RoomRatesUiModel;
import com.whitbread.premierinn.login.LoginActivity;
import com.whitbread.premierinn.login.Screen;
import com.whitbread.premierinn.login.ScreenType;
import com.whitbread.premierinn.reviewbooking.ReviewBookActivity;
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput;
import com.whitbread.premierinn.roomcriteria.RoomCriteriaActivity;
import com.whitbread.premierinn.summary.SummaryActivity;
import com.whitbread.premierinn.summary.SummaryInput;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kotlin.Unit;

public class HotelDetailsViewContainer extends ViewContainer implements HotelDetailsPresenter.View {

    private static final int TITLE_FADE_DURATION = 100;
    private static final int HOTEL_NAME_LIST_POSITION = 1;

    private HotelDetailsRecyclerViewAdapter uiModelsAdapter;
    private PublishRelay<Object> clicksRelay = PublishRelay.create();
    private CompositeDisposable subscriptions = new CompositeDisposable();
    private CompositeDisposable bookingSubscriptions = new CompositeDisposable();
    private ActivityHotelDetailsBinding binding;

    public HotelDetailsViewContainer(@NonNull BaseActivity activity, ActivityHotelDetailsBinding binding, LogService logService) {
        super(activity);
        this.binding = binding;
        setUpToolbar();

        setUpContentRecyclerView(logService);

        RxView.clicks(binding.hotelDetailsSelectRateButton).subscribe(__ -> clicksRelay.accept(SelectRateClickEvent.INSTANCE));
    }

    public void addCommonSubscriptions(Disposable... disposables) {
        for (Disposable disposable : disposables) {
            if (disposable != null) {
                subscriptions.add(disposable);
            }
        }
    }

    public void addBookingSubscriptions(Disposable... disposables) {
        for (Disposable disposable : disposables) {
            if (disposable != null) {
                bookingSubscriptions.add(disposable);
            }
        }
    }

    public void clearCommonSubscriptions() {
        subscriptions.clear();
    }

    public void clearBookingSubscriptions() {
        bookingSubscriptions.clear();
    }

    private void setUpToolbar() {
        getActivity().setSupportActionBar(binding.hotelDetailsToolbar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpContentRecyclerView(LogService logService) {
        uiModelsAdapter = new HotelDetailsRecyclerViewAdapter(clicksRelay, logService);
        binding.hotelDetailsContentList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        binding.hotelDetailsContentList.setHasFixedSize(true);
        binding.hotelDetailsContentList.setItemAnimator(new DefaultItemAnimator());
        binding.hotelDetailsContentList.setAdapter(uiModelsAdapter);
        binding.hotelDetailsContentList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItemPosition = ((LinearLayoutManager) (recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                if (uiModelsAdapter.itemViewTypeExists(RoomRatesUiModel.LAYOUT_TYPE)
                        && uiModelsAdapter.itemViewAboveViewport(RoomRatesUiModel.LAYOUT_TYPE,
                        firstVisibleItemPosition)) {
                    binding.hotelDetailsButtonWrapper.setVisibility(View.VISIBLE);
                    binding.hotelDetailsSelectRateButton.setVisibility(View.VISIBLE);
                } else {
                    binding.hotelDetailsSelectRateButton.setVisibility(View.GONE);
                    binding.hotelDetailsButtonWrapper.setVisibility(View.GONE);
                }

                if (firstVisibleItemPosition >= HOTEL_NAME_LIST_POSITION) {
                    binding.toolbarLayout.hotelDetailsToolbarTitle.animate()
                            .alpha(1.0f)
                            .setDuration(TITLE_FADE_DURATION)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    binding.toolbarLayout.hotelDetailsToolbarTitle.setVisibility(View.VISIBLE);
                                }
                            });
                } else {
                    binding.toolbarLayout.hotelDetailsToolbarTitle.animate()
                            .alpha(0.0f)
                            .setDuration(TITLE_FADE_DURATION)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    binding.toolbarLayout.hotelDetailsToolbarTitle.setVisibility(View.GONE);
                                }
                            });
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // HotelDetailsPresenter.View
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void addUiModelToRecyclerView(@NonNull UiModelListItem item) {
        uiModelsAdapter.addItem(item);
    }

    @Override
    public void removeUiModelFromRecyclerView(@NonNull int position) {
        uiModelsAdapter.removeItem(position);
        uiModelsAdapter.notifyItemRemoved(position);
    }

    @Override
    public void startLoginActivity() {
        getActivity().startActivityForResult(LoginActivity.createIntent(getActivity(),
                        new Screen(ScreenType.BOOKING_FLOW_LOGIN.name(), AnalyticsConstants.ScreenState.LOG_IN)),
                LoginActivity.ACTIVITY_RESULT_REQUEST_CODE);
    }

    @Override
    public void startBathroomSelectionActivity(@NonNull BathroomSelectionInput bathroomSelectionInput) {
        Intent intent = BathroomSelectionActivity.Companion.createIntent(getActivity(), bathroomSelectionInput);
        getActivity().startActivity(intent);
    }

    @Override
    public void startAlternativeRoomSelectionActivity(@NonNull @NotNull BathroomSelectionInput bathroomSelectionInput) {
        Intent intent = AlternativeRoomSelectionActivity.Companion.createIntent(getActivity(), bathroomSelectionInput);
        getActivity().startActivity(intent);
    }

    @Override
    public void startSummaryActivity(SummaryInput summaryInput) {
        getActivity().startActivityForResult(SummaryActivity.createIntent(getActivity(), summaryInput), REQUEST_CODE_SUMMARY_SCREEN);
    }

    @Override
    public void openGuestAndRooms(@NonNull List<RoomCriteria> roomCriteria) {
        getActivity().startActivityForResult(RoomCriteriaActivity.createIntent(getActivity(), roomCriteria), REQUEST_CODE_ROOMS_SCREEN);
    }

    @Override
    public void showToolBarTitle(@NonNull String title) {
        binding.toolbarLayout.hotelDetailsToolbarTitle.setText(title);
    }

    @Override
    public Observable<RateClickEvent> onClickRateButton() {
        return clicksRelay.filter(o -> o instanceof RateClickEvent).map(o -> (RateClickEvent) o);
    }

    @Override
    public Observable<ImportantInfoClickEvent> onClickImportantHotelInfo() {
        return clicksRelay.filter(o -> o instanceof ImportantInfoClickEvent).map(o -> (ImportantInfoClickEvent) o);
    }

    @Override
    public Observable<DiscountCodeClickEvent> onClickDiscountCode() {
        return clicksRelay.filter(o -> o instanceof DiscountCodeClickEvent).map(o -> (DiscountCodeClickEvent) o);
    }

    @Override
    public Observable<ReadMoreClickEvent> onClickReadMore() {
        return clicksRelay.filter(o -> o instanceof ReadMoreClickEvent).map(o -> (ReadMoreClickEvent) o);
    }

    @Override
    public Observable<MapViewClickEvent> onClickMapView() {
        return clicksRelay.filter(o -> o instanceof MapViewClickEvent).map(o -> (MapViewClickEvent) o);
    }

    @Override
    public Observable<MainGalleryItemClickEvent> onClickMainPhotoGallery() {
        return clicksRelay
                .filter(event -> event instanceof MainGalleryItemClickEvent)
                .map(event -> (MainGalleryItemClickEvent) event);
    }

    @Override
    public Observable<SelectRateClickEvent> onClickMainSelectRateButton() {
        return clicksRelay.filter(e -> e instanceof SelectRateClickEvent).map(e -> (SelectRateClickEvent) e);
    }

    @Override
    public Observable<FindOutMoreClickEvent> onClickRoomFindOutMore() {
        return clicksRelay.filter(e -> e instanceof FindOutMoreClickEvent).map(e -> (FindOutMoreClickEvent) e);
    }

    @Override
    public void goBack() {
        getActivity().onBackPressed();
    }

    @Override
    public Observable<MakePhoneCallEvent> onMakePhoneCallClicked() {
        return clicksRelay.ofType(MakePhoneCallEvent.class);
    }

    @Override
    public void makePhoneCall(String number) {
        Intent callIntent = IntentUtils.createTelephoneIntent(number);
        if (IntentUtils.checkIntentResolvedActivity(getActivity(), callIntent)) {
            getActivity().startActivity(callIntent);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), R.string.phone_call_action_not_supported, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Observable<SendEmailEvent> onSendEmailClicked() {
        return clicksRelay.ofType(SendEmailEvent.class);
    }

    @Override
    public void sendEmail(String emailAddress) {
        getActivity().startActivity(IntentUtils.createEmailIntent(getActivity(), emailAddress, "", ""));
    }

    @Override
    public Observable<OpenWebLinkEvent> onOpenWebLinkClicked() {
        return clicksRelay.ofType(OpenWebLinkEvent.class);
    }

    @Override
    public void openWebLink(String url) {
        getActivity().startActivity(IntentUtils.createWebLinkIntent(url));
    }

    @Override
    public Observable<AccessibilityRoomTypeInfoClickEvent> onAccessibilityRoomTypeInfoClicked() {
        return clicksRelay.ofType(AccessibilityRoomTypeInfoClickEvent.class);
    }

    @Override
    public void startRoomVariantDetailsBottomSheetFragment(@NonNull String hotelCode) {
        RoomVariantDetailsBottomSheetFragment roomVariantFragment = new RoomVariantDetailsBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RoomVariantDetailsBottomSheetFragment.HOTEL_CODE, hotelCode);
        roomVariantFragment.setArguments(bundle);
        roomVariantFragment.show(getActivity().getSupportFragmentManager(), RoomVariantDetailsBottomSheetFragment.TAG);
    }

    @Override
    public void reBindBookingAvailabilitySubscriptions() {
        ((HotelDetailsActivity) getActivity()).reBindBookingAvailabilitySubscriptions();
    }

    @Override
    public void rebindAllSubscriptions() {
        ((HotelDetailsActivity) getActivity()).rebindAllSubscriptions();
    }

    public void startCalendarActivity(@NonNull LocalDate arrival, @NonNull LocalDate departure, Integer maxNight, Integer maxArrivalDate) {
        getActivity().startActivityForResult(HomeCalendarActivity.Companion.createIntent(
                getActivity(), arrival, departure, maxNight, maxArrivalDate), REQUEST_CODE_CALENDAR_SCREEN);
    }

    public void startCalendarActivity(Integer maxNights, Integer maxArrivalDate) {
        getActivity().startActivityForResult(HomeCalendarActivity.Companion
                .createIntent(getActivity(), maxNights, maxArrivalDate), REQUEST_CODE_CALENDAR_SCREEN);
    }

    @Override
    public void startGuestDetailsActivity(@NonNull BookingFlowInput bookingFlowInput) {
        Intent intent = GuestDetailsActivity.createIntent(getActivity(), bookingFlowInput);
        getActivity().startActivity(intent);
    }

    @Override
    public void startAdditionalInformationActivity(@NonNull ReviewBookingInput reviewBookingInput) {
        Intent intent = AdditionalInformationActivity.Companion.createIntent(getActivity(), reviewBookingInput);
        getActivity().startActivity(intent);
    }

    @Override
    public void startReviewAndBookActivity(@NonNull ReviewBookingInput reviewBookingInput) {
        Intent intent = ReviewBookActivity.createIntent(getActivity(), reviewBookingInput);
        getActivity().startActivity(intent);
    }

    @Override
    public Observable<EditDatesClickEvent> onClickEditDates() {
        return clicksRelay.filter(e -> e instanceof EditDatesClickEvent).map(e -> (EditDatesClickEvent) e);
    }

    @Override
    public Observable<CheckAvailabilityClickEvent> onClickCheckAvailability() {
        return clicksRelay.filter(e -> e instanceof CheckAvailabilityClickEvent).map(e -> (CheckAvailabilityClickEvent) e);
    }

    @Override
    public Observable<HotelsNearbyClickEvent> onClickHotelNearby() {
        return clicksRelay.filter(e -> e instanceof HotelsNearbyClickEvent).map(e -> (HotelsNearbyClickEvent) e);
    }

    @Override
    public Observable<CoronavirusDismissClickEvent> onClickCoronavirusDismiss() {
        return clicksRelay.filter(o -> o instanceof CoronavirusDismissClickEvent).map(o -> (CoronavirusDismissClickEvent) o);
    }

    @NonNull
    @Override
    public Observable<EditGuestClickEvent> onEditGuestClicked() {
        return clicksRelay.filter(o -> o instanceof EditGuestClickEvent).map(o -> (EditGuestClickEvent) o);
    }

    @Override
    public void showImportantInfoBottomSheetFragment(List<InfoItem> infoItems) {
        ImportantInfoBottomSheetFragment bottomSheet = new ImportantInfoBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImportantInfoBottomSheetFragment.HOTEL_IMPORTANT_INFOS, new ArrayList<>(infoItems));
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getActivity().getSupportFragmentManager(), ImportantInfoBottomSheetFragment.TAG);
    }

    @Override
    public void showAboutThisHotelBottomSheetFragment(@NonNull HotelFullDescription description) {
        AboutThisHotelBottomSheetFragment bottomSheet = new AboutThisHotelBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AboutThisHotelBottomSheetFragment.HOTEL_FULL_DESCRIPTION, description);
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getActivity().getSupportFragmentManager(), AboutThisHotelBottomSheetFragment.TAG);
    }


    @Override
    public void showInnBusinessGuestRestrictionAlert() {
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.hotel_details_bb_guest_restriction_title)
                .setMessage(R.string.hotel_details_bb_guest_restriction_msg)
                .setPositiveButton(R.string.hotel_details_bb_guest_restriction_cancel, (dialog, i) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void showCreateReservationErrorDialog() {
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.generic_error_title)
                .setMessage(R.string.generic_error_description)
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @Override
    public void startFullScreenMapActivity(@NonNull MapStartInfo info) {
        ActivityCompat.startActivity(getActivity(), HotelMapFullScreenActivity.createIntent(getActivity(), info), null);
    }

    @Override
    public void scrollToRates() {
        if (!binding.hotelDetailsContentList.hasNestedScrollingParent(ViewCompat.TYPE_NON_TOUCH)) {
            binding.hotelDetailsContentList.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
        }
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        int ratesPlanPosition = uiModelsAdapter.getRatesPlanPosition();
        if (ratesPlanPosition != -1) {
            smoothScroller.setTargetPosition(ratesPlanPosition);
            binding.hotelDetailsContentList.getLayoutManager().startSmoothScroll(smoothScroller);
        }
    }


    @Override
    public void showHotelFacilitiesBottomSheet(HotelFacilitiesModel hotelFacilitiesModel) {
        HotelFacilitiesBottomSheetFragment bottomSheet = new HotelFacilitiesBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(HotelFacilitiesBottomSheetFragment.HOTEL_FACILITIES_BOTTOM_SHEET, hotelFacilitiesModel);
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getActivity().getSupportFragmentManager(), bottomSheet.getTag());
    }

    @Override
    public void showParkingBottomSheet(HotelParkingModel hotelParkingModel) {
        ParkingBottomSheetFragment bottomSheet = new ParkingBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ParkingBottomSheetFragment.HOTEL_PARKING_KEY, hotelParkingModel);
        bottomSheet.setArguments(bundle);
        bottomSheet.show(getActivity().getSupportFragmentManager(), bottomSheet.getTag());
    }

    @Override
    public void showAccessibilityBottomSheet() {
        HotelAccessibilityBottomSheetFragment bottomSheet = new HotelAccessibilityBottomSheetFragment();
        bottomSheet.show(getActivity().getSupportFragmentManager(), bottomSheet.getTag());
    }

    @Override
    public Observable<FacilitiesClickEvent> onClickFacilities() {
        return clicksRelay.filter(o -> o instanceof FacilitiesClickEvent).map(o -> (FacilitiesClickEvent) o);
    }

    @Override
    public Observable<RoomTypesClickEvent> onClickRoomTypes() {
        return clicksRelay.filter(o -> o instanceof RoomTypesClickEvent).map(o -> (RoomTypesClickEvent) o);
    }

    @Override
    public Observable<ParkingClickEvent> onClickParking() {
        return clicksRelay.filter(o -> o instanceof ParkingClickEvent).map(o -> (ParkingClickEvent) o);
    }

    @Override
    public Observable<AccesibilityClickEvent> onClickAccessibility() {
        return clicksRelay.filter(o -> o instanceof AccesibilityClickEvent).map(o -> (AccesibilityClickEvent) o);
    }

    @Override
    public Observable<StartSummaryOrRoomSelectionEvent> onClickEmployeeContinue() {
        return clicksRelay.filter(o -> o instanceof StartSummaryOrRoomSelectionEvent)
                .map(o -> (StartSummaryOrRoomSelectionEvent) o);
    }

    public void showDoNotForgetPrivilegeCardAlert(@NonNull BathroomSelectionInput input,
                                                  Boolean selectOrBook, Boolean isAccessibleFlow) {
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.hotel_details_employee_offer_alert_dialog_title)
                .setMessage(R.string.hotel_details_employee_offer_alert_dialog_message)
                .setNegativeButton(R.string.hotel_details_employee_offer_alert_dialog_cancel, (dialog, i) -> dialog.dismiss())
                .setPositiveButton(R.string.hotel_details_employee_offer_alert_dialog_continue, (dialog, i)
                        -> clicksRelay.accept(new StartSummaryOrRoomSelectionEvent(input, selectOrBook, isAccessibleFlow)))
                .show();
    }

    @Override
    public void shouldShowLoadingSpinner(Boolean shouldShowLoadingSpinner) {
        binding.hdpProgressBarContainer.setVisibility(shouldShowLoadingSpinner ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showDiscountCodeBottomSheet(@NonNull DiscountCodeInput input) {
        HotelDetailsActivity activity = (HotelDetailsActivity) getActivity();
        if (activity == null) {
            return;
        }

        DiscountCodeBottomSheetFragment bottomSheet = DiscountCodeBottomSheetFragment.Companion.newInstance(input);
        bottomSheet.setDiscountAppliedCallback((discountCode, availabilityState, successMessage, promoKind) -> {
            // Always update availability state - both when applying and removing discount code
            activity.presenter.updateAvailabilityWithDiscountedRates(availabilityState, successMessage);

            // Only track success analytics when actually applying a code (not when removing)
            if (!discountCode.isEmpty()) {
                activity.presenter.trackDiscountCodeBoxSuccess(discountCode, promoKind);
            }
            return Unit.INSTANCE;
        });
        bottomSheet.setDiscountErrorCallback((discountCode, errorMessage, promoKind) -> {
            activity.presenter.trackDiscountCodeBoxError(discountCode, errorMessage, promoKind);
            return Unit.INSTANCE;
        });
        bottomSheet.show(activity.getSupportFragmentManager(), DiscountCodeBottomSheetFragment.TAG);
    }
}


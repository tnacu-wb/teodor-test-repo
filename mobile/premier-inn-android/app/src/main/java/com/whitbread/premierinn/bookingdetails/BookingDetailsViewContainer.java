package com.whitbread.premierinn.bookingdetails;

import static com.whitbread.premierinn.bookingdetails.BookingDetailsActivity.AMEND_BOOKING_REQUEST;
import static com.whitbread.premierinn.bookingdetails.BookingDetailsActivity.NON_AMEND_BOOKING_REQUEST;
import static com.whitbread.premierinn.bookingdetails.BookingDetailsState.StateType.Success;
import static com.whitbread.premierinn.ciol.fragments.RoomKeyBottomSheetFragmentKt.ROOM_KEY;
import static com.whitbread.premierinn.data.common.Constants.EMPTY_STRING;
import static com.whitbread.premierinn.mybookings.BookingUiModelKt.BASKET_STATUS_PRE_CHECKED_IN;
import static com.whitbread.premierinn.mybookings.BookingUiModelKt.BASKET_STATUS_PRE_CHECKED_OUT;
import static com.whitbread.premierinn.mybookings.BookingUiModelKt.BOOKING_STATUS_PAST;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.amend.AmendReservationActivity;
import com.whitbread.premierinn.amend.NonAmendableReservationActivity;
import com.whitbread.premierinn.amend.ParcelablePromotionsInformationDomain;
import com.whitbread.premierinn.amend.amendguestsrooms.ParcelableAmendTotal;
import com.whitbread.premierinn.api.response.availability.Coordinates;
import com.whitbread.premierinn.apprating.AppRatingModal;
import com.whitbread.premierinn.bookingdetails.view.AccessibilityInfoView;
import com.whitbread.premierinn.bookingdetails.view.ExtrasAdapter;
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl;
import com.whitbread.premierinn.ciol.entity.PreStayUiModel;
import com.whitbread.premierinn.ciol.entity.upsells.UpsellItem;
import com.whitbread.premierinn.ciol.fragments.ReadyToLeaveBottomSheetFragment;
import com.whitbread.premierinn.ciol.fragments.RoomKeyBottomSheetFragment;
import com.whitbread.premierinn.ciol.uimodel.InfoBottomSheetData;
import com.whitbread.premierinn.ciol.uimodel.RoomKeyInstructionsModel;
import com.whitbread.premierinn.ciol.utils.CiolViewsExtensionsKt;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.managebooking.ManageBookingInput;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.common.utils.IntentUtils;
import com.whitbread.premierinn.common.utils.LocationUtils;
import com.whitbread.premierinn.common.utils.RxUtils;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl;
import com.whitbread.premierinn.databinding.ActivityBookingDetailsBinding;
import com.whitbread.premierinn.domain.booking.entity.Booking;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.hotel.entity.Hotel;
import com.whitbread.premierinn.hoteldetails.HotelDetailsActivity;
import com.whitbread.premierinn.hoteldetails.HotelDetailsInput;
import com.whitbread.premierinn.hoteldetails.uimodel.CallUsUiModel;
import com.whitbread.premierinn.parking.ParkingBottomSheet;
import com.whitbread.premierinn.parking.ParkingBottomSheetKt;
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import kotlin.Unit;

public class BookingDetailsViewContainer extends ViewContainer implements BookingDetailsPresenter.View {

    private final PublishRelay<Object> rateUsClicks = PublishRelay.create();
    private final PublishRelay<Object> cancelClicks = PublishRelay.create();
    private final PublishRelay<Object> feedbackClicks = PublishRelay.create();
    private final PublishRelay<Object> sendInvoiceClicks = PublishRelay.create();
    private boolean showPaymentStatusLabel = false;
    private ActivityBookingDetailsBinding binding;

    public BookingDetailsViewContainer(@NonNull BaseActivity activity, ActivityBookingDetailsBinding binding) {
        super(activity);
        this.binding = binding;
        getActivity().setToolbar(activity.getString(R.string.booking_details), true);
    }

    @Override
    public void bindBookingState(@NonNull BookingDetailsState state,
                                 @NonNull SimplePersistenceManagerImpl storage,
                                 @NonNull BusinessPersistenceManagerImpl businessStorage,
                                 ParcelableAmendTotal amendTotal,
                                 @NonNull String checkInInfo,
                                 @NonNull String checkOutInfo,
                                 @NonNull PriceDomain balanceOutstanding,
                                 @NonNull DeviceLocaleProvider getDeviceLocaleProvider,
                                 String eciLcoMessage,
                                 @NonNull List<? extends UpsellItem> upsellItems,
                                 boolean isBusinessBooking) {
        if (state.getType() == Success) {
            final BookingBasicsUiModel bookingUiModel = state.getBookingUiModel();
            final BookingDetailsUiModel hotelUiModel = state.getBookingDetails();
            if (bookingUiModel != null) {
                binding.bookingDetailsCheckInTime.setText(checkInInfo);
                binding.bookingDetailsCheckOutTime.setText(checkOutInfo);
                if (bookingUiModel.getBooking().isEmployeeBooking()) {
                    binding.bookingDetailsEmployeePrivilegeCardMessage.setVisibility(View.VISIBLE);
                }
                if (!eciLcoMessage.isEmpty()) {
                    binding.bookingEciLcoInfobox.setVisibility(View.VISIBLE);
                    binding.bookingEciLcoInfobox.setText(eciLcoMessage);
                }
                final Booking booking = bookingUiModel.getBooking();
                binding.bookingDetailsHotelName.setText(booking.getHotelName());

                binding.bookingDetailsHotelNameContainer.setTag(booking.getHotelCode());
                binding.bookingDetailsReference.setText(booking.getBookingReference());
                binding.bookingDetailsCheckInDate.setText(bookingUiModel.getCheckInDateFormatted());
                binding.bookingDetailsCheckOutDate.setText(bookingUiModel.getCheckOutDateFormatted());

                binding.sendInvoice.setTag(new SendInvoiceAction());
                binding.sendInvoice.setVisibility(BOOKING_STATUS_PAST.equals(booking.getBookingStatus()) ? View.VISIBLE : View.GONE);

                int maxRooms;
                if (isBusinessBooking) {
                    maxRooms = businessStorage.getMaxRoomsInnBusiness();
                } else {
                    maxRooms = storage.getMaxRoomsLeisure();
                }
                boolean isNumberOfRoomsInvalid = Integer.parseInt(bookingUiModel.getNumberOfRoomsFormatted()
                        .replaceAll("\\D+", EMPTY_STRING)) > maxRooms;

                if (!isBusinessBooking) {
                    if (isNumberOfRoomsInvalid) {
                        binding.bookingDetailsNights.setVisibility(View.GONE);
                        binding.bookingDetailsGuestRoom.setVisibility(View.GONE);
                        binding.bookingDetailsPriceContainer.setVisibility(View.GONE);
                        binding.bookingDetailsBalanceOutstandingContainer.setVisibility(View.GONE);
                        binding.bookingDetailsTotalCostSeparator.setVisibility(View.GONE);
                    } else {
                        setBookingDetailsWhenValid(balanceOutstanding, getDeviceLocaleProvider, bookingUiModel);
                    }
                } else {
                    setBookingDetailsWhenValid(balanceOutstanding, getDeviceLocaleProvider, bookingUiModel);
                }

                displayManageBooking(booking);
                displayStatusBanner(bookingUiModel);
                displayConfirmChangesBox(amendTotal, bookingUiModel.getInfoMessage());

                if (bookingUiModel.getJustBooked() && getActivity().getSupportActionBar() != null) {
                    getActivity().getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
                }

                if (hotelUiModel != null) {
                    if (bookingUiModel.isCheckInOnlineFlagEnabled()) {
                        binding.bookingDetailsCiolButton.setVisibility(
                                hotelUiModel.isCheckInOnlineEnabled() ? View.VISIBLE : View.GONE
                        );
                        if (hotelUiModel.getBasketStatus() != null
                                && BASKET_STATUS_PRE_CHECKED_IN.equals(hotelUiModel.getBasketStatus())) {
                            binding.bookingStatus.setVisibility(View.VISIBLE);
                            binding.bookingStatus.setText(getActivity().getString(R.string.checked_in));
                            binding.rlBookingsDetailsRoomKeyInstructions.setTag(new RoomKeyInstructionsAction());
                            binding.rlBookingsDetailsRoomKeyInstructions.setVisibility(View.VISIBLE);
                        } else {
                            binding.bookingStatus.setVisibility(View.GONE);
                            binding.rlBookingsDetailsRoomKeyInstructions.setVisibility(View.GONE);
                        }
                    } else {
                        binding.bookingDetailsCiolButton.setVisibility(View.GONE);
                        binding.bookingStatus.setVisibility(View.GONE);
                        binding.rlBookingsDetailsRoomKeyInstructions.setVisibility(View.GONE);
                    }

                    if (hotelUiModel.getBasketStatus() != null
                            && BASKET_STATUS_PRE_CHECKED_OUT.equals(hotelUiModel.getBasketStatus())) {
                        binding.bookingStatus.setVisibility(View.VISIBLE);
                        binding.bookingStatus.setText(getActivity().getString(R.string.past_reservation_tag));
                        binding.bookingStatus.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_light_gray_stroke));
                        binding.bookingStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.dark_grey_2));
                    }

                    binding.bookingDetailsReadyToLeaveButton.setVisibility(
                            hotelUiModel.isCheckOutOnlineEnabled() ? View.VISIBLE : View.GONE
                    );
                }
            }

            if (hotelUiModel != null) {
                binding.bookingDetailsPriceContainer.setRateName(hotelUiModel.getRateName());
                binding.bookingDetailsCityTaxInfoLabel
                        .setHtmlText(getActivity().getString(R.string.booking_details_business_city_tax_info_message,
                        hotelUiModel.getBusinessUse()
                                ? getActivity().getString(R.string.booking_details_trip_type_business)
                                : getActivity().getString(R.string.booking_details_trip_type_leisure),
                        hotelUiModel.getCityTaxUrl()));
                binding.bookingDetailsCityTaxInfoLabel.setVisibility(hotelUiModel.getShowCityTaxInfo() ? View.VISIBLE : View.GONE);
                binding.bookingDetailsAddress.setText(hotelUiModel.getAddress());
                binding.bookingDetailsDirectionButton.setTag(LocationUtils.gMapsDirectionsUri(hotelUiModel.getLocation().getLatitude(),
                        hotelUiModel.getLocation().getLongitude()));
                binding.bookingDetailsDirectionButton.setVisibility(View.VISIBLE);
                binding.bookingDetailsCiolButton.setTag(new CheckInOnlineAction());
                binding.bookingDetailsReadyToLeaveButton.setTag(new ReadyToLeaveAction());
                binding.bookingDetailsMap.setMapInfo(Coordinates.create(
                        (float) hotelUiModel.getLocation().getLatitude(),
                        (float) hotelUiModel.getLocation().getLongitude()), null, null, hotelUiModel.getHotelBrand());
                binding.bookingDetailsParkingIcon.setVisibility(hotelUiModel.getParkingPair() != null ? View.VISIBLE : View.GONE);
                binding.bookingDetailsParkingDescription.setVisibility(hotelUiModel.getParkingPair() != null ? View.VISIBLE : View.GONE);
                if (hotelUiModel.getParkingPair() != null) {
                    binding.bookingDetailsParkingIcon.setImageDrawable(
                            ContextCompat.getDrawable(getActivity(), hotelUiModel.getParkingPair().getFirst()));
                    binding.bookingDetailsParkingDescription.setText(hotelUiModel.getParkingPair().getSecond());
                }
                binding.bookingDetailsPriceContainer.showPriceIncludesTaxesAndFeesMessage(false);

                String calendarHotelLabel = hotelUiModel.getHotelBrand() == Hotel.Brand.HUB
                        ? getActivity().getString(R.string.hub_by_premier_inn)
                        : getActivity().getString(R.string.premier_inn);

                if (bookingUiModel != null) {
                    Booking booking = bookingUiModel.getBooking();

                    if (booking.isCancelled()) {
                        binding.bookingDetailsAddToCalendarButton.setVisibility(View.GONE);
                    } else {
                        binding.bookingDetailsAddToCalendarButton.setVisibility(View.VISIBLE);
                        binding.bookingDetailsAddToCalendarButton.setTag(new AddToCalendarAction(
                                booking.getArrivalDate(),
                                booking.getDepartureDate(),
                                getActivity().getString(R.string.booking_details_calendar_event_title, booking.getHotelName()),
                                getActivity().getString(R.string.booking_details_calendar_event_description,
                                        booking.getHotelName(), calendarHotelLabel,
                                        booking.getBookingReference()),
                                hotelUiModel.getAddress())
                        );
                    }
                    if (!upsellItems.isEmpty()) {
                        displayExtras(
                                getDeviceLocaleProvider,
                                (List<UpsellItem>) upsellItems,
                                bookingUiModel.getNumberOfNightsFormatted(),
                                (int) booking.getNumberOfNights()
                        );
                    }
                }

                if (hotelUiModel.getParkingPair() != null && hotelUiModel.getParkingDescription() != null) {
                    binding.bookingDetailsParkingContainer.setVisibility(View.VISIBLE);
                    binding.bookingDetailsParkingContainer.setTag(new ParkingAction(hotelUiModel.getParkingDescription()));
                }

                CallUsUiModel callUsUiModel = hotelUiModel.getCallUsUiModel();
                binding.bookingDetailsCallUsView.setVisibility(callUsUiModel != null ? View.VISIBLE : View.GONE);
                if (callUsUiModel != null) {
                    binding.bookingDetailsCallUsView.setTelephoneNumber(callUsUiModel.telNumber());
                    binding.bookingDetailsCallUsView.setLabel(callUsUiModel.label());
                    binding.bookingDetailsCallUsView.setDescription(callUsUiModel.telCostInfo());
                }

                AccessibilityUiModel accessibilityUiModel = hotelUiModel.getAccessibilityInfo();
                if (accessibilityUiModel != null) {
                    binding.accessibilityInfoContainer.setVisibility(View.VISIBLE);
                    AccessibilityInfoView accessibilityInfo = getActivity().findViewById(R.id.accessibility_info_panel);
                    accessibilityInfo.setDescriptionText(accessibilityUiModel.getDescriptionText());
                    accessibilityInfo.setButtonText(accessibilityUiModel.getCallButtonText());
                    accessibilityInfo.setTelephoneNumber(accessibilityUiModel.getPhoneNumber());
                } else {
                    binding.accessibilityInfoContainer.setVisibility(View.GONE);
                }
            }
        }
        if (state.getType() == BookingDetailsState.StateType.Error) {
            binding.bookingDetailsErrorBanner.setVisibility(View.VISIBLE);
        }
    }

    private void setBookingDetailsWhenValid(@NonNull PriceDomain balanceOutstanding,
                                            @NonNull DeviceLocaleProvider getDeviceLocaleProvider,
                                            BookingBasicsUiModel bookingUiModel) {
        binding.bookingDetailsNights.setVisibility(View.VISIBLE);
        binding.bookingDetailsGuestRoom.setVisibility(View.VISIBLE);
        binding.bookingDetailsPriceContainer.setVisibility(View.VISIBLE);
        binding.bookingDetailsBalanceOutstandingContainer.setVisibility(View.VISIBLE);
        binding.bookingDetailsTotalCostSeparator.setVisibility(View.VISIBLE);
        binding.bookingDetailsNights.setText(bookingUiModel.getNumberOfNightsFormatted());
        binding.bookingDetailsGuestRoom.setText(bookingUiModel.getNumberOfRoomsFormatted());
        binding.bookingDetailsPriceContainer.setTotalPrice(bookingUiModel.getTotalCostFormatted());
        binding.bookingDetailsBalanceOutstandingContainer.setBalanceOutstandingPrice(balanceOutstanding, getDeviceLocaleProvider);
    }

    @Override
    public void shouldShowLoadingSpinner(boolean showLoading) {
        binding.bookingDetailsLoadingContainer.setClickable(showLoading);
        binding.bookingDetailsLoadingContainer.setVisibility(showLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAmendJourneyError() {
        Toast.makeText(getActivity(), "Something went wrong.Try again later", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showPromotionAmendmentNotAllowedDialog(@NonNull String  promotionalAmendMessage) {
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.booking_details_promotion_amend_message_title)
                .setMessage(promotionalAmendMessage)
                .setPositiveButton(R.string.dialog_ok_button, (dialog, i) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @Override
    public void showStartCheckInOnlineError() {
        Toast.makeText(getActivity(), getActivity().getString(R.string.start_ciol_generic_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showReadyToLeaveBottomSheet(String guestName, String basketReference, PreStayUiModel preStayUiModel) {
        ReadyToLeaveBottomSheetFragment readyToLeaveBottomSheetFragment =
                ReadyToLeaveBottomSheetFragment.newInstance(guestName, basketReference, preStayUiModel);
        readyToLeaveBottomSheetFragment.show(getActivity().getSupportFragmentManager(), readyToLeaveBottomSheetFragment.getTag());
    }

    private void displayStatusBanner(BookingBasicsUiModel bookingUiModel) {
        if (bookingUiModel.getBannerMessage() == null) {
            binding.bookingDetailsPaymentStatusLabel.setVisibility(View.GONE);
        } else {
            binding.bookingDetailsPaymentStatusLabel.setVisibility(View.VISIBLE);
            switch (bookingUiModel.getBannerMessage().getType()) {
                case CANCELLED:
                    binding.bookingDetailsPaymentStatusLabel.setBackgroundResource(R.color.red);
                    break;
                case SUCCESS:
                    binding.bookingDetailsPaymentStatusLabel.setBackgroundResource(R.color.success_green);
                    break;
                case INFORMATION:
                    binding.bookingDetailsPaymentStatusLabel.setBackgroundResource(R.color.information_blue);
                    break;
                default:
                    throw new IllegalArgumentException("No type defined for such a view");
            }
            binding.bookingDetailsPaymentStatusLabel.setText(bookingUiModel.getBannerMessage().getText());
            showPaymentStatusLabel = true;
        }
    }

    public void displayConfirmChangesBox(@Nullable ParcelableAmendTotal price, @Nullable String infoMessage) {
        if (price != null && !price.getAmount().equals("£0.00")) {
            binding.amendConfirmChangesBanner.setVisibility(View.VISIBLE);
            binding.bookingDetailsInfoBox.setVisibility(View.GONE);

            if (price.getDescription().equals(getActivity().getString(R.string.review_amends_balance_description_poa))) {
                binding.amendConfirmChangesBannerInfoMessageBoxView.setText(
                        getActivity().getString(R.string.my_bookings_amend_confirm_pay_on_arrival_body,
                                price.getAmount()));
            } else {
                binding.amendConfirmChangesBannerInfoMessageBoxView.setText(getActivity()
                        .getString(R.string.my_bookings_amend_confirm_refund_title));
                binding.amendConfirmChangesBannerInfoMessageBoxView.setText(
                        getActivity().getString(R.string.my_bookings_amend_confirm_refund_body,
                                price.getAmount()));
            }
            binding.amendConfirmChangesBannerInfoMessageBoxView.setVisibility(View.VISIBLE);
            binding.bookingDetailsPaymentStatusLabel.setVisibility(View.GONE);
        } else {
            binding.amendConfirmChangesBanner.setVisibility(View.GONE);
            binding.bookingDetailsPaymentStatusLabel.setVisibility(showPaymentStatusLabel ? View.VISIBLE : View.GONE);
            if (infoMessage != null) {
                binding.bookingDetailsInfoBox.setVisibility(View.VISIBLE);
                binding.bookingDetailsInfoBox.setText(infoMessage);
            } else {
                binding.bookingDetailsInfoBox.setVisibility(View.GONE);
            }
        }
    }

    private void displayManageBooking(Booking booking) {
        if ((booking.getCancellable() || booking.getAmendable()) && !booking.isCancelled()) {
            binding.bookingDetailsManageBookingButton.setVisibility(View.VISIBLE);
        } else {
            binding.bookingDetailsManageBookingButton.setVisibility(View.GONE);
        }
    }

    private void displayExtras(
        DeviceLocaleProvider getDeviceLocaleProvider,
        List<UpsellItem> upsellItems,
        String numberOfNightsFormatted,
        int numberOfNights
    ) {
        ExtrasAdapter adapter = new ExtrasAdapter();
        binding.extrasHeader.setVisibility(upsellItems.isEmpty() ? View.GONE : View.VISIBLE);
        binding.extrasRecyclerview.setVisibility(upsellItems.isEmpty() ? View.GONE : View.VISIBLE);
        binding.extrasRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.extrasRecyclerview.setAdapter(adapter);
        adapter.setDeviceLocale(getDeviceLocaleProvider.getDeviceLocale());
        adapter.setUpsellItems(upsellItems);
        adapter.setNumberOfNightsFormatted(numberOfNightsFormatted);
        adapter.setNumberOfNights(numberOfNights);
    }

    @NonNull
    @Override
    public Observable<MapDirectionsAction> onDirectionClicked() {
        return RxView.clicks(binding.bookingDetailsDirectionButton)
                .compose(RxUtils.mapViewTagToTypeIfItIsAllowed(binding.bookingDetailsDirectionButton, String.class))
                .map(MapDirectionsAction::new);
    }

    @Override
    public void startUriActivity(@NonNull String uri) {
        getActivity().startActivity(IntentUtils.createWebLinkIntent(uri));
    }

    @NonNull
    @Override
    public Observable<AddToCalendarAction> onAddToCalendarClick() {
        return RxView.clicks(binding.bookingDetailsAddToCalendarButton)
                .compose(RxUtils.mapViewTagToTypeIfItIsAllowed(binding.bookingDetailsAddToCalendarButton, AddToCalendarAction.class));
    }

    @Override
    public @NotNull Observable<@NotNull SendInvoiceAction> onSendInvoiceClicked() {
        return RxView.clicks(binding.sendInvoice)
                .compose(RxUtils.mapViewTagToTypeIfItIsAllowed(binding.sendInvoice, SendInvoiceAction.class));
    }

    @Override
    public void sendInvoice() {
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.send_invoice)
                .setMessage(R.string.send_invoice_dialog_message)
                .setPositiveButton(R.string.dialog_positive_send_button, (dialog, i) -> {
                    sendInvoiceClicks.accept(new Object());
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.dialog_positive_cancel_button, (dialog, i) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @Override
    public @NotNull Observable<@NotNull Object> onSendInvoiceDialogClicked() {
        return sendInvoiceClicks;
    }

    @Override
    public void startCalendarApp(@NonNull AddToCalendarAction action) {
        getActivity().startActivity(IntentUtils.createCalendarIntent(action.getBeginTime(), action.getEndTime(),
                action.getTitle(), action.getDescription(), action.getLocation()));
    }

    @NonNull
    @Override
    public Observable<Unit> onFaqButtonClicked() {
        return RxView.clicks(getActivity().findViewById(R.id.rl_bookings_details_faq_section));
    }

    @NonNull
    @Override
    public Observable<RoomKeyInstructionsAction> onRoomKeyInstructionsButtonClicked() {
        return RxView.clicks(binding.rlBookingsDetailsRoomKeyInstructions)
                .compose(RxUtils.mapViewTagToTypeIfItIsAllowed(
                        binding.rlBookingsDetailsRoomKeyInstructions, RoomKeyInstructionsAction.class));
    }

    @NonNull
    @Override
    public Observable<ParkingAction> onParkingButtonClicked() {
        return RxView.clicks(binding.bookingDetailsParkingContainer)
            .compose(RxUtils.mapViewTagToTypeIfItIsAllowed(
                binding.bookingDetailsParkingContainer, ParkingAction.class));
    }

    @Override
    public void showRoomKeyInstructionsBottomSheet(RoomKeyInstructionsModel roomKeyInstructionsModel) {
        RoomKeyBottomSheetFragment roomKeyBottomSheet = new RoomKeyBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ROOM_KEY, roomKeyInstructionsModel);
        roomKeyBottomSheet.setArguments(bundle);
        roomKeyBottomSheet.show(getActivity().getSupportFragmentManager(), roomKeyBottomSheet.getTag());
    }

    @NonNull
    @Override
    public Observable<Unit> onManageBookingClicked() {
        return RxView.clicks(binding.bookingDetailsManageBookingButton);
    }

    @Override
    public void startAmendBookingActivity(@NotNull ManageBookingInput manageBookingInput, @NotNull String uuidBasketReference,
                                          @NotNull String token, @NotNull String hotelBrand,
                                          @NonNull String hotelName,
                                          boolean isEciLcoBooking, @NonNull List<ParcelableExtrasItem> listOfEciLco,
                                          boolean isBusinessBooker, ParcelablePromotionsInformationDomain promotionsInfoDomain,
                                          @NotNull String bookingFlowId) {
        getActivity().startActivityForResult(AmendReservationActivity.createIntent(getActivity(),
                manageBookingInput, null, uuidBasketReference, token, hotelBrand,
                hotelName, isEciLcoBooking, new ArrayList<>(listOfEciLco), isBusinessBooker, promotionsInfoDomain, bookingFlowId
                ), AMEND_BOOKING_REQUEST);
    }

    @Override
    public void startNonAmendableBookingActivity(@NotNull ManageBookingInput input, @NotNull String uuidBasketReference,
                                                 @NotNull String token, int nightsCount, int roomCriteriaSize) {
        getActivity().startActivityForResult(NonAmendableReservationActivity.createIntent(getActivity(),
                input, uuidBasketReference, binding.bookingDetailsHotelName.getText().toString(), token, nightsCount, roomCriteriaSize),
                NON_AMEND_BOOKING_REQUEST);
    }

    @NonNull
    @Override
    public Observable<Unit> onPriceBreakdownButtonClicked() {
        return RxView.clicks(binding.bookingDetailsPriceBreakdownButton);
    }

    @NonNull
    @Override
    public Observable<HotelDetailsAction> onHotelNameClick() {
        return RxView.clicks(binding.bookingDetailsHotelName)
                .compose(RxUtils.mapViewTagToTypeIfItIsAllowed(binding.bookingDetailsHotelNameContainer, String.class))
                .map(HotelDetailsAction::new);
    }

    @NonNull
    @Override
    public Observable<CheckInOnlineAction> onCheckInOnlineButtonClick() {
        return RxView.clicks(binding.bookingDetailsCiolButton)
                .compose(RxUtils.mapViewTagToTypeIfItIsAllowed(binding.bookingDetailsCiolButton, CheckInOnlineAction.class));
    }

    @NonNull
    @Override
    public Observable<ReadyToLeaveAction> onReadyToLeaveButtonClick() {
        return RxView.clicks(binding.bookingDetailsReadyToLeaveButton)
                .compose(RxUtils.mapViewTagToTypeIfItIsAllowed(binding.bookingDetailsReadyToLeaveButton, ReadyToLeaveAction.class));
    }

    @Override
    public void startHotelDetailsActivity(@NonNull String hotelCode, String hotelBrand) {
        getActivity().startActivity(HotelDetailsActivity.createIntent(getActivity(),
                HotelDetailsInput.fromBookingDetails(hotelCode, hotelBrand).build()));
    }

    @Override
    public void showParkingDetailsBottomSheet(@NonNull String parkingDescription) {
        Bundle args = new Bundle();
        args.putString(ParkingBottomSheetKt.PARKING_DESCRIPTION_KEY, parkingDescription);
        ParkingBottomSheet parkingBottomSheet = new ParkingBottomSheet();
        parkingBottomSheet.setArguments(args);
        parkingBottomSheet.setCancelable(true);
        parkingBottomSheet.show(getActivity().getSupportFragmentManager());
    }

    @Override
    public void showCheckInInfoBottomSheet(@NonNull PreStayUiModel preStayModel) {
        InfoBottomSheetData infoBottomSheetData = InfoBottomSheetData.Companion.startCheckInData(getActivity().getResources());
        CiolViewsExtensionsKt.showCheckInInformationBottomSheet(getActivity(), infoBottomSheetData, () -> {
            IntentUtils.startCiolActivity(getActivity(), preStayModel);
            return Unit.INSTANCE;
        });
    }

    @Override
    public void scrollToBanner() {
        binding.scrollView.smoothScrollTo(0, 0);
    }

    @Override
    public void showAppRatingPrompt() {
        AppRatingModal modal = new AppRatingModal();
        modal.setCancelable(false);
        modal.show(getActivity().getSupportFragmentManager());
        modal.setListener(new AppRatingModal.ActionListener() {
            @Override
            public void onCancel() {
                cancelClicks.accept(new Object());
            }

            @Override
            public void onRateUsClicked() {
                rateUsClicks.accept(new Object());
            }

            @Override
            public void onFeedbackClicked() {
                feedbackClicks.accept(new Object());
            }
        });
    }

    @NonNull
    @Override
    public Observable<Object> onAppRatingPromptOkClicked() {
        return rateUsClicks;
    }

    @NonNull
    @Override
    public Observable<Object> onAppRatingPromptCancelClicked() {
        return cancelClicks;
    }

    @NonNull
    @Override
    public Observable<Object> onAppRatingFeedbackClicked() {
        return feedbackClicks;
    }

    @Override
    public void navigateToPlaystore(@NotNull String fallbackUrl) {
        Context appContext = getActivity().getApplicationContext();
        try {
            getActivity().startActivity(IntentUtils.createPlaystoreIntent(appContext));
        } catch (ActivityNotFoundException e) {
            getActivity().startActivity(IntentUtils.createWebLinkIntent(String.format(fallbackUrl,
                    appContext.getPackageName())));
        }
    }


    @Override
    public void showFeedbackPrompt(@NotNull String receiverAddress, @NotNull String
            subject, @NotNull String body) {
        getActivity().startActivity(IntentUtils.createEmailIntent(getActivity().getApplicationContext(),
                receiverAddress, subject, body));
    }

    @Override
    public void showAmendBookingInfoBanner(@NonNull String message, boolean isBusinessBooker) {
        binding.bookingAmendInfoBanner.setVisibility(View.VISIBLE);

        int messageResId = isBusinessBooker
                ? R.string.my_bookings_amend_info_message_fb_business
                : R.string.my_bookings_amend_info_message_fb;

        binding.bookingAmendInfoBanner.setHtmlText(getActivity().getString(messageResId, message));
    }

    @Override
    public void showSendInvoiceMessage(boolean success, @NonNull String email) {
        binding.invoiceSendMessage.setVisibility(View.VISIBLE);
        if (success && !email.equals(EMPTY_STRING)) {
            String formatText = String.format(getActivity().getString(R.string.send_invoice_success_text), email);
            SpannableString spannable = new SpannableString(formatText);
            int start = formatText.indexOf(email);
            int end = start + email.length();
            spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            binding.invoiceSendMessage.setText(spannable);
            binding.invoiceSendMessage.setIconColorFilter(R.color.success_green);
            binding.invoiceSendMessage.setIconResource(R.drawable.notifications_success);
            binding.invoiceSendMessage.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_green_stroke_background));
        } else {
            binding.invoiceSendMessage.setText(R.string.send_invoice_fail_text);
            binding.invoiceSendMessage.setIconResource(R.drawable.ic_info);
            binding.invoiceSendMessage.setIconColorFilter(R.color.new_error_red);
            binding.invoiceSendMessage.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_red_stroke_background));
        }
    }
}
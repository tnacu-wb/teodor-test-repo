package com.whitbread.premierinn.findbooking;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState;
import static com.whitbread.premierinn.common.utils.DateUtils.date6MonthsInPast;
import static com.whitbread.premierinn.domain.booking.usecase.ImportBookingKt.KEY_BOOKING_EXISTS;
import static com.whitbread.premierinn.domain.booking.usecase.ImportBookingKt.KEY_BOOKING_NOT_FOUND;
import static com.whitbread.premierinn.domain.booking.usecase.ImportBookingKt.KEY_PAST_BOOKING;
import static com.whitbread.premierinn.findbooking.analytics.FindBookingAnalyticsData.ERROR_BOOKING_ALREADY_IMPORTED;
import static com.whitbread.premierinn.findbooking.analytics.FindBookingAnalyticsData.ERROR_BUSINESS_BOOKING_IMPORT;
import static com.whitbread.premierinn.findbooking.analytics.FindBookingAnalyticsData.ERROR_FETCHING_BOOKING;
import static com.whitbread.premierinn.findbooking.analytics.FindBookingAnalyticsData.ERROR_PAST_BOOKING_IMPORT;
import static com.whitbread.premierinn.findbooking.analytics.FindBookingAnalyticsData.ERROR_UNABLE_TO_RETRIEVE_BOOKING;

import androidx.annotation.NonNull;

import com.whitbread.premierinn.api.response.ErrorBody;
import com.whitbread.premierinn.common.AsyncResult;
import com.whitbread.premierinn.common.AsyncResultKt;
import com.whitbread.premierinn.common.analytics.CampaignDataModel;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.format.FormatExtensionsKt;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.booking.usecase.BusinessBookingImportError;
import com.whitbread.premierinn.domain.booking.usecase.ImportBooking;
import com.whitbread.premierinn.domain.booking.usecase.ImportBookingException;
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer;
import com.whitbread.premierinn.domain.graphql.findBooking.entity.FindBookingDomain;
import com.whitbread.premierinn.findbooking.analytics.FindBookingAnalyticsData;

import org.threeten.bp.LocalDate;

import java.util.Locale;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;

@ActivityRetainedScoped
public class FindBookingPresenter extends Presenter<FindBookingPresenter.View> {

    private final CompositeDisposable viewCompositeDisposable;
    private final ImportBooking importBooking;
    private final TrackingAnalytics analytics;
    private final DeviceLocaleProvider deviceLocaleProvider;
    private final LogService crashlyticsLogger;
    private String referenceBooking;
    private String lastName;
    private LocalDate arrivalDate;
    private FindBookingInput findBookingInput;
    private GetCustomer getCustomer;

    @Inject
    public FindBookingPresenter(@NonNull ImportBooking importBooking,
                                @NonNull CompositeDisposable compositeDisposable,
                                @NonNull TrackingAnalytics analytics,
                                @NonNull DeviceLocaleProvider deviceLocaleProvider,
                                @NonNull LogService crashlyticsLogger,
                                @NonNull GetCustomer getCustomer) {
        this.importBooking = importBooking;
        this.viewCompositeDisposable = compositeDisposable;
        this.analytics = analytics;
        this.deviceLocaleProvider = deviceLocaleProvider;
        this.crashlyticsLogger = crashlyticsLogger;
        this.getCustomer = getCustomer;
    }

    public void initParams(FindBookingInput findBookingInput) {
        this.findBookingInput = findBookingInput;
    }

    @Override
    protected void onAttachView(View view) {
        if (findBookingInput != null) {
            String arrivalDate = findBookingInput.getArrivalDate();
            this.referenceBooking = findBookingInput.getBookingReference();
            this.lastName = findBookingInput.getLastName();
            if (!arrivalDate.isBlank()) {
                try {
                    this.arrivalDate = FormatExtensionsKt.toLocalDate(findBookingInput.getArrivalDate());
                } catch (Exception e) {
                    this.arrivalDate = null;
                }

            }

            if (findBookingInput.getLastName().isEmpty()) {
                fetchCustomerLastNameAndUpdateView(view);
            } else {
                view.updateScreenFields(findBookingInput);
            }
        }

        viewCompositeDisposable.add(view.onClickArrivalDate().subscribe(__ ->
                view.startCalendarActivity(date6MonthsInPast(), arrivalDate == null ? LocalDate.now() : arrivalDate)));

        viewCompositeDisposable.add(view.onArrivalDateSelected()
                .subscribe(calendarDay -> {
                    this.arrivalDate = calendarDay;
                    if (isViewAttached()) {
                        view.showArrivalDateError(false);
                        view.showArrivalDate(arrivalDate);
                    }
                }));

        viewCompositeDisposable.add(view.onReferenceBookingChanged()
                .subscribe(referenceBooking -> {
                    if (referenceBooking.length() > 0) {
                        view.showBookingRefError(false);
                    }
                    this.referenceBooking = referenceBooking;
                }));

        viewCompositeDisposable.add(view.onLastNameChanged()
                .subscribe(lastName -> {
                    if (lastName.length() > 0) {
                        view.showLastNameError(false);
                    }
                    this.lastName = lastName;
                }));

        viewCompositeDisposable.add(view.onFindBookingClick()
                .filter(__ -> validateFields(view))
                .flatMap(isFormValid -> AsyncResultKt.mapToAsyncResult(
                        importBooking.execute(referenceBooking, lastName, arrivalDate,
                                        deviceLocaleProvider.getDeviceLanguage().toLowerCase(),
                                        deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale())
                                                .toLowerCase(Locale.getDefault())
                                       )
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())))
                .subscribe(state -> {
                            view.showLoading(state instanceof AsyncResult.Loading);
                            if (state instanceof AsyncResult.Success) {
                                Object result = ((AsyncResult.Success<?>) state).getData();
                                if (result instanceof FindBookingDomain findBookingResult) {
                                    view.goToBookingDetailsScreen(
                                            referenceBooking,
                                            lastName,
                                            arrivalDate,
                                            findBookingResult.getUuidBasketReference(),
                                            findBookingResult.getToken() != null ? findBookingResult.getToken() : "",
                                            findBookingInput != null
                                    );
                                }
                            }
                            if (state instanceof AsyncResult.Error) {
                                Throwable error = ((AsyncResult.Error) state).getError();
                                if (error instanceof ImportBookingException) {
                                    switch (((ImportBookingException) error).getKey()) {
                                        case KEY_BOOKING_EXISTS:
                                            showAlreadyImportedBookingError(view);
                                            break;
                                        case KEY_PAST_BOOKING:
                                            showPastBookingImportAttemptError(view);
                                            break;
                                        case KEY_BOOKING_NOT_FOUND:
                                            showUnableToRetrieveBookingError(view);
                                            break;
                                        default:
                                            throw new IllegalArgumentException("Error Key not supported");
                                    }
                                } else if (error instanceof BusinessBookingImportError) {
                                    showBusinessBookingImportError(view);
                                } else {
                                    showFetchingBookingError(view);
                                }
                            }

                        }, error ->
                                crashlyticsLogger.logException(error, "Find Booking Error")
                )
        );

        final CampaignDataModel campaignModel = findBookingInput != null ? findBookingInput.getCampaignModel() : new CampaignDataModel();
        analytics.track(ScreenState.FIND_BOOKING, new FindBookingAnalyticsData(campaignModel));
    }

    private void fetchCustomerLastNameAndUpdateView(View view) {
        viewCompositeDisposable.add(
                Observable.just(getCustomer.getCustomerFromSharedPref())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                customer -> {
                                    lastName = customer.getFullName().getLastName();
                                    view.updateScreenFields(
                                            new FindBookingInput(
                                                    findBookingInput.getArrivalDate(),
                                                    findBookingInput.getBookingReference(),
                                                    lastName,
                                                    new CampaignDataModel()));
                                },
                                __ -> view.updateScreenFields(findBookingInput))
        );
    }


    private boolean validateFields(@NonNull View view) {
        boolean validFields = true;
        if (referenceBooking == null || referenceBooking.length() <= 0) {
            validFields = false;
            view.showBookingRefError(true);
        }
        if (lastName == null || lastName.length() <= 0) {
            validFields = false;
            view.showLastNameError(true);
        }
        if (arrivalDate == null) {
            validFields = false;
            view.showArrivalDateError(true);
        }
        return validFields;
    }

    @Override
    protected void onDetachView() {
        viewCompositeDisposable.clear();
    }

    @Override
    protected void onDestroy() {
        viewCompositeDisposable.clear();
    }

    private void showAlreadyImportedBookingError(View view) {
        view.showErrorAlreadyImportedBooking();
        analytics.trackError(ErrorBody.create(-1, ERROR_BOOKING_ALREADY_IMPORTED));
    }

    private void showPastBookingImportAttemptError(View view) {
        view.showErrorPastBookingImportAttempt();
        analytics.trackError(ErrorBody.create(-1, ERROR_PAST_BOOKING_IMPORT));
    }

    private void showUnableToRetrieveBookingError(View view) {
        view.showErrorUnableToRetrieveBooking();
        analytics.trackError(ErrorBody.create(-1, ERROR_UNABLE_TO_RETRIEVE_BOOKING));
    }

    private void showBusinessBookingImportError(View view) {
        view.showBusinessBookingImportError();
        analytics.trackError(ErrorBody.create(-1, ERROR_BUSINESS_BOOKING_IMPORT));
    }

    private void showFetchingBookingError(View view) {
        view.showErrorFetchingBooking();
        analytics.trackError(ErrorBody.create(-1, ERROR_FETCHING_BOOKING));
    }

    public interface View extends PresenterView {
        Observable<Unit> onClickArrivalDate();

        void startCalendarActivity(@NonNull LocalDate rangeStartDate, @NonNull LocalDate selectedDate);

        Observable<LocalDate> onArrivalDateSelected();

        void showArrivalDate(@NonNull LocalDate selectedDate);

        Observable<Unit> onFindBookingClick();

        Observable<String> onReferenceBookingChanged();

        Observable<String> onLastNameChanged();

        void showLoading(boolean show);

        void showErrorFetchingBooking();

        void showBusinessBookingImportError();

        void showErrorAlreadyImportedBooking();

        void showErrorPastBookingImportAttempt();

        void showErrorUnableToRetrieveBooking();

        void goToBookingsScreen(boolean isDeeplinkedActivity);

        void goToBookingDetailsScreen(@NonNull String bookingReference,
                                      @NonNull String lastName,
                                      @NonNull LocalDate arrivalDate,
                                      @NonNull String uuidBasketReference,
                                      @NonNull String token,
                                      boolean isDeeplinkedActivity);

        void showBookingRefError(boolean show);

        void showLastNameError(boolean show);

        void showArrivalDateError(boolean show);

        void updateScreenFields(@NonNull FindBookingInput findBookingInput);
    }
}

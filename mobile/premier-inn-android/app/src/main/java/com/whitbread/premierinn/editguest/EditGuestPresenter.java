package com.whitbread.premierinn.editguest;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.CONTACT_NUMBER;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.EMAIL;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.FIRST_NAME;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.LAST_NAME;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.TITLE;
import androidx.annotation.NonNull;
import com.whitbread.premierinn.common.GuestDetailsCommonPresenter;
import com.whitbread.premierinn.common.Validator;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.guestdetails.adapter.RoomGuestDetailsData;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@ActivityRetainedScoped
public class EditGuestPresenter extends GuestDetailsCommonPresenter<EditGuestPresenter.View> {

    private List<GuestDetailsFormDataInput> guestDetailsList;
    private final TrackingAnalytics analytics;
    private final String language;
    private GuestDetailsFormDataInput bookerDetails;
    private final CompositeDisposable viewCompositeDisposable;
    private boolean bookerIsStaying;

    @Inject
    public EditGuestPresenter(@NonNull CompositeDisposable compositeDisposable,
                              @NonNull TrackingAnalytics analytics, @NonNull DeviceLocaleProvider deviceLocaleProvider) {
        this.viewCompositeDisposable = compositeDisposable;
        this.analytics = analytics;
        this.language = deviceLocaleProvider.getDeviceLanguage();
    }

    public void initParams(@NonNull EditGuestInput editGuestInput) {
        this.bookerDetails = editGuestInput.bookerDetails();
        this.guestDetailsList = editGuestInput.guestDetailsFormDataInputs();
        this.bookerIsStaying = editGuestInput.isBookerStaying();
    }

    @Override
    public void onAttachView(View view) {
        if (bookerIsStaying) {
            view.displayGuestsForm(guestDetailsList, bookerIsStaying);
        } else {
            view.displayBookerAndGuestsForm(bookerDetails, guestDetailsList);
        }

        viewCompositeDisposable.add(view.getBookerForm()
                .compose(processGuestDataForm(true))
                .subscribe(formViewData -> {
                    switch (formViewData.form()) {
                        case TITLE:
                            this.bookerDetails = GuestDetailsFormDataInput.create(formViewData.text(), bookerDetails.firstName(),
                                    bookerDetails.lastName(), bookerDetails.email(), bookerDetails.phoneNumber(), language);
                            break;
                        case FIRST_NAME:
                            this.bookerDetails = GuestDetailsFormDataInput.create(bookerDetails.title(), formViewData.text(),
                                    bookerDetails.lastName(), bookerDetails.email(), bookerDetails.phoneNumber(), language);
                            break;
                        case LAST_NAME:
                            this.bookerDetails = GuestDetailsFormDataInput.create(bookerDetails.title(), bookerDetails.firstName(),
                                    formViewData.text(), bookerDetails.email(), bookerDetails.phoneNumber(), language);
                            break;
                        case CONTACT_NUMBER:
                            this.bookerDetails = GuestDetailsFormDataInput.create(bookerDetails.title(), bookerDetails.firstName(),
                                    bookerDetails.lastName(), bookerDetails.email(), formViewData.text(), language);
                            break;
                        case EMAIL:
                            this.bookerDetails = GuestDetailsFormDataInput.create(bookerDetails.title(), bookerDetails.firstName(),
                                    bookerDetails.lastName(), formViewData.text(), bookerDetails.phoneNumber(), language);
                            break;
                        default:
                            break;
                    }
                    view.showBookerValidationError(formViewData.focus(), formViewData.form());

                }));

        viewCompositeDisposable.add(view.getRoomFormWithTextAndFocus()
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(formViewDataForRecycler ->
                        Observable.just(formViewDataForRecycler)
                                .map(RoomGuestDetailsData::formViewData)
                                .compose(processGuestDataForm(isBooker(formViewDataForRecycler)))
                                .map(formViewData -> RoomGuestDetailsData.create(formViewData, formViewDataForRecycler.position())))
                .subscribe(roomGuestDetailsDataConsumer -> {
                    GuestDetailsFormDataInput guestDetails = guestDetailsList.get(roomGuestDetailsDataConsumer.position());

                    GuestDetailsFormDataOutput guestDetailsFormDataOutput = roomGuestDetailsDataConsumer.formViewData();
                    GuestDetailsFormDataInput guestDetailsMutated = mutateGuestDetails(guestDetails,
                            guestDetailsFormDataOutput, language);

                    if (isBooker(roomGuestDetailsDataConsumer)) {
                        if (TITLE.equals(guestDetailsFormDataOutput.form())) {
                            this.bookerDetails = GuestDetailsFormDataInput.create(guestDetailsFormDataOutput.text(),
                                    guestDetails.firstName(), guestDetails.lastName(), guestDetails.email(),
                                    bookerDetails.phoneNumber(), language);
                        } else if (FIRST_NAME.equals(guestDetailsFormDataOutput.form())) {
                            this.bookerDetails = GuestDetailsFormDataInput.create(guestDetails.title(),
                                    guestDetailsFormDataOutput.text(), guestDetails.lastName(), guestDetails.email(),
                                    bookerDetails.phoneNumber(), language);
                        } else if (LAST_NAME.equals(guestDetailsFormDataOutput.form())) {
                            this.bookerDetails = GuestDetailsFormDataInput.create(guestDetails.title(),
                                    guestDetails.firstName(), guestDetailsFormDataOutput.text(), guestDetails.email(),
                                    bookerDetails.phoneNumber(), language);
                        } else if (EMAIL.equals(guestDetailsFormDataOutput.form())) {
                            this.bookerDetails = GuestDetailsFormDataInput.create(guestDetails.title(),
                                    guestDetails.firstName(), guestDetails.lastName(), guestDetailsFormDataOutput.text(),
                                    bookerDetails.phoneNumber(), language);
                        }
                    }

                    guestDetailsList.set(roomGuestDetailsDataConsumer.position(), guestDetailsMutated);

                    if (!TITLE.equals(guestDetailsFormDataOutput.form()) && !guestDetails.equals(guestDetailsMutated)) {
                        view.updateRoomFormList(guestDetailsMutated, roomGuestDetailsDataConsumer.position());
                    }
                }));

        viewCompositeDisposable.add(view.getOnUpdateClick()
                .subscribe(__ -> {
                    view.showBookerValidationError(!Validator.isNotEmpty(bookerDetails.firstName()), FIRST_NAME);
                    view.showBookerValidationError(!Validator.isNotEmpty(bookerDetails.lastName()), LAST_NAME);
                    view.showBookerValidationError(!Validator.isPhoneNumberValid(bookerDetails.phoneNumber()), CONTACT_NUMBER);
                    view.showBookerValidationError(!Validator.isEmailValid(bookerDetails.email()), EMAIL);

                    for (int index = 0; index < guestDetailsList.size(); index++) {
                        GuestDetailsFormDataInput guestDetails = guestDetailsList.get(index);
                        guestDetailsList.set(index, createGuestDetailsInputWithStayingGuestValidation(guestDetails, language));
                        view.updateRoomFormList(guestDetailsList.get(index), index);
                    }

                    if (bookerDetailsAreValid(bookerDetails) && areNotStayingFormFieldsValid(guestDetailsList)) {
                        view.startReviewBookingActivity(EditGuestInput.create(guestDetailsList, bookerDetails, bookerIsStaying));
                    }
                }));

        analytics.track(ScreenState.GUEST_DETAILS, Type.BOOKING_FLOW);
    }

    private boolean isBooker(RoomGuestDetailsData formViewDataForRecycler) {
        return formViewDataForRecycler.position() == 0 && bookerIsStaying;
    }

    @Override
    public void onDetachView() {
        if (!viewCompositeDisposable.isDisposed()) {
            viewCompositeDisposable.clear();
        }
    }


    public interface View extends PresenterView {
        void displayGuestsForm(@NonNull List<GuestDetailsFormDataInput> guestDetailsFormDataInputs, boolean bookerIsStaying);

        void displayBookerAndGuestsForm(@NonNull GuestDetailsFormDataInput bookerDetails,
                                        @NonNull List<GuestDetailsFormDataInput> guestDetailsFormDataInputs);

        Observable<GuestDetailsFormDataOutput> getBookerForm();

        void showBookerValidationError(boolean showError, GuestDetailsFormDataOutput.Form form);

        Observable<RoomGuestDetailsData> getRoomFormWithTextAndFocus();

        void updateRoomFormList(GuestDetailsFormDataInput guestDetailsMutated, int position);

        Observable<Unit> getOnUpdateClick();

        void startReviewBookingActivity(@NonNull EditGuestInput editGuestInput);
    }
}

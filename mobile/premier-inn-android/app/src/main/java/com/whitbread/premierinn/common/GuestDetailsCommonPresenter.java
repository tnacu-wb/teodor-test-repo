package com.whitbread.premierinn.common;

import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput;

import java.util.List;

import io.reactivex.ObservableTransformer;

public abstract class GuestDetailsCommonPresenter<T extends PresenterView> extends Presenter<T> {

    protected ObservableTransformer<GuestDetailsFormDataOutput, GuestDetailsFormDataOutput> processGuestDataForm(boolean isBooker) {
        return upstream ->
                upstream.map(formDataOutput -> GuestDetailsFormDataOutput
                        .create(formDataOutput.text().trim(), formDataOutput.focus(), formDataOutput.form()))
                        .distinctUntilChanged()
                        .map(formDataOutput -> {
                            boolean conditionForText = false;
                            switch (formDataOutput.form()) {
                                case TITLE:
                                    conditionForText = true;
                                    break;
                                case FIRST_NAME:
                                case LAST_NAME:
                                    conditionForText = Validator.isNotEmpty(formDataOutput.text());
                                    break;
                                case CONTACT_NUMBER:
                                    conditionForText = Validator.isPhoneNumberValid(formDataOutput.text());
                                    break;
                                case EMAIL:
                                    if (isBooker) {
                                        conditionForText = Validator.isEmailValid(formDataOutput.text());
                                    } else {
                                        conditionForText = Validator.isOptionalEmailValid(formDataOutput.text());
                                    }
                                    break;
                                default:
                                    break;
                            }
                            return GuestDetailsFormDataOutput.create(formDataOutput.text(), !conditionForText && !formDataOutput.focus(),
                                    formDataOutput.form());
                        });
    }

    protected GuestDetailsFormDataInput mutateGuestDetails(GuestDetailsFormDataInput guestDetails,
                                                           GuestDetailsFormDataOutput guestDetailsFormDataOutput,
                                                           String language) {
        GuestDetailsFormDataInput guestDetailsMutated = null;

        switch (guestDetailsFormDataOutput.form()) {
            case TITLE:
                guestDetailsMutated = GuestDetailsFormDataInput.create(guestDetailsFormDataOutput.text(),
                        guestDetails.firstName(), guestDetails.errorFirstName(),
                        guestDetails.lastName(), guestDetails.errorLastName(),
                        guestDetails.email(), guestDetails.errorEmail(),
                        language);
                break;
            case FIRST_NAME:
                guestDetailsMutated = GuestDetailsFormDataInput.create(guestDetails.title(),
                        guestDetailsFormDataOutput.text(), guestDetailsFormDataOutput.focus(),
                        guestDetails.lastName(), guestDetails.errorLastName(),
                        guestDetails.email(), guestDetails.errorEmail(), language);
                break;
            case LAST_NAME:
                guestDetailsMutated = GuestDetailsFormDataInput.create(guestDetails.title(),
                        guestDetails.firstName(), guestDetails.errorFirstName(),
                        guestDetailsFormDataOutput.text(), guestDetailsFormDataOutput.focus(),
                        guestDetails.email(), guestDetails.errorEmail(), language);
                break;
            case EMAIL:
                guestDetailsMutated = GuestDetailsFormDataInput.create(guestDetails.title(),
                        guestDetails.firstName(), guestDetails.errorFirstName(),
                        guestDetails.lastName(), guestDetails.errorLastName(),
                        guestDetailsFormDataOutput.text(), guestDetailsFormDataOutput.focus(), language);
                break;
            default:
                break;
        }
        return guestDetailsMutated;
    }

    protected boolean bookerDetailsAreValid(GuestDetailsFormDataInput bookerDetails) {
        return Validator.isNotEmpty(bookerDetails.firstName())
                && Validator.isNotEmpty(bookerDetails.lastName())
                && Validator.isPhoneNumberValid(bookerDetails.phoneNumber())
                && Validator.isEmailValid(bookerDetails.email());
    }

    protected boolean areNotStayingFormFieldsValid(List<GuestDetailsFormDataInput> guestDetailsList) {
        boolean listValid = true;
        for (GuestDetailsFormDataInput guestDetailsFormDataInput : guestDetailsList) {
            listValid = listValid
                    && Validator.isNotEmpty(guestDetailsFormDataInput.firstName())
                    && Validator.isNotEmpty(guestDetailsFormDataInput.lastName())
                    && Validator.isOptionalEmailValid(guestDetailsFormDataInput.email());
        }
        return listValid;
    }

    protected GuestDetailsFormDataInput createGuestDetailsInputWithStayingGuestValidation(GuestDetailsFormDataInput guestDetails,
                                                                                          String language) {
        return GuestDetailsFormDataInput.create(guestDetails.title(),
                guestDetails.firstName(), !Validator.isNotEmpty(guestDetails.firstName()),
                guestDetails.lastName(), !Validator.isNotEmpty(guestDetails.lastName()),
                guestDetails.email(), !Validator.isOptionalEmailValid(guestDetails.email()), language);
    }
}
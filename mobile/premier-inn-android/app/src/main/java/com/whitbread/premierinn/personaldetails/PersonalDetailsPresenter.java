package com.whitbread.premierinn.personaldetails;

import static com.whitbread.premierinn.data.common.Constants.LANGUAGE_DEUTSCH;
import static com.whitbread.premierinn.domain.common.Constants.COUNTRY_CODE_UK;
import static com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.GDPR_MY_DETAILS_USAGE;
import static com.whitbread.premierinn.data.common.Constants.BRAND_CODE;
import androidx.annotation.NonNull;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.forms.FormInputErrorMessageProvider;
import com.whitbread.premierinn.common.forms.FormUiModel;
import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.action.Action;
import com.whitbread.premierinn.common.forms.action.ClickAction;
import com.whitbread.premierinn.common.forms.action.ResetInputErrorAction;
import com.whitbread.premierinn.common.forms.action.SubmitFormAction;
import com.whitbread.premierinn.common.forms.observabletransformer.CountryChangedActionTransformer;
import com.whitbread.premierinn.common.forms.observabletransformer.HomeWorkToggleActionTransformer;
import com.whitbread.premierinn.common.forms.observabletransformer.PostcodeAddressTransformer;
import com.whitbread.premierinn.common.forms.observabletransformer.ResetInputErrorActionTransformer;
import com.whitbread.premierinn.common.forms.observabletransformer.SubmitFormActionTransformer;
import com.whitbread.premierinn.common.forms.result.Result;
import com.whitbread.premierinn.common.forms.result.SubmitFormResult;
import com.whitbread.premierinn.common.forms.scan.FormSubmissionScanBifunction;
import com.whitbread.premierinn.common.mvp.FormPresenterView;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.createaccount.action.CountryChangedAction;
import com.whitbread.premierinn.createaccount.action.HomeWorkToggleAction;
import com.whitbread.premierinn.createaccount.observabletransformer.ClickActionTransformer;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.countries.GetCountries;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.domain.customer.usecase.GetMarketingPreference;
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPersonalDetails;
import com.whitbread.premierinn.domain.graphql.marketingPreferences.usecase.GraphQLMarketingPreferencesUseCase;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import com.whitbread.premierinn.personaldetails.action.NationalityChangedAction;
import com.whitbread.premierinn.personaldetails.observabletransformer.NationalityChangedActionTransformer;
import com.whitbread.premierinn.personaldetails.observabletransformer.PersonalDetailsUpdateApiFormTransformer;
import com.whitbread.premierinn.postcodefinder.ParcelableAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@ActivityRetainedScoped
public class PersonalDetailsPresenter extends Presenter<PersonalDetailsPresenter.View> {

    private final UpdateCustomerPersonalDetails updateCustomerPersonalDetails;
    private final GraphQLMarketingPreferencesUseCase updateMarketingPreferencesUseCase;
    private final GetMarketingPreference getMarketingPreference;
    private Customer originalCustomerDetails;
    private final CompositeDisposable compositeDisposable;
    private final GetCountries getCountries;
    private final TrackingAnalytics trackingAnalytics;
    private final GetStringResource getStringResource;
    private final LogService crashlyticsLogger;
    private final FormInputErrorMessageProvider formInputErrorMessageProvider;
    private final String language;
    private boolean currentMarketingOptIn = false;

    @Inject
    public PersonalDetailsPresenter(@NonNull UpdateCustomerPersonalDetails updateCustomerPersonalDetails,
                                    @NonNull GraphQLMarketingPreferencesUseCase updateMarketingPreferencesUseCase,
                                    @NonNull GetMarketingPreference getMarketingPreference,
                                    @NonNull CompositeDisposable compositeDisposable,
                                    @NonNull GetCountries getCountries,
                                    @NonNull TrackingAnalytics trackingAnalytics,
                                    @NonNull GetStringResource getStringResource,
                                    @NonNull LogService crashlyticsLogger,
                                    @NonNull FormInputErrorMessageProvider formInputErrorMessageProvider,
                                    @NonNull DeviceLocaleProvider deviceLocaleProvider) {
        this.updateCustomerPersonalDetails = updateCustomerPersonalDetails;
        this.updateMarketingPreferencesUseCase = updateMarketingPreferencesUseCase;
        this.getMarketingPreference = getMarketingPreference;
        this.compositeDisposable = compositeDisposable;
        this.getCountries = getCountries;
        this.trackingAnalytics = trackingAnalytics;
        this.getStringResource = getStringResource;
        this.crashlyticsLogger = crashlyticsLogger;
        this.formInputErrorMessageProvider = formInputErrorMessageProvider;
        this.language = deviceLocaleProvider.getDeviceLanguage();
    }

    public void initParams(@NonNull Customer customerDetails) {
        this.originalCustomerDetails = customerDetails;
    }

    @Override
    protected void onAttachView(View view) {
        trackingAnalytics.track(AnalyticsConstants.ScreenState.MY_DETAILS, AnalyticsConstants.Type.MY_PREMIER_INN);
                    if (isViewAttached()) {
                        List<CountryDomain> countries = getCountries.fetchCountriesFromSharedPref();
                        view.populateCountriesList(countries);
                        view.populateNationalitiesList(countries);

                        // Should only populate original customer details after lists are populated
                        view.populateContactDetails(originalCustomerDetails, language);

                        // Load marketing preferences
                        loadMarketingPreferences(view);
                    }

        if (originalCustomerDetails.getContact() != null && originalCustomerDetails.getAddress().getCompanyName() != null
                && !originalCustomerDetails.getAddress().getCompanyName().isEmpty()) {
            displayCompanyName(view);
        }

        compositeDisposable.add(view.onMarketingOptInChange()
                .subscribe(optIn -> currentMarketingOptIn = optIn));

        compositeDisposable.add(view.onFindAddressClick()
                .subscribe(__ -> view.startPostcodeFinderActivity()));

        compositeDisposable.add(view.onPostcodeFindingSuccess()
                .compose(new PostcodeAddressTransformer())
                .subscribe(view::update));

        view.update(FormUiModel.updateForm(Collections.singletonList(InputState.builder()
                .id(R.id.personal_details_gdpr_top_info)
                .state(InputState.State.IDLE)
                .value(getStringResource.invoke(GDPR_MY_DETAILS_USAGE))
                .build()), formInputErrorMessageProvider));

        ObservableTransformer<Action, Result> sharedActionTransformer = getActionResultObservableTransformer();

        showFullAddressForm(view);

        Observable<FormUiModel> scanFormResults =
                view.getActions()
                        .compose(sharedActionTransformer)
                        .scan(FormUiModel.idle(), new FormSubmissionScanBifunction(formInputErrorMessageProvider));

        compositeDisposable.add(scanFormResults
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::update, e -> {
                    crashlyticsLogger.logException(e, e.getMessage());
                    view.update(FormUiModel.error());
                }));

        view.setHomeWorkToggle(view.isBusinessAddress());

    }

    @NonNull
    private ObservableTransformer<Action, Result> getActionResultObservableTransformer() {
        ObservableTransformer<List<Input>, SubmitFormResult> saveApiFormTransformer =
                new PersonalDetailsUpdateApiFormTransformer(updateCustomerPersonalDetails,
                        originalCustomerDetails, trackingAnalytics, crashlyticsLogger,
                        () -> {
                            updateMarketingPreferencesIfNeeded();
                            return Unit.INSTANCE;
                        });

        return events -> events.publish(shared -> Observable.merge(Arrays.asList(
                shared.ofType(SubmitFormAction.class)
                        .compose(new SubmitFormActionTransformer(saveApiFormTransformer)),
                shared.ofType(ResetInputErrorAction.class)
                        .compose(new ResetInputErrorActionTransformer()),
                shared.ofType(CountryChangedAction.class)
                        .compose(new CountryChangedActionTransformer()),
                shared.ofType(ClickAction.class)
                        .compose(new ClickActionTransformer()),
                shared.ofType(HomeWorkToggleAction.class)
                        .compose(new HomeWorkToggleActionTransformer()),
                shared.ofType(NationalityChangedAction.class)
                        .compose(new NationalityChangedActionTransformer()))));
    }

    private void loadMarketingPreferences(View view) {
        String email = originalCustomerDetails.getContact().getEmail();
        compositeDisposable.add(
            getMarketingPreference.invoke(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    newsletterPreference -> {
                        newsletterPreference.getNewsletterPermission();
                        if (!newsletterPreference.getNewsletterPermission().isEmpty()) {
                            boolean optIn = newsletterPreference.getNewsletterPermission().get(0).getOptIn();
                            currentMarketingOptIn = optIn;
                            view.setMarketingOptIn(optIn);
                        }
                    },
                    error -> {
                        crashlyticsLogger.logException(error, "Failed to load marketing preferences");
                        // Default to false if we can't load preferences
                        currentMarketingOptIn = false;
                        view.setMarketingOptIn(false);
                    }
                )
        );
    }

    private void updateMarketingPreferencesIfNeeded() {
        String email = originalCustomerDetails.getContact().getEmail();
        String countryCode = originalCustomerDetails.getAddress().getCountryCode() != null
                ? originalCustomerDetails.getAddress().getCountryCode() : COUNTRY_CODE_UK;

        // doubleOptIn is true only for Germany (DE), but marketing toggle is off for German already.
        boolean doubleOptIn = LANGUAGE_DEUTSCH.equals(countryCode);

        compositeDisposable.add(
            updateMarketingPreferencesUseCase.updateMarketingPreferences(
                email,
                currentMarketingOptIn,
                doubleOptIn,
                Collections.singletonList(BRAND_CODE),
                countryCode
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                result -> {
                    // Marketing preferences updated successfully
                },
                error -> crashlyticsLogger.logException(error, "Failed to update marketing preferences")
            )
        );
    }

    private void displayCompanyName(View view) {
        InputState input = InputState.builder()
                .id(R.id.address_form_company_input)
                .state(InputState.State.VISIBLE)
                .build();
        List<InputState> inputState = Collections.singletonList(input);

        FormUiModel formUiModel = FormUiModel.builder().inputStates(inputState).state(FormUiModel.State.FORM_UPDATE).build();
        view.update(formUiModel);
    }

    private void showFullAddressForm(View view) {
        InputState manualAddressWrapperState = InputState.builder()
                .id(R.id.address_form_manual_address_wrapper)
                .state(InputState.State.VISIBLE).build();

        InputState manualAddressLabel = InputState.builder()
                .id(R.id.address_form_manual_address_label)
                .state(InputState.State.INVISIBLE).build();

        List<InputState> inputStates = Arrays.asList(manualAddressWrapperState, manualAddressLabel);

        FormUiModel formUiModel = FormUiModel.builder().inputStates(inputStates).state(FormUiModel.State.FORM_UPDATE).build();
        view.update(formUiModel);
    }

    public interface View extends FormPresenterView {
        void populateCountriesList(List<CountryDomain> countries);

        void update(FormUiModel formUiModel);

        void showGenericError();

        Observable<Unit> onFindAddressClick();

        void startPostcodeFinderActivity();

        Observable<ParcelableAddress> onPostcodeFindingSuccess();

        void populateNationalitiesList(@NonNull List<CountryDomain> countries);

        void populateContactDetails(Customer contactDetail, String language);

        void setHomeWorkToggle(boolean isWork);

        boolean isBusinessAddress();

        void setMarketingOptIn(boolean optIn);

        Observable<Boolean> onMarketingOptInChange();
    }
}
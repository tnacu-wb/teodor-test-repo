package com.whitbread.premierinn.resetpassword;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.Validator;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.graphql.forgotpassword.entity.ForgotPasswordDomain;
import com.whitbread.premierinn.domain.graphql.forgotpassword.usecase.ForgotPasswordUseCase;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

@ActivityRetainedScoped
public class ResetPasswordPresenter extends Presenter<ResetPasswordPresenter.View> {
    private final CompositeDisposable viewCompositeDisposable;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final TrackingAnalytics analytics;
    private final DeviceLocaleProvider deviceLocaleProvider;
    private String email;

    @Inject
    public ResetPasswordPresenter(@NonNull CompositeDisposable compositeDisposable,
                                  @NonNull ForgotPasswordUseCase forgotPasswordUseCase,
                                  @NonNull TrackingAnalytics analytics,
                                  @NonNull DeviceLocaleProvider deviceLocaleProvider) {
        this.viewCompositeDisposable = compositeDisposable;
        this.forgotPasswordUseCase = forgotPasswordUseCase;
        this.analytics = analytics;
        this.deviceLocaleProvider = deviceLocaleProvider;
    }

    @Override
    public void onAttachView(View view) {
        viewCompositeDisposable.add(view.onEmailChanged()
                .subscribe(emailFormInput -> {
                    this.email = emailFormInput.email();
                    if (emailFormInput.hasError() && Validator.isEmailValid(emailFormInput.email())) {
                        view.showEmailErrorValidation(false);
                    }
                }));

        viewCompositeDisposable.add(view.onResetPasswordClick()
                .flatMapSingle(pair -> {
                    if (Validator.isEmailValid(pair.first)) {
                        return forgotPasswordUseCase.execute(pair.first, pair.second,
                                deviceLocaleProvider.getDeviceLanguage());
                    } else {
                        return Single.just(new Object());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object -> {
                            if (object instanceof ForgotPasswordDomain response) {
                                if (response.getSuccess()) {
                                    view.resetSuccessful(email);
                                } else {
                                    view.showWrongUsernameMessage();
                                }
                            } else {
                                view.showEmailErrorValidation(true);
                            }
                        },
                        throwable -> view.showRequestFailedMessage()
                ));

        analytics.track(ScreenState.RESET_PASSWORD, Type.MY_PREMIER_INN);
    }

    @Override
    public void onDetachView() {
        if (viewCompositeDisposable.size() > 0) {
            viewCompositeDisposable.clear();
        }
    }

    public interface View extends PresenterView {
        Observable<UsernameFormInput> onEmailChanged();

        Observable<Pair<String, Boolean>> onResetPasswordClick();

        void showRequestFailedMessage();

        void resetSuccessful(@NonNull String email);

        void showWrongUsernameMessage();

        void showEmailErrorValidation(boolean show);
    }

    @AutoValue
    abstract static class UsernameFormInput {

        public abstract boolean hasError();

        public abstract String email();

        public static UsernameFormInput create(boolean errorEnabled, @NonNull String username) {
            return new AutoValue_ResetPasswordPresenter_UsernameFormInput(errorEnabled, username);
        }
    }
}

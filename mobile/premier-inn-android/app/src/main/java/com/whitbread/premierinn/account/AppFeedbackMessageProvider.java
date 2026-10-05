package com.whitbread.premierinn.account;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.StringResourceProvider;
import javax.inject.Inject;

public class AppFeedbackMessageProvider {
    private static final String ANDROID_LABEL = "Android version";
    private static final String PHONE_LABEL = "Phone";
    private static final String HTML_BREAK_LINE = "<br>";
    private static final String SEMICOLON = ";";
    private static final String SPACE = " ";
    private final Context context;
    private final StringResourceProvider stringResourceProvider;
    private final AppFeedbackEmailBodyMessageProvider appFeedbackEmailBodyMessageProvider;

    @Inject
    public AppFeedbackMessageProvider(@NonNull Context context,
                                      @NonNull StringResourceProvider stringResourceProvider,
                                      @NonNull AppFeedbackEmailBodyMessageProvider appFeedbackEmailBodyMessageProvider) {
        this.context = context;
        this.stringResourceProvider = stringResourceProvider;
        this.appFeedbackEmailBodyMessageProvider = appFeedbackEmailBodyMessageProvider;
    }

    public String getEmailAddress() {
        return context.getString(R.string.email_feedback_address);
    }

    public String getEmailFeedbackSubject() {
        return context.getString(R.string.email_feedback_subject);
    }

    public String getEmailFeedbackBody() throws PackageManager.NameNotFoundException {
        return appFeedbackEmailBodyMessageProvider.getPhoneModel() + SEMICOLON + SPACE
                + appFeedbackEmailBodyMessageProvider.getAndroidVersion() + SEMICOLON + SPACE
                + stringResourceProvider.getVersionName() + HTML_BREAK_LINE + HTML_BREAK_LINE + HTML_BREAK_LINE
                + appFeedbackEmailBodyMessageProvider.getBodyQuestion() + HTML_BREAK_LINE + HTML_BREAK_LINE + HTML_BREAK_LINE;
    }
}
package com.whitbread.premierinn.account;


import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.StringResourceProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AppFeedbackMessageProviderTest {

    @Mock Context contextMock;
    @Mock StringResourceProvider stringResourceProvider;
    @Mock AppFeedbackEmailBodyMessageProvider appFeedbackEmailBodyMessageProvider;

    private AppFeedbackMessageProvider messageProvider;

    @Before
    public void onSetup() {
        messageProvider = new AppFeedbackMessageProvider(contextMock, stringResourceProvider, appFeedbackEmailBodyMessageProvider);
    }

    @Test
    public void getEmailFeedbackSubjectTest() {
        String subject = "subject this 8======D";
        when(contextMock.getString(R.string.email_feedback_subject)).thenReturn(subject);
        assertEquals(messageProvider.getEmailFeedbackSubject(), subject);
    }

    @Test
    public void getEmailFeedbackBodyTest() throws Exception {
        String appVersion = "app version";
        String androidVersion = "android version";
        String phoneModel = "phone model";
        String emailBody = "emailBody";

        when(stringResourceProvider.getVersionName()).thenReturn(appVersion);
        when(appFeedbackEmailBodyMessageProvider.getPhoneModel()).thenReturn("Phone " + phoneModel);
        when(appFeedbackEmailBodyMessageProvider.getAndroidVersion()).thenReturn("Android version " + androidVersion);
        when(appFeedbackEmailBodyMessageProvider.getBodyQuestion()).thenReturn("<b>" + emailBody + "</b>");


        String result = "Phone " + phoneModel + "; Android version " + androidVersion + "; " + appVersion
                + "<br><br><br><b>" + emailBody
                + "</b><br><br><br>";

        assertEquals(messageProvider.getEmailFeedbackBody(), result);
    }
}

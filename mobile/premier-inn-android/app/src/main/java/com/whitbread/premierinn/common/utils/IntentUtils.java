package com.whitbread.premierinn.common.utils;

import static com.whitbread.premierinn.ciol.CheckInOnlineActivityKt.CHECK_IN_ONLINE_ACTIVITY_REQUEST_KEY;
import static com.whitbread.premierinn.ciol.CheckInOnlineActivityKt.PRE_STAY_KEY;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Html;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.ciol.CheckInOnlineActivity;
import com.whitbread.premierinn.ciol.entity.PreStayUiModel;

import org.threeten.bp.LocalDate;

import java.util.concurrent.TimeUnit;

public class IntentUtils {

    private IntentUtils() {
        throw new AssertionError("no instances allowed");
    }

    public static Intent createWebLinkIntent(@NonNull String webUrl) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
    }

    public static Intent createTelephoneIntent(@NonNull String callNumber) {
        return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callNumber));
    }

    public static boolean checkIntentResolvedActivity(@NonNull Context context, @NonNull Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        return intent.resolveActivity(packageManager) != null;
    }

    public static void openTelephone(@NonNull Context context, String phoneNumber) {
        Intent callIntent = IntentUtils.createTelephoneIntent(phoneNumber);
        if (IntentUtils.checkIntentResolvedActivity(context, callIntent)) {
            context.startActivity(callIntent);
        } else {
            Toast.makeText(
                    context,
                    R.string.phone_call_action_not_supported,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    public static Intent createPlaystoreIntent(@NonNull Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        return intent;
    }

    public static Intent createEmailIntent(@NonNull Context context, @NonNull String receiverAddress, @NonNull String subject,
                                           @NonNull String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String[] addressesArray = {receiverAddress};
        intent.setType("text/html");
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addressesArray);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_HTML_TEXT, body);
        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
        return Intent.createChooser(intent, context.getString(R.string.send_email_title));
    }

    public static Intent createCalendarIntent(LocalDate beginTime, LocalDate endTime,
                                              @NonNull String title,
                                              @NonNull String description,
                                              @NonNull String location) {
        return new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, TimeUnit.DAYS.toMillis(beginTime.toEpochDay()))
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, TimeUnit.DAYS.toMillis(endTime.toEpochDay()))
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, description)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
    }

    public static void startCiolActivity(Activity context, @NonNull PreStayUiModel preStayModel) {
        Intent intent = new Intent(context, CheckInOnlineActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(PRE_STAY_KEY, preStayModel);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, CHECK_IN_ONLINE_ACTIVITY_REQUEST_KEY);
    }
}

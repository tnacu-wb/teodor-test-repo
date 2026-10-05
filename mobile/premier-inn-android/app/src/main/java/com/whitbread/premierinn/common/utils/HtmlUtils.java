package com.whitbread.premierinn.common.utils;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

public final class HtmlUtils {

    private HtmlUtils() {
        throw new AssertionError("no instances allowed");
    }

    public static Spanned parseTags(String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(content, android.text.Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(content);
        }
    }
}

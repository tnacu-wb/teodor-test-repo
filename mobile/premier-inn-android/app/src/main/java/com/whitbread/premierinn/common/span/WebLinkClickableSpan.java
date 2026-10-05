package com.whitbread.premierinn.common.span;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.utils.IntentUtils;

public class WebLinkClickableSpan extends ClickableSpan {

    private final Context context;
    private final String url;

    public WebLinkClickableSpan(@NonNull Context context, @NonNull String url) {
        this.context = context;
        this.url = url;
    }

    @Override
    public void onClick(View view) {
        context.startActivity(IntentUtils.createWebLinkIntent(url));
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(ContextCompat.getColor(context, R.color.premier_inn_purple));
        ds.setUnderlineText(false);
    }
}

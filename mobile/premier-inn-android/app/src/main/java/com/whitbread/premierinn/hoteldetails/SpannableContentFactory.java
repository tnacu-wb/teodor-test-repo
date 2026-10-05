package com.whitbread.premierinn.hoteldetails;

import android.content.Context;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.utils.HtmlUtils;
import com.whitbread.premierinn.common.utils.Truss;

import java.util.List;

public class SpannableContentFactory {

    public static CharSequence createContent(@NonNull Context context, @NonNull List<Content> contents) {
        Truss contentBuilder = new Truss();
        for (int contentItem = 0; contentItem < contents.size(); contentItem++) {
            Content content = contents.get(contentItem);
            switch (content.contentType()) {
                case LIST:
                    contentBuilder
                            .append(contentItem > 0 ? "\n" : "")
                            .pushSpan(new BulletSpan(15, ContextCompat.getColor(context, R.color.premier_inn_purple)))
                            .pushSpan(new TextAppearanceSpan(context, R.style.Body))
                            .append(content.text())
                            .popSpan()
                            .popSpan();

                    break;
                case HEADER:
                    contentBuilder
                            .append(contentItem > 0 ? "\n\n" : "")
                            .pushSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey_dark)))
                            .pushSpan(new TextAppearanceSpan(context, R.style.Header3Bold))
                            .append(content.text())
                            .popSpan()
                            .popSpan();
                    break;
                case BODY:
                default:
                    contentBuilder
                            .append(contentItem > 0 ? "\n\n" : "")
                            .pushSpan(new TextAppearanceSpan(context, R.style.Body))
                            .append(content.text())
                            .popSpan();
                    break;
                case BODY_WITH_HTML:
                    contentBuilder
                            .append(contentItem > 0 ? "\n\n" : "")
                            .pushSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey_dark)))
                            .pushSpan(new TextAppearanceSpan(context, R.style.Body))
                            .append(HtmlUtils.parseTags(content.text()))
                            .popSpan()
                            .popSpan();
                    break;
                case INLINE:
                    contentBuilder
                            .pushSpan(new TextAppearanceSpan(context, R.style.Body))
                            .append(content.text())
                            .append(" ")
                            .popSpan();
                    break;
                case INLINE_WITH_PURPLE_COLOUR:
                    contentBuilder
                            .pushSpan(new TextAppearanceSpan(context, R.style.SpannableContentInlinePurple))
                            .append(content.text())
                            .append(" ")
                            .popSpan();
                    break;
                case INLINE_BOLD:
                    contentBuilder
                            .pushSpan(new TextAppearanceSpan(context, R.style.SpannableContentInlineBoldPurple))
                            .append(content.text())
                            .append(" ")
                            .popSpan();
                    break;
                case DIVIDER:
                    contentBuilder
                            .append("\n\n")
                            .pushSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey_light)))
                            .append(content.text())
                            .popSpan();
                    break;
            }
        }
        return contentBuilder.build();
    }
}

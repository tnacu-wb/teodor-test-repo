package com.whitbread.premierinn.common.utils;

import android.text.SpannableStringBuilder;
import java.util.ArrayDeque;
import java.util.Deque;

import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;

/** A {@link SpannableStringBuilder} wrapper whose API doesn't make me want to stab my eyes out. */
public class Truss {
    private final SpannableStringBuilder builder;
    private final Deque<Span> stack;

    public Truss() {
        builder = new SpannableStringBuilder();
        stack = new ArrayDeque<>();
    }

    public Truss append(String string) {
        builder.append(string);
        return this;
    }

    public Truss append(CharSequence charSequence) {
        builder.append(charSequence);
        return this;
    }

    public Truss append(char c) {
        builder.append(c);
        return this;
    }

    public Truss append(int number) {
        builder.append(String.valueOf(number));
        return this;
    }

    public Truss appendIf(String string, Boolean isName) {
        if (string != null && !string.equals(StringUtils.EMPTY_STRING) && !string.equals(StringUtils.SPACE)) {
            builder.append(string);
            if (isName) {
                builder.append('\n');
            }
        }
        return this;
    }

    /** Starts {@code span} at the current position in the builder. */
    public Truss pushSpan(Object span) {
        stack.addLast(new Span(builder.length(), span));
        return this;
    }

    /** End the most recently pushed span at the current position in the builder. */
    public Truss popSpan() {
        Span span = stack.removeLast();
        builder.setSpan(span.span, span.start, builder.length(), SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }

    /** Create the final {@link CharSequence}, popping any remaining spans. */
    public CharSequence build() {
        while (!stack.isEmpty()) {
            popSpan();
        }
        return builder;
    }

    private static final class Span {
        final int start;
        final Object span;

        Span(int start, Object span) {
            this.start = start;
            this.span = span;
        }
    }
}


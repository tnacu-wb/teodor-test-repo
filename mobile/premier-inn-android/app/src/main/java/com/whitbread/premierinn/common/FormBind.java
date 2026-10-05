package com.whitbread.premierinn.common;

import androidx.annotation.NonNull;
import android.util.Pair;
import android.widget.EditText;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;

import io.reactivex.Observable;

public class FormBind {

    private FormBind() {
        throw new AssertionError("No instances allowed.");
    }

    public static Observable<Pair<CharSequence, Boolean>> getFieldTextAndFocus(EditText field) {
        return Observable.combineLatest(RxTextView.textChanges(field), emitFocusChanges(field), Pair::new);
    }

    private static Observable<Boolean> emitFocusChanges(@NonNull EditText editText) {
        return RxTextView.textChanges(editText)
            .flatMap(text -> {
                // for register users we'll want to propagate the focus to identify a possible errors (e.g: empty fields)
                if (text.length() > 0) {
                    return RxView.focusChanges(editText);
                } else {
                    return RxView.focusChanges(editText).skipInitialValue();
                }
        });
    }
}
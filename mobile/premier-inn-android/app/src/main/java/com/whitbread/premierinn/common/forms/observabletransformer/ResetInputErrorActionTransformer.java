package com.whitbread.premierinn.common.forms.observabletransformer;

import com.whitbread.premierinn.common.forms.result.ResetInputErrorResult;
import com.whitbread.premierinn.common.forms.action.ResetInputErrorAction;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

/**
 *
 */
public class ResetInputErrorActionTransformer implements ObservableTransformer<ResetInputErrorAction, ResetInputErrorResult> {

    @Override public ObservableSource<ResetInputErrorResult> apply(Observable<ResetInputErrorAction> upstream) {
        return upstream.flatMap(action -> Observable.fromCallable(() -> ResetInputErrorResult.create(action.inputId())));
    }
}
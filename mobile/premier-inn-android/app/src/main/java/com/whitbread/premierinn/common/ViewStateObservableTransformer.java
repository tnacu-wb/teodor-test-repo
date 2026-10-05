package com.whitbread.premierinn.common;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

/**
 *
 */
public class ViewStateObservableTransformer<Upstream> implements ObservableTransformer<Upstream, ViewState<Upstream>> {

    @Override public ObservableSource<ViewState<Upstream>> apply(Observable<Upstream> upstream) {
        return upstream.map(ViewState::resultOk)
                .onErrorReturn(ViewState::error)
                .startWith(ViewState.inFlight());
    }
}

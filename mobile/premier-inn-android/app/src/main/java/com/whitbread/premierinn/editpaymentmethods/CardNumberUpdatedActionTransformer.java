package com.whitbread.premierinn.editpaymentmethods;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

class CardNumberUpdatedActionTransformer implements ObservableTransformer<CardNumberUpdatedAction, CardNumberUpdatedResult> {


    @Override
    public ObservableSource<CardNumberUpdatedResult> apply(Observable<CardNumberUpdatedAction> upstream) {
        return upstream.map(CardNumberUpdatedAction::cardNumberEntered)
                .flatMap(cardNumber -> Observable.just(CardNumberUpdatedResult.builder()
                        .startDateRequired(false)
                        .issueNumberRequired(false).build()));
    }
}

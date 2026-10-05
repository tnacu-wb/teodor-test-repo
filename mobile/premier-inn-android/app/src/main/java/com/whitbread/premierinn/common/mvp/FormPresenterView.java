package com.whitbread.premierinn.common.mvp;

import com.whitbread.premierinn.common.forms.action.Action;

import io.reactivex.Observable;

public interface FormPresenterView extends PresenterView {
    Observable<Action> getActions();
}
package com.whitbread.premierinn.common.mvp;

public abstract class Presenter<V extends PresenterView> {

    private V presenterView;

    public final void attachView(V presenterView) {
        this.presenterView = presenterView;
        onAttachView(presenterView);
    }

    public final void detachView() {
        onDetachView();
        this.presenterView = null;
    }

    public final void destroy() {
        onDestroy();
    }

    @Deprecated
    public final V getView() {
        return presenterView;
    }

    protected abstract void onAttachView(V view);

    protected void onDetachView() {

    }

    public final boolean isViewAttached() {
        return presenterView != null;
    }

    protected void onDestroy() {

    }
}

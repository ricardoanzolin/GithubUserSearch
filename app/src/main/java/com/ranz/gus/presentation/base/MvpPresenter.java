package com.ranz.gus.presentation.base;

/**
 * Created by Ricardo on 28/08/2016.
 */
public interface MvpPresenter <V extends MvpView> {
    void attachView(V mvpView);
    void detachView();
}

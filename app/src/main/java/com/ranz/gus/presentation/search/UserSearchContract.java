package com.ranz.gus.presentation.search;

import com.ranz.gus.data.remote.model.User;
import com.ranz.gus.presentation.base.MvpPresenter;
import com.ranz.gus.presentation.base.MvpView;

import java.util.List;

/**
 * Created by Ricardo on 28/08/2016.
 */
public interface UserSearchContract {
    interface View extends MvpView {
        void showSearchResults(List<User> githubUserList);

        void showError(String message);

        void showLoading();

        void hideLoading();
    }

    interface Presenter extends MvpPresenter<View> {
        void search(String term);
    }
}

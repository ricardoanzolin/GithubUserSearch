package com.ranz.gus.presentation.search;

import com.ranz.gus.data.UserRepository;
import com.ranz.gus.data.remote.model.User;
import com.ranz.gus.presentation.base.BasePresenter;

import java.util.List;

import rx.Scheduler;
import rx.Subscriber;

/**
 * Created by Ricardo on 28/08/2016.
 */
public class UserSearchPresenter extends BasePresenter<UserSearchContract.View> implements UserSearchContract.Presenter {

    private final Scheduler mainScheduler, ioScheduler;
    private UserRepository userRepository;

    UserSearchPresenter(UserRepository userRepository, Scheduler ioScheduler, Scheduler mainScheduler) {
        this.userRepository = userRepository;
        this.ioScheduler = ioScheduler;
        this.mainScheduler = mainScheduler;
    }

    @Override
    public void search(String term) {
        checkViewAttached();
        getView().showLoading();
        addSubscription(userRepository.searchUsers(term)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(new Subscriber<List<User>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                getView().hideLoading();
                getView().showError(e.getMessage());
            }

            @Override
            public void onNext(List<User> users) {
                getView().hideLoading();
                getView().showSearchResults(users);
            }
        }));
    }
}

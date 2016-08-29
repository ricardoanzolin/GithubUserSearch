package com.ranz.gus.presentation.search;

import com.ranz.gus.data.UserRepository;
import com.ranz.gus.data.remote.model.User;
import com.ranz.gus.data.remote.model.UsersList;
import com.ranz.gus.presentation.base.BasePresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Ricardo on 28/08/2016.
 */
public class UserSearchPresenterTest {

    @Mock
    UserRepository userRepository;
    @Mock
    UserSearchContract.View view;

    UserSearchPresenter userSearchPresenter;

    private static final String USER_LOGIN_RICARDO = "ricardo";
    private static final String USER_LOGIN_DYEGO = "dyego";

    @Test
    public void search_ValidSearchTerm_ReturnsResults() {
        UsersList userList = getDummyUserList();
        when(userRepository.searchUsers(anyString())).thenReturn(Observable.<List<User>>just(userList.getItems()));

        userSearchPresenter.search("ricardo");

        verify(view).showLoading();
        verify(view).hideLoading();
        verify(view).showSearchResults(userList.getItems());
        verify(view, never()).showError(anyString());
    }

    @Test
    public void search_UserRepositoryError_ErrorMsg() {
        String errorMsg = "No internet";
        when(userRepository.searchUsers(anyString())).thenReturn(Observable.error(new IOException(errorMsg)));

        userSearchPresenter.search("bookdash");

        verify(view).showLoading();
        verify(view).hideLoading();
        verify(view, never()).showSearchResults(anyList());
        verify(view).showError(errorMsg);
    }

    @Test(expected = BasePresenter.MvpViewNotAttachedException.class)
    public void search_NotAttached_ThrowsMvpException() {
        userSearchPresenter.detachView();

        userSearchPresenter.search("test");

        verify(view, never()).showLoading();
        verify(view, never()).showSearchResults(anyList());
    }

    UsersList getDummyUserList() {
        List<User> githubUsers = new ArrayList<>();
        githubUsers.add(user1FullDetails());
        githubUsers.add(user2FullDetails());
        return new UsersList(githubUsers);
    }

    User user1FullDetails() {
        return new User(USER_LOGIN_RICARDO, "Ricardo Anzolin", "avatar_url", "Bio1");
    }

    User user2FullDetails() {
        return new User(USER_LOGIN_DYEGO, "Dyego Cantu", "avatar_url2", "Bio2");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userSearchPresenter = new UserSearchPresenter(userRepository, Schedulers.immediate(), Schedulers.immediate());
        userSearchPresenter.attachView(view);
    }
}
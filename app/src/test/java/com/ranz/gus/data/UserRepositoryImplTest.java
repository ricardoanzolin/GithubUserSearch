package com.ranz.gus.data;

import com.ranz.gus.data.remote.GithubUserRestService;
import com.ranz.gus.data.remote.model.User;
import com.ranz.gus.data.remote.model.UsersList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Ricardo on 28/08/2016.
 */
public class UserRepositoryImplTest {

    private static final String USER_LOGIN_RICARDO = "ricardo";
    private static final String USER_LOGIN_DYEGO = "dyego";

    @Mock
    GithubUserRestService githubUserRestService;

    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userRepository = new UserRepositoryImpl(githubUserRestService);
    }

    @Test
    public void searchUsers_200OkResponse_InvokesCorrectApiCalls(){

        //Dado o serviço de busca do Github
        when(githubUserRestService.searchGithubUsers(anyString()))
                .thenReturn(Observable.just(githubUserList()));

        when(githubUserRestService.getUser(anyString()))
                .thenReturn(Observable.just(user1FullDetails()), Observable.just(user2FullDetails()));

        //Quando eu busco um usuario
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_RICARDO).subscribe(subscriber);

        //Entao o resultado deve retornar e converter corretamente
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        List<List<User>> onNextEvents = subscriber.getOnNextEvents();
        List<User> users = onNextEvents.get(0);
        Assert.assertEquals(USER_LOGIN_RICARDO, users.get(0).getLogin());
        Assert.assertEquals(USER_LOGIN_DYEGO, users.get(1).getLogin());
        verify(githubUserRestService).searchGithubUsers(USER_LOGIN_RICARDO);
        verify(githubUserRestService).getUser(USER_LOGIN_RICARDO);
        verify(githubUserRestService).getUser(USER_LOGIN_DYEGO);
    }

    @Test
    public void searchUsers_IOExceptionThenSuccess_SearchUsersRetried(){
        //Dado
        when(githubUserRestService.searchGithubUsers(anyString()))
                .thenReturn(getIOExceptionError(), Observable.just(githubUserList()));
        when(githubUserRestService.getUser(anyString()))
                .thenReturn(Observable.just(user1FullDetails()), Observable.just(user2FullDetails()));

        //Quando
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_RICARDO).subscribe(subscriber);

        //Entao
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        verify(githubUserRestService, times(2)).searchGithubUsers(USER_LOGIN_RICARDO);

        verify(githubUserRestService).getUser(USER_LOGIN_RICARDO);
        verify(githubUserRestService).getUser(USER_LOGIN_DYEGO);
    }

    @Test
    public void searchUsers_GetUserIOExceptionThenSuccess_SearchUsersRetried() {
        //Given
        when(githubUserRestService.searchGithubUsers(anyString())).thenReturn(Observable.just(githubUserList()));
        when(githubUserRestService.getUser(anyString()))
                .thenReturn(getIOExceptionError(), Observable.just(user1FullDetails()),
                        Observable.just(user2FullDetails()));

        //When
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_RICARDO).subscribe(subscriber);

        //Then
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        verify(githubUserRestService, times(2)).searchGithubUsers(USER_LOGIN_RICARDO);

        verify(githubUserRestService, times(2)).getUser(USER_LOGIN_RICARDO);
        verify(githubUserRestService).getUser(USER_LOGIN_DYEGO);
    }

    @Test
    public void searchUsers_OtherHttpError_SearchTerminatedWithError() {
        //Given
        when(githubUserRestService.searchGithubUsers(anyString())).thenReturn(get403ForbiddenError());

        //When
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_RICARDO).subscribe(subscriber);

        //Then
        subscriber.awaitTerminalEvent();
        subscriber.assertError(HttpException.class);

        verify(githubUserRestService).searchGithubUsers(USER_LOGIN_RICARDO);

        verify(githubUserRestService, never()).getUser(USER_LOGIN_RICARDO);
        verify(githubUserRestService, never()).getUser(USER_LOGIN_DYEGO);
    }

    private Observable getIOExceptionError() {
        return Observable.error(new IOException());
    }

    private Observable<UsersList> get403ForbiddenError() {
        return Observable.error(new HttpException(
                Response.error(403, ResponseBody.create(MediaType.parse("application/json"), "Forbidden"))));

    }

    private UsersList githubUserList() {
        User user = new User();
        user.setLogin(USER_LOGIN_RICARDO);

        User user2 = new User();
        user2.setLogin(USER_LOGIN_DYEGO);

        List<User> githubUsers = new ArrayList<>();
        githubUsers.add(user);
        githubUsers.add(user2);
        UsersList usersList = new UsersList();
        usersList.setItems(githubUsers);
        return usersList;
    }

    private User user1FullDetails() {
        User user = new User();
        user.setLogin(USER_LOGIN_RICARDO);
        user.setName("Ricardo Anzolin");
        user.setAvatarUrl("avatar_url");
        user.setBio("Bio1");
        return user;
    }

    private User user2FullDetails() {
        User user = new User();
        user.setLogin(USER_LOGIN_DYEGO);
        user.setName("Dyego Cantu");
        user.setAvatarUrl("avatar_url2");
        user.setBio("Bio2");
        return user;
    }


}
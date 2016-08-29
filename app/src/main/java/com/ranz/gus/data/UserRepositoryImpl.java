package com.ranz.gus.data;

import com.ranz.gus.data.remote.GithubUserRestService;
import com.ranz.gus.data.remote.model.User;

import java.io.IOException;
import java.util.List;

import rx.Observable;

/**
 * Created by Ricardo on 28/08/2016.
 */
public class UserRepositoryImpl implements UserRepository {

    private GithubUserRestService githubUserRestService;

    public UserRepositoryImpl(GithubUserRestService githubUserRestService){
        this.githubUserRestService = githubUserRestService;
    }

    @Override
    public Observable<List<User>> searchUsers(final String searchTerm){
        return Observable.defer(() -> githubUserRestService.searchGithubUsers(searchTerm).concatMap(
                usersList -> Observable.from(usersList.getItems())
                        .concatMap(user -> githubUserRestService.getUser(user.getLogin())).toList()))
                .retryWhen(observable -> observable.flatMap(o -> {
                    if (o instanceof IOException) return Observable.just(null);
                    return Observable.error(o);
                }));
    }
}

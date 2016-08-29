package com.ranz.gus.data;

import com.ranz.gus.data.remote.model.User;

import java.util.List;

import rx.Observable;

/**
 * Created by Ricardo on 28/08/2016.
 */
public interface UserRepository {
    Observable<List<User>> searchUsers(String searchTerm);
}

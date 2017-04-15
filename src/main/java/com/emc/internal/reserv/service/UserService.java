package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.User;
import https.internal_emc_com.reserv_io.ws.SearchType;
import https.internal_emc_com.reserv_io.ws.SortingOrder;
import https.internal_emc_com.reserv_io.ws.UserSearchableField;

import java.util.Collection;
import java.util.Optional;

/**
 * @author trofiv
 * @date 04.04.2017
 */
public interface UserService {
    void registerUser(final String username,
                      final String email,
                      final String password,
                      final String firstName,
                      final String lastName,
                      final String middleName);

    Optional<User> getUser(final int id);

    Collection<User> getUsers(final int page,
                              final int pageSize,
                              final UserSearchableField searchField,
                              final SearchType searchType,
                              final Object searchValue,
                              final Object searchValueLowerBound,
                              final Object searchValueUpperBound,
                              final SortingOrder sortingOrder,
                              final UserSearchableField sortingField);
}

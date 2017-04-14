package com.emc.internal.reserv.service;

import com.emc.internal.reserv.entity.User;
import https.internal_emc_com.reserv_io.ws.SearchType;
import https.internal_emc_com.reserv_io.ws.SortingOrder;
import https.internal_emc_com.reserv_io.ws.UserField;

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
                              final UserField searchField,
                              final SearchType searchType,
                              final String searchValue,
                              final String searchValueLowerBound,
                              final String searchValueUpperBound,
                              final SortingOrder sortingOrder,
                              final UserField sortingField);
}

package com.emc.internal.reserv.service;

import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.SortingOrder;
import com.emc.internal.reserv.dto.UserSearchableField;
import com.emc.internal.reserv.entity.Role;
import com.emc.internal.reserv.entity.User;

import java.util.Collection;

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

    User getUser(final int id);

    User getUser(final String username);

    Collection<User> getUsers(final int page,
                              final int pageSize,
                              final UserSearchableField searchField,
                              final SearchType searchType,
                              final Object searchValue,
                              final Object searchValueLowerBound,
                              final Object searchValueUpperBound,
                              final SortingOrder sortingOrder,
                              final UserSearchableField sortingField);

    void updateUserInfo(final User user,
                        final String email,
                        final String username,
                        final String firstName,
                        final String middleName,
                        final String lastName);

    void updatePassword(final User user,
                        final String newPassword);

    void grantPermissions(final User user,
                          final Role role);
}

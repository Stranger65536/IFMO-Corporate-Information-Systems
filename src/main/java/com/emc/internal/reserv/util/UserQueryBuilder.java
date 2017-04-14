package com.emc.internal.reserv.util;

import com.emc.internal.reserv.entity.User;
import https.internal_emc_com.reserv_io.ws.SearchType;
import https.internal_emc_com.reserv_io.ws.SortingOrder;
import https.internal_emc_com.reserv_io.ws.UserField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import static java.text.MessageFormat.format;

/**
 * @author trofiv
 * @date 14.04.2017
 */
@Service
public class UserQueryBuilder implements QueryBuilder<User, UserField> {
    private final EntityManager entityManager;

    @Autowired
    public UserQueryBuilder(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public TypedQuery<User> buildQuery(
            final UserField searchField,
            final SearchType searchType,
            final Object searchValue,
            final Object searchValueLowerBound,
            final Object searchValueUpperBound,
            final SortingOrder sortingOrder,
            final UserField sortingField) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> query = builder.createQuery(User.class);
        final Root<User> root = query.from(User.class);
        CriteriaQuery<User> select = query.select(root);

        if (sortingField != null) {
            select = select.orderBy(getOrderExpression(builder, sortingOrder, getSortExpression(sortingField, root)));
        }

        if (searchField != null) {
            switch (searchType) {
                case EQUALS:
                    select = select.where(getEqualsExpression(root, builder, searchField, searchValue));
                    break;
                case CONTAINS:
                    select = select.where(getLikeExpression(root, builder, searchField, searchValue));
                    break;
                case BETWEEN:
                    select = select.where(getBetweenExpression(root, builder, searchField,
                            searchValueLowerBound, searchValueUpperBound));
                    break;
            }
        }

        return entityManager.createQuery(select);
    }

    @Override
    public Expression<?> getSortExpression(final UserField sortingField, final From root) {
        return sortingField == UserField.ROLE
                ? root.join(UserField.ROLE.value()).get("name")
                : root.get(sortingField.value());
    }

    @Override
    public Expression<Boolean> getEqualsExpression(
            final From root,
            final CriteriaBuilder builder,
            final UserField searchField,
            final Object searchValue) {
        return builder.equal(
                searchField == UserField.ROLE
                        ? root.join(UserField.ROLE.value()).get("name")
                        : root.get(searchField.value()),
                searchValue);
    }

    @Override
    public Expression<Boolean> getLikeExpression(
            final From root,
            final CriteriaBuilder builder,
            final UserField searchField,
            final Object searchValue) {
        return builder.like(
                searchField == UserField.ROLE
                        ? root.join(UserField.ROLE.value()).get("name")
                        : root.get(searchField.value()).as(String.class),
                format("%{0}%", searchValue), '%');
    }

    @Override
    public Expression<Boolean> getBetweenExpression(
            final Root<User> root,
            final CriteriaBuilder builder,
            final UserField searchField,
            final Object searchValueLowerBound,
            final Object searchValueUpperBound) {
        switch (searchField) {
            case ID:
                return builder.between(root.get(searchField.value()), (Integer) searchValueLowerBound, (Integer) searchValueUpperBound);
            case EMAIL:
            case USERNAME:
            case FIRST_NAME:
            case LAST_NAME:
            case MIDDLE_NAME:
                return builder.between(root.get(searchField.value()), (String) searchValueLowerBound, (String) searchValueUpperBound);
            case ROLE:
                return builder.between(root.join(UserField.ROLE.value()).get("name"), (String) searchValueLowerBound, (String) searchValueUpperBound);
            default:
                throw new UnsupportedOperationException("Some fields have hot been covered!");
        }
    }
}

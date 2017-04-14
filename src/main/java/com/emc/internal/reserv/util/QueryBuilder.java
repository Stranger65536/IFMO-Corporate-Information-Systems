package com.emc.internal.reserv.util;

import com.emc.internal.reserv.entity.User;
import https.internal_emc_com.reserv_io.ws.SearchType;
import https.internal_emc_com.reserv_io.ws.SortingOrder;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import static https.internal_emc_com.reserv_io.ws.SortingOrder.ASC;

/**
 * @author trofiv
 * @date 14.04.2017
 */
public interface QueryBuilder<T, R extends Enum<?>> {
    default Order getOrderExpression(
            final CriteriaBuilder criteriaBuilder,
            final SortingOrder sortingOrder,
            final Expression<?> expression) {
        return sortingOrder == ASC
                ? criteriaBuilder.asc(expression)
                : criteriaBuilder.desc(expression);
    }

    Expression<?> getSortExpression(final R sortingField, final From root);

    Expression<Boolean> getEqualsExpression(
            final From root,
            final CriteriaBuilder builder,
            final R searchField,
            final Object searchValue);

    Expression<Boolean> getLikeExpression(
            final From root,
            final CriteriaBuilder builder,
            final R searchField,
            final Object searchValue);

    Expression<Boolean> getBetweenExpression(
            final Root<User> root,
            final CriteriaBuilder builder,
            final R searchField,
            final Object searchValueLowerBound,
            final Object searchValueUpperBound);

    TypedQuery<T> buildQuery(
            final R searchField,
            final SearchType searchType,
            final Object searchValue,
            final Object searchValueLowerBound,
            final Object searchValueUpperBound,
            final SortingOrder sortingOrder,
            final R sortingField);
}

package com.emc.internal.reserv.util.query;

import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.SortingOrder;
import com.emc.internal.reserv.entity.User;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import static com.emc.internal.reserv.dto.SortingOrder.DESC;

/**
 * @author trofiv
 * @date 14.04.2017
 */
public interface QueryBuilder<T, R extends Enum<?>> {
    default Order getOrderExpression(
            final CriteriaBuilder criteriaBuilder,
            final SortingOrder sortingOrder,
            final Expression<?> expression) {
        return sortingOrder == DESC
                ? criteriaBuilder.desc(expression)
                : criteriaBuilder.asc(expression);
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

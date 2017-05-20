package com.emc.internal.reserv.util.query;

import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.SortingOrder;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import static com.emc.internal.reserv.dto.SortingOrder.DESC;
import static com.emc.internal.reserv.util.RuntimeUtil.raiseForgotEnumBranchException;

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

    TypedQuery<T> buildQuery(
            final R searchField,
            final SearchType searchType,
            final Object searchValue,
            final Object searchValueLowerBound,
            final Object searchValueUpperBound,
            final SortingOrder sortingOrder,
            final R sortingField);

    @SuppressWarnings("OverlyComplexMethod")
    default CriteriaQuery<T> getSearchCriteriaQuery(
            final R searchField,
            final SearchType searchType,
            final Object searchValue,
            final Object searchValueLowerBound,
            final Object searchValueUpperBound,
            final CriteriaBuilder builder,
            final Root<T> root,
            CriteriaQuery<T> select) {
        if (searchField != null && searchType != null) {
            switch (searchType) {
                case EQUALS:
                    select = select.where(getEqualsExpression(root, builder, searchField, searchValue));
                    break;
                case GREATER_EQUAL:
                    select = select.where(getGreaterEqualsExpression(root, builder, searchField, searchValue));
                    break;
                case LESS_EQUAL:
                    select = select.where(getLessEqualsExpression(root, builder, searchField, searchValue));
                    break;
                case GREATER:
                    select = select.where(getGreaterExpression(root, builder, searchField, searchValue));
                    break;
                case LESS:
                    select = select.where(getLessExpression(root, builder, searchField, searchValue));
                    break;
                case CONTAINS:
                    select = select.where(getLikeExpression(root, builder, searchField, searchValue));
                    break;
                case BETWEEN:
                    select = select.where(getBetweenExpression(root, builder, searchField, searchValueLowerBound, searchValueUpperBound));
                    break;
                default:
                    throw raiseForgotEnumBranchException();
            }
        }
        return select;
    }

    Expression<Boolean> getEqualsExpression(
            final From root,
            final CriteriaBuilder builder,
            final R searchField,
            final Object searchValue);

    Expression<Boolean> getGreaterEqualsExpression(
            final From root,
            final CriteriaBuilder builder,
            final R searchField,
            final Object searchValue);

    Expression<Boolean> getLessEqualsExpression(
            final From root,
            final CriteriaBuilder builder,
            final R searchField,
            final Object searchValue);

    Expression<Boolean> getGreaterExpression(
            final From root,
            final CriteriaBuilder builder,
            final R searchField,
            final Object searchValue);

    Expression<Boolean> getLessExpression(
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
            final Root<?> root,
            final CriteriaBuilder builder,
            final R searchField,
            final Object searchValueLowerBound,
            final Object searchValueUpperBound);
}

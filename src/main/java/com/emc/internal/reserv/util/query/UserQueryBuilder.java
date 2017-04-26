package com.emc.internal.reserv.util.query;

import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.SortingOrder;
import com.emc.internal.reserv.dto.UserField;
import com.emc.internal.reserv.dto.UserSearchableField;
import com.emc.internal.reserv.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Root;

import static com.emc.internal.reserv.util.RuntimeUtil.raiseForgotEnumBranchException;
import static java.text.MessageFormat.format;

/**
 * @author trofiv
 * @date 14.04.2017
 */
@Service
public class UserQueryBuilder implements QueryBuilder<User, UserSearchableField> {
    private final EntityManager entityManager;

    @Autowired
    public UserQueryBuilder(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    public TypedQuery<User> buildQuery(
            final UserSearchableField searchField,
            final SearchType searchType,
            final Object searchValue,
            final Object searchValueLowerBound,
            final Object searchValueUpperBound,
            final SortingOrder sortingOrder,
            final UserSearchableField sortingField) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> query = builder.createQuery(User.class);
        final Root<User> root = query.from(User.class);
        CriteriaQuery<User> select = query.select(root);

        if (sortingField != null) {
            select = select.orderBy(getOrderExpression(builder, sortingOrder, getSortExpression(sortingField, root)));
        }

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
                    select = select.where(getBetweenExpression(root, builder, searchField,
                            searchValueLowerBound, searchValueUpperBound));
                    break;
                default:
                    throw raiseForgotEnumBranchException();
            }
        }

        return entityManager.createQuery(select);
    }

    @Override
    public Expression<?> getSortExpression(final UserSearchableField sortingField, final From root) {
        return sortingField == UserSearchableField.ROLE
                ? root.join(UserField.ROLE.value()).get("name")
                : root.get(sortingField.value());
    }

    @Override
    public Expression<Boolean> getEqualsExpression(
            final From root,
            final CriteriaBuilder builder,
            final UserSearchableField searchField,
            final Object searchValue) {
        return builder.equal(
                searchField == UserSearchableField.ROLE
                        ? root.join(UserField.ROLE.value()).get("name")
                        : root.get(searchField.value()),
                searchValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Expression<Boolean> getGreaterEqualsExpression(
            final From root,
            final CriteriaBuilder builder,
            final UserSearchableField searchField,
            final Object searchValue) {
        switch (searchField) {
            case ID:
                return builder.greaterThanOrEqualTo(root.<Integer>get(searchField.value()), (Integer) searchValue);
            case EMAIL:
            case USERNAME:
            case FIRST_NAME:
            case LAST_NAME:
            case MIDDLE_NAME:
                return builder.greaterThanOrEqualTo(root.<String>get(searchField.value()), (String) searchValue);
            case ROLE:
                return builder.greaterThanOrEqualTo(root.join(UserField.ROLE.value()).get("name"), (String) searchValue);
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Expression<Boolean> getLessEqualsExpression(
            final From root,
            final CriteriaBuilder builder,
            final UserSearchableField searchField,
            final Object searchValue) {
        switch (searchField) {
            case ID:
                return builder.lessThanOrEqualTo(root.<Integer>get(searchField.value()), (Integer) searchValue);
            case EMAIL:
            case USERNAME:
            case FIRST_NAME:
            case LAST_NAME:
            case MIDDLE_NAME:
                return builder.lessThanOrEqualTo(root.<String>get(searchField.value()), (String) searchValue);
            case ROLE:
                return builder.lessThanOrEqualTo(root.join(UserField.ROLE.value()).get("name"), (String) searchValue);
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Expression<Boolean> getGreaterExpression(
            final From root,
            final CriteriaBuilder builder,
            final UserSearchableField searchField,
            final Object searchValue) {
        switch (searchField) {
            case ID:
                return builder.greaterThan(root.<Integer>get(searchField.value()), (Integer) searchValue);
            case EMAIL:
            case USERNAME:
            case FIRST_NAME:
            case LAST_NAME:
            case MIDDLE_NAME:
                return builder.greaterThan(root.<String>get(searchField.value()), (String) searchValue);
            case ROLE:
                return builder.greaterThan(root.join(UserField.ROLE.value()).get("name"), (String) searchValue);
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Expression<Boolean> getLessExpression(
            final From root,
            final CriteriaBuilder builder,
            final UserSearchableField searchField,
            final Object searchValue) {
        switch (searchField) {
            case ID:
                return builder.lessThan(root.<Integer>get(searchField.value()), (Integer) searchValue);
            case EMAIL:
            case USERNAME:
            case FIRST_NAME:
            case LAST_NAME:
            case MIDDLE_NAME:
                return builder.lessThan(root.<String>get(searchField.value()), (String) searchValue);
            case ROLE:
                return builder.lessThan(root.join(UserField.ROLE.value()).get("name"), (String) searchValue);
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Expression<Boolean> getLikeExpression(
            final From root,
            final CriteriaBuilder builder,
            final UserSearchableField searchField,
            final Object searchValue) {
        return builder.like(
                searchField == UserSearchableField.ROLE
                        ? root.join(UserField.ROLE.value()).get("name").as(String.class)
                        : root.<String>get(searchField.value()).as(String.class),
                format("%{0}%", searchValue), '%');
    }

    @Override
    public Expression<Boolean> getBetweenExpression(
            final Root<User> root,
            final CriteriaBuilder builder,
            final UserSearchableField searchField,
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
                throw raiseForgotEnumBranchException();
        }
    }
}

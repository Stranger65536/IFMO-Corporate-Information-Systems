package com.emc.internal.reserv.util.query;

import com.emc.internal.reserv.dto.ReservationSearchableField;
import com.emc.internal.reserv.dto.SearchType;
import com.emc.internal.reserv.dto.SortingOrder;
import com.emc.internal.reserv.entity.ActualReservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;

import static com.emc.internal.reserv.util.RuntimeUtil.raiseForgotEnumBranchException;
import static java.text.MessageFormat.format;

/**
 * @author trofiv
 * @date 14.04.2017
 */
@Service
public class ActualReservationQueryBuilder implements QueryBuilder<ActualReservation, ReservationSearchableField> {
    private final EntityManager entityManager;

    @Autowired
    public ActualReservationQueryBuilder(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    public TypedQuery<ActualReservation> buildQuery(
            final ReservationSearchableField searchField,
            final SearchType searchType,
            final Object searchValue,
            final Object searchValueLowerBound,
            final Object searchValueUpperBound,
            final SortingOrder sortingOrder,
            final ReservationSearchableField sortingField) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ActualReservation> query = builder.createQuery(ActualReservation.class);
        final Root<ActualReservation> root = query.from(ActualReservation.class);
        CriteriaQuery<ActualReservation> select = query.select(root);

        if (sortingField != null) {
            select = select.orderBy(getOrderExpression(builder, sortingOrder, getSortExpression(sortingField, root)));
        }

        select = getSearchCriteriaQuery(searchField, searchType, searchValue,
                searchValueLowerBound, searchValueUpperBound, builder, root, select);

        return entityManager.createQuery(select);
    }

    @Override
    public Expression<?> getSortExpression(final ReservationSearchableField sortingField, final From root) {
        switch (sortingField) {
            case ID:
            case OWNER:
            case RESOURCE:
            case ENDS_AT:
            case STARTS_AT:
            case CREATED_AT:
            case UPDATED_AT:
                return root.get(sortingField.value());
            case TYPE:
            case STATUS:
                return root.get(sortingField.value()).get("name");
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    public Expression<Boolean> getEqualsExpression(
            final From root,
            final CriteriaBuilder builder,
            final ReservationSearchableField searchField,
            final Object searchValue) {
        switch (searchField) {
            case ID:
            case OWNER:
            case RESOURCE:
            case ENDS_AT:
            case STARTS_AT:
            case CREATED_AT:
            case UPDATED_AT:
                return builder.equal(root.get(searchField.value()), searchValue);
            case TYPE:
            case STATUS:
                return builder.equal(root.get(searchField.value()).get("name"), searchValue);
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Expression<Boolean> getGreaterEqualsExpression(
            final From root,
            final CriteriaBuilder builder,
            final ReservationSearchableField searchField,
            final Object searchValue) {
        switch (searchField) {
            case ID:
            case OWNER:
            case RESOURCE:
                return builder.greaterThanOrEqualTo(root.<Integer>get(searchField.value()), (Integer) searchValue);
            case ENDS_AT:
            case STARTS_AT:
            case CREATED_AT:
            case UPDATED_AT:
                return builder.greaterThanOrEqualTo(root.<Timestamp>get(searchField.value()), (Timestamp) searchValue);
            case TYPE:
            case STATUS:
                return builder.greaterThanOrEqualTo(root.<String>get(searchField.value()).get("name"), (String) searchValue);
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Expression<Boolean> getLessEqualsExpression(
            final From root,
            final CriteriaBuilder builder,
            final ReservationSearchableField searchField,
            final Object searchValue) {
        switch (searchField) {
            case ID:
            case OWNER:
            case RESOURCE:
                return builder.lessThanOrEqualTo(root.<Integer>get(searchField.value()), (Integer) searchValue);
            case ENDS_AT:
            case STARTS_AT:
            case CREATED_AT:
            case UPDATED_AT:
                return builder.lessThanOrEqualTo(root.<Timestamp>get(searchField.value()), (Timestamp) searchValue);
            case TYPE:
            case STATUS:
                return builder.lessThanOrEqualTo(root.<String>get(searchField.value()).get("name"), (String) searchValue);
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Expression<Boolean> getGreaterExpression(
            final From root,
            final CriteriaBuilder builder,
            final ReservationSearchableField searchField,
            final Object searchValue) {
        switch (searchField) {
            case ID:
            case OWNER:
            case RESOURCE:
                return builder.greaterThan(root.<Integer>get(searchField.value()), (Integer) searchValue);
            case ENDS_AT:
            case STARTS_AT:
            case CREATED_AT:
            case UPDATED_AT:
                return builder.greaterThan(root.<Timestamp>get(searchField.value()), (Timestamp) searchValue);
            case TYPE:
            case STATUS:
                return builder.greaterThan(root.<String>get(searchField.value()).get("name"), (String) searchValue);
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Expression<Boolean> getLessExpression(
            final From root,
            final CriteriaBuilder builder,
            final ReservationSearchableField searchField,
            final Object searchValue) {
        switch (searchField) {
            case ID:
            case OWNER:
            case RESOURCE:
                return builder.lessThan(root.<Integer>get(searchField.value()), (Integer) searchValue);
            case ENDS_AT:
            case STARTS_AT:
            case CREATED_AT:
            case UPDATED_AT:
                return builder.lessThan(root.<Timestamp>get(searchField.value()), (Timestamp) searchValue);
            case TYPE:
            case STATUS:
                return builder.lessThan(root.<String>get(searchField.value()).get("name"), (String) searchValue);
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Expression<Boolean> getLikeExpression(
            final From root,
            final CriteriaBuilder builder,
            final ReservationSearchableField searchField,
            final Object searchValue) {
        switch (searchField) {
            case ID:
            case ENDS_AT:
            case STARTS_AT:
            case CREATED_AT:
            case UPDATED_AT:
                return builder.like(root.<String>get(searchField.value()).as(String.class),
                        format("%{0}%", searchValue), '%');
            case OWNER:
            case RESOURCE:
                return builder.like(root.<String>get(searchField.value()).get("id").as(String.class),
                        format("%{0}%", searchValue), '%');
            case TYPE:
            case STATUS:
                return builder.like(root.<String>get(searchField.value()).get("name").as(String.class),
                        format("%{0}%", searchValue), '%');
            default:
                throw raiseForgotEnumBranchException();
        }
    }

    @Override
    public Expression<Boolean> getBetweenExpression(
            final Root<?> root,
            final CriteriaBuilder builder,
            final ReservationSearchableField searchField,
            final Object searchValueLowerBound,
            final Object searchValueUpperBound) {
        switch (searchField) {
            case ID:
            case OWNER:
            case RESOURCE:
                return builder.between(root.get(searchField.value()), (Integer) searchValueLowerBound, (Integer) searchValueUpperBound);
            case ENDS_AT:
            case STARTS_AT:
            case CREATED_AT:
            case UPDATED_AT:
                return builder.between(root.get(searchField.value()), (Timestamp) searchValueLowerBound, (Timestamp) searchValueUpperBound);
            case TYPE:
            case STATUS:
                return builder.between(root.<String>get(searchField.value()).get("name"), (String) searchValueLowerBound, (String) searchValueUpperBound);
            default:
                throw raiseForgotEnumBranchException();
        }
    }
}

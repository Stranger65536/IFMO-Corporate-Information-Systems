package com.emc.internal.reserv.util;

import https.internal_emc_com.reserv_io.ws.SortingOrder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import java.util.function.Consumer;

import static https.internal_emc_com.reserv_io.ws.SortingOrder.ASC;

/**
 * @author trofiv
 * @date 12.04.2017
 */
public class HibernateUtil {
    public static <T> Order getOrderExpression(
            final CriteriaBuilder criteriaBuilder,
            final SortingOrder sortingOrder,
            final Expression<T> expression) {
        return sortingOrder == ASC
                ? criteriaBuilder.asc(expression)
                : criteriaBuilder.desc(expression);
    }
}

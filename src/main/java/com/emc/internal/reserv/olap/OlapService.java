package com.emc.internal.reserv.olap;


import mondrian.olap.Result;

/**
 * @author trofiv
 * @date 21.05.2017
 */
public interface OlapService {
    Result query(final String mdx);
}

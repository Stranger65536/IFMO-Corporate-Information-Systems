package com.emc.internal.reserv.olap;

import mondrian.olap.Query;
import mondrian.olap.Result;
import mondrian.rolap.RolapConnection;
import mondrian.server.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author trofiv
 * @date 21.05.2017
 */
@Service
public class OlapServiceImpl implements OlapService {
    private final RolapConnection olapConnection;

    @Autowired
    public OlapServiceImpl(final RolapConnection olapConnection) {
        this.olapConnection = olapConnection;
    }

    @Override
    public Result query(final String mdx) {
        final Query query = olapConnection.parseQuery(mdx);
        final Execution execution = new Execution(query.getStatement(), 0L);
        return olapConnection.execute(execution);
    }
}

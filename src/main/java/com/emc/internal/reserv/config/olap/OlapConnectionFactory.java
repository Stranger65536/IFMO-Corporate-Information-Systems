package com.emc.internal.reserv.config.olap;

import mondrian.olap.DriverManager;
import mondrian.olap.Util.PropertyList;
import mondrian.rolap.RolapConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author trofiv
 * @date 21.05.2017
 */
@Configuration
public class OlapConnectionFactory {
    static {
        try {
            Class.forName("mondrian.olap4j.MondrianOlap4jDriver");
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private final DataSource dataSource;

    @Autowired
    public OlapConnectionFactory(@Qualifier("dataSource") final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    @Autowired
    @SuppressWarnings("SynchronizedMethod")
    public synchronized RolapConnection olapConnection(@Value("${olap.schema-file}") final String schemaFile)
            throws ClassNotFoundException, SQLException {
        return (RolapConnection) DriverManager.getConnection(new PropertyList(), catalogPath -> schemaFile, dataSource);
    }
}
package com.open.crm.tenancy;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<UUID> {
	private final DataSource dataSource;

	@Override
	public Connection getAnyConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		connection.close();
	}

	@Override
	public Connection getConnection(UUID tenantIdentifier) throws SQLException {
		Connection connection = getAnyConnection();
		try (Statement stmt = connection.createStatement()) {
			String schemaName = "tenant_" + tenantIdentifier.toString().replace("-", "_");

			stmt.execute("SET search_path TO \"" + schemaName + "\", public");
        } catch (SQLException e) {
			log.error("Failed to set search_path for tenant {}: {}", tenantIdentifier, e.getMessage(), e);
			connection.close();
			throw e;
		}
		return connection;
	}

	@Override
    public void releaseConnection(UUID tenantIdentifier, Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET search_path TO public");
        } finally {
            connection.close();
        }
    }

    // остальные методы стандартно:
    @Override public boolean supportsAggressiveRelease() { return false; }
    @Override public boolean isUnwrappableAs(Class unwrapType) { return false; }
    @Override public <T> T unwrap(Class<T> unwrapType) { return null; }
}

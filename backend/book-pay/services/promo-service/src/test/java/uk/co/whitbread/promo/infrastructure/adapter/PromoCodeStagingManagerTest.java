package uk.co.whitbread.promo.infrastructure.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.PGConnection;

import javax.sql.DataSource;
import java.sql.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PromoCodeStagingManagerTest {

    private DataSource dataSource;
    private Connection connection;
    private Statement statement;

    private PromoCodeStagingManager stagingManager;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        statement = mock(Statement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);

        stagingManager = new PromoCodeStagingManager(dataSource);
    }

    @Test
    void openConnection_shouldReturnConnection() throws Exception {
        Connection conn = stagingManager.openConnection();
        assertNotNull(conn);
        verify(dataSource).getConnection();
    }

    @Test
    void configureSession_shouldExecuteStatements() throws Exception {
        assertDoesNotThrow(() -> stagingManager.configureSession(connection, 1000, 5000));
        verify(statement, atLeastOnce()).execute(anyString());
    }

    @Test
    void createStagingTable_shouldExecuteSql() throws Exception {
        assertDoesNotThrow(() -> stagingManager.createStagingTable(connection, "tmp_table"));
        verify(statement).execute(contains("CREATE TEMP TABLE"));
    }

    @Test
    void dropStagingTable_shouldExecuteSql() throws Exception {
        assertDoesNotThrow(() -> stagingManager.dropStagingTable(connection, "tmp_table"));
        verify(statement).execute(contains("DROP TABLE"));
    }

    @Test
    void acquireAdvisoryLock_shouldReturnTrueWhenDbReturnsTrue() throws Exception {
        PreparedStatement psAcquire = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(psAcquire);

        ResultSet rs = singleBoolean(true);
        when(psAcquire.executeQuery()).thenReturn(rs);

        boolean result = stagingManager.acquireAdvisoryLock(connection, UUID.randomUUID());

        assertTrue(result);
        verify(psAcquire).executeQuery();
        verify(psAcquire).close();
    }

    @Test
    void releaseAdvisoryLock_shouldExecuteWithoutException() throws Exception {
        PreparedStatement psRelease = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(psRelease);

        assertDoesNotThrow(() -> stagingManager.releaseAdvisoryLock(connection, UUID.randomUUID()));

        verify(psRelease).execute();
        verify(psRelease).close();
    }

    @Test
    void unwrapPg_shouldThrowSQLExceptionWhenNotPostgres() throws SQLException {
        when(connection.unwrap(PGConnection.class)).thenThrow(new SQLException("Not a Postgres connection"));
        assertThrows(SQLException.class, () -> stagingManager.unwrapPg(connection));
    }

    public static ResultSet singleBoolean(boolean value) throws Exception {
        ResultSet rs = mock(ResultSet.class);

        when(rs.next()).thenReturn(true, false);
        when(rs.getBoolean(1)).thenReturn(value);

        return rs;
    }
}

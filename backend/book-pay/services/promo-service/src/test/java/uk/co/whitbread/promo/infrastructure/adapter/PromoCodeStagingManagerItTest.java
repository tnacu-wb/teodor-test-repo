package uk.co.whitbread.promo.infrastructure.adapter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import uk.co.whitbread.promo.infrastructure.config.S3Config;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@Import(S3Config.class)
class PromoCodeStagingManagerItTest {

    @Autowired
    private DataSource copyDataSource;

    private PromoCodeStagingManager stagingManager;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        stagingManager = new PromoCodeStagingManager(copyDataSource);
        connection = stagingManager.openConnection();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void openConnection_shouldReturnOpenConnection() throws SQLException {
        assertNotNull(connection);
        assertFalse(connection.isClosed());
    }

    @Test
    void configureSession_shouldFailOnH2() {
        assertThrows(SQLException.class, () ->
                stagingManager.configureSession(connection, 1000, 5000)
        );
    }

    @Test
    void createStagingTable_shouldFailOnH2() {
        assertThrows(SQLException.class, () ->
                stagingManager.createStagingTable(connection, "tmp_test_table")
        );
    }

    @Test
    void dropStagingTable_shouldNotThrowEvenIfTableMissing() {
        assertDoesNotThrow(() ->
                stagingManager.dropStagingTable(connection, "tmp_test_table")
        );
    }

    @Test
    void acquireAdvisoryLock_shouldFailOnH2() {
        assertThrows(SQLException.class, () ->
                stagingManager.acquireAdvisoryLock(connection, UUID.randomUUID())
        );
    }

    @Test
    void unwrapPg_shouldFailOnNonPostgresConnection() {
        assertThrows(SQLException.class, () ->
                stagingManager.unwrapPg(connection)
        );
    }
}

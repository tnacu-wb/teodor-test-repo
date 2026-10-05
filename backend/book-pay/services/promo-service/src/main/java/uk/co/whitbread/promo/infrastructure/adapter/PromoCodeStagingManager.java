package uk.co.whitbread.promo.infrastructure.adapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(
    name = "copy-datasource.enabled",
    havingValue = "true"
)
@Component
@RequiredArgsConstructor
@Slf4j
public class PromoCodeStagingManager {

  private final @Qualifier("copyDataSource") DataSource dataSource;

  public Connection openConnection() throws SQLException {
    return dataSource.getConnection();
  }

  public void configureSession(
      Connection conn,
      int lockTimeoutMs,
      int statementTimeoutMs) throws SQLException {

    try (Statement st = conn.createStatement()) {
      st.execute("SET LOCAL synchronous_commit = off");
      st.execute("SET LOCAL work_mem = '256MB'");
      st.execute("SET LOCAL lock_timeout = " + lockTimeoutMs);
      st.execute("SET LOCAL statement_timeout = " + statementTimeoutMs);
      st.execute("SET LOCAL jit = off");
      st.execute("SET LOCAL temp_buffers = '64MB'");
    }
  }

  public boolean acquireAdvisoryLock(Connection conn, UUID batchId) throws SQLException {
    String sql =
        "SELECT pg_try_advisory_lock((('x' || substring(md5(?),1,16))::bit(64))::bigint)";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, batchId.toString());
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() && rs.getBoolean(1);
      }
    }
  }

  public void releaseAdvisoryLock(Connection conn, UUID batchId) throws SQLException {
    String sql =
        "SELECT pg_advisory_unlock((('x' || substring(md5(?),1,16))::bit(64))::bigint)";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, batchId.toString());
      ps.execute();
    }
  }

  public void createStagingTable(Connection conn, String tableName) throws SQLException {
    try (Statement st = conn.createStatement()) {
      st.execute("""
          CREATE TEMP TABLE IF NOT EXISTS %s
          (code VARCHAR, promo_batch_id UUID, status VARCHAR)
          ON COMMIT PRESERVE ROWS
          """.formatted(tableName));
    }
  }

  public void dropStagingTable(Connection conn, String tableName) {
    try (Statement st = conn.createStatement()) {
      st.execute("DROP TABLE IF EXISTS " + tableName);
    } catch (Exception e) {
      log.warn("Failed to drop temp table {}", tableName, e);
    }
  }

  public PGConnection unwrapPg(Connection conn) throws SQLException {
    return conn.unwrap(PGConnection.class);
  }
}
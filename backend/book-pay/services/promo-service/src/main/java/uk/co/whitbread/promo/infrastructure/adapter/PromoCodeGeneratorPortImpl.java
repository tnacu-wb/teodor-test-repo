package uk.co.whitbread.promo.infrastructure.adapter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.springframework.stereotype.Component;
import uk.co.whitbread.promo.domain.ports.secondary.PromoBatchCodeGeneratorOutPort;
import uk.co.whitbread.promo.infrastructure.exception.ErrorCode;
import uk.co.whitbread.promo.infrastructure.exception.PromoBatchException;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeStatus;
import uk.co.whitbread.promo.infrastructure.rest.client.CodeGenerator;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromoCodeGeneratorPortImpl implements PromoBatchCodeGeneratorOutPort {

  private final PromoCodeStagingManager stagingManager;
  private static final int MAX_ATTEMPTS = 12;
  private static final int CHUNK_BYTES = 1 << 20;
  private static final int COPY_BATCH_LIMIT = 250_000;
  private static final int UNIQUE_MEMORY_THRESHOLD = 200_000;
  private static final int LOCK_TIMEOUT_MS = 30_000;
  private static final int STATEMENT_TIMEOUT_MS = 600_000;
  private static final int INSERT_DEADLOCK_RETRIES = 5;
  private static final long BASE_RETRY_SLEEP_MS = 100L;
  private static final int MIN_UNIQUE_CODE_LENGTH = 8;
  private static final int BATCH_PREFIX_LENGTH = 2;

  @Override
  public void generateCodes(UUID batchId, int count, String prefix, int codeLen) {
    try {
      generateCodesUsingStaging(batchId, count, prefix, codeLen);
    } catch (SQLException ex) {
      throw new PromoBatchException(ErrorCode.DATABASE_ERROR,
          String.format("Database error during code generation for batch: %s", batchId));
    } catch (IllegalStateException ex) {
      throw new PromoBatchException(ErrorCode.LOCK_ACQUISITION_FAILED,
          ex.getMessage());
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new PromoBatchException(ErrorCode.CODE_GENERATION_FAILED,
          String.format("Code generation interrupted for batch: %s", batchId));
    } catch (Exception ex) {
      throw new PromoBatchException(ErrorCode.CODE_GENERATION_FAILED,
          String.format("Code generation failed for batch: %s", batchId));
    }
  }

  public void generateCodesUsingStaging(
      UUID batchId, Integer count, String prefix, Integer codeLen) throws Exception {

    log.info(
        "Starting promo code generation - Batch: {}, Count: {}, Prefix: {}, CodeLen: {}",
        batchId,
        count,
        prefix,
        codeLen);

    validateInputs(batchId, count);

    String tempTable = "promo_code_staging_" + sanitizeUuidForTableName(batchId);

    Connection conn = null;
    boolean locked = false;
    boolean originalAutoCommit = true;

    try {
      conn = openAndConfigureConnection();

      acquireBatchLock(conn, batchId);
      locked = true;

      setupStagingTable(conn, tempTable);

      long totalInserted =
          generateAndInsertCodes(conn, batchId, count, prefix, codeLen, tempTable);

      verifyInsertionCount(batchId, count, totalInserted);

      conn.commit();

      log.info(
          "Promo code generation completed successfully for batch {} - Total inserted: {}",
          batchId,
          totalInserted);

    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      rollbackQuietly(conn);
      throw ie;

    } catch (Exception ex) {
      rollbackQuietly(conn);
      throw ex;

    } finally {
      cleanup(conn, locked, batchId, tempTable, originalAutoCommit);
    }
  }

  private void verifyInsertionCount(UUID batchId, int expected, long actual) {
    if (actual < expected) {
      String msg =
          String.format(
              "Failed to generate required codes for batch %s. Requested=%d, Inserted=%d",
              batchId, expected, actual);
      log.error(msg);
      throw new IllegalStateException(msg);
    }
  }

  private void cleanup(
      Connection conn,
      boolean locked,
      UUID batchId,
      String tempTable,
      boolean originalAutoCommit) {

    if (locked && conn != null) {
      try {
        stagingManager.releaseAdvisoryLock(conn, batchId);
      } catch (Exception e) {
        log.warn("Failed to release advisory lock for batch {}", batchId, e);
      }
    }

    if (conn != null) {
      try {
        stagingManager.dropStagingTable(conn, tempTable);
      } catch (Exception e) {
        log.warn("Failed to drop temp table {}", tempTable, e);
      }

      try {
        conn.setAutoCommit(originalAutoCommit);
        conn.close();
      } catch (Exception e) {
        log.warn("Failed to close connection", e);
      }
    }
  }

  private Connection openAndConfigureConnection() throws SQLException {
    Connection conn = stagingManager.openConnection();
    if (conn == null) {
      throw new SQLException("Failed to acquire connection from pool");
    }

    conn.setAutoCommit(false);
    stagingManager.configureSession(conn, LOCK_TIMEOUT_MS, STATEMENT_TIMEOUT_MS);

    return conn;
  }

  private void acquireBatchLock(Connection conn, UUID batchId) throws SQLException {
    boolean locked = stagingManager.acquireAdvisoryLock(conn, batchId);
    if (!locked) {
      throw new IllegalStateException(
          "Another generator is running for batch " + batchId);
    }
  }

  private void setupStagingTable(Connection conn, String tempTable) throws SQLException {
    stagingManager.createStagingTable(conn, tempTable);
  }


  private void validateInputs(UUID batchId, Integer count)
      throws IllegalArgumentException {
    if (batchId == null) {
      throw new IllegalArgumentException("batchId cannot be null");
    }
    if (count == null || count <= 0) {
      throw new IllegalArgumentException("count must be greater than 0");
    }
  }

  private String sanitizeUuidForTableName(UUID batchId) throws IllegalArgumentException {
    String shortSuffix =
        batchId.toString().replace("-", "").substring(0, Math.min(8, 32));

    if (!shortSuffix.matches("^[a-f0-9]{1,8}$")) {
      throw new IllegalArgumentException("Invalid table suffix: " + shortSuffix);
    }

    return shortSuffix;
  }

  private long generateAndInsertCodes(
      Connection conn, UUID batchId, Integer count, String prefix, Integer codeLen,
      String tempTable)
      throws Exception {

    PGConnection pg = stagingManager.unwrapPg(conn);
    long remaining = count;
    int attempt = 0;
    long totalInserted = 0L;

    while (remaining > 0 && attempt < MAX_ATTEMPTS) {
      if (Thread.currentThread().isInterrupted()) {
        Thread.currentThread().interrupt();
        throw new InterruptedException(
            "Interrupted while generating promo codes for batch " + batchId);
      }

      attempt++;
      long toGenerate = Math.min(remaining, COPY_BATCH_LIMIT);

      log.info("Attempt {}/{}: Generating {} codes for batch {}", attempt, MAX_ATTEMPTS, toGenerate,
          batchId);

      final long generated = generateAndCopyToStaging(pg, batchId, toGenerate, prefix, codeLen,
          tempTable);

      if (attempt > 1) {
        Thread.sleep(2000);
        log.debug("Staggering insert for batch {} (attempt {})", batchId, attempt);
      }

      long insertedThisAttempt = insertFromStagingToMain(conn, batchId, tempTable);

      totalInserted += insertedThisAttempt;
      remaining = count - totalInserted;

      try (Statement st = conn.createStatement()) {
        st.execute("TRUNCATE " + tempTable);
      }

      conn.commit();

      log.info(
          "Attempt {}: generated={}, inserted={}, totalInserted={}, remaining={}",
          attempt,
          generated,
          insertedThisAttempt,
          totalInserted,
          remaining);

      if (insertedThisAttempt == 0) {
        handleZeroInsertions(attempt);
      }
    }

    return totalInserted;
  }

  private long generateAndCopyToStaging(
      PGConnection pg,
      UUID batchId,
      long toGenerate,
      String prefix,
      Integer codeLen,
      String tempTable)
      throws Exception {

    String copySql =
        "COPY " + tempTable + "(code, promo_batch_id, status) FROM STDIN WITH (FORMAT csv)";

    ByteArrayOutputStream buffer = new ByteArrayOutputStream(CHUNK_BYTES);
    org.postgresql.copy.CopyIn ci = null;
    long generated = 0L;

    boolean useInMemorySet = toGenerate <= UNIQUE_MEMORY_THRESHOLD;
    Set<String> seen = useInMemorySet
        ? new HashSet<>((int) Math.min(toGenerate, 10_000))
        : null;

    try {
      ci = pg.getCopyAPI().copyIn(copySql);

      while (generated < toGenerate) {
        checkInterrupted();

        String code = generateValidatedCode(batchId, prefix, codeLen);

        if (useInMemorySet && !seen.add(code)) {
          continue;
        }

        byte[] csvLine = buildCsvLine(code, batchId);
        flushIfRequired(ci, buffer, csvLine);

        generated++;
      }

      flushRemaining(ci, buffer);

      ci.endCopy();
      ci = null;

      return generated;

    } catch (Exception e) {
      cancelCopyQuietly(ci);
      throw e;
    }
  }

  private String generateValidatedCode(UUID batchId, String prefix, int codeLen) {

    String batchIdString = batchId.toString()
            .replace("-", "")
            .toUpperCase();
    String batchPrefix = batchIdString.substring(0, BATCH_PREFIX_LENGTH);
    Objects.requireNonNull(prefix, "prefix");
    String promoPrefix = prefix.toUpperCase();
    String codePrefix = promoPrefix + batchPrefix;
    final int minimumCodeLength = codePrefix.length() + MIN_UNIQUE_CODE_LENGTH;

    if (codeLen < minimumCodeLength) {
      throw new IllegalArgumentException(
              "Code length must be at least "
                      + minimumCodeLength
                      + " characters to accommodate the prefix and unique code.");
    }

    int uniqueCodeLength = codeLen - codePrefix.length();
    String code = CodeGenerator.generateCode(codePrefix, uniqueCodeLength);

    validateCodeFormat(code, codePrefix, codeLen);

    return code;
  }

  private byte[] buildCsvLine(String code, UUID batchId) {
    String line =
        String.join(
            ",",
            escapeForCsv(code),
            escapeForCsv(batchId.toString()),
            PromoCodeStatus.ISSUED.name())
            + "\n";

    return line.getBytes(StandardCharsets.UTF_8);
  }

  private void flushIfRequired(
      org.postgresql.copy.CopyIn ci,
      ByteArrayOutputStream buffer,
      byte[] data) throws Exception {

    if (buffer.size() + data.length > CHUNK_BYTES) {
      ci.writeToCopy(buffer.toByteArray(), 0, buffer.size());
      buffer.reset();
    }

    buffer.write(data);
  }

  private void checkInterrupted() throws InterruptedException {
    if (Thread.currentThread().isInterrupted()) {
      Thread.currentThread().interrupt();
      throw new InterruptedException("Interrupted during code generation");
    }
  }

  private void flushRemaining(
      org.postgresql.copy.CopyIn ci,
      ByteArrayOutputStream buffer) throws Exception {

    if (buffer.size() > 0) {
      ci.writeToCopy(buffer.toByteArray(), 0, buffer.size());
      buffer.reset();
    }
  }

  private void cancelCopyQuietly(org.postgresql.copy.CopyIn ci) {
    if (ci != null) {
      try {
        ci.cancelCopy();
      } catch (Exception e) {
        log.warn("Failed to cancel COPY", e);
      }
    }
  }

  private long insertFromStagingToMain(Connection conn, UUID batchId, String tempTable)
      throws Exception {

    String insertSql =
        "INSERT INTO promotions.promo_code(code, batch_id, status) "
            + "SELECT code, promo_batch_id, status FROM "
            + tempTable
            + " ORDER BY code "
            + " ON CONFLICT (code) DO NOTHING RETURNING 1";

    int attempt = 0;

    while (attempt <= INSERT_DEADLOCK_RETRIES) {
      try {
        return executeInsert(conn, insertSql);

      } catch (SQLException ex) {
        attempt++;

        if (!shouldRetry(ex, attempt)) {
          log.error(
              "Insert failed permanently for batch {} after {} attempts",
              batchId,
              attempt,
              ex);
          throw ex;
        }

        handleRetry(conn, batchId, ex, attempt);
      }
    }

    return 0;
  }

  private long executeInsert(Connection conn, String insertSql) throws SQLException {
    int inserted = 0;

    try (PreparedStatement ps = conn.prepareStatement(insertSql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        inserted++;
      }
    }

    return inserted;
  }

  private boolean shouldRetry(SQLException ex, int attempt) {
    String sqlState = ex.getSQLState();

    if ("57014".equals(sqlState)) {
      return attempt < 3;
    }

    if ("40P01".equals(sqlState)) {
      return attempt <= INSERT_DEADLOCK_RETRIES;
    }

    return false;
  }

  private void handleRetry(
      Connection conn,
      UUID batchId,
      SQLException ex,
      int attempt) throws InterruptedException {

    log.warn(
        "Retrying insert for batch {} (attempt {}), sqlState={}, message={}",
        batchId,
        attempt,
        ex.getSQLState(),
        ex.getMessage());

    rollbackQuietly(conn);

    long sleepMs = calculateBackoff(ex, attempt);
    Thread.sleep(sleepMs);
  }

  private long calculateBackoff(SQLException ex, int attempt) {
    String sqlState = ex.getSQLState();

    if ("57014".equals(sqlState)) {
      return 5000L * attempt;
    }

    long base = BASE_RETRY_SLEEP_MS * (1L << Math.min(attempt, 4));
    long jitter = ThreadLocalRandom.current().nextLong(base / 2);

    return base + jitter;
  }

  private void rollbackQuietly(Connection conn) {
    try {
      conn.rollback();
    } catch (Exception e) {
      log.warn("Rollback failed during retry", e);
    }
  }

  private void handleZeroInsertions(int attempt) throws InterruptedException {
    log.warn(
        "Zero insertions on attempt {} — high collision rate or all codes already exist",
        attempt);

    if (attempt > 3) {
      log.warn("Multiple zero-insertion attempts — consider adjusting code generation strategy");
    }

    long sleep = Math.min(1000L * attempt, 10_000L);
    log.debug("Backoff sleep: {}ms", sleep);
    Thread.sleep(sleep);
  }

  private void validateCodeFormat(String code, String prefix, Integer codeLen)
      throws IllegalStateException {
    code = code.replace("-", "");
    if (code.length() != codeLen) {
      throw new IllegalStateException(
          "Generated code has invalid length: " + code + " (expected: " + codeLen + ")");
    }

    if (prefix != null && !code.startsWith(prefix.toUpperCase())) {
      throw new IllegalStateException(
          "Generated code missing prefix: " + code + " (expected: " + prefix + ")");
    }

    if (!code.matches("^[A-Z0-9]{" + codeLen + "}$")) {
      throw new IllegalStateException(
          "Generated code has invalid characters: " + code);
    }
  }

  private String escapeForCsv(String v) {
    if (v == null) {
      return "";
    }

    if (v.indexOf(',') >= 0 || v.indexOf('"') >= 0 || v.indexOf('\n') >= 0) {
      String q = v.replace("\"", "\"\"");
      return "\"" + q + "\"";
    }

    return v;
  }
}

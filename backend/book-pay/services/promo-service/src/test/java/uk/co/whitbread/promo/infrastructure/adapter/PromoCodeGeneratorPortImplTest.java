package uk.co.whitbread.promo.infrastructure.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.co.whitbread.promo.infrastructure.exception.PromoBatchException;

@ExtendWith(MockitoExtension.class)
class PromoCodeGeneratorPortImplTest {

    @Mock
    private PromoCodeStagingManager stagingManager;

    @InjectMocks
    private PromoCodeGeneratorPortImpl generator;

    @Test
    void generateCodes_invalidCount_throwsException() {
        UUID batchId = UUID.randomUUID();

        PromoBatchException ex = assertThrows(
                PromoBatchException.class,
                () -> generator.generateCodes(batchId, 0, "AB", 8)
        );

        assertTrue(ex.getMessage().contains("Code generation failed"));
    }

    @Test
    void generateCodes_invalidCodeLength_throwsException() {
        UUID batchId = UUID.randomUUID();

        assertThrows(
                PromoBatchException.class,
                () -> generator.generateCodes(batchId, 5, "AB", 20)
        );
    }

    @Test
    void generateCodes_failsGracefully_whenAcquireLockFails() throws Exception {
        Connection conn = mock(Connection.class);

        when(stagingManager.openConnection()).thenReturn(conn);
        when(stagingManager.acquireAdvisoryLock(any(), any()))
                .thenThrow(new SQLException("lock failed"));

        UUID batchId = UUID.randomUUID();

        PromoBatchException ex =
                assertThrows(PromoBatchException.class,
                        () -> generator.generateCodes(batchId, 1, "AB", 8));

        assertTrue(
                ex.getMessage().contains("Database error")
                        || ex.getMessage().contains("generation")
        );
    }


    @Test
    void rollbackQuietly_doesNotThrow_whenRollbackFails() throws Exception {
        Method rollbackQuietly =
                PromoCodeGeneratorPortImpl.class
                        .getDeclaredMethod("rollbackQuietly", Connection.class);

        rollbackQuietly.setAccessible(true);

        Connection conn = mock(Connection.class);
        doThrow(new SQLException("rollback failed")).when(conn).rollback();

        assertDoesNotThrow(() -> rollbackQuietly.invoke(generator, conn));
    }

    @Test
    void rollbackQuietly_handlesNullConnection() throws Exception {
        Method rollbackQuietly =
                PromoCodeGeneratorPortImpl.class
                        .getDeclaredMethod("rollbackQuietly", Connection.class);

        rollbackQuietly.setAccessible(true);

        assertDoesNotThrow(() -> rollbackQuietly.invoke(generator, (Connection) null));
    }

    @Test
    void sanitizeUuidForTableName_validUuid_returnsShortSuffix() throws Exception {
        Method m = PromoCodeGeneratorPortImpl.class
                .getDeclaredMethod("sanitizeUuidForTableName", UUID.class);
        m.setAccessible(true);

        String suffix = (String) m.invoke(generator, UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        assertNotNull(suffix);
        assertTrue(suffix.length() <= 8);
    }

    @Test
    void verifyInsertionCount_lessThanExpected_throwsIllegalState() throws Exception {
        Method m = PromoCodeGeneratorPortImpl.class
                .getDeclaredMethod("verifyInsertionCount", UUID.class, int.class, long.class);
        m.setAccessible(true);

        assertThrows(InvocationTargetException.class,
                () -> m.invoke(generator, UUID.randomUUID(), 10, 5));
    }

    @Test
    void shouldRetry_deadlockSqlState_returnsTrue() throws Exception {
        Method m = PromoCodeGeneratorPortImpl.class
                .getDeclaredMethod("shouldRetry", SQLException.class, int.class);
        m.setAccessible(true);

        SQLException ex = new SQLException("deadlock", "40P01");

        boolean retry = (boolean) m.invoke(generator, ex, 1);
        assertTrue(retry);
    }

    @Test
    void shouldRetry_unknownSqlState_returnsFalse() throws Exception {
        Method m = PromoCodeGeneratorPortImpl.class
                .getDeclaredMethod("shouldRetry", SQLException.class, int.class);
        m.setAccessible(true);

        SQLException ex = new SQLException("error", "99999");

        boolean retry = (boolean) m.invoke(generator, ex, 1);
        assertFalse(retry);
    }

    @Test
    void calculateBackoff_timeoutSqlState_returnsLinearBackoff() throws Exception {
        Method m = PromoCodeGeneratorPortImpl.class
                .getDeclaredMethod("calculateBackoff", SQLException.class, int.class);
        m.setAccessible(true);

        SQLException ex = new SQLException("timeout", "57014");

        long backoff = (long) m.invoke(generator, ex, 2);
        assertTrue(backoff >= 10000);
    }

    @Test
    void validateCodeFormat_invalidCharacters_throws() throws Exception {
        Method m = PromoCodeGeneratorPortImpl.class
                .getDeclaredMethod("validateCodeFormat", String.class, String.class, Integer.class);
        m.setAccessible(true);

        assertThrows(InvocationTargetException.class,
                () -> m.invoke(generator, "AB@#1234", "AB", 8));
    }

    @Test
    void escapeForCsv_withComma_wrapsInQuotes() throws Exception {
        Method m = PromoCodeGeneratorPortImpl.class
                .getDeclaredMethod("escapeForCsv", String.class);
        m.setAccessible(true);

        String escaped = (String) m.invoke(generator, "A,B");
        assertEquals("\"A,B\"", escaped);
    }

    @Test
    void rollbackQuietly_handlesException() throws Exception {
        Method m = PromoCodeGeneratorPortImpl.class
                .getDeclaredMethod("rollbackQuietly", Connection.class);
        m.setAccessible(true);

        Connection conn = mock(Connection.class);
        doThrow(new SQLException("rollback failed")).when(conn).rollback();

        assertDoesNotThrow(() -> m.invoke(generator, conn));
    }

    @Test
    void generateValidatedCode_shouldUseProvidedPrefix_whenPrefixIsValid() throws Exception {

        UUID batchId =
                UUID.fromString("acb2c24d-5865-4cb3-8cba-8a7f4340ef2a");

        Method method = PromoCodeGeneratorPortImpl.class
                .getDeclaredMethod(
                        "generateValidatedCode",
                        UUID.class,
                        String.class,
                        int.class
                );

        method.setAccessible(true);

        String code = (String) method.invoke(
                generator,
                batchId,
                "SALE",
                14
        );

        assertNotNull(code);
        assertTrue(code.startsWith("SALEAC"));
        assertEquals(14, code.length());
    }

    @Test
    void generateValidatedCode_shouldThrowException_whenCodeLengthIsLessThanMinimum()
            throws Exception {

        UUID batchId =
                UUID.fromString("acb2c24d-5865-4cb3-8cba-8a7f4340ef2a");

        Method method = PromoCodeGeneratorPortImpl.class
                .getDeclaredMethod(
                        "generateValidatedCode",
                        UUID.class,
                        String.class,
                        int.class);

        method.setAccessible(true);

        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                () -> method.invoke(generator, batchId, "SALE", 10)
        );

        assertInstanceOf(
                IllegalArgumentException.class,
                exception.getCause());

        assertEquals(
                "Code length must be at least 14 characters to accommodate the prefix and unique code.",
                exception.getCause().getMessage());
    }

}

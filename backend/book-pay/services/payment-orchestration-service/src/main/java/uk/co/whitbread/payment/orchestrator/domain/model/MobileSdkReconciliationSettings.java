package uk.co.whitbread.payment.orchestrator.domain.model;

/**
 * Workflow-safe settings for Mobile SDK transaction reconciliation.
 *
 * @param enabled whether reconciliation polling is enabled
 * @param initialDelayMillis delay before the first status poll, in milliseconds
 * @param pollIntervalMillis interval between status polls, in milliseconds
 * @param maxDurationMillis maximum reconciliation duration, in milliseconds
 */
public record MobileSdkReconciliationSettings(
    boolean enabled,
    long initialDelayMillis,
    long pollIntervalMillis,
    long maxDurationMillis
) {}

import type { APIRequestContext } from '@playwright/test';

/** Shared guards for untyped API payload boundaries. */
export function asObject(value: unknown): Record<string, unknown> {
  return value && typeof value === 'object' && !Array.isArray(value)
    ? value as Record<string, unknown>
    : {};
}

/** Whether a value is a Playwright API request context. */
export function isApiRequestContext(value: unknown): value is APIRequestContext {
  return !!value && typeof value === 'object' && typeof (value as { post?: unknown }).post === 'function';
}

/** Runtime options supplied by the Playwright fixture. */
export function getBrowserOptions(): Record<string, unknown> {
  return (global.browser?.options as Record<string, unknown> | undefined) ?? {};
}
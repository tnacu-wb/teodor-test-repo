import { Environment } from './environments';

const ALLOWED_ENVIRONMENTS: readonly Environment[] = ['UAT', 'SIT', 'PERF'];

/**
 * Validates that a value is one of the allowed OHIP environments.
 * Guards against untrusted input being used as a dynamic object key
 * (e.g. prototype pollution via the token cache).
 */
export function validateEnvironment(env: unknown): Environment {
  if (typeof env === 'string' && (ALLOWED_ENVIRONMENTS as readonly string[]).includes(env)) {
    return env as Environment;
  }
  throw new Error(`Invalid environment: "${String(env)}". Must be one of: ${ALLOWED_ENVIRONMENTS.join(', ')}.`);
}

/**
 * Validates and sanitizes a hotel ID to prevent SSRF attacks.
 */
export function validateHotelId(hotelId: string): string {
  const sanitized = hotelId.trim();
  if (!sanitized) {
    throw new Error('Hotel ID is required.');
  }
  if (!/^[A-Za-z0-9]+$/.test(sanitized)) {
    throw new Error(`Invalid Hotel ID: "${sanitized}". Must contain only letters and numbers.`);
  }
  if (sanitized.length > 20) {
    throw new Error(`Invalid Hotel ID: "${sanitized}". Must be 20 characters or fewer.`);
  }
  return sanitized;
}

/**
 * Validates and sanitizes a rate plan code to prevent SSRF attacks.
 */
export function validateRatePlanCode(code: string): string {
  const sanitized = code.trim();
  if (!sanitized) {
    throw new Error('Rate Plan Code is required.');
  }
  if (!/^[A-Za-z0-9_-]+$/.test(sanitized)) {
    throw new Error(
      `Invalid Rate Plan Code: "${sanitized}". Must contain only letters, numbers, hyphens, and underscores.`
    );
  }
  if (sanitized.length > 50) {
    throw new Error(`Invalid Rate Plan Code: "${sanitized}". Must be 50 characters or fewer.`);
  }
  return sanitized;
}

/**
 * Validates and sanitizes a package code to prevent SSRF attacks.
 */
export function validatePackageCode(code: string): string {
  const sanitized = code.trim();
  if (!sanitized) {
    throw new Error('Package Code is required.');
  }
  if (!/^[A-Za-z0-9_-]+$/.test(sanitized)) {
    throw new Error(
      `Invalid Package Code: "${sanitized}". Must contain only letters, numbers, hyphens, and underscores.`
    );
  }
  if (sanitized.length > 50) {
    throw new Error(`Invalid Package Code: "${sanitized}". Must be 50 characters or fewer.`);
  }
  return sanitized;
}

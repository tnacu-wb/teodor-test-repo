import { Environment } from './ohip/environments';

/**
 * Required environment variable suffixes for each OHIP environment.
 * At minimum, base URL, client ID, client secret, and app key must be set.
 */
const REQUIRED_SUFFIXES = ['BASE_URL', 'CLIENT_ID', 'CLIENT_SECRET', 'APP_KEY'] as const;

interface EnvCheckResult {
  valid: boolean;
  configured: Environment[];
  errors: string[];
}

/**
 * Reports whether at least one OHIP environment is fully configured, along with
 * any partial-configuration problems. This only inspects config and returns a
 * result — it does not throw.
 *
 * Called at startup from `instrumentation.ts`, which logs the findings (warnings
 * for partial config, an error when nothing is configured) but deliberately lets
 * the app continue: the pod may start before the ExternalSecret has synced, and
 * missing config surfaces per-request via `getEnvConfig`. It is a startup
 * diagnostic, not a fail-fast gate.
 */
export function validateEnvironmentConfig(): EnvCheckResult {
  const environments: Environment[] = ['UAT', 'SIT', 'PERF'];
  const configured: Environment[] = [];
  const errors: string[] = [];

  for (const env of environments) {
    const missing = REQUIRED_SUFFIXES.filter((suffix) => !process.env[`${env}_OHIP_${suffix}`]);

    if (missing.length === 0) {
      configured.push(env);
    } else if (missing.length < REQUIRED_SUFFIXES.length) {
      // Partially configured — likely a mistake
      errors.push(
        `Environment "${env}" is partially configured. Missing: ${missing.map((s) => `${env}_OHIP_${s}`).join(', ')}`
      );
    }
    // Fully missing environments are fine — not every env needs to be configured locally
  }

  if (configured.length === 0) {
    errors.unshift(
      'No OHIP environments are configured. Set at least one complete set of environment variables (e.g. UAT_OHIP_BASE_URL, UAT_OHIP_CLIENT_ID, UAT_OHIP_CLIENT_SECRET, UAT_OHIP_APP_KEY) in .env.local.'
    );
  }

  return { valid: configured.length > 0, configured, errors };
}

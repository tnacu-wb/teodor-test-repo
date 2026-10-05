export type Environment = 'UAT' | 'SIT' | 'PERF';

export interface EnvConfig {
  baseUrl: string;
  clientId: string;
  clientSecret: string;
  appKey: string;
  scope: string;
  enterpriseId: string;
}

export function getEnvConfig(env: Environment): EnvConfig {
  const prefix = env.toUpperCase();
  const baseUrl = process.env[`${prefix}_OHIP_BASE_URL`];
  const clientId = process.env[`${prefix}_OHIP_CLIENT_ID`];
  const clientSecret = process.env[`${prefix}_OHIP_CLIENT_SECRET`];
  const appKey = process.env[`${prefix}_OHIP_APP_KEY`];
  const scope = process.env[`${prefix}_OHIP_SCOPE`];
  const enterpriseId = process.env[`${prefix}_OHIP_ENTERPRISE_ID`];

  if (!baseUrl || !clientId || !clientSecret || !appKey) {
    throw new Error(
      `Environment "${env}" is not configured. Check your .env.local file for ${prefix}_OHIP_* variables.`
    );
  }

  return { baseUrl, clientId, clientSecret, appKey, scope: scope || '', enterpriseId: enterpriseId || '' };
}

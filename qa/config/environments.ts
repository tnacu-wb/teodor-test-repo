/**
 * Environment URL Configuration
 *
 * Maps each Environment (uat, dit) × App (pi, pib, ccui) combination to its
 * base URL, secure URL, GraphQL API endpoint, and AEM content-service base URL.
 *
 * These are non-sensitive values — they belong in code, not .env.
 * The active config is selected via ENV and APP environment variables.
 */

export type Environment = 'dit' | 'uat';
export type App = 'pi' | 'pib' | 'ccui';

export interface EnvironmentConfig {
  baseUrl: string;
  secureUrl: string;
  secureUrl2?: string;
  apiBaseUrl: string;
  aemBaseUrl: string;
  ohipApiBaseUrl: string;
  ohipClientId: string;
  ohipClientSecret: string;
  ohipXApiKey: string;
}

export interface GetEnvironmentConfigOptions {
  env: Environment;
  app: App;
  locale?: string;
  ohipClientId?: string;
  ohipClientSecret?: string;
  ohipXApiKey?: string;
}

const DEFAULT_LOCALE = 'gb-en';

function parseLocale(localeString = DEFAULT_LOCALE): { country: string; language: string } {
  const [country = 'gb', language = 'en'] = localeString.toLowerCase().split('-');
  return { country, language };
}

function getLocalizedRoute(app: App, localeString?: string): string {
  const { country, language } = parseLocale(localeString);
  return app === 'pib' ? `/${language}-${country}` : `/${country}/${language}`;
}

function localizeUrl(url: string, app: App, localeString?: string): string {
  return url.replace(getLocalizedRoute(app, DEFAULT_LOCALE), getLocalizedRoute(app, localeString));
}

const OHIP_API_BASE_URL = 'https://whitbce1ua.whb.hospitality-api.eu-frankfurt-1.ocs.oc-test.com';

const environments: Record<Environment, Record<App, Omit<EnvironmentConfig, 'ohipClientId' | 'ohipClientSecret' | 'ohipXApiKey'>>> = {
  dit: {
    pi: {
      baseUrl: 'https://www.dit.premierinn.digital/gb/en',
      secureUrl: 'https://secure2.dit.premierinn.digital:443/gb/en',
      apiBaseUrl: 'https://api.dit.premierinn.digital/graphql',
      aemBaseUrl: 'https://www.dit.premierinn.digital',
      ohipApiBaseUrl: OHIP_API_BASE_URL,
    },
    pib: {
      baseUrl: 'https://business.dit.premierinn.digital/en-gb/account/login',
      secureUrl: 'https://business.dit.premierinn.digital',
      apiBaseUrl: 'https://api.dit.premierinn.digital/graphql',
      aemBaseUrl: 'https://www.dit.premierinn.digital',
      ohipApiBaseUrl: OHIP_API_BASE_URL,
    },
    ccui: {
      baseUrl: 'https://ccui.dit.premierinn.digital/gb/en',
      secureUrl: 'https://ccui.dit.premierinn.digital',
      secureUrl2: 'https://www.dit.premierinn.digital/gb/en/home.html',
      apiBaseUrl: 'https://api.dit.premierinn.digital/graphql',
      aemBaseUrl: 'https://www.dit.premierinn.digital',
      ohipApiBaseUrl: OHIP_API_BASE_URL,
    }
  },
  uat: {
    pi: {
      baseUrl: 'https://www.uat.premierinn.digital/gb/en',
      secureUrl: 'https://secure2.uat.premierinn.digital:443/gb/en',
      apiBaseUrl: 'https://api.uat.premierinn.digital/graphql',
      aemBaseUrl: 'https://www.uat.premierinn.digital',
      ohipApiBaseUrl: OHIP_API_BASE_URL,
    },
    pib: {
      baseUrl: 'https://business.uat.premierinn.digital/en-gb/account/login',
      secureUrl: 'https://business.uat.premierinn.digital',
      apiBaseUrl: 'https://api.uat.premierinn.digital/graphql',
      aemBaseUrl: 'https://www.uat.premierinn.digital',
      ohipApiBaseUrl: OHIP_API_BASE_URL,
    },
    ccui: {
      baseUrl: 'https://ccui.uat.premierinn.digital/gb/en',
      secureUrl: 'https://ccui.uat.premierinn.digital',
      secureUrl2: 'https://www.uat.premierinn.digital/gb/en/home.html',
      apiBaseUrl: 'https://api.uat.premierinn.digital/graphql',
      aemBaseUrl: 'https://www.uat.premierinn.digital',
      ohipApiBaseUrl: OHIP_API_BASE_URL,
    }
  },
};

/**
 * Get the environment config for the current ENV + APP combination.
 *
 * Controlled via options parameter or environment variables (in order of precedence):
 *   options.env / ENV=uat|dit (default: uat)
 *   options.app / APP=pi|pib|ccui (default: pi)
 *
 * OHIP configuration is always loaded from UAT environment variables (OHIP API is UAT for both DIT and UAT test environments):
 *   UAT_OHIP_API_BASE_URL, UAT_OHIP_CLIENT_ID, UAT_OHIP_CLIENT_SECRET, UAT_OHIP_X_API_KEY
 *   with fallback to generic OHIP_* variables
 */
export function getEnvironmentConfig(options: Partial<GetEnvironmentConfigOptions> = {}): EnvironmentConfig {
  const env = options.env ?? 'uat';
  const app = options.app ?? 'pi';

  if (!environments[env]) {
    throw new Error(`Unknown environment: "${env}". Valid options: ${Object.keys(environments).join(', ')}`);
  }

  if (!environments[env][app]) {
    throw new Error(`Unknown app: "${app}". Valid options: ${Object.keys(environments[env]).join(', ')}`);
  }

  // Load OHIP credentials from UAT environment variables (OHIP API endpoint is non-sensitive and hardcoded)
  const environment = environments[env][app];
  const ohipApiBaseUrl = environment.ohipApiBaseUrl;
  const ohipClientId = options.ohipClientId ?? '';
  const ohipClientSecret = options.ohipClientSecret ?? '';
  const ohipXApiKey = options.ohipXApiKey ?? '';

  return {
    ...environment,
    baseUrl: localizeUrl(environment.baseUrl, app, options.locale),
    secureUrl: localizeUrl(environment.secureUrl, app, options.locale),
    ohipApiBaseUrl,
    ohipClientId,
    ohipClientSecret,
    ohipXApiKey,
  };
}

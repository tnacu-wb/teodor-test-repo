import { LOCALES } from '@whitbread-eos/api';

type CanonicalLocale = LOCALES.EN | LOCALES.DE;

type OneTrustLocaleConfig = {
  dataLanguage: 'en' | 'de';
  getDomainScript: () => string | undefined;
};

const ONE_TRUST_CONFIG_BY_LOCALE: Record<CanonicalLocale, OneTrustLocaleConfig> = {
  [LOCALES.EN]: {
    dataLanguage: 'en',
    getDomainScript: () => process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB,
  },
  [LOCALES.DE]: {
    dataLanguage: 'de',
    getDomainScript: () => process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_DE,
  },
};

const LOCALE_MATCHERS: Record<CanonicalLocale, string[]> = {
  [LOCALES.EN]: [LOCALES.EN, '/gb/en', 'en', 'gb'],
  [LOCALES.DE]: [LOCALES.DE, '/de/de', 'de'],
};

export const getCanonicalLocale = (urlOrPath: string | null | undefined): CanonicalLocale => {
  const value = urlOrPath?.toLowerCase() ?? '';
  const localeMatch = Object.entries(LOCALE_MATCHERS).find(([, matchers]) =>
    matchers.some((matcher) => value === matcher || value.includes(matcher))
  )?.[0] as CanonicalLocale | undefined;

  return localeMatch ?? LOCALES.EN;
};

export const getOneTrustConfigByLocale = (locale: CanonicalLocale) => {
  const localeConfig = ONE_TRUST_CONFIG_BY_LOCALE[locale];

  return {
    dataLanguage: localeConfig.dataLanguage,
    domainScript: localeConfig.getDomainScript(),
  };
};

// Single source of truth: OneTrust only takes over consent when the flag AND its config are present,
// otherwise the legacy cookie consent UI must stay in charge.
export const isOneTrustCookieConsentActive = (
  isFeatureEnabled: boolean | undefined,
  localeOrPath?: string | null
): boolean => {
  const { domainScript } = getOneTrustConfigByLocale(getCanonicalLocale(localeOrPath));

  return !!(isFeatureEnabled && process.env.NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL && domainScript);
};

import { LOCALES } from '@whitbread-eos/api';

import { getCanonicalLocale, getOneTrustConfigByLocale } from './oneTrustLocaleConfig';

describe('oneTrustLocaleConfig', () => {
  describe('getCanonicalLocale', () => {
    it('returns en-gb for new URL locale path', () => {
      expect(getCanonicalLocale('/en-gb/homepage')).toEqual(LOCALES.EN);
    });

    it('returns de-de for new URL locale path', () => {
      expect(getCanonicalLocale('/de-de/homepage')).toEqual(LOCALES.DE);
    });

    it('returns en-gb for legacy gb/en URL path', () => {
      expect(getCanonicalLocale('/gb/en/home.html')).toEqual(LOCALES.EN);
    });

    it('returns de-de for legacy de/de URL path', () => {
      expect(getCanonicalLocale('/de/de/home.html')).toEqual(LOCALES.DE);
    });

    it('supports absolute URLs', () => {
      expect(getCanonicalLocale('https://example.com/de/de/manage')).toEqual(LOCALES.DE);
    });
  });

  describe('getOneTrustConfigByLocale', () => {
    const originalEnv = process.env;

    beforeEach(() => {
      process.env = {
        ...originalEnv,
        NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB: 'gb-domain-script',
        NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_DE: 'de-domain-script',
      };
    });

    afterAll(() => {
      process.env = originalEnv;
    });

    it('returns GB OneTrust config for en-gb locale', () => {
      expect(getOneTrustConfigByLocale(LOCALES.EN)).toEqual({
        dataLanguage: 'en',
        domainScript: 'gb-domain-script',
      });
    });

    it('returns DE OneTrust config for de-de locale', () => {
      expect(getOneTrustConfigByLocale(LOCALES.DE)).toEqual({
        dataLanguage: 'de',
        domainScript: 'de-domain-script',
      });
    });
  });
});

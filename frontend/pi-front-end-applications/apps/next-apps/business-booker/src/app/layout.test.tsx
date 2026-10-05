import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { LOCALES, PI_FAVICON, FT_ONE_TRUST_COOKIE_CONSENT } from '@whitbread-eos/api';
import * as utilsServer from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';

import { generateMetadata, default as RootLayout } from './layout';

jest.mock('next/script', () => {
  const MockScript = (props: any) => <script {...props} />;
  MockScript.displayName = 'MockScript';
  return MockScript;
});

jest.mock('../components/innBusiness/FontWrapper/font-wrapper', () => () => null);
jest.mock('../components/innBusiness/DynatraceConsentSync/DynatraceConsentSync', () => {
  const MockDynatraceConsentSync = () => <div data-testid="dynatrace-consent-sync" />;
  MockDynatraceConsentSync.displayName = 'MockDynatraceConsentSync';
  return MockDynatraceConsentSync;
});

jest.mock('~components/innBusiness/UserPilot', () => ({
  UserPilot: () => null,
}));

// Mock Next.js headers
jest.mock('next/headers', () => ({
  headers: jest.fn(),
  cookies: jest.fn(),
}));

// Mock utilities
jest.mock('@whitbread-eos/utils/server', () => ({
  formatIBAssetsUrl: jest.fn((path: string) => `mocked-${path}`),
  getCountryLanguageByLocale: jest.fn((locale: string) => ({
    language: locale === LOCALES.DE ? 'de' : 'en',
    country: locale === LOCALES.DE ? 'de' : 'gb',
  })),
  getCanonicalLocale: jest.fn((urlOrPath: string) =>
    urlOrPath?.includes('/de-de/') || urlOrPath?.includes('/de/de/') ? LOCALES.DE : LOCALES.EN
  ),
  getOneTrustConfigByLocale: jest.fn((locale: LOCALES) => ({
    locale,
    dataLanguage: locale === LOCALES.DE ? 'de' : 'en',
    domainScript:
      locale === LOCALES.DE
        ? process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_DE
        : process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB,
  })),
  isOneTrustCookieConsentActive: jest.fn(
    (isFeatureEnabled?: boolean, urlOrPath?: string) =>
      !!(
        isFeatureEnabled &&
        process.env.NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL &&
        (urlOrPath?.includes('/de-de/') || urlOrPath?.includes('/de/de/')
          ? process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_DE
          : process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB)
      )
  ),
  getTranslations: jest.fn(),
  getEmployeeDataforUserPilot: jest.fn(),
  getServerUnleashToggles: jest.fn(),
  ID_TOKEN_COOKIE: 'ID_TOKEN',
}));

const mockHeaders = headers as jest.MockedFunction<typeof headers>;
const mockCookies = cookies as jest.MockedFunction<typeof cookies>;
const mockGetServerUnleashToggles = utilsServer.getServerUnleashToggles as jest.MockedFunction<any>;
const mockGetEmployeeData = utilsServer.getEmployeeDataforUserPilot as jest.MockedFunction<any>;
const mockUseTranslationServer = utilsServer.getTranslations as jest.MockedFunction<any>;

describe('layout.tsx', () => {
  describe('generateMetadata', () => {
    beforeEach(() => {
      jest.clearAllMocks();
    });

    it('should return default metadata when WB-Url header is empty', async () => {
      mockHeaders.mockReturnValue({
        get: jest.fn().mockReturnValue(''),
      } as any);

      const result = await generateMetadata();

      expect(result).toEqual({
        icons: { icon: `mocked-${PI_FAVICON}` },
        robots: { index: false, follow: false },
      });
    });

    it('should extract locale from pathname and use English by default', async () => {
      mockHeaders.mockReturnValue({
        get: jest.fn().mockReturnValue('http://localhost:3000/en-gb/homepage'),
      } as any);

      mockUseTranslationServer.mockResolvedValue({
        t: jest.fn((key: string) => {
          if (key === 'layout.pageTitle.homepage') return 'Homepage';
          if (key === 'layout.pageTitle') return 'InnBusiness';
          return key;
        }),
        translations: {},
      });

      const result = await generateMetadata();

      expect(mockUseTranslationServer).toHaveBeenCalledWith('en', ['layout']);
      expect(result.title).toEqual({ default: 'Homepage', template: '%s' });
    });

    it('should handle nested paths and find matching translation', async () => {
      mockHeaders.mockReturnValue({
        get: jest.fn().mockReturnValue('http://localhost:3000/en-gb/manage/cards'),
      } as any);

      mockUseTranslationServer.mockResolvedValue({
        t: jest.fn((key: string) => {
          if (key === 'layout.pageTitle.manage.cards') return 'Card Management';
          if (key === 'layout.pageTitle') return 'InnBusiness';
          return key; // Return key itself if not found
        }),
        translations: {},
      });

      const result = await generateMetadata();

      expect(result.title).toEqual({ default: 'Card Management', template: '%s' });
    });

    it('should try progressively shorter paths until translation found', async () => {
      mockHeaders.mockReturnValue({
        get: jest.fn().mockReturnValue('http://localhost:3000/en-gb/manage/cards/123/details'),
      } as any);

      const mockT = jest.fn((key: string) => {
        // Only 'manage.cards' exists in translations
        if (key === 'layout.pageTitle.manage.cards') return 'Card Management';
        if (key === 'layout.pageTitle') return 'InnBusiness';
        return key; // Return key itself if not found
      });

      mockUseTranslationServer.mockResolvedValue({
        t: mockT,
        translations: {},
      });

      const result = await generateMetadata();

      // Should have tried longer paths first, then fallen back to 'manage.cards'
      expect(mockT).toHaveBeenCalledWith('layout.pageTitle.manage.cards.123');
      expect(mockT).toHaveBeenCalledWith('layout.pageTitle.manage.cards.123');
      expect(mockT).toHaveBeenCalledWith('layout.pageTitle.manage.cards');
      expect(result.title).toEqual({ default: 'Card Management', template: '%s' });
    });

    it('should fallback to base title when no specific page title found', async () => {
      mockHeaders.mockReturnValue({
        get: jest.fn().mockReturnValue('http://localhost:3000/en-gb/unknown-page'),
      } as any);

      mockUseTranslationServer.mockResolvedValue({
        t: jest.fn((key: string) => {
          if (key === 'layout.pageTitle') return 'InnBusiness Default';
          return key; // Return key itself if not found
        }),
        translations: {},
      });

      const result = await generateMetadata();

      expect(result.title).toEqual({ default: 'InnBusiness Default', template: '%s' });
    });

    it('should handle invalid URL and return default metadata', async () => {
      mockHeaders.mockReturnValue({
        get: jest.fn().mockReturnValue('not-a-valid-url'),
      } as any);

      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();

      const result = await generateMetadata();

      expect(result).toEqual({
        icons: { icon: `mocked-${PI_FAVICON}` },
        robots: { index: false, follow: false },
      });

      consoleErrorSpy.mockRestore();
    });

    it('should include icons and robots in metadata', async () => {
      mockHeaders.mockReturnValue({
        get: jest.fn().mockReturnValue('http://localhost:3000/en-gb/homepage'),
      } as any);

      mockUseTranslationServer.mockResolvedValue({
        t: jest.fn(() => 'Test Title'),
        translations: {},
      });

      const result = await generateMetadata();

      expect(result).toMatchObject({
        icons: { icon: `mocked-${PI_FAVICON}` },
        robots: { index: false, follow: false },
      });
    });
  });
});

describe('RootLayout — OneTrust scripts', () => {
  const originalEnv = process.env;

  beforeEach(() => {
    jest.clearAllMocks();
    mockHeaders.mockReturnValue({ get: jest.fn().mockReturnValue('') } as any);
    mockCookies.mockResolvedValue({ get: jest.fn().mockReturnValue(undefined) } as any);
    mockGetEmployeeData.mockResolvedValue(null);
    process.env = {
      ...originalEnv,
      NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL:
        'https://cdn-ukwest.onetrust.com/scripttemplates/otSDKStub.js',
      NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB: 'test-domain-gb',
      NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_DE: 'test-domain-de',
    };
  });

  afterAll(() => {
    process.env = originalEnv;
  });

  it('renders OneTrust scripts when flag is enabled and env vars are set', async () => {
    mockGetServerUnleashToggles.mockResolvedValue({ [FT_ONE_TRUST_COOKIE_CONSENT]: true });

    render(await RootLayout({ children: <div /> }));

    const script = screen.getByTestId('one-trust-cookie-consent');
    expect(script).toBeInTheDocument();
    expect(script).toHaveAttribute(
      'src',
      'https://cdn-ukwest.onetrust.com/scripttemplates/otSDKStub.js'
    );
    expect(script).toHaveAttribute('data-domain-script', 'test-domain-gb');
    expect(script).toHaveAttribute('data-language', 'en');
  });

  it('renders DE locale OneTrust config when path locale is de-de', async () => {
    mockHeaders.mockReturnValue({
      get: jest.fn().mockReturnValue('http://localhost:3000/de-de/homepage'),
    } as any);
    mockGetServerUnleashToggles.mockResolvedValue({ [FT_ONE_TRUST_COOKIE_CONSENT]: true });

    render(await RootLayout({ children: <div /> }));

    const script = screen.getByTestId('one-trust-cookie-consent');
    expect(script).toHaveAttribute('data-domain-script', 'test-domain-de');
    expect(script).toHaveAttribute('data-language', 'de');
  });

  it('renders Dynatrace consent sync when OneTrust is enabled', async () => {
    mockGetServerUnleashToggles.mockResolvedValue({ [FT_ONE_TRUST_COOKIE_CONSENT]: true });

    render(await RootLayout({ children: <div /> }));

    expect(screen.getByTestId('dynatrace-consent-sync')).toBeInTheDocument();
  });

  it('renders an empty OptanonWrapper placeholder required by OneTrust', async () => {
    mockGetServerUnleashToggles.mockResolvedValue({ [FT_ONE_TRUST_COOKIE_CONSENT]: true });

    const { container } = render(await RootLayout({ children: <div /> }));

    const inlineScript = container.querySelector('script#optanon-wrapper');
    expect(inlineScript).toBeInTheDocument();
    expect(inlineScript).toHaveTextContent('function OptanonWrapper() {}');
  });

  it('does not render OneTrust scripts when flag is disabled', async () => {
    mockGetServerUnleashToggles.mockResolvedValue({ [FT_ONE_TRUST_COOKIE_CONSENT]: false });

    render(await RootLayout({ children: <div /> }));

    expect(screen.queryByTestId('one-trust-cookie-consent')).not.toBeInTheDocument();
  });

  it('does not render OneTrust scripts when NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL is not set', async () => {
    mockGetServerUnleashToggles.mockResolvedValue({ [FT_ONE_TRUST_COOKIE_CONSENT]: true });
    delete process.env.NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL;

    render(await RootLayout({ children: <div /> }));

    expect(screen.queryByTestId('one-trust-cookie-consent')).not.toBeInTheDocument();
  });

  it('does not render OneTrust scripts when NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB is not set', async () => {
    mockGetServerUnleashToggles.mockResolvedValue({ [FT_ONE_TRUST_COOKIE_CONSENT]: true });
    delete process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB;

    render(await RootLayout({ children: <div /> }));

    expect(screen.queryByTestId('one-trust-cookie-consent')).not.toBeInTheDocument();
  });
});

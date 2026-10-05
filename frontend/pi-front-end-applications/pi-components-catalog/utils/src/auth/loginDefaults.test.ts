import {
  DEFAULT_LOGIN_APPLICATION,
  DEFAULT_LOGIN_EXT_APPLICATION,
  DEFAULT_LOGIN_EXT_PATH,
  getDefaultLoginParams,
  getLanguageFromPath,
  resolveAuthEntryPoint,
} from './loginDefaults';

describe('resolveAuthEntryPoint', () => {
  it('returns guest_details_page for /guest-details path', () => {
    expect(resolveAuthEntryPoint('https://example.com/guest-details')).toBe('guest_details_page');
  });

  it('returns top_nav_srp for /search path', () => {
    expect(resolveAuthEntryPoint('https://example.com/search?q=london')).toBe('top_nav_srp');
  });

  it('returns top_nav_hdp for /hotels/ path', () => {
    expect(resolveAuthEntryPoint('https://example.com/hotels/london-city')).toBe('top_nav_hdp');
  });

  it('returns top_nav_other for an unrecognised path', () => {
    expect(resolveAuthEntryPoint('https://example.com/offers')).toBe('top_nav_other');
  });

  it('falls back gracefully when given a non-absolute URL', () => {
    expect(resolveAuthEntryPoint('/search')).toBe('top_nav_srp');
    expect(resolveAuthEntryPoint('/unknown')).toBe('top_nav_other');
  });
});

describe('getLanguageFromPath', () => {
  it('returns en for a /gb/en/... path', () => {
    expect(getLanguageFromPath('/gb/en/home.html')).toBe('en');
  });

  it('returns de for a /de/de/... path', () => {
    expect(getLanguageFromPath('/de/de/home.html')).toBe('de');
  });

  it('returns null when the path has no recognised locale segments', () => {
    expect(getLanguageFromPath('/offers')).toBeNull();
  });

  it('returns null when given null or undefined', () => {
    expect(getLanguageFromPath(null)).toBeNull();
    expect(getLanguageFromPath(undefined)).toBeNull();
  });
});

describe('getDefaultLoginParams', () => {
  it('always sets returnTo and the static application/ext-* params', () => {
    const params = getDefaultLoginParams({
      currentUrl: 'https://example.com/gb/en/search',
      origin: 'https://example.com',
    });

    expect(params).toEqual({
      returnTo: 'https://example.com/gb/en/search',
      application: DEFAULT_LOGIN_APPLICATION,
      'ext-application': DEFAULT_LOGIN_EXT_APPLICATION,
      'ext-path': DEFAULT_LOGIN_EXT_PATH,
      'ext-authEntryPoint': 'top_nav_srp',
    });
  });

  it('includes ui_locales when a language is provided', () => {
    const params = getDefaultLoginParams({
      currentUrl: 'https://example.com/gb/en/home',
      origin: 'https://example.com',
      language: 'en',
    });

    expect(params.ui_locales).toBe('en');
  });

  it('omits ui_locales when no language is provided', () => {
    const params = getDefaultLoginParams({
      currentUrl: 'https://example.com/offers',
      origin: 'https://example.com',
    });

    expect(params).not.toHaveProperty('ui_locales');
  });

  it('includes connection, ext-connection and ext-origin when a connection is provided', () => {
    const params = getDefaultLoginParams({
      currentUrl: 'https://example.com/gb/en/home',
      origin: 'https://example.com',
      connection: 'pi-uat',
    });

    expect(params.connection).toBe('pi-uat');
    expect(params['ext-connection']).toBe('pi-uat');
    expect(params['ext-origin']).toBe('https://example.com');
  });

  it('omits connection params when no connection is provided', () => {
    const params = getDefaultLoginParams({
      currentUrl: 'https://example.com/gb/en/home',
      origin: 'https://example.com',
    });

    expect(params).not.toHaveProperty('connection');
    expect(params).not.toHaveProperty('ext-connection');
    expect(params).not.toHaveProperty('ext-origin');
  });
});

import { redirect } from 'next/navigation';

import checkValidRedirect from '~utils/checkValidRedirect';

import getPageUrlBeforeRedirectToLogin from './getPageUrlBeforeRedirectToLogin';

jest.mock('@whitbread-eos/utils/server', () => ({
  getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
}));
jest.mock('next/navigation', () => ({
  redirect: jest.fn((url) => url),
}));
jest.mock('~utils/checkValidRedirect', () => jest.fn(() => true));

describe('getPageUrlBeforeRedirectToLogin', () => {
  const locale = 'en-gb';
  beforeEach(() => {
    jest.clearAllMocks();
    (checkValidRedirect as jest.Mock).mockImplementation(() => true);
  });

  it('redirects to login without redirectURL if pathAfterLocale is homepage', () => {
    getPageUrlBeforeRedirectToLogin('/en-gb/homepage', locale, true);
    expect(redirect).toHaveBeenCalledWith('/en-gb/account/login');
  });

  it('redirects to login without redirectURL if pathAfterLocale is account/login', () => {
    getPageUrlBeforeRedirectToLogin('/en-gb/account/login', locale, true);
    expect(redirect).toHaveBeenCalledWith('/en-gb/account/login');
  });

  it('redirects to login without redirectURL if pathAfterLocale is empty', () => {
    getPageUrlBeforeRedirectToLogin('/en-gb/', locale, true);
    expect(redirect).toHaveBeenCalledWith('/en-gb/account/login');
  });

  it('redirects to login with redirectURL if flag is enabled and path is valid', () => {
    getPageUrlBeforeRedirectToLogin('/en-gb/spending', locale, true);
    expect(redirect).toHaveBeenCalledWith('/en-gb/account/login?redirectURL=%2Fen-gb%2Fspending');
  });

  it('does not redirect with redirectURL if flag is disabled', () => {
    getPageUrlBeforeRedirectToLogin('/en-gb/spending', locale, false);
    expect(redirect).toHaveBeenCalledWith('/en-gb/account/login');
    expect(redirect).not.toHaveBeenCalledWith(
      '/en-gb/account/login?redirectURL=%2Fen-gb%2Fspending'
    );
  });

  it('redirects to login with redirectURL for old locale pattern /gb/en/', () => {
    getPageUrlBeforeRedirectToLogin('/gb/en/business-booker', locale, true);
    expect(redirect).toHaveBeenCalledWith(
      '/en-gb/account/login?redirectURL=%2Fgb%2Fen%2Fbusiness-booker'
    );
  });

  it('redirects to login with redirectURL for old locale pattern /de/de/', () => {
    getPageUrlBeforeRedirectToLogin('/de/de/business-booker', locale, true);
    expect(redirect).toHaveBeenCalledWith(
      '/en-gb/account/login?redirectURL=%2Fde%2Fde%2Fbusiness-booker'
    );
  });

  it('redirects to login without redirectURL if checkValidRedirect returns false', () => {
    (checkValidRedirect as jest.Mock).mockImplementation(() => false);
    getPageUrlBeforeRedirectToLogin('/en-gb/invalid-path', locale, true);
    expect(redirect).toHaveBeenCalledWith('/en-gb/account/login');
  });
});

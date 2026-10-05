/**
 * Extracts the path after the locale from a URL or path and builds a login URL with redirectURL param.
 * @param {string} currentPath - The full URL or path (e.g. '/en-gb/spending
 * @param {string} locale - The locale string (e.g. 'en-gb')
 * @returns {string} - The login URL with redirectURL param
 */
import { getPathForLocale } from '@whitbread-eos/utils/server';
import { redirect } from 'next/navigation';

import checkValidRedirect from '~utils/checkValidRedirect';

export default function getPageUrlBeforeRedirectToLogin(
  currentPath: string,
  locale: string,
  isRedirectAfterLoginEnabled: boolean
) {
  // Support both new and old locale patterns
  const localePatterns = [`/${locale}/`, '/gb/en/', '/de/de/']; // or /en-gb/ /de-de/

  // Find which pattern matches the currentPath
  const matchedPattern = localePatterns.find((pattern) => currentPath.includes(pattern));
  const localeIndex = matchedPattern ? currentPath.indexOf(matchedPattern) : -1;

  // e.g. spending, business-pay/apply etc
  let pathWithoutLocale =
    localeIndex !== -1 && matchedPattern
      ? currentPath.slice(localeIndex + matchedPattern.length)
      : '';

  // Decode the path in case it's URL-encoded
  pathWithoutLocale = decodeURIComponent(pathWithoutLocale);

  // Ignore redirect if already on homepage, login, or empty path
  if (
    pathWithoutLocale === 'homepage' ||
    pathWithoutLocale.startsWith('account/login') ||
    pathWithoutLocale === '' ||
    !isRedirectAfterLoginEnabled
  ) {
    return redirect(getPathForLocale(locale, 'account/login'));
  }

  // Use checkValidRedirect util for validation
  const fullPathWithLocale = matchedPattern
    ? `${matchedPattern}${pathWithoutLocale}`
    : pathWithoutLocale;
  const isValid = checkValidRedirect(fullPathWithLocale);

  if (!isValid) {
    // default to login without redirect param
    return redirect(getPathForLocale(locale, 'account/login'));
  }

  // redirct to login with redirect param if flag is enabled and valid path
  if (isRedirectAfterLoginEnabled) {
    // encodeURIComponent to fix URLs being broken up incorrectly if mulitple params
    const redirectPath = `account/login?redirectURL=${encodeURIComponent(
      matchedPattern + pathWithoutLocale
    )}`;
    return redirect(getPathForLocale(locale, redirectPath));
  }
}

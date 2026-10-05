/**
 * Check validity of the login redirectURL param.
 * @param {string} - The login URL with redirectURL param
 * @returns {boolean} - Whether the redirect is valid
 */
import { LOCALES } from '@whitbread-eos/api';

export default function checkValidRedirect(redirectVal: string): boolean {
  redirectVal = decodeURIComponent(redirectVal);
  return (
    (typeof redirectVal === 'string' &&
      !!redirectVal && // not empty, not falsy - redirectValue
      redirectVal !== "''" &&
      !redirectVal.startsWith('http') &&
      // locale present - e.g. /en-gb/ /de-de/
      Object.values(LOCALES).some((loc) => redirectVal.startsWith(`/${loc}/`))) ||
    // or old locale path - e.g. /en-gb/ /de-de/ for /business-booker paths
    redirectVal.startsWith('/gb/en/business-booker') ||
    redirectVal.startsWith('/de/de/business-booker')
  );
}

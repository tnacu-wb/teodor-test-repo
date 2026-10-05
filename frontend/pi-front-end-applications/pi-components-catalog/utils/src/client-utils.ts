/**
 * Client-safe utilities that can be imported from the main @whitbread-eos/utils package.
 * These utilities do NOT use next/headers and are safe to use in both client and server components.
 *
 * Previously these were only available via @whitbread-eos/utils/server, but that path also includes
 * server-only code that uses next/headers, causing issues with Next.js pages router and tree-shaking.
 *
 * IMPORTANT: Only import from files that do NOT import 'next/headers'!
 * - server/getters/getters.ts - imports 'cookies' from next/headers - DO NOT IMPORT
 * - server/getters/search.ts - imports 'headers' from next/headers - DO NOT IMPORT
 * - server/getters/labels.ts - safe, no next/headers import
 * - server/formatters/formatters.ts - safe, no next/headers import
 * - server/validators/ - safe, no next/headers import
 * - server/mutations/ - safe, no next/headers import (check individual files)
 */

// Tailwind merge utility
export { cn } from './utils/tailwind';

// Client-side translation hook and provider
export { TranslationProvider, useTranslation } from './utils/i18nClient';

// Higher-order components
export { RolesRequired } from './hoc';

// Path/URL formatters from main formatters
export { getPathForLocale } from './formatters/getPathForLocale';

// Client-safe formatters from server/formatters (these don't use next/headers)
export {
  formatIBAssetsUrl,
  sanitize,
  findError,
  getSavedCardType,
  ParseDateToYMD,
  parseRegistrationQuestions,
  formatHDPUrl,
  capitalizeFirstLetter,
  getLabelType,
  getStyling,
  resolveAndDownloadBlob,
  formatAccountNumber,
  mapSwitchState,
  getDefaultSwitchState,
  getVariant,
  normalizeAddress,
  parseAnswersObj,
} from './server/formatters';

// Client-safe validators (validators don't use next/headers)
export { isPIBACardType } from './server/validators';

// Client-safe label getters from server/getters/labels (this file doesn't import next/headers)
export { getInitials } from './server/getters/labels';

// Client-safe functions from getters.ts (now uses dynamic imports for next/headers)
// These are pure functions that don't actually use next/headers
export { getLocaleByPathname, getCountryLanguageByLocale } from './server/getters';

/**
 * Download utilities - browser-only functions
 * These use the DOM (document) and must run in a browser environment
 */

/**
 * Downloads a file from an S3 pre-signed URL by creating a temporary anchor element.
 * Must be called from browser context (uses document).
 * @param downloadUrl - The pre-signed S3 URL
 * @param fileName - The desired filename for the download
 */
export const downloadFromS3PreSignedUrl = (downloadUrl: string, fileName: string) => {
  if (typeof document === 'undefined') {
    console.warn('downloadFromS3PreSignedUrl can only be called in a browser environment');
    return;
  }
  const anchor = document.createElement('a');
  anchor.href = downloadUrl;
  anchor.download = fileName;
  document.body.appendChild(anchor);
  anchor.click();
  document.body.removeChild(anchor);
};

// NOTE: Functions like getCountriesList, getEmployees, getEmployeeDetails, companyDetailsLookup
// use cookies from next/headers at runtime and should be imported from @whitbread-eos/utils/server

// NOTE: businessTetherLogin is in mutations.ts - need to verify it doesn't import next/headers
// For now, keeping it out to be safe

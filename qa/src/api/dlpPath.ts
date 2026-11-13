/**
 * Convert a DLP browser path into the content-service/GraphQL path segment.
 *
 * Reference WDIO tests derived this from the current URL by removing the locale,
 * the leading `hotels/`, and the trailing `.html`.
 */
export function normalizeDlpContentPath(dlpPath = ''): string {
  let normalizedPath = dlpPath.trim();

  try {
    normalizedPath = new URL(normalizedPath).pathname;
  } catch {
    // Input is already a relative path.
  }

  normalizedPath = normalizedPath
    .split('?')[0]
    .split('#')[0]
    .replace(/^\/+/, '')
    .replace(/^[a-z]{2}\/[a-z]{2}\//i, '')
    .replace(/^[a-z]{2}-[a-z]{2}\//i, '')
    .replace(/^hotels\//i, '')
    .replace(/\.html$/i, '');

  return normalizedPath;
}
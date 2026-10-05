/**
 * Constructs the Datatrans return URL by appending `source=datatrans` query parameter
 * to the given current URL, using the correct separator (`?` or `&`).
 */
export function buildReturnUrl(currentUrl: string): string {
  return currentUrl.includes('?')
    ? `${currentUrl}&source=datatrans`
    : `${currentUrl}?source=datatrans`;
}

/**
 * Cookie from AEM
 * {
 *      "cookieName":"permissionEssential",
 *      "title":"Essential",
 *      "description":"<p>Some cookies are essential – our website wouldn’t work without them! We collect them to keep our website secure and ensure that from browsing to booking, your online experience runs smoothly.</p>\n",
 *      "isAlwaysActive":true,
 *      "toggleLabel":"Essentials are always active."
 * }
 */
export class Cookie {
  [key: string]: unknown;
  cookieName?: string;
  description?: string;
  isAlwaysActive?: boolean;
  title?: string;
  toggleLabel?: string;

  /**
   * Cookie constructor
   * @param data data object
   * @param data.cookie cookie object
   */
  constructor(data: { cookie?: Record<string, unknown> } = {}) {
    const cookie = data.cookie ?? {};
    this.cookieName = cookie.cookieName as string | undefined;
    this.title = cookie.title as string | undefined;
    this.description = cookie.description as string | undefined;
    this.isAlwaysActive = cookie.isAlwaysActive as boolean | undefined;
    this.toggleLabel = cookie.toggleLabel as string | undefined;
  }

  static fromResponse(data: { cookie?: Record<string, unknown> }): Cookie {
    return new Cookie(data);
  }
}

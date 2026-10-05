import { Cookie } from './cookie';

/**
 * Cookie Policies from AEM
 * {
 *      "version":"1",
 *      "brand":"pi",
 *      "introView":
 *          {
 *              "title":"Cookies and how we use them",
 *              "description":"...",
 *              "manageButtonText":"Manage cookies",
 *              "acceptAllButtonText":"Accept all cookies"
 *          },
 *       "manageView":
 *          {
 *              "title":"Manage cookies",
 *              "description":"<p>Choose the cookies that work for you.</p>",
 *              "saveSettingsButtonText":"Confirm settings",
 *              "alwaysActiveText":"Always Active",
 *              "cookieGroup":
 *                  [
 *                      {
 *                          "cookieName":"permissionEssential",
 *                          "title":"Essential",
 *                          "description":"...",
 *                          "isAlwaysActive":true,
 *                          "toggleLabel":"Essentials are always active."
 *                      }
 *                  ]
 *          }
 * }
 */
interface CookiesPolicyView {
  title?: string;
  description?: string;
  manageButtonText?: string;
  acceptAllButtonText?: string;
  necessaryOnlyButtonText?: string;
}

/** API model for the current TypeScript test framework. */
export class CookiesPolicies {
  [key: string]: unknown;
  introView?: CookiesPolicyView;
  manageView?: CookiesPolicyView & { saveSettingsButtonText?: string; alwaysActiveText?: string; cookieGroup: Cookie[] };

  /**
   * CookiesPolicies constructor
   * @param data data object
   * @param data.cookiesPolicies cookies policies object
   */
  constructor(data: { cookiesPolicies?: unknown } = {}) {
    const cookiesPolicies = (data.cookiesPolicies ?? {}) as Record<string, unknown>;
    const introView = (cookiesPolicies.introView ?? {}) as Record<string, unknown>;
    const manageView = (cookiesPolicies.manageView ?? {}) as Record<string, unknown>;

    this.introView = {
      title: introView.title as string | undefined,
      description: introView.description as string | undefined,
      manageButtonText: introView.manageButtonText as string | undefined,
      acceptAllButtonText: introView.acceptAllButtonText as string | undefined,
      necessaryOnlyButtonText: introView.necessaryOnlyButtonText as string | undefined,
    };
    const cookieGroup = Array.isArray(manageView.cookieGroup) ? (manageView.cookieGroup as Array<Record<string, unknown>>) : [];
    this.manageView = {
      title: manageView.title as string | undefined,
      description: manageView.description as string | undefined,
      saveSettingsButtonText: manageView.saveSettingsButtonText as string | undefined,
      alwaysActiveText: manageView.alwaysActiveText as string | undefined,
      cookieGroup: cookieGroup.map((cookie) => new Cookie({ cookie })),
    };
  }

  static fromResponse(data: { cookiesPolicies?: unknown }): CookiesPolicies {
    return new CookiesPolicies(data);
  }

}

/**
 * The newsletter signup information from GetNewsletterSignup API response
 *{
 *   "data": {
 *        "footer": {
 *            "newsletterSignup": {
 *                "introViewText": "Mit dem Premier Inn Newsletter sind Sie immer auf dem neuesten Stand. Geben Sie einfach Ihre Daten unten ein und sichern Sie sich exklusive Angebote, Tipps und Neuigkeiten.",
 *                "introViewTitle": "Ich möchte traumhafte News erhalten!",
 *                "signUpButtonText": "Jetzt abonnieren"
 *            }
 *        }
 *    }
 *}
 */
export class NewsletterSignup {
  [key: string]: unknown;
  introViewText?: string;
  introViewTitle?: string;
  signUpButtonText?: string;

  /**
   * NewsletterSignup constructor
   * @param data object data
   * @param data.newsletterSignupApiResponse response from API
   */
  constructor(data: { newsletterSignupApiResponse?: Record<string, unknown> } = {}) {
    const newsletterSignupApiResponse = data.newsletterSignupApiResponse ?? {};
    this.introViewText = newsletterSignupApiResponse.introViewText as string | undefined;
    this.introViewTitle = newsletterSignupApiResponse.introViewTitle as string | undefined;
    this.signUpButtonText = newsletterSignupApiResponse.signUpButtonText as string | undefined;
  }

  static fromResponse(data: { newsletterSignupApiResponse?: Record<string, unknown> }): NewsletterSignup {
    return new NewsletterSignup(data);
  }
}

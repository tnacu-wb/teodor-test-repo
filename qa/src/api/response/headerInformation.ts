/**
 * response example:
 *{
 *    "data": {
 *        "headerInformation": {
 *            "content": {
 *                "menu": {
 *                    "agentMemo": "Agent memo",
 *                    "bookHotel": "Search for a hotel",
 *                    "business": "Business",
 *                    "changeLogs": "Change logs",
 *                    "discoverPI": "Discover Premier Inn",
 *                    "findBooking": "Manage booking",
 *                    "guestAccount": "Guest account",
 *                    "language": "Language",
 *                    "languageButton": "English",
 *                    "logIn": "Log in",
 *                    "mobileMenuButton": "Menu",
 *                    "tick": "/etc/clientlibs/pi-header/resources/images/tick.svg"
 *                }
 *            }
 *        }
 *    }
 *}
 */
export class HeaderInformation {
  [key: string]: unknown;
  agentMemo?: string;
  bookHotel?: string;
  business?: string;
  changeLogs?: string;
  discoverPI?: string;
  findBooking?: string;
  guestAccount?: string;
  language?: string;
  languageButton?: string;
  logIn?: string;
  mobileMenuButton?: string;
  tick?: string;

  /**
   * Header information constructor
   * @param data object data
   * @param data.menu menu
   */
  constructor(data: { menu?: Record<string, unknown> } = {}) {
    const menu = data.menu ?? {};
    this.agentMemo = menu.agentMemo as string | undefined;
    this.bookHotel = menu.bookHotel as string | undefined;
    this.business = menu.business as string | undefined;
    this.changeLogs = menu.changeLogs as string | undefined;
    this.discoverPI = menu.discoverPI as string | undefined;
    this.findBooking = menu.findBooking as string | undefined;
    this.guestAccount = menu.guestAccount as string | undefined;
    this.language = menu.language as string | undefined;
    this.languageButton = menu.languageButton as string | undefined;
    this.logIn = menu.logIn as string | undefined;
    this.mobileMenuButton = menu.mobileMenuButton as string | undefined;
    this.tick = menu.tick as string | undefined;
  }

  static fromResponse(data: { menu?: Record<string, unknown> }): HeaderInformation {
    return new HeaderInformation(data);
  }
}

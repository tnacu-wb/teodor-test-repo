import { expect, type Locator } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { Strings } from "../../test-data/strings";
import { BasePibPage } from "./basePib.page";
/** InnBusiness application > Sidebar > Contact menu */
export class ContactUsPage extends BasePibPage {
  readonly url = "contact-us";
  // ######## UI elements/properties ########
  readonly contactUsContainer: Locator = this.page.getByTestId( "ContactUsPage-container", );
  readonly contactUsTitleLabel: Locator = this.page.getByTestId( "ContactUsPage-container-title", );
  readonly contactUsSubtitleLabel: Locator = this.page.getByTestId( "ContactUsPage-container-subtitle", );
  readonly bookingsAndPremierInnBusinessLabel: Locator = this.page.locator( '[data-testid="ContactUsPage-bookings-container"] h2', );
  readonly generalContactLabel: Locator = this.page.locator( '[data-testid="ContactUsPage-bookings-container"] span', );
  readonly emailCard: Locator = this.page.getByTestId( "ContactUsPage-email-card", );
  readonly emailTitleLabel: Locator = this.emailCard.locator("h3");
  readonly emailCardDescriptionLabel: Locator = this.emailCard .locator("div") .first();
  readonly emailCardLink: Locator = this.page.getByTestId( "ContactUsPage-email-card-link", );
  readonly innbusinessPayTitleLabel: Locator = this.page.locator( '[data-testid="ContactUsPage-innBusiness-pay-container"] h2', );
  readonly innbusinessPayDescriptionLabel: Locator = this.page.locator( '[data-testid="ContactUsPage-innBusiness-pay-container"] span', );
  readonly phoneBusinessAccountCard: Locator = this.page.getByTestId( "ContactUsPage-phone-card", );
  readonly phoneBusinessAccountTitleLabel: Locator = this.phoneBusinessAccountCard.locator("h3");
  readonly phoneBusinessAccountDescriptionLabel: Locator = this.phoneBusinessAccountCard.locator("div").first();
  readonly phoneBusinessAccountNumberLabel: Locator = this.phoneBusinessAccountCard.locator("div").nth(1).locator("span");
  readonly emailBusinessAccountCard: Locator = this.page.getByTestId( "ContactUsPage-email-business-account-card", );
  readonly emailBusinessAccountLabel: Locator = this.emailBusinessAccountCard.locator("h3");
  readonly emailBusinessAccountDescriptionLabel: Locator = this.emailBusinessAccountCard.locator("div").first();
  readonly emailBusinessAccountLink: Locator = this.page.getByTestId( "ContactUsPage-email-business-account-card-link", );
  readonly addressBusinessAccountCard: Locator = this.page.getByTestId( "ContactUsPage-address-card", );
  readonly addressTitleLabel: Locator = this.addressBusinessAccountCard.locator("h3");
  readonly ifYoudLikeToWriteUsLabel: Locator = this.addressBusinessAccountCard .locator("div") .first();
  readonly wordlineAddressLabel: Locator = this.addressBusinessAccountCard.locator("span");
  // ######## UI actions/navigation ########
  /** Open Contact us page. */
  async open(): Promise<void> {
    console.log("Open Contact us page");
    await this.openPath(this.url);
    await this.validateContactUsPageData();
  }
  // ######## UI validations ########
  /** Validate Contact us page content. */
  async validateContactUsPageData(): Promise<void> {
    console.log("Validate Contact us page content");
    await expect(this.contactUsContainer, "Contact us container").toBeVisible();
    await expect(this.contactUsTitleLabel, "Contact us title").toHaveText( await IbStrings.CONTACT_US.name, );
    await expect(this.contactUsSubtitleLabel, "Contact us subtitle").toHaveText( await IbStrings.CONTACT_US_DESCRIPTION.name, );
    await expect( this.bookingsAndPremierInnBusinessLabel, "Bookings and PIB section title", ).toHaveText(await IbStrings.CONTACT_US_BOOKINGS_AND_PIB.name);
    await expect(this.generalContactLabel, "General contact label").toHaveText( await IbStrings.CONTACT_US_GENERAL_CONTACT.name, );
    await expect(this.emailCard, "Email card").toBeVisible();
    await expect(this.emailTitleLabel, "Email card title").toHaveText( await Strings.EMAIL.name, );
    await expect( this.emailCardDescriptionLabel, "Email card description", ).toHaveText(await IbStrings.CONTACT_US_GENERAL_GET_IN_TOUCH.name);
    await expect(this.emailCardLink, "General email link").toHaveAttribute( "href", new RegExp( `mailto:${await IbStrings.CONTACT_US_GENERAL_EMAIL_ADDRESS.name}`, ), );
    await expect( this.innbusinessPayTitleLabel, "InnBusiness Pay title", ).toHaveText(await IbStrings.CONTACT_US_IBPAY.name);
    await expect( this.innbusinessPayDescriptionLabel, "InnBusiness Pay description", ).toHaveText(await IbStrings.CONTACT_US_IBPAY_SPENDING_CREDIT_ACCOUNT.name);
    await expect( this.phoneBusinessAccountCard, "Phone account card", ).toBeVisible();
    await expect(this.phoneBusinessAccountTitleLabel, "Phone title").toHaveText( await IbStrings.CONTACT_US_IBPAY_PHONE.name, );
    await expect( this.phoneBusinessAccountDescriptionLabel, "Phone description", ).toHaveText(await IbStrings.CONTACT_US_IBPAY_PHONE_ACCOUNT_QUERIES.name);
    await expect( this.phoneBusinessAccountNumberLabel, "Phone number", ).toHaveText(await IbStrings.CONTACT_US_IBPAY_PHONE_NUMBER.name);
    await expect( this.emailBusinessAccountCard, "Business account email card", ).toBeVisible();
    await expect( this.emailBusinessAccountLabel, "Business account email title", ).toHaveText(await Strings.EMAIL.name);
    await expect( this.emailBusinessAccountDescriptionLabel, "Business account email description", ).toHaveText(await IbStrings.CONTACT_US_IBPAY_GET_IN_TOUCH.name);
    await expect( this.emailBusinessAccountLink, "Business account email link", ).toHaveAttribute( "href", new RegExp( `mailto:${await IbStrings.CONTACT_US_IBPAY_EMAIL_ADDRESS.name}`, ), );
    await expect( this.addressBusinessAccountCard, "Address account card", ).toBeVisible();
    await expect(this.addressTitleLabel, "Address title").toHaveText( await Strings.ADDRESS.name, );
    await expect( this.ifYoudLikeToWriteUsLabel, "Address description", ).toHaveText(await IbStrings.CONTACT_US_IBPAY_ADDRESS_DESCRIPTION.name);
    await expect(this.wordlineAddressLabel, "Worldline address").toHaveText( await IbStrings.CONTACT_US_IBPAY_ADDRESS_WORDLINE.name, );
  }
}

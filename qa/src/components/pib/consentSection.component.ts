import { expect, type Locator, type Page } from "@playwright/test";
import { CardHolderSectionComponent } from "./cardHolderSection.component";

/**
 * InnBusiness application > Consent section
 */
export class ConsentSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly consentSection: Locator = this.page.locator( '//div[@data-testid="Consent-Radio"]/parent::node()', );
  readonly consentSectionTitleLabel: Locator = this.consentSection.locator("span");
  readonly consentInfoTooltipLabel: Locator = this.page.locator( '(//div[contains(@class,"bg-tooltipInfo mt-4")])//span', );
  readonly consentYesRadioButton: Locator = this.page.locator( '//div[@data-testid="Consent-Radio"]//button[@id="yes"]', );
  readonly consentNoRadioButton: Locator = this.page.locator( '//div[@data-testid="Consent-Radio"]//button[@id="no"]', );
  readonly consentNoAlertLabel: Locator = this.page.locator( 'div[data-testid="Consent-Radio"] ~ div span', );
  readonly cardHolderSection = new CardHolderSectionComponent();
  // ######## UI actions/navigation ########
  /** Click on Continue Yes radio button. */
  async clickYesRadioButtonForDE(): Promise<void> {
    console.log("Click Yes radio button for DE");
    if (global.browser.options.locale !== "gb-en")
      await this.consentYesRadioButton.click();
  }
  /** Click on No radio button. */
  async clickNoRadioButtonForDE(): Promise<void> {
    console.log("Click No radio button for DE");
    if (global.browser.options.locale !== "gb-en")
      await this.consentNoRadioButton.click();
  }
  // ######## UI validations ########
  /** Validate employee consent section. */
  async validateEmployeeConsentSection({
    isDisplayed = true,
    yesChecked = false,
    noChecked = false,
  }: {
    isDisplayed?: boolean;
    yesChecked?: boolean;
    noChecked?: boolean;
  } = {}): Promise<void> {
    console.log("Validate employee consent section");
    if (!isDisplayed) {
      await expect( this.consentSection, "Employee consent section", ).not.toBeVisible();
      return;
    }
    await expect(this.consentSection, "Employee consent section").toBeVisible();
    await expect( this.consentYesRadioButton, "Employee consent yes button", ).toHaveAttribute("aria-checked", String(yesChecked));
    await expect( this.consentNoRadioButton, "Employee consent no button", ).toHaveAttribute("aria-checked", String(noChecked));
    if (noChecked)
      await expect( this.consentNoAlertLabel, "Employee consent no alert label", ).toBeVisible();
    if (yesChecked) await this.cardHolderSection.validateCardHolderNameInput();
  }
}

import { expect, type Locator, type Page } from '@playwright/test';
import { Locales, getCurrentLocale } from '../../../test-data/locales';
import { Strings } from '../../../test-data/strings';

/**
 * The header section from home page containing the UI elements, custom actions and validations.
 */
export class HeaderSectionComponent {
  private readonly page: Page = global.page;

  // ######## properties ########

  // ######## UI elements/properties ########

  readonly headerContainer: Locator = this.page.locator('//div[@data-testid="common-header-wrapper"]');
  readonly logoImage: Locator = this.page.locator('//div[contains(@data-testid, "logo-container-")]');
  readonly discoverPiLabel: Locator = this.page.locator('[data-testid="popover-enabler-text-discoverPI"]');
  readonly manageBookingLabel: Locator = this.page.locator('[data-testid="navigation-link-findBooking"]');
  readonly guestAccountLabel: Locator = this.page.locator('[data-testid="navigation-link-guestAccount"]');
  readonly changeLogsLabel: Locator = this.page.locator('[data-testid="navigation-link-changeLogs"]');
  readonly agentMemoLabel: Locator = this.page.locator('[data-testid="navigation-link-agentMemo"]');
  readonly logoutLabel: Locator = this.page.locator('[data-testid="navigation-link-logout"]');
  readonly selectedFlagLabel: Locator = this.page.locator('//div[@id="popover-trigger-2"]//*[name()="svg"]');
  readonly searchContainer: Locator = this.page.locator('//div[@data-testid="search-component"]');
  readonly checkMarkIcon: Locator = this.page.locator('//img[@class="chakra-image css-1plvz19"]');
  readonly languageSelectorContainer: Locator = this.page.locator('[data-testid="languageSelectorContainer"]');
  readonly discoverPiContainer: Locator = this.page.locator('//div[@data-testid="HeaderAgent-entire-popover-DiscoverPI"]');
  readonly shortBreaks: Locator = this.page.locator('//li[@data-testid="discoverPIfirstColumn_listItemPopover-0"]');
  readonly shortBreaksColumnLabel: Locator = this.page.locator('//ul[@data-testid="discoverPIfirstColumn"]//h2');
  readonly discoverPiMenuElements: Locator = this.page.locator('//div[@id="popover-body-4"]//ul/*');
  readonly englishLanguageOptionFlagIcon: Locator = this.page.getByRole('dialog').locator('[data-testid="englishFlag"], img[src*="British"], img[src*="british"]');
  readonly germanLanguageOptionFlagIcon: Locator = this.page.getByRole('dialog').locator('[data-testid="germanFlag"], img[src*="German"], img[src*="german"]');

  // ######## UI actions/navigation ########

  /**
   * Get language path from url based on locale.
   * @returns Default language path.
   */
  async getPathBasedOnLocale(): Promise<string> {
    const locale = getCurrentLocale();
    return `/${locale.country}/${locale.language}`;
  }

  /** Click logo image. */
  async clickLogoImage(): Promise<void> {
    console.log('Click logo image');
    await this.logoImage.scrollIntoViewIfNeeded();
    await this.logoImage.click();
  }

  /** Click on Premier Inn button. */
  async clickOnPiButton(): Promise<void> {
    console.log('Click on Premier Inn button');
    await this.discoverPiLabel.click();
  }

  /** Click outside Premier Inn menu. */
  async clickOutsideOfDiscoverPiMenu(): Promise<void> {
    console.log('Click outside of Discover Premier Inn menu');
    await this.page.mouse.click(0, 0);
  }

  /** Click on Short Breaks option from the Discover Premier Inn Menu. */
  async clickOnShortBreaksOptionDiscoverPiMenu(): Promise<void> {
    console.log('Click on Short Breaks option from the Discover Premier Inn menu');
    await this.shortBreaks.click();
  }

  /** Click on Manage Booking button. */
  async clickOnManageBookingButton(): Promise<void> {
    console.log('Click on Manage Booking button');
    await this.manageBookingLabel.click();
  }

  /** Change the CCUI header language using the requested locale language code. */
  async changeLanguage(language: string): Promise<void> {
    console.log(`Change CCUI header language to ${language}`);
    await this.languageSelectorContainer.click();
    const languageOption = language === Locales.DE_DE.language
      ? this.germanLanguageOptionFlagIcon
      : this.englishLanguageOptionFlagIcon;
    await languageOption.click();
    await this.page.waitForLoadState('domcontentloaded');
  }

  // ######## UI validations ########

  /** Validate homepage is displayed. */
  async validateHomePageIsDisplayed(): Promise<void> {
    console.log('Validate that the agent is redirected to homepage');
    await expect(this.searchContainer, 'Search container').toBeVisible();
  }

  /**
   * Validate the header menu options labels.
   * @param headerInformation The header information API response.
   */
  async validateHeaderOptionsLabels(headerInformation: {
    discoverPI: string;
    findBooking: string;
    guestAccount: string;
    changeLogs: string;
    agentMemo: string;
  }): Promise<void> {
    console.log('Validate header options labels');

    await expect(this.discoverPiLabel, 'Discover Premier Inn button').toBeVisible();
    await expect(this.discoverPiLabel, 'Discover Premier Inn label').toHaveText(headerInformation.discoverPI.replace(/\s+$/g, ''));

    await expect(this.manageBookingLabel, 'Manage booking button').toBeVisible();
    await expect(this.manageBookingLabel, 'Manage booking label').toHaveText(headerInformation.findBooking);

    await expect(this.guestAccountLabel, 'Guest account button').toBeVisible();
    await expect(this.guestAccountLabel, 'Guest account label').toHaveText(headerInformation.guestAccount);

    await expect(this.changeLogsLabel, 'Change logs button').toBeVisible();
    await expect(this.changeLogsLabel, 'Change logs label').toHaveText(headerInformation.changeLogs);

    await expect(this.agentMemoLabel, 'Agent memo button').toBeVisible();
    await expect(this.agentMemoLabel, 'Agent memo label').toHaveText(headerInformation.agentMemo);

    await expect(this.logoutLabel, 'Log out button').toBeVisible();
    await expect(this.logoutLabel, 'Log out label').toHaveText(await Strings.LOGOUT_CCUI.name);
  }

  /** Validate the page is displayed according to the account default language. */
  async validateLanguageSelectedFunctionality(): Promise<void> {
    console.log('Validate that the correct language is displayed in the url');
    await this.validateLanguageSelectorOptions();
    expect(this.page.url(), 'Url path should contain the correct language').toContain(await this.getPathBasedOnLocale());
  }

  /** Validate flag depending on locale. */
  async validateLanguageSelectedFlag(): Promise<void> {
    console.log('Validate the language flag depending on locale');
    const expectedFlagIcon = getCurrentLocale().name === Locales.DE_DE.name ? (await Strings.GERMAN.name).toLowerCase() : await Strings.BRITISH.name;
    await expect(this.selectedFlagLabel, 'Desktop language selector flag icon is displayed').toHaveAttribute('data-testid', new RegExp(expectedFlagIcon, 'i'));
  }

  /** Validate language selector options. */
  async validateLanguageSelectorOptions(): Promise<void> {
    console.log('Validate that the language selector options are displayed');
    await expect(this.englishLanguageOptionFlagIcon, 'English language option').toBeVisible();
    await expect(this.germanLanguageOptionFlagIcon, 'German language option').toBeVisible();
  }

  /** Validate page is loaded in the selected language. */
  async validateHomePageIsDisplayedInSelectedLanguage(): Promise<void> {
    console.log('Validate that the page is loaded in the selected language');
    await expect(this.discoverPiLabel, 'Discover Premier Inn button').toBeVisible();
    const selectedFlagLabelAttribute = await this.selectedFlagLabel.getAttribute('data-testid');
    const expectedText = selectedFlagLabelAttribute === 'germanFlag'
      ? Strings.DISCOVER_PREMIER_INN.data.de
      : Strings.DISCOVER_PREMIER_INN.data.default;
    await expect(this.discoverPiLabel, 'Discover Premier Inn label').toHaveText(expectedText ?? '');
  }

  /** Validate flag depending on locale. */
  async validateSelectedFlag(): Promise<void> {
    console.log('Validate selected language flag');
    if (getCurrentLocale().name === Locales.GB_EN.name) {
      await this.validateEnglishFlagAndCheckMark();
    } else {
      await this.validateGermanFlagAndCheckMark();
    }
  }

  /** Validate language flag and check mark are displayed for english language option. */
  async validateEnglishFlagAndCheckMark(): Promise<void> {
    console.log('Validate English language flag and check mark');
    await expect(this.englishLanguageOptionFlagIcon, 'English language option').toBeVisible();
    await expect(this.checkMarkIcon, 'Language checkmark').toBeVisible();
  }

  /** Validate language flag and check mark are displayed for german language option. */
  async validateGermanFlagAndCheckMark(): Promise<void> {
    console.log('Validate German language flag and check mark');
    await expect(this.germanLanguageOptionFlagIcon, 'German language option').toBeVisible();
    await expect(this.checkMarkIcon, 'Language checkmark').toBeVisible();
  }

  /** Validates if the elements in the list are displayed. */
  async validateHeaderElementsAreDisplayed(): Promise<void> {
    console.log('Validate header elements are displayed');
    const displayedElements = [this.logoImage, this.languageSelectorContainer, this.discoverPiLabel, this.manageBookingLabel, this.logoutLabel, this.guestAccountLabel];
    for (const element of displayedElements) {
      await expect(element, 'Header element should be displayed').toBeVisible();
    }
  }

  /** Validates if the elements in the list are disabled and not clickable. */
  async validateHeaderElementsAreDisabledAndNotClickable(): Promise<void> {
    console.log('Validate header elements are disabled and not clickable');
    const disabledElements = [this.changeLogsLabel, this.agentMemoLabel];
    for (const element of disabledElements) {
      await expect(element, 'Header element should be disabled').toBeDisabled();
    }
  }

  /**
   * Validate Discover Premier Inn menu is displayed.
   * @param isDisplayed True/false display state of Discover Premier Inn menu.
   */
  async validateDiscoverPiMenuIsDisplayed(isDisplayed = true): Promise<void> {
    console.log('Validate Discover Premier Inn menu is displayed');
    if (isDisplayed) {
      await expect(this.discoverPiContainer, 'Discover Premier inn menu').toBeVisible();
    } else {
      await expect(this.discoverPiContainer, 'Discover Premier inn menu').toBeHidden();
    }
  }

  /**
   * Validate Discover Premier Inn elements comparing the text in UI with the text from Api Dictionary.
   * @param expectedDiscoverElements AEM dictionary text for discover premier inn elements in container.
   * @param language Expected language.
   */
  async validateDiscoverPremierInnElements(expectedDiscoverElements: string[], language: string): Promise<void> {
    console.log('Validate Discover Premier Inn elements');
    const discoverUiLabels = await this.discoverPiMenuElements.allTextContents();

    if (language === Locales.GB_EN.language) {
      expect(discoverUiLabels.length, 'Discover Premium Inn elements length does not match').toBe(expectedDiscoverElements.length);
    } else {
      expect(discoverUiLabels.length, 'Discover Premium Inn elements length does not match').toBeLessThan(expectedDiscoverElements.length);
    }

    for (const expectedDiscoverElement of expectedDiscoverElements) {
      if (discoverUiLabels.includes(expectedDiscoverElement)) {
        expect(discoverUiLabels, 'Expected element should be in Discover premier Inn UI menu').toContain(expectedDiscoverElement);
      }
    }

    if (language === Locales.GB_EN.language) {
      expect(discoverUiLabels, 'Discover PI Elements from UI is not the same with Discover PI Elements from API response').toEqual(expectedDiscoverElements);
    } else {
      expect(discoverUiLabels, 'Discover PI Elements from UI should differ from API response until DNRQ-50653 is fixed').not.toEqual(expectedDiscoverElements);
    }
  }
}
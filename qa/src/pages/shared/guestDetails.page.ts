import { type Locator, expect } from '@playwright/test';
import { type GuestInfo, type GuestAddress, type ReasonForStay } from '../../test-data/guestData';
import { Locales } from '../../test-data/locales';
import { Strings } from '../../test-data/strings';
import { BasePage } from './base.page';
import { UiUtils } from '../../utils/uiUtils';
import { ReasonForStaySectionComponent } from '../../components/pi/guestDetails/reasonForStaySection.component';
import { SignInSectionComponent } from '../../components/pi/guestDetails/signInSection.component';
import { EmailUpdatesAndOffersComponent } from '../../components/pi/guestDetails/emailUpdatesAndOffers.component';
import { PrivacyNoticeSectionComponent } from '../../components/pi/guestDetails/privacyNoticeSection.component';
import { BookerInformationSectionComponent } from '../../components/pi/guestDetails/bookerInformationSection.component';
import { LeadGuestSectionComponent } from '../../components/pi/guestDetails/leadGuestSection.component';
import { AccompanyingGuestDetailsComponent } from '../../components/pi/guestDetails/accompanyingGuestDetails.component';
import { GuestDetailsBookingSummarySectionComponent } from '../../components/pi/guestDetails/bookingSummarySection.component';
import { YourAddressSectionComponent } from '../../components/pi/guestDetails/yourAddressSection.component';

/**
 * Normalised guest details as read back from the form after the app
 * has processed/auto-filled them. These values — not the originally typed
 * inputs — should be used for later assertions (e.g. booking confirmation).
 */
export interface NormalisedGuestDetails {
  title: string;
  firstName: string;
  lastName: string;
  email: string;
  mobilePrefix: string;
  mobile: string;
  landlinePrefix: string;
  landline: string;
  addressLine1: string;
  addressLine2: string;
  addressLine3: string;
  postalCode: string;
}

/** PI-supported subset of the legacy GuestDetails payload. */
export interface GuestDetailsFormData {
  reasonForStay: ReasonForStay;
  booker: GuestInfo & { address: GuestAddress };
  stayingGuests: Array<{
    sameAsBooker: boolean;
    stayingGuestDetails: Pick<GuestInfo, 'title' | 'firstName' | 'lastName'>;
  }>;
}

interface GuestDetailsReservationInfo {
  bookingFlowId?: string;
  reservationDetails: { basketReference: string };
}

/**
 * PI Guest Details Page (GDP) - where booker information, address,
 * and reason for stay are collected before proceeding to payment.
 */
export class GuestDetailsPage extends BasePage {
  // ######## properties ########

  readonly url = 'guest-details';

  // ######## UI elements/properties ########

  readonly reasonForStaySection: ReasonForStaySectionComponent = new ReasonForStaySectionComponent();
  readonly signInSection: SignInSectionComponent = new SignInSectionComponent();
  readonly emailUpdatesAndOffers: EmailUpdatesAndOffersComponent = new EmailUpdatesAndOffersComponent();
  readonly privacyNoticeSection: PrivacyNoticeSectionComponent = new PrivacyNoticeSectionComponent();
  readonly bookerInformationSection: BookerInformationSectionComponent = new BookerInformationSectionComponent();
  readonly leadGuestSection: LeadGuestSectionComponent = new LeadGuestSectionComponent();
  readonly accompanyingGuestDetails: AccompanyingGuestDetailsComponent = new AccompanyingGuestDetailsComponent();
  readonly bookingSummarySection: GuestDetailsBookingSummarySectionComponent = new GuestDetailsBookingSummarySectionComponent();
  readonly yourAddressSection: YourAddressSectionComponent = new YourAddressSectionComponent();

  // Page loaded indicator
  readonly pageLoadedIndicator: Locator = this.page.locator('#guestDetailsForm');

  // Reason for stay radio buttons
  readonly reasonForStayGroup: Locator = this.page.locator('div[data-testid="GuestDetails-ReasonForStay"]');
  readonly leisureRadioButton: Locator = this.page.locator('span[data-testid="GuestDetails-ReasonForStay-Leisure"]');
  readonly businessRadioButton: Locator = this.page.locator('span[data-testid="GuestDetails-ReasonForStay-Business"]');

  // Booker information fields
  readonly bookerInfoSection: Locator = this.page.locator('div[data-testid="GuestDetails-Title"]').locator('..');
  readonly titleButton: Locator = this.page.locator('button[data-testid="DropdownComp-GuestDetails-Title-InnerDropdown-menuButton"]');
  readonly titleValueLabel: Locator = this.page.locator('div[data-testid="DropdownComp-GuestDetails-Title-InnerDropdown-menuButtonText"]');
  readonly firstNameInput: Locator = this.page.locator('input[data-testid="input-firstName"]');
  readonly lastNameInput: Locator = this.page.locator('input[data-testid="input-lastName"]');
  readonly emailInput: Locator = this.page.locator('input[data-testid="input-email"]');
  readonly mobileInput: Locator = this.page.locator('input[data-testid="GuestDetails-Mobile-phoneNumber"]');
  readonly landlineInput: Locator = this.page.locator('input[data-testid="GuestDetails-Landline-phoneNumber"]');

  // Phone prefix selectors
  readonly mobileCountrySelectorPrefixLabel: Locator = this.page.locator('div[data-testid="GuestDetails-Mobile-countrySelector"] span');
  readonly landlineCountrySelectorPrefixLabel: Locator = this.page.locator('div[data-testid="GuestDetails-Landline-countrySelector"] span');

  // Address fields
  readonly addressSection: Locator = this.page.locator('div[data-testid="GuestDetails-AddressSelection"]');
  readonly manualAddressLink: Locator = this.page.locator('[data-testid="GuestDetails-ManualAddressToggle"]');
  readonly homeAddressRadio: Locator = this.page.locator('span[data-testid="GuestDetails-AddressSelection-PersonalAddress"]');
  readonly postalCodeInput: Locator = this.page.locator('input[data-testid="input-postalCode"]');
  readonly addressLine1Input: Locator = this.page.locator('input[data-testid="input-addressLine1"]');
  readonly addressLine2Input: Locator = this.page.locator('input[data-testid="input-addressLine2"]');
  readonly addressLine3Input: Locator = this.page.locator('input[data-testid="input-addressLine3"]');
  readonly cityInput: Locator = this.page.locator('input[data-testid="input-cityName"]');
  readonly countryCodeSelect: Locator = this.page.locator('div[data-testid="GuestDetails-CountrySelector-countrySelector"]');
  readonly countryNameInput: Locator = this.page.locator('div[data-testid="GuestDetails-CountrySelector"] input[data-testid="GuestDetails-CountrySelector-countryName"]');

  // Postcode lookup fields
  readonly postcodeSearchInput: Locator = this.page.locator('input[data-testid="input-postcodeAddress"]');
  readonly findAddressButton: Locator = this.page.locator('button[data-testid="PostcodeAddress-FindAddressBtn"]');
  readonly postcodeAddressDropdownButton: Locator = this.page.locator('button[data-testid="DropdownComp-PostcodeAddress-DropdownComp-menuButton"]');
  readonly firstPostcodeAddressOption: Locator = this.page.locator('[data-testid="DropdownComp-PostcodeAddress-DropdownComp-0"]');
  readonly postcodeAddressOptions: Locator = this.page.locator('div[data-testid="DropdownComp-PostcodeAddress-DropdownComp-entireList"] button, div[data-testid="DropdownComp-PostcodeAddress-DropdownComp-entireList"] [role="option"], div[data-testid="DropdownComp-PostcodeAddress-DropdownComp-entireList"] [role="menuitem"], [data-testid^="DropdownComp-PostcodeAddress-DropdownComp-"][role="menuitem"]');

  // Booking summary (mobile)
  readonly bookingSummaryMobileExpandButton: Locator = this.page.locator('div[data-testid="BookingSummary-MobileVariant-SectionHeader"]');
  readonly bookingSummaryContainer: Locator = this.page.locator('div[data-testid="BookingSummary-MobileVariant-SectionWrapper"]');

  // Navigation
  readonly continueButton: Locator = this.page.locator('button[data-testid="GuestDetails-Submit"]');
  readonly backToAncillariesButton: Locator = this.page.locator('div[data-testid="GuestDetails-BackToAncillariesButton"]');
  readonly bookingForMyselfSection: Locator = this.page.locator('div[data-testid="GuestDetails-whoBookerIsTabs"] div[data-testid="radio-box-wrapper"]').nth(0);
  readonly bookingForSomeoneElseSection: Locator = this.page.locator('div[data-testid="GuestDetails-whoBookerIsTabs"] div[data-testid="radio-box-wrapper"]').nth(1);
  readonly bookingForSomeoneElseNewControl: Locator = this.page.locator('[name="SOMEONEELSE"] + span + span');
  readonly additionalDateOfBirthInput: Locator = this.page.locator('input[data-testid="GuestDetails-AdditionalInformation--SingleDatePicker"]');
  readonly additionalNationalityInput: Locator = this.page.locator('div[data-testid="GuestDetails-AdditionalInformation-Nationality"] input');
  readonly additionalPassportInput: Locator = this.page.locator('div[data-testid="GuestDetails-AdditionalInformation-Passport"] input');
  readonly stayingGuestDateOfBirthInput: Locator = this.page.locator('input[data-testid="GuestDetails-leadGuest--SingleDatePicker"]');
  readonly stayingGuestNationalityInput: Locator = this.page.locator('div[data-testid="GuestDetails-leadGuest-Nationality"] input');
  readonly stayingGuestPassportInput: Locator = this.page.locator('div[data-testid="GuestDetails-leadGuest-Passport"] input');
  readonly leadGuestConsentRadioButton: Locator = this.page.locator('span[data-testid="GuestDetails-leadGuest-consent_consentYes"]');
  readonly checkInButton: Locator = this.page.locator('[data-testid="GuestDetails-leadGuest-CheckinButton"]');
  readonly expandableCheckinInfoSection: Locator = this.page.locator('button[data-testid="GuestDetails-AdditionalInformation-CheckinButton"]');
  readonly differentBillingAddressCheckbox: Locator = this.page.locator('[name="billingAddressCheckbox"] + span');
  readonly differentBillingCompanyAddressCheckbox: Locator = this.page.locator('[data-testid="GuestDetails-BillingAddress-CompanyAddress"]');
  readonly bookerEmailSection: Locator = this.page.locator('[id="leadGuest[0][email]"]');

  // ######## UI actions/navigation ########

  /** Navigate directly to the localized Guest Details page for a reservation. */
  async open(reservationInfo?: GuestDetailsReservationInfo): Promise<void> {
    console.log('Open Guest Details page');
    if (!reservationInfo) {
      await this.openLocalizedPath(this.url);
      return;
    }

    if (global.browser?.options?.app === 'ccui') reservationInfo.bookingFlowId = '';
    const bookingFlowPath = global.browser?.options?.app === 'pi' && reservationInfo.bookingFlowId
      ? `${reservationInfo.bookingFlowId}/`
      : '';
    const reservationId = encodeURIComponent(reservationInfo.reservationDetails.basketReference);
    await this.openLocalizedPath(`${bookingFlowPath}${this.url}?reservationId=${reservationId}`);
  }

  /**
   * Select the reason for stay radio button.
   * @param reason - 'leisure' or 'business'
   */
  async selectReasonForStay(reason: ReasonForStay): Promise<void> {
    console.log(`Select reason for stay=${reason}`);
    await this.reasonForStaySection.clickReasonForStayRadioButton(reason);
  }

  /** Click the PI control for booking on behalf of another guest. */
  async clickBookingForSomeoneElseOption(): Promise<void> {
    console.log('Click booking for someone else option');
    await this.leadGuestSection.clickLeadGuestCheckbox();
  }

  /** Select the legacy "booking for myself" option when that UI variant is displayed. */
  async clickCallerIsBookingForThemselvesSection(): Promise<void> {
    console.log('Click Caller is booking for themselves section');
    await this.bookingForMyselfSection.scrollIntoViewIfNeeded();
    await this.bookingForMyselfSection.click();
  }

  /** Select the legacy "booking for someone else" option. */
  async clickIAmBookingForSomeoneElseSection(): Promise<void> {
    console.log('Click I am making this booking for someone else section');
    await this.bookingForSomeoneElseSection.scrollIntoViewIfNeeded();
    await this.bookingForSomeoneElseSection.click();
  }

  /** Select the newer legacy "booking for someone else" control. */
  async clickIAmBookingForSomeoneElseSectionNew(): Promise<void> {
    console.log('Click newer I am making this booking for someone else control');
    await this.bookingForSomeoneElseNewControl.scrollIntoViewIfNeeded();
    await this.bookingForSomeoneElseNewControl.click();
  }

  /** Click the different billing address option. */
  async clickDifferentBillingAddressCheckbox(): Promise<void> {
    console.log('Click different billing address checkbox');
    await this.differentBillingAddressCheckbox.scrollIntoViewIfNeeded();
    await this.differentBillingAddressCheckbox.click();
  }

  /** Click the different billing company address option. */
  async clickDifferentBillingCompanyAddressCheckbox(): Promise<void> {
    console.log('Click different billing company address checkbox');
    await this.differentBillingCompanyAddressCheckbox.scrollIntoViewIfNeeded();
    await this.differentBillingCompanyAddressCheckbox.click();
  }

  /**
   * Fill all booker information fields with the provided guest data.
   * Title is selected via a custom dropdown (click button, then click option).
   * @param guest - GuestInfo object with title, firstName, lastName, emailAddress, mobile, landline
   */
  async fillBookerInformation(guest: GuestInfo): Promise<void> {
    console.log(`Filling booker information: ${guest.firstName} ${guest.lastName}`);
    // Select title from dropdown
    await this.titleButton.click();
    const title = await this.getLocalizedTitle(guest.title);
    const titleOption = this.page.locator(
      `button[data-testid*="DropdownComp-GuestDetails-Title-InnerDropdown-"]`,
      { hasText: new RegExp(`^${this.escapeRegExp(title)}$`) }
    );
    await titleOption.click();

    // Fill text inputs
    await this.firstNameInput.fill(guest.firstName);
    await this.lastNameInput.fill(guest.lastName);
    await this.emailInput.fill(guest.emailAddress);
    await this.mobileInput.fill(guest.mobile);
    // Landline field may not be visible on all form layouts (A/B test)
    if (await this.landlineInput.isVisible()) {
      await this.landlineInput.scrollIntoViewIfNeeded();
      await this.landlineInput.fill(guest.landline);
    }
  }

  /** Set the mobile and landline country prefixes using the localized country name. */
  async setPrefixMobileLandline(countryCode: string): Promise<void> {
    console.log(`Set mobile and landline prefixes for country=${countryCode}`);
    const countryName = await this.getCountryName(countryCode);
    await this.bookerInformationSection.setPrefixMobile(countryName);
    await this.bookerInformationSection.setPrefixLandline(countryName);
  }

  /** Set the additional guest's date of birth. */
  async setDateOfBirth(value: string): Promise<void> {
    console.log('Set additional guest date of birth');
    await this.additionalDateOfBirthInput.fill(value);
  }

  /** Set the additional guest's nationality and confirm the option. */
  async setNationality(value: string): Promise<void> {
    console.log(`Set additional guest nationality=${value}`);
    await this.additionalNationalityInput.fill(value);
    await this.additionalNationalityInput.press('Enter');
  }

  /** Set the additional guest's passport number. */
  async setPassport(value: string): Promise<void> {
    console.log('Set additional guest passport number');
    await this.additionalPassportInput.fill(value);
  }

  /** Set the first staying guest's date of birth. */
  async setDateOfBirthForStayingGuest(value: string): Promise<void> {
    console.log('Set first staying guest date of birth');
    await this.stayingGuestDateOfBirthInput.first().fill(value);
  }

  /** Set the first staying guest's nationality and confirm the option. */
  async setNationalityForStayingGuest(value: string): Promise<void> {
    console.log(`Set first staying guest nationality=${value}`);
    await this.stayingGuestNationalityInput.first().fill(value);
    await this.stayingGuestNationalityInput.first().press('Enter');
  }

  /** Set the first staying guest's passport number. */
  async setPassportForStayingGuest(value: string): Promise<void> {
    console.log('Set first staying guest passport number');
    await this.stayingGuestPassportInput.first().fill(value);
  }

  /** Set the first staying guest's email address. */
  async setGuestEmail(value: string): Promise<void> {
    console.log(`Set first staying guest email=${value}`);
    await this.page.locator('input[data-testid="input-leadGuest[0].email"]').fill(value);
  }

  /** Set the second staying guest's email address. */
  async setGuestEmailRoom2(value: string): Promise<void> {
    console.log(`Set second staying guest email=${value}`);
    await this.page.locator('input[data-testid="input-leadGuest[1][email]"]').fill(value);
  }

  /** Confirm consent for the lead guest. */
  async clickOnConsentRadioButton(): Promise<void> {
    console.log('Click consent radio button for lead guest');
    await this.leadGuestConsentRadioButton.scrollIntoViewIfNeeded();
    await this.leadGuestConsentRadioButton.click();
  }

  /** Click the booker email section. */
  async clickBookerEmailSection(): Promise<void> {
    console.log('Click booker email section');
    await this.bookerEmailSection.scrollIntoViewIfNeeded();
    await this.bookerEmailSection.click();
  }

  /** Expand the additional check-in-information section when it is available. */
  async clickCheckinInfoExpandableSection(): Promise<void> {
    console.log('Click check-in info expandable section');
    if (await this.expandableCheckinInfoSection.isVisible()) {
      await this.expandableCheckinInfoSection.click();
    } else {
      await this.additionalDateOfBirthInput.scrollIntoViewIfNeeded();
    }
  }

  /** Open the check-in details for the lead guest. */
  async clickCheckInCheckBox(): Promise<void> {
    console.log('Click check-in checkbox');
    await this.checkInButton.scrollIntoViewIfNeeded();
    await this.checkInButton.click();
  }

  /** Confirm consent for the main guest. */
  async clickMainGuestConsentRadioButton(): Promise<void> {
    console.log('Click main guest consent radio button');
    await this.leadGuestConsentRadioButton.scrollIntoViewIfNeeded();
    await this.leadGuestConsentRadioButton.click();
  }

  /** Set the first room's lead guest date of birth. */
  async setDateOfBirthRoom1(value: string): Promise<void> {
    console.log('Set Room 1 lead guest date of birth');
    await this.stayingGuestDateOfBirthInput.first().fill(value);
  }

  /** Set the first room's lead guest nationality and confirm the option. */
  async setNationalityRoom1(value: string): Promise<void> {
    console.log(`Set Room 1 lead guest nationality=${value}`);
    await this.stayingGuestNationalityInput.first().fill(value);
    await this.stayingGuestNationalityInput.first().press('Enter');
  }

  /** Set the first room's lead guest passport number. */
  async setPassportRoom1(value: string): Promise<void> {
    console.log('Set Room 1 lead guest passport number');
    await this.stayingGuestPassportInput.first().fill(value);
  }

  /** Set the second room's additional-guest date of birth. */
  async setDateOfBirthRoom2(value: string): Promise<void> {
    console.log('Set Room 2 additional guest date of birth');
    await this.additionalDateOfBirthInput.fill(value);
  }

  /** Set the second room's additional-guest nationality and confirm the option. */
  async setNationalityRoom2(value: string): Promise<void> {
    console.log(`Set Room 2 additional guest nationality=${value}`);
    await this.setNationality(value);
  }

  /** Set the second room's additional-guest passport number. */
  async setPassportRoom2(value: string): Promise<void> {
    console.log('Set Room 2 additional guest passport number');
    await this.setPassport(value);
  }

  /** Confirm consent for the given 1-based room number. */
  async clickOnConsentRadioButtonForRoom(roomNumber: number): Promise<void> {
    console.log(`Click consent radio button for room ${roomNumber}`);
    const consentButton = this.page.locator(
      `div[data-testid="GuestDetails-leadGuest-RoomCheckbox-${roomNumber}"] label`
    );
    await consentButton.scrollIntoViewIfNeeded();
    await consentButton.click();
  }

  /** Confirm consent for the first room's lead guest. */
  async clickOnConsentRadioButtonRoom1(): Promise<void> {
    console.log('Click consent radio button for Room 1');
    await this.clickOnConsentRadioButtonForRoom(1);
  }

  /** Confirm consent for the second room's lead guest. */
  async clickOnConsentRadioButtonRoom2(): Promise<void> {
    console.log('Click consent radio button for Room 2');
    await this.clickOnConsentRadioButtonForRoom(2);
  }

  /** Confirm consent for the third room's lead guest. */
  async clickOnConsentRadioButtonRoom3(): Promise<void> {
    console.log('Click consent radio button for Room 3');
    await this.clickOnConsentRadioButtonForRoom(3);
  }

  /** Confirm consent for the fourth room's lead guest. */
  async clickOnConsentRadioButtonRoom4(): Promise<void> {
    console.log('Click consent radio button for Room 4');
    await this.clickOnConsentRadioButtonForRoom(4);
  }

  /** Scroll to the Continue button before interacting with it. */
  async scrollToContinueButton(): Promise<void> {
    console.log('Scroll to Continue button');
    await this.continueButton.scrollIntoViewIfNeeded();
  }

  /**
   * Click the "Enter address manually" link to reveal manual address fields.
   * Handles both the direct manual link and the postcode-lookup-first flow.
   */
  async selectManualAddressEntry(): Promise<void> {
    console.log('Selecting manual address entry');
    if (await this.postcodeSearchInput.isVisible()) return;

    if (global.browser?.options?.app === 'ccui' && await this.postcodeAddressOptions.first().isVisible()) {
      console.log('Select the first address suggestion from open postcode lookup');
      await this.postcodeAddressOptions.first().click();
      return;
    }

    const isManualLinkVisible = await this.yourAddressSection.manualAddressLink.isVisible();
    if (isManualLinkVisible) {
      await this.yourAddressSection.clickManualAddressLink();
    }
  }

  /**
   * Open the manual address form and fill all address fields.
   * @param address - GuestAddress object with postalCode, addressLines, city, countryCode
   */
  async fillAddress(address: GuestAddress): Promise<void> {
    console.log(`Filling address: ${address.addressLine1}, ${address.postalCode}`);

    if (!(await this.addressLine1Input.isVisible()) && await this.postcodeSearchInput.isVisible()) {
      if (await this.selectAddressFromPostcodeLookup(address)) {
        return;
      }
    }

    if (!(await this.addressLine1Input.isVisible()) && await this.manualAddressLink.isVisible()) {
      await this.selectManualAddressEntry();
    }

    await this.page.keyboard.press('Escape');
    if (await this.homeAddressRadio.isVisible()) {
      await this.homeAddressRadio.click({ force: true });
    }

    if (!(await this.addressLine1Input.isVisible()) && await this.manualAddressLink.isVisible()) {
      await this.selectManualAddressEntry();
    }

    if (!(await this.addressLine1Input.isVisible()) && await this.postcodeSearchInput.isVisible()) {
      await this.postcodeSearchInput.scrollIntoViewIfNeeded();
      await this.postcodeSearchInput.fill(address.postalCode);
      await this.findAddressButton.click();

      const postcodeAddressDropdownButton = this.postcodeAddressDropdownButton.or(
        this.page.getByRole('button', { name: new RegExp(this.escapeRegExp(address.postalCode), 'i') })
      ).first();
      const postcodeAddressOptions = this.firstPostcodeAddressOption.or(this.postcodeAddressOptions).or(
        this.page.getByRole('menuitem', { name: new RegExp(this.escapeRegExp(address.postalCode), 'i') })
      );

      if (await postcodeAddressDropdownButton.isVisible()) {
        console.log('Select the first address suggestion from postcode lookup');
        const isExpanded = await postcodeAddressDropdownButton.getAttribute('aria-expanded');
        if (isExpanded !== 'true') {
          await postcodeAddressDropdownButton.click();
        }
        await postcodeAddressOptions.first().click();
        return;
      }

      const manualLinkNow = await this.manualAddressLink.isVisible();
      if (manualLinkNow) {
        await this.selectManualAddressEntry();
      }
    }

    // Wait for address fields to be visible
    await this.addressLine1Input.waitFor({ state: 'visible', timeout: global.browser.options.actionTimeout });

    // Fill address fields
    await this.addressLine1Input.fill(address.addressLine1);
    await this.addressLine2Input.fill(address.addressLine2);
    await this.addressLine3Input.fill(address.addressLine3);
    // City field may not exist on all form versions
    if (await this.cityInput.isVisible()) {
      await this.cityInput.fill(address.cityName);
    }

    await this.selectCountry(address.countryCode);
    await this.postalCodeInput.fill(address.postalCode);
  }

  /**
   * Select the first address returned by the postcode lookup.
   * @param address - Guest address containing the postcode to search for.
   * @returns Whether an address suggestion was selected.
   */
  private async selectAddressFromPostcodeLookup(address: GuestAddress): Promise<boolean> {
    console.log('Select address from postcode lookup');
    await this.postcodeSearchInput.scrollIntoViewIfNeeded();
    await this.postcodeSearchInput.fill(address.postalCode);
    await this.findAddressButton.click();

    await this.postcodeAddressDropdownButton.waitFor({
      state: 'visible',
      timeout: global.browser.options.actionTimeout,
    });
    const isExpanded = await this.postcodeAddressDropdownButton.getAttribute('aria-expanded');
    if (isExpanded !== 'true') {
      await this.postcodeAddressDropdownButton.click();
    }
    await this.postcodeAddressOptions.first().waitFor({
      state: 'visible',
      timeout: global.browser.options.actionTimeout,
    });
    await this.postcodeAddressOptions.first().click();
    return true;
  }

  /**
   * Fill the entire guest details form: personal info, address, and reason for stay.
   * This is the high-level orchestrator that calls the individual section methods.
   * @param guest - Guest personal information (title, name, contact)
   * @param address - Guest address (manual entry)
   * @param reason - Reason for stay (leisure/business), defaults to 'leisure'
   */
  async fillGuestDetails(
    guest: GuestInfo,
    address: GuestAddress,
    reason: ReasonForStay = 'leisure'
  ): Promise<void> {
    console.log('Filling complete guest details form');
    // Select reason for stay first (it's at the top of the form)
    await this.selectReasonForStay(reason);

    // Fill booker personal details
    await this.fillBookerInformation(guest);

    // Fill address using manual entry
    await this.fillAddress(address);
  }

  /**
   * Fill the PI-supported fields from the legacy Guest Details payload.
   * @param data Guest details and staying-room data.
   */
  async setAllGuestDetailsFields({
    guestDetails,
    bookingForSomeoneElse = false,
    stayingInRoomNo,
  }: {
    guestDetails: GuestDetailsFormData;
    bookingForSomeoneElse?: boolean;
    stayingInRoomNo?: number;
  }): Promise<void> {
    console.log('Set all Guest Details fields');
    await this.fillGuestDetails(guestDetails.booker, guestDetails.booker.address, guestDetails.reasonForStay);
    const leadGuest = guestDetails.stayingGuests.find((guest) => !guest.sameAsBooker)?.stayingGuestDetails;
    if ((bookingForSomeoneElse || leadGuest) && stayingInRoomNo !== 1) {
      await this.clickBookingForSomeoneElseOption();
    }
    if (leadGuest) {
      const [container] = await this.leadGuestSection.getLeadGuestContainersArray();
      if (!container) {
        throw new Error('Lead guest container was not displayed after selecting booking for someone else.');
      }
      await container.fillLeadGuestDetails(leadGuest);
    }
  }

  /**
   * Read back the app-normalised values from the form AFTER filling.
   * The app normalises/auto-fills certain fields (phone prefixes, address lines
   * from postcode lookup, title display value). These normalised values must be
   * used for later assertions (e.g. booking confirmation cross-validation) instead
   * of the originally typed values which may differ.
   */
  async readNormalisedGuestDetails(): Promise<NormalisedGuestDetails> {
    console.log('Reading normalised guest details from form');
    // Read title from the dropdown display value
    const title = await this.titleValueLabel.textContent() ?? '';

    // Read personal info from input values
    const firstName = await this.firstNameInput.inputValue();
    const lastName = await this.lastNameInput.inputValue();
    const email = await this.emailInput.inputValue();

    // Read phone prefixes (auto-detected by the app based on number format)
    const mobilePrefix = await this.mobileCountrySelectorPrefixLabel.textContent() ?? '';
    const mobile = await this.mobileInput.inputValue();

    // Landline prefix and value — may not be visible
    let landlinePrefix = '';
    let landline = '';
    if (await this.landlineCountrySelectorPrefixLabel.isVisible()) {
      landlinePrefix = await this.landlineCountrySelectorPrefixLabel.textContent() ?? '';
    }
    if (await this.landlineInput.isVisible()) {
      landline = await this.landlineInput.inputValue();
    }

    // Read address fields (these may have been normalised by postcode lookup)
    const addressLine1 = await this.addressLine1Input.inputValue();
    const addressLine2 = await this.addressLine2Input.inputValue();
    const addressLine3 = await this.addressLine3Input.inputValue();
    const postalCode = await this.postalCodeInput.inputValue();

    return {
      title: title.trim(),
      firstName,
      lastName,
      email,
      mobilePrefix: mobilePrefix.trim(),
      mobile,
      landlinePrefix: landlinePrefix.trim(),
      landline,
      addressLine1,
      addressLine2,
      addressLine3,
      postalCode,
    };
  }

  /**
   * Expand the booking summary on mobile viewports by clicking the toggle header.
   * On desktop viewports this is a no-op (the summary is always visible).
   */
  async expandBookingSummaryOnMobile(): Promise<void> {
    console.log('Expanding booking summary on mobile if needed');
    const isExpandButtonVisible = await this.bookingSummaryMobileExpandButton.isVisible();
    if (isExpandButtonVisible) {
      await this.bookingSummaryMobileExpandButton.scrollIntoViewIfNeeded();
      await this.bookingSummaryMobileExpandButton.click();
      await this.bookingSummaryContainer.waitFor({ state: 'visible', timeout: global.browser.options.actionTimeout });
    }
  }

  /**
   * Click Continue to submit guest details and proceed to the Payment page.
   */
  async clickContinueToPayment(): Promise<void> {
    console.log('Clicking continue to payment');
    await this.continueButton.scrollIntoViewIfNeeded();
    await this.continueButton.click();
  }

  /**
   * @deprecated Use clickContinueToPayment() instead. Kept for backward compatibility.
   */
  async clickContinue(): Promise<void> {
    console.log('Clicking continue (deprecated, use clickContinueToPayment)');
    await this.clickContinueToPayment();
  }

  /** Click the Back to Ancillaries control when the PI journey exposes it. */
  async clickBackToAncillariesButton(): Promise<void> {
    console.log('Click Back to Ancillaries Button');
    await this.backToAncillariesButton.scrollIntoViewIfNeeded();
    await this.backToAncillariesButton.click();
  }

  /** Resolve a legacy title code to its localized display value. */
  private async getLocalizedTitle(title: string): Promise<string> {
    const titleStrings: Record<string, Promise<string>> = {
      Mr: Strings.MR_TITLE.name,
      Miss: Strings.MISS_TITLE.name,
      Master: Strings.MASTER_TITLE.name,
      Lord: Strings.LORD_TITLE.name,
    };

    return titleStrings[title] ?? title;
  }

  /** Escape regular-expression characters in a value used for an exact locator match. */
  private escapeRegExp(value: string): string {
    return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  }

  /** Select the address country when the requested country differs from the current value. */
  private async selectCountry(countryCode: string): Promise<void> {
    console.log(`Select country=${countryCode}`);
    if (!(await this.countryCodeSelect.isVisible())) {
      return;
    }

    const countryName = await this.getCountryName(countryCode);
    const countrySelector = this.page.locator('div[data-testid="GuestDetails-CountrySelector"]');
    const currentCountry = await this.countryNameInput.isVisible() ? await this.countryNameInput.inputValue() : '';
    if (currentCountry === countryName) {
      return;
    }

    await this.countryCodeSelect.click();
    const countryImageName = countryName.split(' (')[0];
    let countryOption = countrySelector.locator(`li:has(img[alt*="${countryImageName}"])`).first();
    if (!(await countryOption.isVisible())) {
      const countrySearchInput = countrySelector.locator('input:not([readonly])').first();
      await countrySearchInput.fill(countryName);
      countryOption = countrySelector.locator(`li:has(img[alt*="${countryImageName}"])`).first();
    }
    await countryOption.click();
  }

  /** Resolve a supported country code to its localized country name. */
  private async getCountryName(countryCode: string): Promise<string> {
    const countryNames: Record<string, Promise<string>> = {
      GB: Strings.UNITED_KINGDOM_THE.name,
      gb: Strings.UNITED_KINGDOM_THE.name,
      uk: Strings.UNITED_KINGDOM_THE.name,
      DE: Strings.GERMANY.name,
      de: Strings.GERMANY.name,
    };

    return countryNames[countryCode] ?? countryCode;
  }

  // ######## UI validations ########

  /** Validate the locale-specific default enabled state of additional guest fields. */
  async validateDefaultViewOfAdditionalFields(): Promise<void> {
    console.log('Validate default view of additional guest fields');
    const isEnglishWebsite = global.browser?.options?.locale === Locales.GB_EN.name;
    if (isEnglishWebsite) {
      await expect(this.stayingGuestDateOfBirthInput.first(), 'The DOB field should be disabled on English sites.').toBeDisabled();
      await expect(this.stayingGuestNationalityInput.first(), 'The Nationality dropdown should be disabled on English sites.').toBeDisabled();
      return;
    }

    await expect(this.stayingGuestDateOfBirthInput.first(), 'The DOB field should be enabled on non-English sites.').toBeEnabled();
    await expect(this.stayingGuestNationalityInput.first(), 'The Nationality dropdown should be enabled on non-English sites.').toBeEnabled();
  }

  /** Validate that the privacy notice appears above the reason-for-stay controls. */
  async validatePrivacyNoticeIsAboveReasonForStaySection(): Promise<void> {
    console.log('Validate privacy notice is above reason-for-stay section');
    await UiUtils.validateIsBelow({
      belowElement: this.reasonForStaySection.reasonForStayGroup,
      aboveElement: this.privacyNoticeSection.privacyNoticeContainer,
      maxDistanceBetween: 300,
      elementDescription: 'Reason For Stay Group',
    });
  }

  /** Validate that the sign-in section appears above the reason-for-stay controls. */
  async validateSignInSectionIsAboveReasonForStaySection(): Promise<void> {
    console.log('Validate sign-in section is above reason-for-stay section');
    await UiUtils.validateIsBelow({
      belowElement: this.reasonForStaySection.reasonForStayGroup,
      aboveElement: this.signInSection.signInContainer,
      maxDistanceBetween: 600,
      elementDescription: 'Reason For Stay Group',
    });
  }

  /** Validate that reason-for-stay appears above the booker-information controls. */
  async validateReasonForStaySectionIsAboveYourDetailsTitle(): Promise<void> {
    console.log('Validate reason-for-stay section is above your details title');
    await UiUtils.validateIsBelow({
      belowElement: this.bookerInfoSection,
      aboveElement: this.reasonForStaySection.reasonForStayGroup,
      maxDistanceBetween: 300,
      elementDescription: 'Reason for Stay',
    });
  }

  /** Validate that Continue appears above Back to Ancillaries. */
  async validateContinueButtonIsAboveBackToAncillaries(): Promise<void> {
    console.log('Validate Continue button is above Back to Ancillaries');
    await UiUtils.validateIsBelow({
      belowElement: this.backToAncillariesButton,
      aboveElement: this.continueButton,
      maxDistanceBetween: 200,
      elementDescription: 'Back To Ancillaries Button',
    });
  }

  /** Validate the Back to Ancillaries control is visible and labelled Back. */
  async validateBackToAncillariesButton(): Promise<void> {
    console.log('Validate Back to Ancillaries button');
    await UiUtils.validateButton({
      element: this.backToAncillariesButton,
      buttonLabel: await Strings.BACK.name,
      isDisplayed: true,
      hasText: true,
    });
  }

  /** Validate a title is present in a supplied title-dropdown list. */
  async validateTitleExists({ titleName = 'Mx', listOfTitles }: { titleName?: string; listOfTitles: string[] }): Promise<void> {
    console.log(`Validate title exists=${titleName}`);
    const titleExists = listOfTitles.includes(titleName);
    if (!titleExists) {
      throw new Error(`${titleName} does not exist in Title dropdown.`);
    }
  }

  /** Validate that a title immediately follows another title in a supplied title-dropdown list. */
  async validateTitleIsBelowOtherTitle({
    titleName = 'Mx',
    otherTitleName = 'Miss',
    listOfTitles,
  }: { titleName?: string; otherTitleName?: string; listOfTitles: string[] }): Promise<void> {
    console.log(`Validate title ${titleName} is below ${otherTitleName}`);
    if (listOfTitles.indexOf(titleName) !== listOfTitles.indexOf(otherTitleName) + 1) {
      throw new Error(`${titleName} is not after ${otherTitleName}.`);
    }
  }

  /** Validate a title's index in a supplied title-dropdown list. */
  async validateTitlePositionByIndex({
    titleName = 'Divers',
    index,
    listOfTitles,
  }: { titleName?: string; index?: number; listOfTitles: string[] }): Promise<void> {
    console.log(`Validate title position for ${titleName}`);
    const expectedIndex = index ?? listOfTitles.length - 1;
    if (listOfTitles.indexOf(titleName) !== expectedIndex) {
      throw new Error(`${titleName} is not at position ${expectedIndex} in the title dropdown.`);
    }
  }

  /**
   * Validate that the Guest Details page has loaded by waiting
   * for the page loaded indicator to be visible.
   */
  async validatePage(): Promise<void> {
    console.log('Validating guest details page loaded');
    await this.pageLoadedIndicator.waitFor({ state: 'visible', timeout: 30000 });
  }
}

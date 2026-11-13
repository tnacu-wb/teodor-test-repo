import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { Strings } from "../../../test-data/strings";
/** InnBusiness application > Pay App > Payment details section direct debit iframe */
export class DirectDebitIframeSectionComponent {
  private readonly page: Page = global.page;
  static readonly DIRECT_DEBIT_IFRAME =
    'iframe[data-testid="DirectDebit-Iframe"]';
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly directDebitFrame = this.page.frameLocator(
    DirectDebitIframeSectionComponent.DIRECT_DEBIT_IFRAME,
  );
  readonly accountNameInput: Locator = this.page.locator( 'input[id="txtBnkAccountName"], input[id="txtBankAccountName"]', );
  readonly accountNumberInput: Locator = this.page.locator( 'input[id="txtBnkAccountNumber"]', );
  readonly sortCode1Input: Locator = this.page.locator( 'input[id="txtBnkSortCode1"]', );
  readonly sortCode2Input: Locator = this.page.locator( 'input[id="txtBnkSortCode2"]', );
  readonly sortCode3Input: Locator = this.page.locator( 'input[id="txtBnkSortCode3"]', );
  readonly bankNameInput: Locator = this.page.locator( 'input[id="txtBnkBankName"]', );
  readonly address1Input: Locator = this.page.locator('input[id="txtAddr1"]');
  readonly address2Input: Locator = this.page.locator('input[id="txtAddr2"]');
  readonly address3Input: Locator = this.page.locator('input[id="txtAddr3"]');
  readonly address4Input: Locator = this.page.locator('input[id="txtAddr4"]');
  readonly postcodeInput: Locator = this.page.locator( 'input[id="txtPostcode"], input[id="txtBankPostCode"]', );
  readonly bankBicInput: Locator = this.page.locator('input[id="txtBIC"]');
  readonly bankIbanInput: Locator = this.page.locator('input[id="txtIBAN"]');
  readonly signatureInput: Locator = this.page.locator( 'input[id="txtSignature"]', );
  readonly signaturePlaceInput: Locator = this.page.locator( 'input[id="txtSignaturePlace"]', );
  readonly lookupBankButton: Locator = this.page.locator("#btnBankLookup");
  readonly submitButton: Locator = this.page.locator( "#btnDDDetails2Next, div.col-md-4.text-end > button", );
  readonly directDebitDetailsLabel: Locator = this.page.locator( "h2.SubSectionHeader, div.col-12 > h2", );
  readonly notificationHeaderLabel: Locator = this.page.locator( "#lblNotificationHeader", );
  readonly notificationMessageLabel: Locator = this.page.locator( "#lblNotificationMsg", );
  // ######## UI actions/navigation ########
  /** Switch to Direct Debit iFrame. */
  async switchToDirectDebitIFrame(): Promise<void> {
    console.log("Switch to Direct Debit iFrame");
    await expect( this.directDebitFrame.locator("body"), "Direct Debit iFrame", ).toBeVisible();
  }
  /** Switch to Direct Debit parent frame. */
  async switchToDirectDebitParentFrame(): Promise<void> {
    console.log("Switch to Direct Debit parent frame");
  }
  /** Set account name. */
  async setAccountName(accountName: string): Promise<void> {
    console.log("Set account name");
    await this.accountNameInput.fill(accountName);
  }
  /** Set account number. */
  async setAccountNumber(accountNumber: string): Promise<void> {
    console.log("Set account number");
    await this.accountNumberInput.fill(accountNumber);
  }
  /** Set sort code. */
  async setSortCode(sortCode: string): Promise<void> {
    console.log("Set sort code");
    await this.sortCode1Input.fill(sortCode.slice(0, 2));
    await this.sortCode2Input.fill(sortCode.slice(2, 4));
    await this.sortCode3Input.fill(sortCode.slice(4, 6));
  }
  /** Set bank name. */
  async setBankName(bankName: string): Promise<void> {
    console.log("Set bank name");
    await this.bankNameInput.fill(bankName);
  }
  /** Set address 1. */
  async setAddr1(address1: string): Promise<void> {
    console.log("Set address 1");
    await this.address1Input.fill(address1);
  }
  /** Set address 2. */
  async setAddr2(address2: string): Promise<void> {
    console.log("Set address 2");
    await this.address2Input.fill(address2);
  }
  /** Set address 3. */
  async setAddr3(address3: string): Promise<void> {
    console.log("Set address 3");
    await this.address3Input.fill(address3);
  }
  /** Set address 4. */
  async setAddr4(address4: string): Promise<void> {
    console.log("Set address 4");
    await this.address4Input.fill(address4);
  }
  /** Set postcode. */
  async setPostcode(postcode: string): Promise<void> {
    console.log("Set postcode");
    await this.postcodeInput.fill(postcode);
  }
  /** Click bank lookup. */
  async clickBankLookup(): Promise<void> {
    console.log("Click bank lookup");
    await this.lookupBankButton.click();
  }
  /** Click submit. */
  async clickSubmit(): Promise<void> {
    console.log("Click direct debit submit");
    await this.submitButton.click();
  }
  /** Set and submit direct debit fields. */
  async setAndSubmitDirectDebitFields({
    accountName,
    accountNumber,
    sortCode,
  }: {
    accountName: string;
    accountNumber: string;
    sortCode: string;
  }): Promise<void> {
    console.log("Set and submit Direct Debit fields");
    await this.setAccountName(accountName);
    await this.setAccountNumber(accountNumber);
    await this.setSortCode(sortCode);
    await this.clickBankLookup();
    await this.clickSubmit();
  }
  /** Set and submit DE direct debit fields. */
  async setAndSubmitDirectDebitFieldsForDE({
    accountName,
    bankAddress,
    bankAccountNumber,
  }: {
    accountName: string;
    bankAddress: {
      addressLine1: string;
      addressLine2: string;
      addressLine3: string;
      postalCode: string;
      cityName: string;
    };
    bankAccountNumber: string;
  }): Promise<void> {
    console.log("Set and submit DE Direct Debit fields");
    await this.setAccountName(accountName);
    await this.setAddr1(bankAddress.addressLine1);
    await this.setAddr2(bankAddress.addressLine2);
    await this.setPostcode(bankAddress.postalCode);
    await this.setAddr3(bankAddress.addressLine3);
    await this.bankBicInput.fill(bankAccountNumber.slice(0, 8));
    await this.bankIbanInput.fill(bankAccountNumber.slice(8));
    await this.signaturePlaceInput.fill(bankAddress.cityName);
    await this.clickSubmit();
  }
  // ######## UI validations ########
  /** Validate Submit button enabled state. */
  async validateSubmitButton({
    isEnabled = false,
  }: { isEnabled?: boolean } = {}): Promise<void> {
    console.log("Validate Submit button");
    if (isEnabled)
      await expect(this.submitButton, "Submit button").toBeEnabled();
    else await expect(this.submitButton, "Submit button").toBeDisabled();
  }
  /** Validate Direct debit section visibility. */
  async validateDirectDebitSectionIsDisplayed({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate Direct debit section");
    if (isDisplayed)
      await expect( this.directDebitDetailsLabel, "Direct Debit section header", ).toBeVisible();
    else
      await expect( this.directDebitDetailsLabel, "Direct Debit section header", ).not.toBeVisible();
  }
  /** Validate bank lookup notification. */
  async validateBankDetailsNotificationMessage(
    isSuccessful = true,
  ): Promise<void> {
    console.log("Validate bank details notification");
    await expect( this.notificationHeaderLabel, "Bank details notification header", ).toHaveText(await (isSuccessful ? Strings.SUCCESS : Strings.ERROR).name);
  }
}

import { expect, type Locator } from "@playwright/test";
import { Constants } from "@test-data/constants";
import { Strings } from "@test-data/strings";

/** BB payment type section containing stored cards, employee questions, authorisation, and allowances. */
export class PaymentTypeSectionComponent {
  // ######## UI elements/properties ########
  readonly paymentTypeContainer: Locator = global.page.locator( '[data-testid="PaymentType-Container"]', );
  readonly paymentNewCreditDebitCardRadioButton: Locator = this.cardRadio( "CARD", Strings.NEW_CREDIT_DEBIT_CARD, );
  readonly paymentNewBusinessAccountCardRadioButton: Locator = global.page
    .locator('p[data-testid="payment-type-method_option-text-PIBA"]')
    .filter({ hasText: /New (Business Account Card|Premier Inn Business Pay Card)|Neue (Geschäftskontokarte|Premier Inn Business Pay-Karte)/ })
    .locator("xpath=ancestor::label/span")
    .first();
  readonly referenceDetailsContainer: Locator = global.page.locator( '[data-testid="EmployeeQuestions-Container"]', );
  readonly referenceDetailsLabel: Locator = global.page.locator( '[data-testid="EmployeeQuestions-Title"]', );
  readonly yourReferenceInput: Locator = global.page.locator( 'input[data-testid="input-EmployeeQuestions-8"]', );
  readonly purchaseOrderNumberInput: Locator = global.page.locator( 'input[data-testid="input-EmployeeQuestions-9"]', );
  readonly firstEmployeeQuestionInput: Locator = global.page .locator('input[data-testid*="input-EmployeeQuestions-"]') .nth(0);
  readonly secondEmployeeQuestionInput: Locator = global.page .locator('input[data-testid*="input-EmployeeQuestions-"]') .nth(1);
  readonly firstDropdownEmployeeQuestionButton: Locator = global.page .locator('button[data-testid*="DropdownComp-EmployeeQuestions-"]') .nth(0);
  readonly firstDropdownEmployeeQuestionAnswerButton: Locator = global.page .locator('button[data-testid*="DropdownComp-EmployeeQuestions-"]') .nth(1);
  readonly paymentAuthorizationContainer: Locator = global.page.locator( '[data-testid="PaymentAuth-Container"]', );
  readonly paymentAuthorizationLabel: Locator = global.page.locator( '[data-testid="PaymentAuth-PaymentAuthTitle"]', );
  readonly paymentAuthorizationCheckbox: Locator = global.page.locator( '[data-testid="PaymentAuth-CheckboxInfo"]', );
  readonly paymentAuthorizationPasswordInput: Locator = global.page.locator( 'label[data-testid="input-password-label"] + div input', );
  readonly paymentAuthorizationCheckboxLabel: Locator = global.page.locator( '[data-testid="PaymentAuth-CheckboxLabel"]', );
  readonly businessAllowancesLabel: Locator = global.page.locator( '[data-testid="BusinessAllowances-Title"]', );
  readonly includeMyDinnerAllowanceLabel: Locator = global.page.locator( '[data-testid="BusinessAllowances-ToggleDinnerAllowance-Label"]', );
  readonly includeMyDinnerAllowanceCheckbox: Locator = global.page.locator( '[data-testid="BusinessAllowances-ToggleDinnerAllowance"]', );
  readonly otherAllowancesLabel: Locator = global.page.locator( '[data-testid="BusinessAllowances-OtherAllowances"]', );
  readonly carParkingLabel: Locator = global.page.locator( '[data-testid="BusinessAllowances-CarParkingLabel"]', );
  readonly totalDinnerBudgetLabel: Locator = global.page.locator( '[data-testid="input-totalDinnerBudgetPersonNight-label"]', );
  readonly totalDinnerBudgetInput: Locator = global.page.locator( '[data-testid="input-totalDinnerBudgetPersonNight"]', );
  readonly alcoholWithDinnerCheckbox: Locator = global.page.locator( '[data-testid="BusinessAllowances-ToggleAlcoholDinner"]', );
  readonly carParkingCheckbox: Locator = global.page.locator( '[data-testid="BusinessAllowances-CarParkingCheckbox"]', );

  private cardRadio(
    cardType: string,
    text: { data: { default?: string } },
  ): Locator {
    return global.page
      .locator(`p[data-testid="payment-type-method_option-text-${cardType}"]`)
      .filter({ hasText: text.data.default ?? "" })
      .locator("xpath=ancestor::label/span")
      .first();
  }

  /** Return a stored-card label for the requested payment card type. */
  getStoredCardLabel(
    cardType = Constants.CARD_TYPE_AC,
    storedCard = Strings.CENTRALLY_SAVED_CARD,
  ): Locator {
    return global.page
      .locator(`p[data-testid="payment-type-method_option-text-${cardType}"]`)
      .filter({ hasText: storedCard.data.default ?? "" });
  }

  /** Return a stored-card radio for the requested payment card type. */
  getStoredCardRadio(
    cardType = Constants.CARD_TYPE_AC,
    storedCard = Strings.CENTRALLY_SAVED_CARD,
  ): Locator {
    return this.getStoredCardLabel(cardType, storedCard)
      .locator("xpath=ancestor::label/span")
      .first();
  }

  // ######## UI actions/navigation ########
  /** Select a new credit/debit card. */
  async selectNewCreditDebitCardRadioButton(): Promise<void> {
    console.log("Click on New Credit / Debit Card");
    await this.paymentNewCreditDebitCardRadioButton.click();
  }
  /** Select a new business account card. */
  async selectNewBusinessAccountCardButton(): Promise<void> {
    console.log("Click on New Business Account Card");
    await this.paymentNewBusinessAccountCardRadioButton.click();
  }
  /** Select a centrally or personally stored card. */
  async selectStoredCardByLabel(
    storedCard = Strings.CENTRALLY_SAVED_CARD,
    cardType = Constants.CARD_TYPE_AC,
  ): Promise<void> {
    const expected = await storedCard.name;
    console.log(`Click on ${expected} by label`);
    if (
      expected !== (await Strings.CENTRALLY_SAVED_CARD.name) &&
      expected !== (await Strings.PERSONAL_STORED_CARD.name)
    )
      throw new Error(`Payment type with value ${expected} is not available`);
    await this.getStoredCardRadio(cardType, storedCard).click();
  }
  /** Set an employee reference field. */
  async setYourReferenceInput(value: string): Promise<void> {
    console.log("Set value for Your Reference field");
    await this.yourReferenceInput.fill(value);
  }
  /** Set the purchase order number. */
  async setPurchaseOrderNumber(value: string): Promise<void> {
    console.log("Set value for Purchase Order Number field");
    await this.purchaseOrderNumberInput.fill(value);
  }
  /** Set the BB reference-details fields. */
  async setReferenceDetails(data: {
    yourReferenceInput?: string;
    purchaseOrderNumberInput?: string;
    setReferenceInput?: boolean;
    setPurchaseOrder?: boolean;
  }): Promise<void> {
    console.log("Set values for Reference Details Section");
    if (
      data.setReferenceInput !== false &&
      data.yourReferenceInput !== undefined
    )
      await this.setYourReferenceInput(data.yourReferenceInput);
    if (
      data.setPurchaseOrder !== false &&
      data.purchaseOrderNumberInput !== undefined
    )
      await this.setPurchaseOrderNumber(data.purchaseOrderNumberInput);
  }
  /** Set the first employee question answer. */
  async setFirstEmployeeQuestion(value: string): Promise<void> {
    console.log("Set value to first employee questions input");
    await this.firstEmployeeQuestionInput.fill(value);
  }
  /** Set the second employee question answer. */
  async setSecondEmployeeQuestion(value: string): Promise<void> {
    console.log("Set value to second employee questions input");
    await this.secondEmployeeQuestionInput.fill(value);
  }
  /** Select the first employee-question dropdown answer. */
  async selectFirstDropdownEmployeeQuestion(): Promise<void> {
    console.log("Select first value to first dropdown employee question input");
    await this.firstDropdownEmployeeQuestionButton.click();
    await this.firstDropdownEmployeeQuestionAnswerButton.click();
  }
  /** Set payment authorisation password or memorable word. */
  async setPaymentAuthorizationPasswordInput(value = ""): Promise<void> {
    console.log("Set value to Payment Authorization Password input");
    await this.paymentAuthorizationPasswordInput.fill(value);
  }
  /** Set the requested allowance checkbox state. */
  async selectCheckbox(
    checkbox: Locator,
    shouldBeChecked = true,
    name = "allowance",
  ): Promise<void> {
    console.log(
      `${shouldBeChecked ? "Checking" : "Unchecking"} ${name} checkbox`,
    );
    const input = checkbox.locator("input").first();
    if ((await input.isChecked()) !== shouldBeChecked) await checkbox.click();
  }
  /** Select the dinner allowance checkbox. */
  async selectIncludeMyDinnerAllowanceCheckbox(value = true): Promise<void> {
    await this.selectCheckbox(
      this.includeMyDinnerAllowanceCheckbox,
      value,
      "Include My Dinner Allowance",
    );
  }
  /** Select the alcohol allowance checkbox. */
  async selectAlcoholWithDinnerAllowanceCheckbox(value = true): Promise<void> {
    await this.selectCheckbox(
      this.alcoholWithDinnerCheckbox,
      value,
      "Alcohol With Dinner Allowance",
    );
  }
  /** Select the car parking allowance checkbox. */
  async selectCarParkingAllowanceCheckbox(value = true): Promise<void> {
    await this.selectCheckbox(
      this.carParkingCheckbox,
      value,
      "Car Parking Allowance",
    );
  }
  /** Select payment authorisation. */
  async selectPaymentAuthorizationCheckbox(value = true): Promise<void> {
    await this.selectCheckbox(
      this.paymentAuthorizationCheckbox,
      value,
      "Payment Authorization",
    );
  }
  /** Set the dinner budget per person per night. */
  async setTotalDinnerBudgetPerPersonPerNightInput(
    value: string,
  ): Promise<void> {
    console.log("Set value to Total Dinner Budget Per Person Per Night input");
    await this.totalDinnerBudgetInput.fill(value);
  }

  // ######## UI validations ########
  /** Validate a payment radio selection. */
  async validateRadioSelected(
    radio: Locator,
    isSelected: boolean,
    description: string,
  ): Promise<void> {
    console.log(`Validate ${description} selected=${isSelected}`);
    await expect(radio, description).toBeChecked({ checked: isSelected });
  }
  /** Validate the new credit/debit card selection. */
  async validateNewCreditDebitCardIsSelected(
    isSelected: boolean,
  ): Promise<void> {
    await this.validateRadioSelected(
      this.paymentNewCreditDebitCardRadioButton,
      isSelected,
      "New Credit Debit Card selection",
    );
  }
  /** Validate the new business account card selection. */
  async validateNewBusinessAccountCardIsSelected(
    isSelected: boolean,
  ): Promise<void> {
    await this.validateRadioSelected(
      this.paymentNewBusinessAccountCardRadioButton,
      isSelected,
      "New Business Account Card selection",
    );
  }
  /** Validate stored-card selection. */
  async validateStoredCardByLabelIsSelected(
    isSelected: boolean,
    cardType = Constants.CARD_TYPE_AC,
    storedCard = Strings.CENTRALLY_SAVED_CARD,
  ): Promise<void> {
    await this.validateRadioSelected(
      this.getStoredCardRadio(cardType, storedCard),
      isSelected,
      `${await storedCard.name} selection`,
    );
  }
  /** Validate that a personal stored-card option is displayed. */
  async validatePersonalStoredCardIsDisplayed(
    cardType = Constants.CARD_TYPE_AC,
  ): Promise<void> {
    console.log("Validate that Personal Stored Card option is displayed");
    await expect( this.getStoredCardLabel(cardType, Strings.PERSONAL_STORED_CARD), "Personal stored card option", ).toBeVisible();
  }
  /** Validate the dinner budget label and value. */
  async validateTotalDinnerBudgetPerPersonPerNight(
    value: string | number,
  ): Promise<void> {
    console.log(`Validate Total Dinner Budget Per Person Per Night=${value}`);
    await expect( this.totalDinnerBudgetLabel, "Total dinner budget label", ).toContainText(await Strings.TOTAL_DINNER_BUDGET.name);
    await expect( this.totalDinnerBudgetInput, "Total dinner budget value", ).toHaveValue(String(value));
  }
  /** Validate the BB business allowance labels and controls. */
  async validateBusinessAllowance(): Promise<void> {
    console.log("Validate the Business Allowance section labels are displayed");
    await expect( this.businessAllowancesLabel, "Business Allowances label", ).toHaveText(await Strings.BUSINESS_ALLOWANCES.name);
    await expect( this.includeMyDinnerAllowanceLabel, "Dinner allowance label", ).toHaveText(await Strings.INCLUDE_MY_DINNER_ALLOWANCE.name);
    await expect( this.otherAllowancesLabel, "Other allowances label", ).toHaveText(await Strings.OTHER_ALLOWANCES.name);
    await expect(this.carParkingLabel, "Car parking label").toHaveText( await Strings.CAR_PARKING.name, );
  }
  /** Validate payment reference details. */
  async validatePaymentReferenceDetails(isDisplayed = true): Promise<void> {
    console.log(`Validate payment reference details displayed=${isDisplayed}`);
    if (isDisplayed) {
      await expect( this.referenceDetailsContainer, "Payment reference details container", ).toBeVisible();
      await expect( this.referenceDetailsLabel, "Payment reference details title", ).toHaveText(await Strings.REFERENCES.name);
    } else
      await expect( this.referenceDetailsContainer, "Payment reference details container", ).toBeHidden();
  }
  /** Validate payment authorisation details. */
  async validatePaymentAuthorization(isDisplayed = true): Promise<void> {
    console.log(`Validate payment authorization displayed=${isDisplayed}`);
    if (isDisplayed) {
      await expect( this.paymentAuthorizationContainer, "Payment authorization container", ).toBeVisible();
      await expect( this.paymentAuthorizationLabel, "Payment authorization title", ).toHaveText(await Strings.PAYMENT_AUTHORISATION.name);
    } else
      await expect( this.paymentAuthorizationContainer, "Payment authorization container", ).toBeHidden();
  }
}

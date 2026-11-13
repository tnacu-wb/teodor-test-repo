import { expect, type Locator } from "@playwright/test";
import {
  AccountHolderSectionComponent,
  FooterSectionComponent,
  MenuContainerComponent,
  NotificationsSectionComponent,
} from "../../components/pib";
import { Constants } from "../../test-data/constants";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

interface CurrencyAmount {
  amount: number | string;
  currencySymbol: string;
}
export interface StatementInvoice {
  statementDate: string;
  invoiceNo: string;
  broughtForward: CurrencyAmount;
  paymentsReceived: CurrencyAmount;
  overdueBalance: CurrencyAmount;
  invoiceValue: CurrencyAmount;
  statementBalance: CurrencyAmount;
}
export interface StatementsInvoices {
  invoices: StatementInvoice[];
}
export interface PaymentInfo {
  paymentDate: string;
  paymentDescription: string;
  paymentFailed: boolean;
  paymentValue: { value: number | string; currencySymbol: string };
}
export interface StatementsSectionInput {
  accountStatementValue: { currencyCode: string; value: number | string };
  currentBalanceItem: {
    interimPayments: CurrencyAmount;
    outstanding: CurrencyAmount;
  };
}
const formatAmount = ({ amount, currencySymbol }: CurrencyAmount): string =>
  `${currencySymbol}${Number(amount).toFixed(2)}`;

/** IB Statements, Invoices and Payments page */
export class StatementsInvoicesAndPaymentsPage extends BasePibPage {
  // ######## UI elements/properties ########
  readonly titleLabel: Locator = this.page.getByTestId( "StatementsInvoices-Title", );
  readonly paymentsSectionTitleLabel: Locator = this.page.getByText(/Payments|Zahlungen|Zahlung/i);
  readonly paymentsTable: Locator = this.page.getByTestId( "PaymentsTable-DataTableClient", );
  readonly paymentsTableHeaderLabels: Locator = this.page.locator( 'tr[data-testid="PaymentsTable-DataTableClient-row"] th', );
  readonly noPaymentsLabel: Locator = this.page.locator( 'tr[data-testid="PaymentsTable-NoResults-container"] div', );
  readonly statementsTitleLabel: Locator = this.page.locator( '//div[@data-testid="StatementsInvoices-StatementsTotal"]/parent::node()/h2', );
  readonly statementsTooltipIcon: Locator = this.page.getByTestId( "StatementsInvoices-StatementsTitle-title-icon", );
  readonly statementsTooltipLabel: Locator = this.page.locator( '//div[@data-testid="StatementsInvoices-StatementsTitle-InfoTooltip"]/span[1]', );
  readonly latestStatementValueLabel: Locator = this.page.locator( '//div[@data-testid="StatementsInvoices-StatementsTotal"]/div[1]/span[1]/span', );
  readonly latestStatementAmountValueLabel: Locator = this.page.locator( '//div[@data-testid="StatementsInvoices-StatementsTotal"]/div[1]/span[2]', );
  readonly latestStatementValueTooltipIcon: Locator = this.page.getByTestId( "StatementsInvoices-StatementsTotal-Statements-LastValue-title-icon", );
  readonly latestStatementValueTooltipLabel: Locator = this.page.locator( '//div[@data-testid="StatementsInvoices-StatementsTotal-Statements-LastValue-InfoTooltip"]/span[1]', );
  readonly interimPaymentsLabel: Locator = this.page.locator( '//div[@data-testid="StatementsInvoices-StatementsTotal"]/div[2]/span[1]/span', );
  readonly interimPaymentsAmountLabel: Locator = this.page.locator( '//div[@data-testid="StatementsInvoices-StatementsTotal"]/div[2]/span[2]', );
  readonly interimPaymentsTooltipIcon: Locator = this.page.getByTestId( "StatementsInvoices-StatementsTotal-Statements-TotalValue-title-icon", );
  readonly interimPaymentsTooltipLabel: Locator = this.page.locator( '//div[@data-testid="StatementsInvoices-StatementsTotal-Statements-TotalValue-InfoTooltip"]/span[1]', );
  readonly outstandingBalanceLabel: Locator = this.page.locator( '//div[@data-testid="StatementsInvoices-StatementsTotal"]/div[3]/span[1]/span', );
  readonly outstandingBalanceAmountLabel: Locator = this.page.locator( '//div[@data-testid="StatementsInvoices-StatementsTotal"]/div[3]/span[2]', );
  readonly outstandingBalanceTooltipIcon: Locator = this.page.getByTestId( "StatementsInvoices-StatementsTotal-Statements-Balance-title-icon", );
  readonly outstandingBalanceTooltipLabel: Locator = this.page.locator( '//div[@data-testid="StatementsInvoices-StatementsTotal-Statements-Balance-InfoTooltip"]/span[1]', );
  readonly makeAPaymentButton: Locator = this.page.locator( 'button[data-testid="StatementsInvoices-StatementsTotal-Make-a-payment-Button"], button[data-testid="Notifications-AccountSuspended-Inline-Make-a-payment-Button"]', );
  readonly statementsTable: Locator = this.page.getByTestId( "InnBusiness-DataTable", );
  readonly statementsTableHeaderLabels: Locator = this.page.locator( 'tr[data-testid="InnBusiness-DataTable-row"] th', );
  readonly noStatementHistoryLabel: Locator = this.page.locator( 'tr[data-testid="NoResultsStatements-container"] div', );
  readonly paginationNextButton: Locator = this.page .locator('img[alt="pagination-next"]') .locator("..");
  readonly paginationPreviousButton: Locator = this.page .locator('img[alt="pagination-previous"]') .locator("..");
  readonly accountHolderSection = new AccountHolderSectionComponent();
  readonly notifications = new NotificationsSectionComponent();
  readonly menuContainer = new MenuContainerComponent();
  readonly footerSection = new FooterSectionComponent();
  /** Return a statement table column by its test-id prefix. */
  private column(
    prefix: string,
  ): Locator {
    return this.page.locator(`td[data-testid^="${prefix}"]`);
  }
  // ######## UI actions/navigation ########
  /** Click Make a Payment button. */
  async clickMakeAPaymentButton(): Promise<void> {
    console.log("Click Make a Payment button");
    await this.makeAPaymentButton.click();
  }
  /** Click next page in pagination. */
  async clickPaginationNext(): Promise<void> {
    console.log("Click next pagination page");
    await this.paginationNextButton.click();
  }
  /** Click previous page in pagination. */
  async clickPaginationPrevious(): Promise<void> {
    console.log("Click previous pagination page");
    await this.paginationPreviousButton.click();
  }
  // ######## UI validations ########
  /** Validate elements in the Statements section. */
  async validateStatementsSectionElements({
    accountStatementValue,
    currentBalanceItem,
  }: StatementsSectionInput): Promise<void> {
    console.log("Validate statements section elements");
    await expect( this.statementsTitleLabel, "Statements section title", ).toHaveText(await IbStrings.STATEMENTS.name);
    await expect( this.latestStatementValueLabel, "Latest statement value label", ).toHaveText(await IbStrings.LATEST_STATEMENT_VALUE.name);
    await expect( this.latestStatementAmountValueLabel, "Latest statement amount", ).toHaveText( formatAmount({ amount: accountStatementValue.value, currencySymbol: accountStatementValue.currencyCode, }), );
    await expect( this.interimPaymentsLabel, "Interim payments label", ).toHaveText(await IbStrings.INTERIM_PAYMENTS.name);
    await expect( this.interimPaymentsAmountLabel, "Interim payments amount", ).toHaveText(formatAmount(currentBalanceItem.interimPayments));
    await expect( this.outstandingBalanceLabel, "Outstanding balance label", ).toHaveText(await IbStrings.OUTSTANDING_BALANCE.name);
    await expect( this.outstandingBalanceAmountLabel, "Outstanding balance amount", ).toHaveText(formatAmount(currentBalanceItem.outstanding));
  }
  /** Validate make a payment button. */
  async validateMakeAPaymentButton(): Promise<void> {
    console.log("Validate Make a Payment button");
    await expect(this.makeAPaymentButton, "Make a Payment button").toHaveText( await IbStrings .HOME_INN_BUSINESS_PAY_SPENDING_SUMMARY_MAKE_A_PAYMENT_BUTTON.name, );
  }
  /** Validate statements tooltip. */
  async validateStatementsTooltip(): Promise<void> {
    console.log("Validate statements tooltip");
    await this.statementsTooltipIcon.hover();
    await expect( this.statementsTooltipIcon, "Statements tooltip is open", ).toHaveAttribute("data-state", "delayed-open");
    await expect( this.statementsTooltipLabel, "Statements tooltip label", ).toHaveText(await IbStrings.STATEMENTS_TOOLTIP.name);
    await this.statementsTooltipIcon.click();
  }
  /** Validate latest statement value tooltip. */
  async validateLatestStatementValueTooltip(): Promise<void> {
    console.log("Validate latest statement value tooltip");
    await this.latestStatementValueTooltipIcon.hover();
    await expect( this.latestStatementValueTooltipIcon, "Latest statement tooltip is open", ).toHaveAttribute("data-state", "delayed-open");
    await expect( this.latestStatementValueTooltipLabel, "Latest statement tooltip label", ).toHaveText(await IbStrings.LATEST_STATEMENT_VALUE_TOOLTIP.name);
    await this.latestStatementValueTooltipIcon.click();
  }
  /** Validate interim payments tooltip. */
  async validateInterimPaymentsTooltip(): Promise<void> {
    console.log("Validate interim payments tooltip");
    await this.interimPaymentsTooltipIcon.hover();
    await expect( this.interimPaymentsTooltipIcon, "Interim payments tooltip is open", ).toHaveAttribute("data-state", "delayed-open");
    await expect( this.interimPaymentsTooltipLabel, "Interim payments tooltip label", ).toHaveText(await IbStrings.INTERIM_PAYMENTS_TOOLTIP.name);
    await this.interimPaymentsTooltipIcon.click();
  }
  /** Validate outstanding balance tooltip. */
  async validateOutstandingBalanceTooltip(): Promise<void> {
    console.log("Validate outstanding balance tooltip");
    await this.outstandingBalanceTooltipIcon.hover();
    await expect( this.outstandingBalanceTooltipIcon, "Outstanding balance tooltip is open", ).toHaveAttribute("data-state", "delayed-open");
    await expect( this.outstandingBalanceTooltipLabel, "Outstanding balance tooltip label", ).toHaveText( (await IbStrings.OUTSTANDING_BALANCE_TOOLTIP.name) .replace(/\.N/g, ". N") .replace(/:I/g, ": I"), );
    await this.outstandingBalanceTooltipIcon.click();
  }
  /** Validate statements table header. */
  async validateStatementsTableHeader(): Promise<void> {
    console.log("Validate statements table header");
    const expected = Constants.BROWSER_RESOLUTIONS.isDesktop()
      ? [
          await IbStrings.STATEMENTS_DATE.name,
          await IbStrings.STATEMENTS_INVOICE_NO.name,
          await IbStrings.STATEMENTS_BROUGHT_FORWARD.name,
          await IbStrings.STATEMENTS_PAID.name,
          await IbStrings.STATEMENTS_BALANCE_DUE.name,
          await IbStrings.STATEMENTS_INVOICE_VALUE.name,
          await IbStrings.STATEMENTS_STATEMENT_VALUE.name,
          await IbStrings.STATEMENTS_DOWNLOAD.name,
        ]
      : [
          await IbStrings.STATEMENTS_DATE.name,
          "",
          "",
          "",
          "",
          "",
          await IbStrings.STATEMENTS_STATEMENT_VALUE.name,
          await IbStrings.STATEMENTS_DOWNLOAD.name,
        ];
    await expect(this.statementsTable, "Statements table").toBeVisible();
    await expect( this.statementsTableHeaderLabels, "Statements table header labels", ).toHaveText(expected);
  }
  /** Validate payments table header. */
  async validatePaymentTableHeader(): Promise<void> {
    console.log("Validate payments table header");
    const expected = Constants.BROWSER_RESOLUTIONS.isDesktop()
      ? [
          await IbStrings.PAYMENTS_DATE.name,
          await IbStrings.PAYMENTS_DESCRIPTION.name,
          await IbStrings.PAYMENTS_STATUS.name,
          await IbStrings.PAYMENTS_VALUE.name,
        ]
      : [
          await IbStrings.PAYMENTS_DATE.name,
          "",
          await IbStrings.PAYMENTS_STATUS.name,
          await IbStrings.PAYMENTS_VALUE.name,
        ];
    await expect(this.paymentsTable, "Payments table").toBeVisible();
    await expect( this.paymentsTableHeaderLabels, "Payments table header labels", ).toHaveText(expected);
  }
  /** Validate statements table rows against customerInvoices API response. */
  async validateStatementsTableRows({
    apiInvoices,
  }: {
    apiInvoices: StatementsInvoices;
  }): Promise<void> {
    console.log("Validate statements table rows content");
    await expect( apiInvoices.invoices.length, "Expected at most 15 invoices in API response", ).toBeLessThanOrEqual(Constants.IB_TABLE_ROWS_NUMBER);
    for (const [index, invoice] of apiInvoices.invoices.entries()) {
      const [year, month, day] = invoice.statementDate.split("-");
      await expect( this.column("DataTablePage-row-statementDate-").nth(index), `Statement date row ${index + 1}`, ).toHaveText(`${day}/${month}/${year.slice(2)}`);
      if (Constants.BROWSER_RESOLUTIONS.isDesktop()) {
        await expect( this.column("DataTablePage-row-invoiceNo-").nth(index), `Invoice number row ${index + 1}`, ).toHaveText(invoice.invoiceNo);
        await expect( this.column("DataTablePage-row-broughtForward-").nth(index), `Brought forward row ${index + 1}`, ).toHaveText(formatAmount(invoice.broughtForward));
        await expect( this.column("DataTablePage-row-paymentsReceived-").nth(index), `Paid row ${index + 1}`, ).toHaveText(formatAmount(invoice.paymentsReceived));
        await expect( this.column("DataTablePage-row-overdueBalance-").nth(index), `Balance due row ${index + 1}`, ).toHaveText(formatAmount(invoice.overdueBalance));
        await expect( this.column("DataTablePage-row-invoiceValue-").nth(index), `Invoice value row ${index + 1}`, ).toHaveText(formatAmount(invoice.invoiceValue));
      }
      await expect( this.column("DataTablePage-row-statementBalance-").nth(index), `Statement value row ${index + 1}`, ).toHaveText(formatAmount(invoice.statementBalance));
      await expect( this.page .locator('button[data-testid^="Download-Statements-Pdf-Button"]') .nth(index), `Download PDF button row ${index + 1}`, ).toBeVisible();
    }
  }
  /** Validate payments table rows against paymentInfo API response. */
  async validatePaymentsTableRows({
    paymentInfo,
  }: {
    paymentInfo: PaymentInfo[];
  }): Promise<void> {
    console.log("Validate payments table rows content");
    await expect( paymentInfo.length, "Expected at most 15 payments in API response", ).toBeLessThanOrEqual(Constants.IB_TABLE_ROWS_NUMBER);
    for (const [index, payment] of paymentInfo.entries()) {
      await expect( this.column("PaymentsTable-DataTableClient-row-paymentDate-").nth( index, ), `Payment date row ${index + 1}`, ).toHaveText(payment.paymentDate);
      await expect( this.column( "PaymentsTable-DataTableClient-row-paymentDescription-", ).nth(index), `Payment description row ${index + 1}`, ).toHaveText(payment.paymentDescription);
      await expect( this.column("PaymentsTable-DataTableClient-row-paymentFailed-").nth( index, ), `Payment status row ${index + 1}`, ).toHaveText( payment.paymentFailed ? await IbStrings.PAYMENT_STATUS_FAILED.name : await IbStrings.PAYMENT_STATUS_SUCCESSFUL.name, );
      await expect( this.column("PaymentsTable-DataTableClient-row-paymentValue-").nth( index, ), `Payment value row ${index + 1}`, ).toHaveText( formatAmount({ amount: payment.paymentValue.value, currencySymbol: payment.paymentValue.currencySymbol, }), );
    }
  }
  /** Validate no payments label in payments table. */
  async validateNoPaymentsLabel(): Promise<void> {
    console.log("Validate no payments label");
    await expect(this.noPaymentsLabel, "No payments label").toHaveText( `${await IbStrings.STATEMENTS_INVOICES_PAYMENTS_NO_PAYMENTS_TITLE.name}\n${await IbStrings.STATEMENTS_INVOICES_PAYMENTS_NO_PAYMENTS_DESCRIPTION.name}`, );
  }
  /** Validate no statement history table label. */
  async validateNoStatementHistoryLabel({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate no statement history label");
    if (isDisplayed)
      await expect( this.noStatementHistoryLabel, "No statement history label", ).toHaveText( `${await IbStrings.STATEMENTS_INVOICES_PAYMENTS_NO_STATEMENTS_TITLE.name}\n${await IbStrings.STATEMENTS_INVOICES_PAYMENTS_NO_STATEMENTS_DESCRIPTION.name}`, );
    else
      await expect( this.noStatementHistoryLabel, "No statement history label", ).not.toBeVisible();
  }
  /** Validate Statements, Invoices and Payments page content. */
  async validatePage(): Promise<void> {
    console.log("Validate Statements, Invoices and Payments page");
    await this.validatePageMarker(
      this.titleLabel,
      "Statements, Invoices and Payments",
    );
    await expect(this.titleLabel, "Page title").toHaveText( await IbStrings.STATEMENTS_INVOICES_PAYMENTS.name, );
  }
}

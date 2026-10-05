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
interface TransactionAmount {
  amount: number | string;
  currencySymbol: string;
}
interface TransactionLineItem {
  description: string;
  guestName?: string;
  grossAmount: TransactionAmount;
}
export interface AccountTransaction {
  transactionDate: string;
  cardName: string;
  pan: string;
  location: string;
  purchaseOrderReference: string;
  customerOwnRef: string;
  grossAmount: TransactionAmount;
  lineItems: TransactionLineItem[];
}
const format = ({ amount, currencySymbol }: TransactionAmount): string =>
  `${currencySymbol}${Number(amount).toFixed(2)}`;
/** IB Statements, Transactions */
export class TransactionsPage extends BasePibPage {
  // ######## UI elements/properties ########
  readonly titleLabel: Locator = this.page.getByTestId("Transactions-Title");
  readonly transactionsTooltipIcon: Locator = this.page.getByTestId( "TransactionsTable-Title-title-icon", );
  readonly transactionsTooltipLabel: Locator = this.page.locator( '//div[@data-testid="TransactionsTable-Title-InfoTooltip"]/span[1]', );
  readonly transactionsTable: Locator = this.page.getByTestId("TransactionsTable");
  readonly transactionsTableHeaderLabels: Locator = this.page.locator( 'tr[data-testid="TransactionsTable-row"] th', );
  readonly noTransactionsLabel: Locator = this.page.locator( 'table[data-testid="TransactionsTable"] div div', );
  readonly accountHolderSection = new AccountHolderSectionComponent();
  readonly notifications = new NotificationsSectionComponent();
  readonly menuContainer = new MenuContainerComponent();
  readonly footerSection = new FooterSectionComponent();
  /** Get table row expandable button by index. */
  getExpandableButtonByIndex(
    index: number,
  ): Locator {
    return this.page.locator(
      `td[data-testid^="TransactionsTable-row-expandable-${index}"]`,
    );
  }
  /** Get table location expanded by row index. */
  getTransactionExtendedRowLocationByIndex(
    index: number,
  ): Locator {
    return this.page.locator(
      `//tr[@data-testid="TransactionsTable-row-${index}"]/following-sibling::tr[@data-testid="TransactionsTable-row-expanded"]//td[4]/div/div`,
    );
  }
  /** Get table gross value expanded by row index. */
  getTransactionExtendedRowGrossValueByIndex(
    index: number,
  ): Locator {
    return this.page.locator(
      `//tr[@data-testid="TransactionsTable-row-${index}"]/following-sibling::tr[@data-testid="TransactionsTable-row-expanded"]//td[7]/div/div`,
    );
  }
  // ######## UI actions/navigation ########
  /** Click on expandable button from transactions table by index. */
  async clickExpandRowButton({
    rowNumber,
  }: {
    rowNumber: number;
  }): Promise<void> {
    console.log(`Click transaction expandable row: ${rowNumber}`);
    const button = this.getExpandableButtonByIndex(rowNumber - 1);
    await button.scrollIntoViewIfNeeded();
    await button.click();
  }
  // ######## UI validations ########
  /** Validate transactions tooltip. */
  async validateTransactionsTooltip(): Promise<void> {
    console.log("Validate transactions tooltip");
    await this.transactionsTooltipIcon.hover();
    await expect( this.transactionsTooltipIcon, "Transactions tooltip is open", ).toHaveAttribute("data-state", "delayed-open");
    await expect( this.transactionsTooltipLabel, "Transactions tooltip label", ).toHaveText( await IbStrings.TRANSACTIONS_OUTSTANDING_TRANSACTIONS_TOOLTIP.name, );
    await this.transactionsTooltipIcon.click();
  }
  /** Validate transactions table header. */
  async validateTransactionsTableHeader(): Promise<void> {
    console.log("Validate transactions table header");
    const expected = Constants.BROWSER_RESOLUTIONS.isDesktop()
      ? [
          await IbStrings.PAYMENTS_DATE.name,
          await IbStrings.CARD_HOLDER.name,
          await IbStrings.CARD_NO.name,
          await IbStrings.LOCATION_TRANSACTIONS.name,
          await IbStrings.PURCHASE_ORDER.name,
          await IbStrings.CUSTOMER_REF_TRANSACTIONS.name,
          await IbStrings.GROSS_VALUE.name,
          "",
        ]
      : [
          await IbStrings.PAYMENTS_DATE.name,
          "",
          "",
          await IbStrings.LOCATION_TRANSACTIONS.name,
          "",
          "",
          await IbStrings.GROSS_VALUE.name,
          "",
        ];
    await expect(this.transactionsTable, "Transactions table").toBeVisible();
    await expect( this.transactionsTableHeaderLabels, "Transactions table header labels", ).toHaveText(expected);
  }
  /** Validate transactions table rows against AccountTransactions API response. */
  async validateTransactionsTableRows({
    transactionsArray,
  }: {
    transactionsArray: AccountTransaction[];
  }): Promise<void> {
    console.log("Validate transactions table rows content");
    const dates = this.page
      .locator('td[data-testid^="TransactionsTable-row-"]')
      .filter({ has: this.page.locator(":scope") });
    await expect( transactionsArray.length, "Transaction count does not exceed table rows", ).toBeLessThanOrEqual(await dates.count());
    for (const [index, transaction] of transactionsArray.entries()) {
      const date = new Date(transaction.transactionDate);
      const dateLabel = `${String(date.getDate()).padStart(2, "0")}/${String(date.getMonth() + 1).padStart(2, "0")}/${String(date.getFullYear()).slice(2)} ${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;
      const cells = this.page.locator(
        `tr[data-testid="TransactionsTable-row-${index}"] td`,
      );
      await expect( cells.nth(0), `Transaction date row ${index + 1}`, ).toHaveText(dateLabel);
      await expect(cells.nth(1), `Cardholder row ${index + 1}`).toHaveText( transaction.cardName, );
      await expect(cells.nth(2), `Card number row ${index + 1}`).toHaveText( `**** ${transaction.pan.slice(-4)}`, );
      await expect(cells.nth(3), `Location row ${index + 1}`).toHaveText( transaction.location, );
      await expect(cells.nth(4), `Purchase order row ${index + 1}`).toHaveText( transaction.purchaseOrderReference, );
      await expect( cells.nth(5), `Customer reference row ${index + 1}`, ).toHaveText(transaction.customerOwnRef);
      await expect(cells.nth(6), `Gross value row ${index + 1}`).toHaveText( format(transaction.grossAmount), );
      await expect( this.getExpandableButtonByIndex(index), `Expandable button row ${index + 1}`, ).toBeVisible();
    }
  }
  /** Validate transactions extra info dropdown rows against AccountTransactions API response. */
  async validateTransactionsExtraInfoDropdownRows({
    rowNumber,
    transactionsArray,
  }: {
    rowNumber: number;
    transactionsArray: AccountTransaction[];
  }): Promise<void> {
    console.log(`Validate transaction extra rows: ${rowNumber}`);
    const extras = transactionsArray[rowNumber - 1].lineItems;
    const locations = this.getTransactionExtendedRowLocationByIndex(
      rowNumber - 1,
    );
    const values = this.getTransactionExtendedRowGrossValueByIndex(
      rowNumber - 1,
    );
    await expect(locations, "Expanded location count").toHaveCount( extras.length, );
    for (const [index, extra] of extras.entries()) {
      await expect( locations.nth(index), `Expanded location row ${index + 1}`, ).toHaveText( extra.guestName ? `${extra.description} (${extra.guestName})` : extra.description, );
      await expect( values.nth(index), `Expanded gross value row ${index + 1}`, ).toHaveText(format(extra.grossAmount));
    }
  }
  /** Validate no transactions label in transactions table. */
  async validateNoTransactionsLabel(): Promise<void> {
    console.log("Validate no transactions label");
    await expect(this.noTransactionsLabel, "No transactions label").toHaveText( `${await IbStrings.TRANSACTIONS_NO_TRANSACTIONS_YET_TITLE.name}\n${await IbStrings.TRANSACTIONS_NO_TRANSACTIONS_YET_DESCRIPTION.name}`, );
  }
  /** Validate Transactions page content. */
  async validatePage(): Promise<void> {
    console.log("Validate Transactions page");
    await this.validatePageMarker(this.titleLabel, "Transactions");
    await expect(this.titleLabel, "Transactions page title").toHaveText( await IbStrings.SPENDING_AND_REPORTING_TRANSACTIONS_TITLE.name, );
  }
}

import { type StringBase } from '../stringBase';
import { Strings } from '../strings';
import { IbStrings } from './ibStrings';

/** Report card shown in Inn Business spending and reporting. */
export interface SpendingAndReportingCard {
  title: StringBase;
  description: StringBase;
  icon: StringBase;
}

/**
 * The report cards form IB spending and reporting as an enum
 */
export class SpendingAndReportingCards {
  private constructor() {}

  static readonly MANAGEMENT_INFORMATION_REPORTING: SpendingAndReportingCard = {
    title: IbStrings.SPENDING_AND_REPORTING_CARD_MANAGEMENT_REPORTING_TITLE,
    description: IbStrings.SPENDING_AND_REPORTING_CARD_MANAGEMENT_REPORTING_DESCRIPTION,
    icon: Strings.SPENDING_AND_REPORTING_CARD_MANAGEMENT_REPORTING_ICON,
  };
  static readonly OUT_OF_POLICY_REPORTING: SpendingAndReportingCard = {
    title: IbStrings.SPENDING_AND_REPORTING_OUT_OF_POLICY_REPORTING_TITLE,
    description: IbStrings.SPENDING_AND_REPORTING_OUT_OF_POLICY_REPORTING_DESCRIPTION,
    icon: Strings.SPENDING_AND_REPORTING_OUT_OF_POLICY_REPORTING_ICON,
  };
  static readonly EMERGENCY_REPORT: SpendingAndReportingCard = {
    title: IbStrings.SPENDING_AND_REPORTING_EMERGENCY_REPORT_TITLE,
    description: IbStrings.SPENDING_AND_REPORTING_EMERGENCY_REPORT_DESCRIPTION,
    icon: Strings.SPENDING_AND_REPORTING_EMERGENCY_REPORT_ICON,
  };
  static readonly ABOUT_REPORTS: SpendingAndReportingCard = {
    title: IbStrings.SPENDING_AND_REPORTING_ABOUT_REPORTS_TITLE,
    description: IbStrings.SPENDING_AND_REPORTING_ABOUT_REPORTS_DESCRIPTION,
    icon: IbStrings.SPENDING_AND_REPORTING_ABOUT_REPORTS_ICON,
  };
  static readonly CREATE_REPORTS: SpendingAndReportingCard = {
    title: IbStrings.SPENDING_AND_REPORTING_CREATE_REPORTS_TITLE,
    description: IbStrings.SPENDING_AND_REPORTING_CREATE_REPORTS_DESCRIPTION,
    icon: IbStrings.SPENDING_AND_REPORTING_CREATE_REPORTS_ICON,
  };
  static readonly SCHEDULED_REPORTS: SpendingAndReportingCard = {
    title: IbStrings.SPENDING_AND_REPORTING_SCHEDULED_REPORTS_TITLE,
    description: IbStrings.SPENDING_AND_REPORTING_SCHEDULED_REPORTS_DESCRIPTION,
    icon: IbStrings.SPENDING_AND_REPORTING_SCHEDULED_REPORTS_ICON,
  };
  static readonly LINKED_ACCOUNTS: SpendingAndReportingCard = {
    title: IbStrings.SPENDING_AND_REPORTING_LINKED_ACCOUNTS_TITLE,
    description: IbStrings.SPENDING_AND_REPORTING_LINKED_ACCOUNTS_DESCRIPTION,
    icon: IbStrings.SPENDING_AND_REPORTING_LINKED_ACCOUNTS_ICON,
  };
  static readonly STATEMENT_AND_INVOICES: SpendingAndReportingCard = {
    title: IbStrings.SPENDING_AND_REPORTING_STATEMENT_AND_INVOICES_TITLE,
    description: IbStrings.SPENDING_AND_REPORTING_STATEMENT_AND_INVOICES_DESCRIPTION,
    icon: IbStrings.SPENDING_AND_REPORTING_STATEMENT_AND_INVOICES_ICON,
  };
  static readonly TRANSACTIONS: SpendingAndReportingCard = {
    title: IbStrings.SPENDING_AND_REPORTING_TRANSACTIONS_TITLE,
    description: IbStrings.SPENDING_AND_REPORTING_TRANSACTIONS_DESCRIPTION,
    icon: IbStrings.SPENDING_AND_REPORTING_TRANSACTIONS_ICON,
  };
  static readonly INTEREST_FREE_CREDIT: SpendingAndReportingCard = {
    title: IbStrings.INTEREST_FREE_CREDIT,
    description: IbStrings.UTILISE_INTEREST_FREE_CREDIT,
    icon: IbStrings.CREDIT_BOX_ICON,
  };
  static readonly EXPENSE_MANAGEMENT: SpendingAndReportingCard = {
    title: IbStrings.EXPENSE_MANAGEMENT,
    description: IbStrings.PRE_AUTHORISE_EMPLOYEE_STAYS,
    icon: IbStrings.EXPENSE_BOX_ICON,
  };
  static readonly CONSOLIDATED_INVOICES: SpendingAndReportingCard = {
    title: IbStrings.CONSOLIDATED_INVOICES,
    description: IbStrings.RECEIVE_A_SINGLE_CONSOLIDATED_VAT,
    icon: IbStrings.CONSOLIDATED_INVOICES_ICON,
  };

  /**
   * Get SpendingAndReportingCard object based on 'title' property
   * @param spendingAndReportingCardTitle card title
   * @returns SpendingAndReportingCard object
   */
  static async getCardByTitle(spendingAndReportingCardTitle: string): Promise<SpendingAndReportingCard> {
    for (const card of Object.values(SpendingAndReportingCards)) {
      if (typeof card === 'object' && 'title' in card && await card.title.name === spendingAndReportingCardTitle) {
        return card;
      }
    }
    throw new Error(`${spendingAndReportingCardTitle} card is not found in the list!`);
  }

  static async getSpendingAndReportingCardByTitleKey(titleKey: string): Promise<SpendingAndReportingCard> {
    return SpendingAndReportingCards.getCardByTitle(titleKey);
  }
}

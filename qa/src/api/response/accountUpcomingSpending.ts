/**
{
    "data": {
        "getAccountUpcomingSpending": {
            "expectedSpendTodayDate": "2025-04-08",
            "expectedSpendToday": 0,
            "expectedNextBillingStartDate": "2025-04-08",
            "expectedNextBillingEndDate": "2025-04-14",
            "expectedNextBilling": 0,
            "expectedNextPeriodStartDate": "2025-04-15",
            "expectedNextPeriodEndDate": "2025-04-21",
            "expectedNextPeriod": 0,
            "currency": "826",
            "accountStatus": "Current"
        }
    }
}
 */
export class AccountUpcomingSpending {
  [key: string]: unknown;
  accountStatus?: string;
  currency?: string;
  expectedNextBilling?: number;
  expectedNextBillingEndDate?: string;
  expectedNextBillingStartDate?: string;
  expectedNextPeriod?: number;
  expectedNextPeriodEndDate?: string;
  expectedNextPeriodStartDate?: string;
  expectedSpendToday?: number;
  expectedSpendTodayDate?: string;

  /**
   * AccountUpcomingSpending constructor
   * @param data data object
   * @param data.accountUpcomingSpending AccountUpcomingSpending object
   */
  constructor(data: { accountUpcomingSpending?: Record<string, unknown> } = {}) {
    const accountUpcomingSpending = data.accountUpcomingSpending ?? {};
    this.expectedSpendTodayDate = accountUpcomingSpending.expectedSpendTodayDate as string | undefined;
    this.expectedSpendToday = accountUpcomingSpending.expectedSpendToday as number | undefined;
    this.expectedNextBillingStartDate = accountUpcomingSpending.expectedNextBillingStartDate as string | undefined;
    this.expectedNextBillingEndDate = accountUpcomingSpending.expectedNextBillingEndDate as string | undefined;
    this.expectedNextBilling = accountUpcomingSpending.expectedNextBilling as number | undefined;
    this.expectedNextPeriodStartDate = accountUpcomingSpending.expectedNextPeriodStartDate as string | undefined;
    this.expectedNextPeriodEndDate = accountUpcomingSpending.expectedNextPeriodEndDate as string | undefined;
    this.expectedNextPeriod = accountUpcomingSpending.expectedNextPeriod as number | undefined;
    this.currency = accountUpcomingSpending.currency as string | undefined;
    this.accountStatus = accountUpcomingSpending.accountStatus as string | undefined;
  }

  static fromResponse(data: { accountUpcomingSpending?: Record<string, unknown> }): AccountUpcomingSpending {
    return new AccountUpcomingSpending(data);
  }
}

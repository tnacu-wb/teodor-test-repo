/**
 * AccountSpendingCriteria class
 */
export interface AccountSpendingCriteriaData {
  fromMonthYear?: string;
  pibaAccountId?: string;
  toMonthYear?: string;
  tetheredUserGuid?: string;
}

export class AccountSpendingCriteria {
  [key: string]: unknown;
  fromMonthYear?: string;
  pibaAccountId?: string;
  toMonthYear?: string;
  tetheredUserGuid?: string | undefined;

  constructor(data: AccountSpendingCriteriaData = {}) {
    Object.assign(this, data);
  }

  static fromRequest(data: AccountSpendingCriteriaData): AccountSpendingCriteria {
    return new AccountSpendingCriteria(data);
  }
}

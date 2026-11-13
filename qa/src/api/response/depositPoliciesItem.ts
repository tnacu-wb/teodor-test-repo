import { Amount } from './amount';

/**
{
    "amountDue": {
      "amount": -4.0,
      "currencyCode": null
    },
    "amountPaid": {
      "amount": 598.0,
      "currencyCode": null
    },
    "policyCode": "DAX"
  }
 */
export class DepositPoliciesItem {
  [key: string]: unknown;
  amountDue?: Amount;
  amountPaid?: Amount;
  policyCode?: string;

  /**
   * DepositPoliciesItem
   * @param data data
   * @param data.depositPoliciesItem depositPoliciesItem
   */
  constructor(data: { depositPoliciesItem?: Record<string, unknown> } = {}) {
    const depositPoliciesItem = data.depositPoliciesItem ?? {};
    this.amountDue = new Amount({ amount: depositPoliciesItem.amountDue });
    this.amountPaid = new Amount({ amount: depositPoliciesItem.amountPaid });
    this.policyCode = depositPoliciesItem.policyCode as string | undefined;
  }

  static fromResponse(data: { depositPoliciesItem?: Record<string, unknown> }): DepositPoliciesItem {
    return new DepositPoliciesItem(data);
  }
}

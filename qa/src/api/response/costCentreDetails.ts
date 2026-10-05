/**
{
  "data": {
    "getCostCentreDetails": [
      {
        "accountUniqueCustomerId": "19000857",
        "costCentreUniqueCustomerId": "19000858",
        "costCentreCode": "1687204041",
        "costCentreName": "Cost Centre DE"
      },
      {
        "accountUniqueCustomerId": "19000857",
        "costCentreUniqueCustomerId": "19000859",
        "costCentreCode": "1257510715",
        "costCentreName": "Cost centre DE2"
      },
      {
        "accountUniqueCustomerId": "19000857",
        "costCentreUniqueCustomerId": "19002383",
        "costCentreCode": "123456789",
        "costCentreName": "CCObis"
      },
      {
        "accountUniqueCustomerId": "19000857",
        "costCentreUniqueCustomerId": "19002862",
        "costCentreCode": "124578192",
        "costCentreName": "multiCco"
      },
      {
        "accountUniqueCustomerId": "19000857",
        "costCentreUniqueCustomerId": "19002868",
        "costCentreCode": "15834061",
        "costCentreName": "ccBisBis"
      }
    ]
  }
}
 */
export class CostCentreDetails {
  [key: string]: unknown;
  accountUniqueCustomerId?: string;
  costCentreCode?: string;
  costCentreName?: string;
  costCentreUniqueCustomerId?: string;

  /**
   * CostCentreDetails constructor from Manage cost centres
   * @param data object data
   * @param data.costCentreDetail cost centre details object
   */
  constructor(data: { costCentreDetail?: Record<string, unknown> } = {}) {
    const costCentreDetail = data.costCentreDetail ?? {};
    this.costCentreCode = costCentreDetail.costCentreCode as string | undefined;
    this.costCentreName = costCentreDetail.costCentreName as string | undefined;
    this.accountUniqueCustomerId = costCentreDetail.accountUniqueCustomerId as string | undefined;
    this.costCentreUniqueCustomerId = costCentreDetail.costCentreUniqueCustomerId as string | undefined;
  }

  static fromResponse(data: { costCentreDetail?: Record<string, unknown> }): CostCentreDetails {
    return new CostCentreDetails(data);
  }
}

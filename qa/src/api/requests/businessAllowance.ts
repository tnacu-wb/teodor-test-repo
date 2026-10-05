/**
 * BusinessAllowance object required in initiatePayment request
 */
export interface BusinessAllowanceData {
  allowance?: string;
  budget?: number;
  isAuthorised?: boolean;
}

export class BusinessAllowance {
  [key: string]: unknown;
  allowance?: string;
  budget?: number;
  isAuthorised?: boolean;

  constructor(data: BusinessAllowanceData = {}) {
    Object.assign(this, data);
  }

  static fromRequest(data: BusinessAllowanceData): BusinessAllowance {
    return new BusinessAllowance(data);
  }
}

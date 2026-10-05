import { BusinessAllowance, type BusinessAllowanceData } from './businessAllowance';

export interface BusinessItemsInputData {
  businessAllowances?: Array<BusinessAllowance | BusinessAllowanceData>;
  customReferenceNumber?: string;
  purchaseOrderNumber?: string;
}

/** API model for the current TypeScript test framework. */
export class BusinessItemsInput {
  [key: string]: unknown;
  businessAllowances?: BusinessAllowance[];
  customReferenceNumber?: string;
  purchaseOrderNumber?: string;

  constructor(data: BusinessItemsInputData = {}) {
    const payload = {
      ...data,
      businessAllowances: data.businessAllowances?.map((item) => item instanceof BusinessAllowance ? item : new BusinessAllowance(item)),
    };

    Object.assign(this, payload);
  }

  static fromRequest(data: BusinessItemsInputData): BusinessItemsInput {
    return new BusinessItemsInput(data);
  }
}

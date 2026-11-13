import { CompanySpendingDto } from './companySpendingDto';

/**
 * {
    "data": {
        "getCompanySpending": {
            "companySpendingDtoList": [
                {
                    "noOfBookings": 3,
                    "companyAccountId": "COMP_80eade56-67a5-4eb9-ac07-2e6fe5c24739",
                    "year": 2024,
                    "month": 12,
                    "bookingValue": 268.99,
                    "bookingCurrency": "GBP"
                }
            ]
        }
    }
}
 */
export class CompanySpendingResponse {
  [key: string]: unknown;
  companySpendingDtoList: CompanySpendingDto[] = [];

  /**
   * CompanySpendingResponse constructor
   * @param data object data
   * @param data.companySpendingDtoList list of CompanySpendingDto objects
   */
  constructor(data: { companySpendingDtoList?: Array<Record<string, unknown>> } = {}) {
    this.companySpendingDtoList = Array.isArray(data.companySpendingDtoList)
      ? data.companySpendingDtoList.map((dto) => new CompanySpendingDto(dto))
      : [];
  }

  static fromResponse(data: { companySpendingDtoList?: Array<Record<string, unknown>> }): CompanySpendingResponse {
    return new CompanySpendingResponse(data);
  }

}

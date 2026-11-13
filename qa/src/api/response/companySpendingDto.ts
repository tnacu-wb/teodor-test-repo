/**
 *  {
                    "noOfBookings": 3,
                    "companyAccountId": "COMP_80eade56-67a5-4eb9-ac07-2e6fe5c24739",
                    "year": 2024,
                    "month": 12,
                    "bookingValue": 268.99,
                    "bookingCurrency": "GBP"
                }
 */
export class CompanySpendingDto {
  [key: string]: unknown;
  bookingCurrency?: string;
  bookingValue?: number;
  companyAccountId?: string;
  month?: number;
  year?: number;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromResponse<T extends Record<string, unknown>>(data: T): CompanySpendingDto {
    return new CompanySpendingDto(data);
  }
}

/**
 * AccountSpendingDto class represents the spending details of a PIBA account.
 */
export class AccountSpendingDto {
  [key: string]: unknown;
  bookingValue?: number;
  month?: number;
  noOfBookings?: number;
  pibaAccountId?: string;
  year?: number;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromResponse<T extends Record<string, unknown>>(data: T): AccountSpendingDto {
    return new AccountSpendingDto(data);
  }
}

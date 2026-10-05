/**
 *  {
        "companyAccountId": "COMP_e05f2674-0311-48cc-a901-949d3ae17312",
        "employeeAccountId": "EMPL_f7eee4ba-0ba8-40bc-b681-02d7f335ce63",
        "year": 2025,
        "month": 4,
        "noOfBookings": 14,
        "bookingValue": 1234,
        "bookingCurrency": "GBP"
    }
 */
export class EmployeeSpendDto {
  [key: string]: unknown;
  bookingCurrency?: string;
  bookingValue?: number;
  companyAccountId?: string;
  employeeAccountId?: string;
  month?: number;
  noOfBookings?: number;
  year?: number;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromResponse<T extends Record<string, unknown>>(data: T): EmployeeSpendDto {
    return new EmployeeSpendDto(data);
  }
}

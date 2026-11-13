import { EmployeeSpendDto } from './employeeSpendDto';

/**
 * {
    "data": {
        "getEmployeeSpend": {
            "employeeSpendDtoList": [
                {
                    "companyAccountId": "COMP_e05f2674-0311-48cc-a901-949d3ae17312",
                    "employeeAccountId": "EMPL_f7eee4ba-0ba8-40bc-b681-02d7f335ce63",
                    "year": 2025,
                    "month": 4,
                    "noOfBookings": 14,
                    "bookingValue": 1234,
                    "bookingCurrency": "GBP"
                }
            ]
        }
    }
}
 */
export class EmployeeSpendResponse {
  [key: string]: unknown;
  employeeSpendDtoList: EmployeeSpendDto[] = [];

  /**
   * EmployeeSpendResponse constructor
   * @param data object data
   * @param data.employeeSpendDtoList list of EmployeeSpendDto objects
   */
  constructor(data: { employeeSpendDtoList?: Array<Record<string, unknown>> } = {}) {
    this.employeeSpendDtoList = Array.isArray(data.employeeSpendDtoList)
      ? data.employeeSpendDtoList.map((dto) => new EmployeeSpendDto(dto))
      : [];
  }

  static fromResponse(data: { employeeSpendDtoList?: Array<Record<string, unknown>> }): EmployeeSpendResponse {
    return new EmployeeSpendResponse(data);
  }

}

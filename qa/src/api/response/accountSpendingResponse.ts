import { AccountSpendingDto } from './accountSpendingDto';

/**
 * AccountSpendingResponse class
 */
export class AccountSpendingResponse {
  [key: string]: unknown;
  accountSpendingDtoList: AccountSpendingDto[] = [];

  /**
   * AccountSpendingResponse constructor
   * @param data Parameters for the class
   * @param data.accountSpendingDtoList List of AccountSpendingDto objects
   */
  constructor(data: { accountSpendingDtoList?: Array<Record<string, unknown>> } = {}) {
    this.accountSpendingDtoList = Array.isArray(data.accountSpendingDtoList)
      ? data.accountSpendingDtoList.map((dto) => new AccountSpendingDto(dto))
      : [];
  }

  static fromResponse(data: { accountSpendingDtoList?: Array<Record<string, unknown>> }): AccountSpendingResponse {
    return new AccountSpendingResponse(data);
  }
}

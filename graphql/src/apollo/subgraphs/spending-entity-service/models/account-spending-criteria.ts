export class AccountSpendingCriteria {
  fromMonthYear: string;
  toMonthYear: string;
  pibaAccountId: string;
  tetheredUserGuid?: string;

  constructor(data: any) {
    this.fromMonthYear = data.fromMonthYear;
    this.toMonthYear = data.toMonthYear;
    this.pibaAccountId = data.pibaAccountId;
    this.tetheredUserGuid = data.tetheredUserGuid;
  }
}

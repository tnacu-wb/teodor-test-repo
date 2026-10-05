export class SearchCompanySpendingCriteria {
  fromMonthYear: string;
  toMonthYear: string;

  constructor(data: any) {
    this.fromMonthYear = data.fromMonthYear;
    this.toMonthYear = data.toMonthYear;
  }
}

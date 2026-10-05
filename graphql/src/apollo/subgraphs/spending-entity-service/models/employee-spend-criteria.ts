export class EmployeeSpendCriteria {
  fromMonthYear: string;
  toMonthYear: string;

  constructor(data: any) {
    this.fromMonthYear = data.fromMonthYear;
    this.toMonthYear = data.toMonthYear;
  }
}

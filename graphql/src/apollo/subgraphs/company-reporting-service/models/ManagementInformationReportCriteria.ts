export class ManagementInformationReportCriteria {
  fromDate: string;
  toDate: string;
  showQnAcolumns: boolean;
  language: string;

  constructor({
    fromDate,
    toDate,
    showQnAcolumns,
    language
  }: {
    fromDate: string;
    toDate: string;
    showQnAcolumns: boolean;
    language: string;
  }) {
    this.fromDate = fromDate;
    this.toDate = toDate;
    this.showQnAcolumns = showQnAcolumns;
    this.language = language;
  }
}

export class PaymentInfoCriteria {
  accountId: string;
  page: number;
  size: number;
  nonInvoiceOnly: boolean;
  tetheredUserGuid?: string;

  constructor(data: any) {
    this.accountId = data.accountId;
    this.page = data.page;
    this.size = data.size;
    this.nonInvoiceOnly = data.nonInvoiceOnly;
    this.tetheredUserGuid = data.tetheredUserGuid;
  }
}

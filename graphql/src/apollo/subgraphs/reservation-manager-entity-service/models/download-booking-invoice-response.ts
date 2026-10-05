export type Language = 'EN' | 'DE';

export interface InvoiceMeta {
  invoiceNumber: string;
  issuedDate: string;
  hotelId?: string;
}

export interface InvoiceDownload {
  bookingRef: string;
  url: string;
  expiresAt: string;
  fileName: string;
  mimeType: string;
  language: Language;
  invoiceMeta: InvoiceMeta;
}

export interface InvoiceDownloadResponse {
  invoices: InvoiceDownload[];
}

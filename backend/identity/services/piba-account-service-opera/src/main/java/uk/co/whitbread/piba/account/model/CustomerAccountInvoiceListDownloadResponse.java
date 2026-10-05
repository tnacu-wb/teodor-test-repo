package uk.co.whitbread.piba.account.model;

import lombok.Data;

@Data
public class CustomerAccountInvoiceListDownloadResponse {
    protected String fileName;
    protected byte[] binaryData;
    protected String fileExtension;
}
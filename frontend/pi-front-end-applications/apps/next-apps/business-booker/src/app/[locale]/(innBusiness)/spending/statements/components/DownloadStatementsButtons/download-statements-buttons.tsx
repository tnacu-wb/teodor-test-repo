'use client';

import { CustomerAccountDetails } from '@whitbread-eos/api';
import { Button } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, getStatementsPdf, getStatementsXls } from '@whitbread-eos/utils/server';
import Image from 'next/image';
import React, { useState } from 'react';

type Props = {
  icons: Record<string, string>;
  token: string;
  account: CustomerAccountDetails;
  fileAutoID?: number;
  statementDate?: string;
  invoiceNo?: string;
};

export function DownloadStatementsButtons({
  icons,
  token,
  account,
  fileAutoID,
  statementDate,
  invoiceNo,
}: Props) {
  const [isPdfDownloading, setIsPdfDownloading] = useState(false);
  const [isXlsDownloading, setIsXlsDownloading] = useState(false);

  // Default to 'GB' if scheme is not provided
  const scheme = account.scheme || 'GB';
  const isDeScheme = scheme === 'DE';

  const handleDownloadPDF = async () => {
    window._satellite && window._satellite.track('downloadInvoice');
    setIsPdfDownloading(true);

    try {
      const downloadPdfResponse = await getStatementsPdf(
        token,
        account.tetheredGuid,
        account.schemeCustomerId,
        fileAutoID,
        statementDate,
        invoiceNo,
        scheme
      );

      if (downloadPdfResponse !== null) {
        setIsPdfDownloading(false);
      }
    } catch {
      setIsPdfDownloading(false);
    }
  };

  const handleDownloadExcel = async () => {
    window._satellite && window._satellite.track('reportDownloaded');
    setIsXlsDownloading(true);

    try {
      const downloadXlsResponse = await getStatementsXls(
        token,
        account.tetheredGuid,
        account.schemeCustomerId,
        fileAutoID,
        statementDate,
        invoiceNo,
        scheme
      );

      if (downloadXlsResponse !== null) {
        setIsXlsDownloading(false);
      }
    } catch (e) {
      setIsXlsDownloading(false);
    }
  };

  return (
    <div data-testid="Download-Statements-Buttons-Container" className={buttonsContainerStyle}>
      <Button
        data-testid="Download-Statements-Pdf-Button"
        className={buttonStyle}
        onClick={handleDownloadPDF}
        disabled={isPdfDownloading}
      >
        <Image
          alt="Download PDF Button Image"
          src={formatIBAssetsUrl(icons['icon.file.pdf.purple'])}
          width={24}
          height={24}
          className={imageStyle}
          data-testid="Download-Statements-Pdf-Button-Icon"
        />
      </Button>
      {!isDeScheme && (
        <Button
          data-testid="Download-Statements-Excel-Button"
          className={buttonStyle}
          onClick={handleDownloadExcel}
          disabled={isXlsDownloading}
        >
          <Image
            alt="Download Excel Button Image"
            src={formatIBAssetsUrl(icons['icon.file.xls.purple'])}
            width={24}
            height={24}
            className={imageStyle}
            data-testid="Download-Statements-Excel-Button-Icon"
          />
        </Button>
      )}
    </div>
  );
}

const buttonsContainerStyle = 'flex items-center justify-center';
const buttonStyle = 'bg-transparent hover:bg-transparent px-[.5rem]';
const imageStyle = 'max-w-[1.5rem]';

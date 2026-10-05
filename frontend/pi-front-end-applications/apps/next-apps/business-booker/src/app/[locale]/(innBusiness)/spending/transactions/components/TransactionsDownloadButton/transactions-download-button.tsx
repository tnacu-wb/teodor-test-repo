'use client';

import { useTranslation } from '@whitbread-eos/utils';
import { getTransactionsXls } from '@whitbread-eos/utils/server';
import { useState } from 'react';

import { DownloadButtonContent } from '~components/innBusiness/DownloadButton/download-button-content';

type Props = {
  icons: Record<string, string>;
  token: string;
  schemeCustomerId?: number;
  tetheredUserGuid?: string;
  scheme?: string;
  testId?: string;
  className?: string;
};

export function TransactionsDownloadButton({
  icons,
  token,
  schemeCustomerId,
  tetheredUserGuid,
  scheme,
  testId,
  className,
}: Props) {
  const { t } = useTranslation('spending');
  const [isDownloading, setIsDownloading] = useState(false);

  if (!schemeCustomerId || !tetheredUserGuid) {
    return null;
  }

  const handleDownloadTransactionsXls = async () => {
    if (isDownloading) {
      return;
    }

    const resolvedScheme = scheme || 'GB';

    window?._satellite?.track('downloadTransactions');
    window?._satellite?.track('reportDownloaded');
    setIsDownloading(true);

    try {
      await getTransactionsXls(token, schemeCustomerId, tetheredUserGuid, resolvedScheme);
    } catch (error) {
      window?._satellite?.track('error');
    } finally {
      setIsDownloading(false);
    }
  };

  return (
    <DownloadButtonContent
      handleDownloadCSV={handleDownloadTransactionsXls}
      altText={t('spending.download.fileXls.text')}
      buttonText={t('spending.download.fileXls.text')}
      testId={testId}
      className={className}
      icons={icons}
    />
  );
}

export default TransactionsDownloadButton;

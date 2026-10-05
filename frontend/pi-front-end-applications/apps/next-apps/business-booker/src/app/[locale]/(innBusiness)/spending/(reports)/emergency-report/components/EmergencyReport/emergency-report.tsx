'use client';

import { Language } from '@whitbread-eos/api';
import { Button, Notification } from '@whitbread-eos/atoms/ui';
import { downloadFromS3PreSignedUrl } from '@whitbread-eos/utils';
import { useTranslation, formatIBAssetsUrl, getEmergencyReport } from '@whitbread-eos/utils/server';
import { Download } from 'lucide-react';
import { useCallback, useState } from 'react';

type Props = {
  icons: Record<string, string>;
  baseDataTestId: string;
  language: Language;
  token: string;
};

const EmergencyReport = ({ icons, baseDataTestId, language, token }: Props) => {
  const { t } = useTranslation('spending');
  const [showReportError, setShowReportError] = useState<string | undefined>(undefined);

  const handleGenerateReport = useCallback(async () => {
    let response;

    try {
      response = await getEmergencyReport(token, language);

      if (response?.data?.emergencyReport) {
        const { downloadUrl, fileName } = response.data.emergencyReport;
        downloadFromS3PreSignedUrl(downloadUrl, fileName);
        setShowReportError(undefined);
      } else if (response?.errors) {
        const errorType = response.errors[0]?.extensions?.errorType;
        setShowReportError(
          errorType === 404 ? t('emergency.report.error') : t('report.error.generic')
        );
      }
    } catch (error) {
      setShowReportError(t('report.error.generic'));
    }
  }, [language, t, token]);

  return (
    <div
      data-testid={`${baseDataTestId}-container`}
      className={'max-w-[39.063rem] mobile:max-w-[100%]'}
    >
      <p className={'text-neutral-900 pb-[3rem]'}>{t('emergency.report.description')}</p>
      {showReportError && (
        <Notification
          className={'mb-4 max-w-[100%]'}
          type="error"
          icon={formatIBAssetsUrl(icons['icon.notification.error'])}
          title={''}
          message={showReportError}
        />
      )}
      <Button
        onClick={() => handleGenerateReport()}
        data-testid="IB-Generate-Emergency-Report"
        variant="default"
      >
        <Download className={'mr-[0.5rem]'} color="white" width={24} height={24} />
        {t('emergency.report.download.button')}
      </Button>
    </div>
  );
};

export default EmergencyReport;

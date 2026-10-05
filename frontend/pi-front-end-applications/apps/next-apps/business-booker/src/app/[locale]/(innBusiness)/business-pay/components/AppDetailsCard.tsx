import { useTranslation } from '@whitbread-eos/utils';
import React from 'react';

type AppDetailsCardProps = {
  companyName: string;
  applicationReference: string;
  startedBy: string;
  baseDataTestId: string;
};

export const AppDetailsCard = ({
  companyName,
  applicationReference,
  startedBy,
  baseDataTestId,
}: AppDetailsCardProps) => {
  const { t } = useTranslation('payApplication');
  return (
    <div className={fieldStyleWithBorder} data-testid={`${baseDataTestId}-application-details`}>
      <h2
        data-testid={`${baseDataTestId}-application-details-company-name`}
        className="text-lg font-bold leading-8"
      >
        {companyName}
      </h2>
      <span className={spanStyle} data-testid={`${baseDataTestId}-application-details-reference`}>
        {t('payApp.application.reference')}
        <span className="font-bold"> {applicationReference}</span>
      </span>
      <span className={spanStyle} data-testid={`${baseDataTestId}-application-details-started-by`}>
        <span className="font-bold">{t('payApp.startedBy.label')}</span> {startedBy}
      </span>
    </div>
  );
};

const fieldStyleWithBorder =
  'flex flex-col pb-6 first:pt-0 last:pb-0 border-b last:border-0 border-lightGrey3';
const spanStyle = 'block truncate whitespace-normal';

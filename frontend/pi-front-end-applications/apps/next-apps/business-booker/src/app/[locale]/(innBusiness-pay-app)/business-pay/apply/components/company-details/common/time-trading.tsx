'use client';

import { FormSelect } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import React from 'react';
import { Controller, useFormContext } from 'react-hook-form';

interface Props {
  icons: Record<string, string>;
  timeTrading: string;
}

export const TimeTradingForm = ({ icons, timeTrading }: Readonly<Props>) => {
  const { t } = useTranslation('payApplication');

  const timeTradingArray = timeTrading.split(',');

  const options = timeTradingArray.map((period) => ({
    // eslint-disable-next-line no-useless-escape
    value: period.trim().replace(/['"\[\]]/g, ''),
    // eslint-disable-next-line no-useless-escape
    displayValue: period.trim().replace(/['"\[\]]/g, ''),
  }));

  const {
    control,
    formState: { errors },
  } = useFormContext();

  return (
    <div data-testid="TimeTradingForm-container">
      <h4 data-testid={`title`} className={headingStyle}>
        {t('companyDetails.timeTrading')}
      </h4>
      <div className={'mb-8'}>
        <Controller
          name="timeTradingId"
          control={control}
          render={({ field }) => (
            <FormSelect
              {...field}
              id="TimeTradingForm-timeTradingId"
              placeholder={t('companyDetails.timePeriod')}
              showLabel={false}
              errors={errors}
              arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
              options={options}
            />
          )}
        />
      </div>
    </div>
  );
};

const headingStyle = 'font-bold text-base pb-6';
